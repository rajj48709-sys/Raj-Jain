package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val merchantPhone: String,
    val merchantUpiId: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String, // "SUCCESS", "PENDING", "FAILED"
    val customerName: String,
    val transactionId: String,
    val announced: Boolean = false
)
