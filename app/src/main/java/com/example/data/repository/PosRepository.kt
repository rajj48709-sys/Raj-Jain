package com.example.data.repository

import com.example.data.db.MerchantDao
import com.example.data.db.TransactionDao
import com.example.data.model.Merchant
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class PosRepository(
    private val merchantDao: MerchantDao,
    private val transactionDao: TransactionDao
) {
    val allMerchants: Flow<List<Merchant>> = merchantDao.getAllMerchantsFlow()

    fun getMerchantFlow(phone: String): Flow<Merchant?> = merchantDao.getMerchantFlow(phone)

    suspend fun getMerchantByPhone(phone: String): Merchant? = merchantDao.getMerchantByPhone(phone)

    suspend fun insertMerchant(merchant: Merchant) = merchantDao.insertMerchant(merchant)

    suspend fun updateMerchant(merchant: Merchant) = merchantDao.updateMerchant(merchant)

    suspend fun deleteMerchant(phone: String) = merchantDao.deleteMerchant(phone)

    suspend fun getAllMerchants(): List<Merchant> = merchantDao.getAllMerchants()

    fun getPaymentsForMerchantFlow(phone: String): Flow<List<Transaction>> =
        transactionDao.getPaymentsForMerchantFlow(phone)

    fun getAllPaymentsFlow(): Flow<List<Transaction>> = transactionDao.getAllPaymentsFlow()

    fun getDailyVolumeFlow(phone: String): Flow<Double?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return transactionDao.getDailyVolumeFlow(phone, calendar.timeInMillis)
    }

    suspend fun insertTransaction(transaction: Transaction) =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)
}
