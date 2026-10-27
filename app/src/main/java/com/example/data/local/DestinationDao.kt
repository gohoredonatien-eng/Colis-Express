package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Destination
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationDao {
  @Query("SELECT * FROM destinations WHERE isActive = 1 ORDER BY basePriceFcfa ASC, cityName ASC")
  fun getActiveDestinations(): Flow<List<Destination>>

  @Query("SELECT * FROM destinations ORDER BY cityName ASC")
  fun getAllDestinations(): Flow<List<Destination>>

  @Query("SELECT * FROM destinations WHERE cityName = :city LIMIT 1")
  suspend fun getDestinationByName(city: String): Destination?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDestinations(destinations: List<Destination>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDestination(destination: Destination)

  @Update
  suspend fun updateDestination(destination: Destination)
}
