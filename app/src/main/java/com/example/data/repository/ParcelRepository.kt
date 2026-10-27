package com.example.data.repository

import com.example.data.local.DestinationDao
import com.example.data.local.ParcelDao
import com.example.data.model.Destination
import com.example.data.model.Parcel
import com.example.data.model.ParcelStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.StatusHistoryItem
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class ParcelRepository(
  private val parcelDao: ParcelDao,
  private val destinationDao: DestinationDao
) {
  val allParcels: Flow<List<Parcel>> = parcelDao.getAllParcels()
  val activeDestinations: Flow<List<Destination>> = destinationDao.getActiveDestinations()
  val allDestinations: Flow<List<Destination>> = destinationDao.getAllDestinations()

  fun observeParcel(ref: String): Flow<Parcel?> = parcelDao.observeParcelByRef(ref)

  suspend fun getParcel(ref: String): Parcel? = parcelDao.getParcelByRef(ref)

  suspend fun createParcel(parcel: Parcel): String {
    parcelDao.insertParcel(parcel)
    return parcel.reference
  }

  suspend fun updateParcelStatus(
    ref: String,
    newStatus: ParcelStatus,
    location: String,
    comment: String
  ): Boolean {
    val existing = parcelDao.getParcelByRef(ref) ?: return false
    val newHistoryItem = StatusHistoryItem(
      status = newStatus,
      timestamp = System.currentTimeMillis(),
      location = location,
      comment = comment
    )
    val updatedList = existing.history + newHistoryItem
    val updatedParcel = existing.copy(
      status = newStatus,
      history = updatedList
    )
    parcelDao.updateParcel(updatedParcel)
    return true
  }

  suspend fun recordPayment(
    ref: String,
    method: String,
    transactionId: String,
    status: PaymentStatus = PaymentStatus.PAYE_MOBILE_MONEY
  ): Boolean {
    val existing = parcelDao.getParcelByRef(ref) ?: return false
    val updated = existing.copy(
      paymentStatus = status,
      paymentMethod = method,
      paymentTransactionId = transactionId
    )
    parcelDao.updateParcel(updated)
    return true
  }

  suspend fun saveDestination(destination: Destination) {
    destinationDao.insertDestination(destination)
  }

  suspend fun calculateEstimatedPrice(cityName: String, weightKg: Double, category: String): Int {
    val dest = destinationDao.getDestinationByName(cityName)
    val base = dest?.basePriceFcfa ?: 1500
    val weightExtra = when {
      weightKg <= 1.0 -> 0
      weightKg <= 3.0 -> 500
      weightKg <= 6.0 -> 1000
      weightKg <= 10.0 -> 1500
      else -> ((weightKg - 10) * 200).toInt() + 2000
    }
    val categoryExtra = if (category.contains("Fragile", ignoreCase = true) || category.contains("Électronique", ignoreCase = true)) {
      500
    } else {
      0
    }
    return base + weightExtra + categoryExtra
  }

  companion object {
    fun generateReference(): String {
      val randomCode = (1000..9999).random()
      return "IVO-CI-$randomCode"
    }

    fun formatDate(timestamp: Long): String {
      val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
      return sdf.format(Date(timestamp))
    }
  }
}
