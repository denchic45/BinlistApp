package com.denchic45.binlist.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BinDetailsDao {

    @Insert(entity = BinDetailsEntity::class)
    suspend fun insert(entity: BinDetailsEntity)

    @Query("SELECT * FROM bin_details ORDER BY requested_at DESC")
    fun getAll(): Flow<List<BinDetailsEntity>>

    @Query("DELETE FROM bin_details WHERE bind_details_id=:id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM bin_details")
    suspend fun deleteAll()

    @Query("SELECT * FROM bin_details WHERE bin=:bin")
    suspend fun getByBin(bin: String): BinDetailsEntity
} 