package com.example.cryptotracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingDao {

    @Query("SELECT * FROM holdings ORDER BY addedAt DESC")
    fun getAllHoldings(): Flow<List<HoldingEntity>>

    @Query("SELECT * FROM holdings ORDER BY addedAt DESC")
    suspend fun getAllHoldingsList(): List<HoldingEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM holdings WHERE pairAddress = :pairAddress)")
    fun isInPortfolio(pairAddress: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolding(holding: HoldingEntity)

    @Query("DELETE FROM holdings WHERE pairAddress = :pairAddress")
    suspend fun deleteHolding(pairAddress: String)
}
