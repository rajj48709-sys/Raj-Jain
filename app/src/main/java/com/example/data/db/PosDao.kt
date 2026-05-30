package com.example.data.db

import androidx.room.*
import com.example.data.model.Merchant
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MerchantDao {
    @Query("SELECT * FROM merchants ORDER BY registrationTimestamp DESC")
    fun getAllMerchantsFlow(): Flow<List<Merchant>>

    @Query("SELECT * FROM merchants")
    suspend fun getAllMerchants(): List<Merchant>

    @Query("SELECT * FROM merchants WHERE phone = :phone LIMIT 1")
    suspend fun getMerchantByPhone(phone: String): Merchant?

    @Query("SELECT * FROM merchants WHERE phone = :phone LIMIT 1")
    fun getMerchantFlow(phone: String): Flow<Merchant?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchant(merchant: Merchant)

    @Update
    suspend fun updateMerchant(merchant: Merchant)

    @Query("DELETE FROM merchants WHERE phone = :phone")
    suspend fun deleteMerchant(phone: String)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM payments WHERE merchantPhone = :phone ORDER BY timestamp DESC")
    fun getPaymentsForMerchantFlow(phone: String): Flow<List<Transaction>>

    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    fun getAllPaymentsFlow(): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM payments WHERE merchantPhone = :phone AND status = 'SUCCESS' AND timestamp >= :startOfDay")
    fun getDailyVolumeFlow(phone: String, startOfDay: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)
}
