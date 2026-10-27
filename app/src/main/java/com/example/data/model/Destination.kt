package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destinations")
data class Destination(
  @PrimaryKey
  val id: String,
  val cityName: String,
  val region: String,
  val basePriceFcfa: Int, // A partir de 1500 FCFA
  val deliveryDelayHours: String, // ex: "24h chrono", "24h - 48h"
  val agencyAddress: String, // ex: "Gare centrale / Agence Ivocolis"
  val isPopular: Boolean = false,
  val isActive: Boolean = true
)

object DefaultDestinations {
  val initialList = listOf(
    Destination(
      id = "dest_yamoussoukro",
      cityName = "Yamoussoukro",
      region = "Bélier",
      basePriceFcfa = 1500,
      deliveryDelayHours = "24h",
      agencyAddress = "Quartier Habitat face Gare UTB",
      isPopular = true
    ),
    Destination(
      id = "dest_bouake",
      cityName = "Bouaké",
      region = "Gbêkê",
      basePriceFcfa = 1500,
      deliveryDelayHours = "24h",
      agencyAddress = "Commerce, carrefour Sokoura",
      isPopular = true
    ),
    Destination(
      id = "dest_sanpedro",
      cityName = "San-Pédro",
      region = "San-Pédro",
      basePriceFcfa = 2000,
      deliveryDelayHours = "24h - 48h",
      agencyAddress = "Cité, face pharmacie du Port",
      isPopular = true
    ),
    Destination(
      id = "dest_korhogo",
      cityName = "Korhogo",
      region = "Poro",
      basePriceFcfa = 2000,
      deliveryDelayHours = "24h - 48h",
      agencyAddress = "Quartier 14, avenue principale",
      isPopular = true
    ),
    Destination(
      id = "dest_daloa",
      cityName = "Daloa",
      region = "Haut-Sassandra",
      basePriceFcfa = 2000,
      deliveryDelayHours = "24h",
      agencyAddress = "Gare routière, quartier Tazibouo",
      isPopular = true
    ),
    Destination(
      id = "dest_man",
      cityName = "Man",
      region = "Tonkpi",
      basePriceFcfa = 2500,
      deliveryDelayHours = "48h",
      agencyAddress = "Grand Marché, rue du commerce",
      isPopular = false
    ),
    Destination(
      id = "dest_gagnoa",
      cityName = "Gagnoa",
      region = "Gôh",
      basePriceFcfa = 1800,
      deliveryDelayHours = "24h",
      agencyAddress = "Quartier Dioulabougou",
      isPopular = false
    ),
    Destination(
      id = "dest_abengourou",
      cityName = "Abengourou",
      region = "Indénié-Djuablin",
      basePriceFcfa = 2000,
      deliveryDelayHours = "24h",
      agencyAddress = "Carrefour Cafop, centre-ville",
      isPopular = false
    ),
    Destination(
      id = "dest_soubre",
      cityName = "Soubré",
      region = "Nawa",
      basePriceFcfa = 2000,
      deliveryDelayHours = "24h - 48h",
      agencyAddress = "Quartier Campement",
      isPopular = false
    ),
    Destination(
      id = "dest_dabou",
      cityName = "Dabou / Grand-Lahou",
      region = "Grands-Ponts",
      basePriceFcfa = 1500,
      deliveryDelayHours = "Même jour (12h)",
      agencyAddress = "Entrée de ville",
      isPopular = false
    )
  )
}
