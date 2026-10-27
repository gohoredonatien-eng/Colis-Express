package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Parcel
import kotlinx.coroutines.flow.Flow

@Dao
interface ParcelDao {
  @Query("SELECT * FROM parcels ORDER BY createdAt DESC")
  fun getAllParcels(): Flow<List<Parcel>>

  @Query("SELECT * FROM parcels WHERE reference = :ref LIMIT 1")
  suspend fun getParcelByRef(ref: String): Parcel?

  @Query("SELECT * FROM parcels WHERE reference = :ref LIMIT 1")
  fun observeParcelByRef(ref: String): Flow<Parcel?>

  @Query("SELECT * FROM parcels WHERE senderPhone LIKE '%' || :phone || '%' OR recipientPhone LIKE '%' || :phone || '%' ORDER BY createdAt DESC")
  fun getParcelsByPhone(phone: String): Flow<List<Parcel>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertParcel(parcel: Parcel)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertParcels(parcels: List<Parcel>)

  @Update
  suspend fun updateParcel(parcel: Parcel)

  @Query("DELETE FROM parcels WHERE reference = :ref")
  suspend fun deleteParcel(ref: String)
}
