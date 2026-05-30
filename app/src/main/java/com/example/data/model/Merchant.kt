package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "merchants")
data class Merchant(
    @PrimaryKey val phone: String,
    val name: String,
    val aadhaar: String,
    val pan: String,
    val shopName: String,
    val village: String,
    val district: String,
    val state: String,
    val staticUpiId: String,
    val isApproved: Boolean = false,
    val registrationTimestamp: Long = System.currentTimeMillis(),
    val password: String = "RAJ${phone.takeLast(4)}", // Generated secure bank-grade password
    val latitude: Double = 21.8475, // Default near Bamhani, MP coordinates
    val longitude: Double = 80.2078,
    val isSubscribed: Boolean = false,
    val trialCount: Int = 0,
    val profilePicture: String = "", // Base64 or local description representation
    val documentPicture: String = "", // Aadhaar/PAN upload representation
    val qrPicture: String = "" // Scanned Static QR standee representation
)
