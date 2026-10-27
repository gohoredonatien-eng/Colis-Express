package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Converters
import com.example.data.model.DefaultDestinations
import com.example.data.model.Destination
import com.example.data.model.Parcel
import com.example.data.model.ParcelStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.StatusHistoryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [Parcel::class, Destination::class],
  version = 1,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun parcelDao(): ParcelDao
  abstract fun destinationDao(): DestinationDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "ivocolis_database"
        )
          .addCallback(DatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.destinationDao(), database.parcelDao())
          }
        }
      }

      suspend fun populateInitialData(destDao: DestinationDao, parcelDao: ParcelDao) {
        destDao.insertDestinations(DefaultDestinations.initialList)

        val now = System.currentTimeMillis()
        val sampleParcels = listOf(
          Parcel(
            reference = "IVO-CI-8421",
            senderName = "Mamadou Koné",
            senderPhone = "07 67 18 25 45",
            senderCommune = "Cocody",
            senderAddress = "Faya Nouveau Goudron, Cité Sir",
            recipientName = "Awa Traoré",
            recipientPhone = "05 45 88 12 34",
            destinationCity = "Bouaké",
            destinationAddress = "Commerce, Face pharmacie Sokoura",
            parcelCategory = "Textile & Pagnes",
            weightKg = 2.5,
            description = "Colis de 4 pagnes traditionnels et accessoires",
            priceFcfa = 2000,
            status = ParcelStatus.EN_TRANSIT,
            paymentStatus = PaymentStatus.PAYE_MOBILE_MONEY,
            paymentMethod = "Orange Money",
            paymentTransactionId = "OM-CI-9941824",
            createdAt = now - 86400000L,
            history = listOf(
              StatusHistoryItem(
                status = ParcelStatus.DEMANDE_RECU,
                timestamp = now - 86400000L,
                location = "Abidjan - Cocody Faya",
                comment = "Demande d'enlèvement enregistrée sur l'application IVOCOLIS."
              ),
              StatusHistoryItem(
                status = ParcelStatus.RAMASSE_ABIDJAN,
                timestamp = now - 72000000L,
                location = "Cocody Faya Nouveau Goudron",
                comment = "Colis récupéré par le coursier Ivocolis n°3."
              ),
              StatusHistoryItem(
                status = ParcelStatus.RECU_AGENCE,
                timestamp = now - 54000000L,
                location = "Agence Centrale Ivocolis Cocody",
                comment = "Colis pesé, étiqueté et préparé pour l'expédition."
              ),
              StatusHistoryItem(
                status = ParcelStatus.EXPEDIE,
                timestamp = now - 28000000L,
                location = "Gare Routière Abidjan Nord",
                comment = "Colis chargé en car express sécurisé en direction de Bouaké."
              ),
              StatusHistoryItem(
                status = ParcelStatus.EN_TRANSIT,
                timestamp = now - 14000000L,
                location = "Axe Autoroutier du Nord (Tiébissou)",
                comment = "Acheminement en cours. Arrivée estimée dans 3 heures."
              )
            )
          ),
          Parcel(
            reference = "IVO-CI-6510",
            senderName = "Bamba Sékou",
            senderPhone = "01 02 88 44 22",
            senderCommune = "Yopougon",
            senderAddress = "Maroc, Carrefour Anador",
            recipientName = "Kouassi Jean-Marc",
            recipientPhone = "07 11 22 33 44",
            destinationCity = "Yamoussoukro",
            destinationAddress = "Quartier Habitat, près de la Basilique",
            parcelCategory = "Documents & Dossier",
            weightKg = 0.8,
            description = "Enveloppe scellée avec documents administratifs urgents",
            priceFcfa = 1500,
            status = ParcelStatus.LIVRE,
            paymentStatus = PaymentStatus.PAYE_MOBILE_MONEY,
            paymentMethod = "Wave",
            paymentTransactionId = "WV-CI-20261026-881",
            createdAt = now - 172800000L,
            history = listOf(
              StatusHistoryItem(
                status = ParcelStatus.DEMANDE_RECU,
                timestamp = now - 172800000L,
                location = "Abidjan - Yopougon",
                comment = "Demande enregistrée."
              ),
              StatusHistoryItem(
                status = ParcelStatus.RAMASSE_ABIDJAN,
                timestamp = now - 150000000L,
                location = "Yopougon Maroc",
                comment = "Ramassage effectué."
              ),
              StatusHistoryItem(
                status = ParcelStatus.EXPEDIE,
                timestamp = now - 110000000L,
                location = "Abidjan Hub",
                comment = "Départ convoi express Yamoussoukro."
              ),
              StatusHistoryItem(
                status = ParcelStatus.ARRIVE_DESTINATION,
                timestamp = now - 50000000L,
                location = "Yamoussoukro Agence",
                comment = "Arrivé à l'agence de Yamoussoukro. Destinataire contacté par SMS."
              ),
              StatusHistoryItem(
                status = ParcelStatus.LIVRE,
                timestamp = now - 20000000L,
                location = "Yamoussoukro Habitat",
                comment = "Remis en main propre à M. Kouassi Jean-Marc après vérification CNI."
              )
            )
          ),
          Parcel(
            reference = "IVO-CI-9134",
            senderName = "Fatou Diallo",
            senderPhone = "07 89 54 21 00",
            senderCommune = "Marcory",
            senderAddress = "Zone 4, Rue du 7 Décembre",
            recipientName = "Adjoua Delphine",
            recipientPhone = "05 77 66 55 44",
            destinationCity = "San-Pédro",
            destinationAddress = "Bardot 2, face grand marché",
            parcelCategory = "Cosmétiques & Soins",
            weightKg = 3.2,
            description = "Carton de produits cosmétiques naturels",
            priceFcfa = 2500,
            status = ParcelStatus.DEMANDE_RECU,
            paymentStatus = PaymentStatus.NON_PAYE,
            paymentMethod = null,
            createdAt = now - 7200000L,
            history = listOf(
              StatusHistoryItem(
                status = ParcelStatus.DEMANDE_RECU,
                timestamp = now - 7200000L,
                location = "Abidjan - Marcory Zone 4",
                comment = "Demande de collecte créée par la cliente. En attente de passage coursier."
              )
            )
          )
        )
        parcelDao.insertParcels(sampleParcels)
      }
    }
  }
}
