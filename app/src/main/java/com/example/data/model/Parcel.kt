package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

enum class ParcelStatus(val label: String, val stepIndex: Int) {
  DEMANDE_RECU("Demande reçue", 0),
  RAMASSE_ABIDJAN("Ramassé à Abidjan", 1),
  RECU_AGENCE("Reçu à l'agence Cocody", 2),
  EXPEDIE("Expédié vers l'intérieur", 3),
  EN_TRANSIT("En cours d'acheminement", 4),
  ARRIVE_DESTINATION("Arrivé à destination", 5),
  LIVRE("Colis livré avec succès", 6),
  ANNULE("Annulé", -1)
}

enum class PaymentStatus(val label: String) {
  NON_PAYE("En attente de paiement"),
  PAYE_MOBILE_MONEY("Payé par Mobile Money"),
  PAYE_CARTE("Payé par Carte Bancaire"),
  PAYE_A_LA_COLLECTE("À payer à la collecte (Espèces)")
}

data class StatusHistoryItem(
  val status: ParcelStatus,
  val timestamp: Long,
  val location: String,
  val comment: String
)

@Entity(tableName = "parcels")
data class Parcel(
  @PrimaryKey
  val reference: String,
  val senderName: String,
  val senderPhone: String,
  val senderCommune: String, // ex: Cocody, Yopougon, Plateau, Marcory...
  val senderAddress: String, // ex: Faya nouveau goudron, rue ministre...
  val recipientName: String,
  val recipientPhone: String,
  val destinationCity: String, // ex: Bouaké, Yamoussoukro, Korhogo...
  val destinationAddress: String,
  val parcelCategory: String, // Document, Vêtement, Électronique, Colis alimentaire, Fragile...
  val weightKg: Double,
  val description: String,
  val priceFcfa: Int,
  val status: ParcelStatus = ParcelStatus.DEMANDE_RECU,
  val paymentStatus: PaymentStatus = PaymentStatus.NON_PAYE,
  val paymentMethod: String? = null, // "Orange Money", "Wave", "MTN MoMo", "Moov Money", "Carte Bancaire"
  val paymentTransactionId: String? = null,
  val createdAt: Long = System.currentTimeMillis(),
  val history: List<StatusHistoryItem> = emptyList()
)

class Converters {
  private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
  private val listType = Types.newParameterizedType(List::class.java, StatusHistoryItem::class.java)
  private val adapter = moshi.adapter<List<StatusHistoryItem>>(listType)

  @TypeConverter
  fun fromHistoryList(value: List<StatusHistoryItem>?): String {
    return adapter.toJson(value ?: emptyList())
  }

  @TypeConverter
  fun toHistoryList(value: String?): List<StatusHistoryItem> {
    if (value.isNullOrEmpty()) return emptyList()
    return try {
      adapter.fromJson(value) ?: emptyList()
    } catch (e: Exception) {
      emptyList()
    }
  }

  @TypeConverter
  fun fromParcelStatus(status: ParcelStatus): String = status.name

  @TypeConverter
  fun toParcelStatus(value: String): ParcelStatus = try {
    ParcelStatus.valueOf(value)
  } catch (e: Exception) {
    ParcelStatus.DEMANDE_RECU
  }

  @TypeConverter
  fun fromPaymentStatus(status: PaymentStatus): String = status.name

  @TypeConverter
  fun toPaymentStatus(value: String): PaymentStatus = try {
    PaymentStatus.valueOf(value)
  } catch (e: Exception) {
    PaymentStatus.NON_PAYE
  }
}
