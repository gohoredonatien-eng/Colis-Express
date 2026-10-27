package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.firebase.OperationType
import com.example.data.firebase.handleFirestoreError
import com.example.data.model.FirestoreParcel
import com.example.data.model.Parcel
import com.example.data.model.ParcelStatus
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreParcelRepository(
  private val db: FirebaseFirestore
) {
  // CRITICAL: Always resolve the custom database ID from R.string.firestore_database_id
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = Firebase.auth

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("L'utilisateur doit être connecté avec Google pour synchroniser sur Firestore.")
  }

  fun observeUserParcels(): Flow<List<Parcel>> = flow {
    val uid = requireUserId()
    val path = "users/$uid/parcels"
    db.collection("users").document(uid).collection("parcels")
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(FirestoreParcel::class.java).map { it.toParcel() }
      }
      .catch { e ->
        if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
        throw e
      }
      .collect { emit(it) }
  }

  suspend fun saveParcel(parcel: Parcel) {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("parcels").document(parcel.reference)
    val trackingRef = db.collection("tracking").document(parcel.reference)

    try {
      val historyList = parcel.history.map { item ->
        mapOf(
          "status" to item.status.name,
          "timestamp" to item.timestamp,
          "location" to item.location,
          "comment" to item.comment
        )
      }

      val payload = mapOf(
        "id" to parcel.reference,
        "reference" to parcel.reference,
        "senderName" to parcel.senderName,
        "senderPhone" to parcel.senderPhone,
        "senderCommune" to parcel.senderCommune,
        "senderAddress" to parcel.senderAddress,
        "recipientName" to parcel.recipientName,
        "recipientPhone" to parcel.recipientPhone,
        "destinationCity" to parcel.destinationCity,
        "destinationAddress" to parcel.destinationAddress,
        "parcelCategory" to parcel.parcelCategory,
        "weightKg" to parcel.weightKg,
        "description" to parcel.description,
        "priceFcfa" to parcel.priceFcfa.toLong(),
        "status" to parcel.status.name,
        "paymentStatus" to parcel.paymentStatus.name,
        "paymentMethod" to parcel.paymentMethod,
        "paymentTransactionId" to parcel.paymentTransactionId,
        "userId" to uid,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp(),
        "history" to historyList
      )

      docRef.set(payload).await()

      // Also publish public tracking info
      val trackingPayload = mapOf(
        "reference" to parcel.reference,
        "destinationCity" to parcel.destinationCity,
        "senderCommune" to parcel.senderCommune,
        "recipientName" to parcel.recipientName,
        "status" to parcel.status.name,
        "paymentStatus" to parcel.paymentStatus.name,
        "priceFcfa" to parcel.priceFcfa.toLong(),
        "updatedAt" to FieldValue.serverTimestamp(),
        "history" to historyList
      )
      trackingRef.set(trackingPayload).await()

    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docRef.path)
      throw e
    }
  }

  suspend fun updateParcelStatus(
    reference: String,
    newStatus: ParcelStatus,
    location: String,
    comment: String
  ) {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("parcels").document(reference)
    val trackingRef = db.collection("tracking").document(reference)

    try {
      val newHistoryItem = mapOf(
        "status" to newStatus.name,
        "timestamp" to System.currentTimeMillis(),
        "location" to location,
        "comment" to comment
      )

      docRef.update(
        "status", newStatus.name,
        "updatedAt", FieldValue.serverTimestamp(),
        "history", FieldValue.arrayUnion(newHistoryItem)
      ).await()

      trackingRef.update(
        "status", newStatus.name,
        "updatedAt", FieldValue.serverTimestamp(),
        "history", FieldValue.arrayUnion(newHistoryItem)
      ).await()

    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun fetchTracking(reference: String): Map<String, Any?>? {
    val trackingRef = db.collection("tracking").document(reference)
    return try {
      val snap = trackingRef.get().await()
      if (snap.exists()) snap.data else null
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, trackingRef.path)
      null
    }
  }
}
