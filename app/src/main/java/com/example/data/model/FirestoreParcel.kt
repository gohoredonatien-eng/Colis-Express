package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreParcel(
  @DocumentId
  val id: String = "",
  val reference: String = "",
  val senderName: String = "",
  val senderPhone: String = "",
  val senderCommune: String = "",
  val senderAddress: String = "",
  val recipientName: String = "",
  val recipientPhone: String = "",
  val destinationCity: String = "",
  val destinationAddress: String = "",
  val parcelCategory: String = "",
  val weightKg: Double = 0.0,
  val description: String = "",
  val priceFcfa: Long = 0L,
  val status: String = "DEMANDE_RECU",
  val paymentStatus: String = "NON_PAYE",
  val paymentMethod: String? = null,
  val paymentTransactionId: String? = null,
  val userId: String = "",
  @ServerTimestamp
  val createdAt: Timestamp? = null,
  @ServerTimestamp
  val updatedAt: Timestamp? = null,
  val history: List<Map<String, Any>> = emptyList()
) {
  fun toParcel(): Parcel {
    val parcelStatus = try {
      ParcelStatus.valueOf(status)
    } catch (e: Exception) {
      ParcelStatus.DEMANDE_RECU
    }

    val pStatus = try {
      PaymentStatus.valueOf(paymentStatus)
    } catch (e: Exception) {
      PaymentStatus.NON_PAYE
    }

    val historyItems = history.mapNotNull { map ->
      try {
        val stStr = map["status"] as? String ?: "DEMANDE_RECU"
        val st = try { ParcelStatus.valueOf(stStr) } catch (e: Exception) { ParcelStatus.DEMANDE_RECU }
        val ts = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
        val loc = map["location"] as? String ?: ""
        val comment = map["comment"] as? String ?: ""
        StatusHistoryItem(st, ts, loc, comment)
      } catch (e: Exception) {
        null
      }
    }

    val createdMillis = createdAt?.toDate()?.time ?: System.currentTimeMillis()

    return Parcel(
      reference = reference.ifBlank { id },
      senderName = senderName,
      senderPhone = senderPhone,
      senderCommune = senderCommune,
      senderAddress = senderAddress,
      recipientName = recipientName,
      recipientPhone = recipientPhone,
      destinationCity = destinationCity,
      destinationAddress = destinationAddress,
      parcelCategory = parcelCategory,
      weightKg = weightKg,
      description = description,
      priceFcfa = priceFcfa.toInt(),
      status = parcelStatus,
      paymentStatus = pStatus,
      paymentMethod = paymentMethod,
      paymentTransactionId = paymentTransactionId,
      createdAt = createdMillis,
      history = historyItems
    )
  }

  companion object {
    fun fromParcel(parcel: Parcel, userId: String): FirestoreParcel {
      val historyList = parcel.history.map { item ->
        mapOf(
          "status" to item.status.name,
          "timestamp" to item.timestamp,
          "location" to item.location,
          "comment" to item.comment
        )
      }

      return FirestoreParcel(
        id = parcel.reference,
        reference = parcel.reference,
        senderName = parcel.senderName,
        senderPhone = parcel.senderPhone,
        senderCommune = parcel.senderCommune,
        senderAddress = parcel.senderAddress,
        recipientName = parcel.recipientName,
        recipientPhone = parcel.recipientPhone,
        destinationCity = parcel.destinationCity,
        destinationAddress = parcel.destinationAddress,
        parcelCategory = parcel.parcelCategory,
        weightKg = parcel.weightKg,
        description = parcel.description,
        priceFcfa = parcel.priceFcfa.toLong(),
        status = parcel.status.name,
        paymentStatus = parcel.paymentStatus.name,
        paymentMethod = parcel.paymentMethod,
        paymentTransactionId = parcel.paymentTransactionId,
        userId = userId,
        history = historyList
      )
    }
  }
}
