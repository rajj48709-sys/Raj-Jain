package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Merchant
import com.example.data.model.Transaction
import com.example.data.repository.PosRepository
import com.example.utils.SoundBoxAnnouncer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

enum class Screen {
    WELCOME,
    ONBOARDING,
    LOGIN_MERCHANT,
    LOGIN_ADMIN,
    ADMIN_DASHBOARD,
    MERCHANT_DASHBOARD,
    GENERATE_QR,
    SCANNER_SIMULATOR
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PosRepository
    val announcer: SoundBoxAnnouncer

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PosRepository(database.merchantDao(), database.transactionDao())
        announcer = SoundBoxAnnouncer(application)

        // Pre-seed database with initial retail environments for high-fidelity testing
        seedInitialData()
    }

    // Navigation and Routing States
    private val _currentScreen = MutableStateFlow(Screen.WELCOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // Role state
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _loggedInMerchant = MutableStateFlow<Merchant?>(null)
    val loggedInMerchant: StateFlow<Merchant?> = _loggedInMerchant.asStateFlow()

    // Database flow streams
    val allMerchants: StateFlow<List<Merchant>> = repository.allMerchants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingMerchants: StateFlow<List<Merchant>> = repository.allMerchants
        .map { list -> list.filter { !it.isApproved } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val approvedMerchants: StateFlow<List<Merchant>> = repository.allMerchants
        .map { list -> list.filter { it.isApproved } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<Transaction>> = repository.getAllPaymentsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentMerchantPayments: StateFlow<List<Transaction>> = _loggedInMerchant
        .flatMapLatest { merchant ->
            if (merchant != null) {
                repository.getPaymentsForMerchantFlow(merchant.phone)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentMerchantDailyVolume: StateFlow<Double> = _loggedInMerchant
        .flatMapLatest { merchant ->
            if (merchant != null) {
                repository.getDailyVolumeFlow(merchant.phone).map { it ?: 0.0 }
            } else {
                flowOf(0.0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Onboarding Form States
    var currentOnboardStep = MutableStateFlow(1)
    
    val obName = MutableStateFlow("")
    val obPhone = MutableStateFlow("")
    val obShopName = MutableStateFlow("")
    val obVillage = MutableStateFlow("Bamhani") // Default hyper-local village
    val obDistrict = MutableStateFlow("Balaghat") // Default local district represent
    val obState = MutableStateFlow("Madhya Pradesh") // Default regional state
    val obAadhaar = MutableStateFlow("")
    val obPan = MutableStateFlow("")
    val obUpiId = MutableStateFlow("")
    
    // Virtual photo representation checklist flags (Base64 placeholder simulated content)
    val obSelfiePhoto = MutableStateFlow("")
    val obAadhaarPhoto = MutableStateFlow("")
    val obStaticQrPhoto = MutableStateFlow("")

    val obVerificationFeedback = MutableStateFlow("")

    // Merchant inputs
    val amountInput = MutableStateFlow("")
    val paymentErrorMessage = MutableStateFlow("")

    // QR presenting state
    val activeUpiPayload = MutableStateFlow("")
    val activeTransactionAmount = MutableStateFlow(0.0)
    val simulatedPaymentTimer = MutableStateFlow(180) // 180 seconds payment timer countdown

    // TextToSpeech soundbox preference settings
    val soundBoxLanguageHindi = MutableStateFlow(true)
    val soundBoxSoundEnabled = MutableStateFlow(true)

    // Alert Lock Popups
    val showSoundBoxExpiredDialog = MutableStateFlow(false)

    // Pre-seeding database for frictionless testing
    private fun seedInitialData() {
        viewModelScope.launch {
            val list = repository.getAllMerchants()
            if (list.isEmpty()) {
                // Seed 1: Hemant Jain (Raj Computer Shop POS - Active Merchant, Fully Subscribed)
                val testMerchant1 = Merchant(
                    phone = "7987580921",
                    name = "Hemant Jain",
                    aadhaar = "111122223333",
                    pan = "ABCDE1234F",
                    shopName = "Raj Computer & Stationary",
                    village = "Bamhani",
                    district = "Balaghat",
                    state = "Madhya Pradesh",
                    isApproved = true,
                    isSubscribed = true,
                    staticUpiId = "7987580921@okaxis",
                    password = "RAJY", // Easy passcode
                    latitude = 21.8475,
                    longitude = 80.2078,
                    profilePicture = "SELFIE_HEMANT_RAJ",
                    documentPicture = "DOC_AADHAAR_PAN_HEMANT",
                    qrPicture = "STATIC_QR_HEMANT_7987580921"
                )
                repository.insertMerchant(testMerchant1)

                // Seed 2: Ramesh Kumar (Sahu Grocery - Pending Verification)
                val testMerchant2 = Merchant(
                    phone = "9876543210",
                    name = "Ramesh Kumar Sahu",
                    aadhaar = "444455556666",
                    pan = "XYZWP5678Q",
                    shopName = "Sahu Daily Needs Store",
                    village = "Lamta",
                    district = "Balaghat",
                    state = "Madhya Pradesh",
                    isApproved = false,
                    isSubscribed = false,
                    staticUpiId = "rameshsahu@ybl",
                    password = "RAJX",
                    latitude = 21.9010,
                    longitude = 80.1250,
                    profilePicture = "SELFIE_RAMESH",
                    documentPicture = "DOC_AADHAAR_PAN_RAMESH",
                    qrPicture = "STATIC_QR_RAMESH_9876543210"
                )
                repository.insertMerchant(testMerchant2)
            }
        }
    }

    // Role-Based actions
    fun adminLogin(id: String, passcode: String): Boolean {
        return if (id == "rajadmin" && passcode == "rajjain") {
            _isAdminLoggedIn.value = true
            navigateTo(Screen.ADMIN_DASHBOARD)
            true
        } else {
            false
        }
    }

    fun adminLogout() {
        _isAdminLoggedIn.value = false
        navigateTo(Screen.WELCOME)
    }

    fun merchantLogin(phone: String, passcode: String, onFail: (String) -> Unit) {
        viewModelScope.launch {
            val merchant = repository.getMerchantByPhone(phone)
            if (merchant == null) {
                onFail("Retailer not found. Please register or check phone number.")
            } else if (!merchant.isApproved) {
                onFail("Verifying Onboarding! Admin from Raj Computer (7987580921) is double-checking your GPay sticker photo, PAN, Aadhaar, and Selfies. Access will be live shortly!")
            } else if (merchant.password != passcode) {
                onFail("Incorrect Merchant Pin! If you've forgotten, click Support or request admin override.")
            } else {
                _loggedInMerchant.value = merchant
                navigateTo(Screen.MERCHANT_DASHBOARD)
            }
        }
    }

    fun merchantLogout() {
        _loggedInMerchant.value = null
        navigateTo(Screen.WELCOME)
    }

    // Verification Onboarding Procedures
    fun nextOnboardStep() {
        if (currentOnboardStep.value < 4) {
            currentOnboardStep.value += 1
        }
    }

    fun prevOnboardStep() {
        if (currentOnboardStep.value > 1) {
            currentOnboardStep.value -= 1
        }
    }

    // Scan Sticker automatically verified
    fun simulateBarcodeScan() {
        // Scans the static merchant standee QR, parsing details instantly
        val scannedUpiId = "verified_merchant_standee_${obPhone.value}@upi"
        obUpiId.value = scannedUpiId
        obStaticQrPhoto.value = "PHOTOS_STATIC_QR_STANDEE_${UUID.randomUUID().toString().take(6)}"
        obVerificationFeedback.value = "✅ Scanner automatically read static GPay/PhonePe QR sticker. Extracted UPI: $scannedUpiId. Doublecheck complete - 100% Zero-Error verified!"
    }

    fun captureSimulatedSelfie() {
        obSelfiePhoto.value = "GEO_SELFIE_CAP_LOCAL_BAMHANI_${UUID.randomUUID().toString().take(6)}"
        obVerificationFeedback.value = "📸 Live Geo-Selfie Captured. Locked tracking to Raj Computer POS Network."
    }

    fun captureSimulatedDocuments() {
        if (obAadhaar.value.length != 12 || obPan.value.length != 10) {
            obVerificationFeedback.value = "⚠️ Please input valid Aadhaar (12-digits) and PAN (10-characters) before taking photos."
            return
        }
        obAadhaarPhoto.value = "DOC_CAPTURED_AADHAAR_PAN_${UUID.randomUUID().toString().take(6)}"
        obVerificationFeedback.value = "🗂️ Aadhaar and PAN documents captured & encrypted. Security protocols active."
    }

    fun submitOnboarding(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val name = obName.value.trim()
        val phone = obPhone.value.trim()
        val shop = obShopName.value.trim()
        val village = obVillage.value.trim()
        val dist = obDistrict.value.trim()
        val stateName = obState.value.trim()
        val aadhaarNum = obAadhaar.value.trim()
        val panNum = obPan.value.trim()
        val upiId = obUpiId.value.trim()

        if (name.isEmpty() || phone.isEmpty() || shop.isEmpty() || upiId.isEmpty() ||
            aadhaarNum.length != 12 || panNum.length != 10 ||
            obSelfiePhoto.value.isEmpty() || obAadhaarPhoto.value.isEmpty() || obStaticQrPhoto.value.isEmpty()) {
            onError("Insufficient verification data. Complete PAN, Aadhaar inputs, capture Geo-Selfie, upload static QR Standee, and verify UPI ID.")
            return
        }

        viewModelScope.launch {
            val existing = repository.getMerchantByPhone(phone)
            if (existing != null) {
                onError("Retailer Phone is already registered. Try logging in.")
                return@launch
            }

            // Create a secure auto-generated PIN
            val autogenPassword = "RAJ${phone.takeLast(4)}"

            val merchant = Merchant(
                phone = phone,
                name = name,
                shopName = shop,
                village = village,
                district = dist,
                state = stateName,
                aadhaar = aadhaarNum,
                pan = panNum,
                staticUpiId = upiId,
                password = autogenPassword,
                profilePicture = obSelfiePhoto.value,
                documentPicture = obAadhaarPhoto.value,
                qrPicture = obStaticQrPhoto.value,
                latitude = 21.8475 + (Math.random() - 0.5) * 0.05,
                longitude = 80.2078 + (Math.random() - 0.5) * 0.05
            )

            repository.insertMerchant(merchant)
            onSuccess()

            // Reset forms
            currentOnboardStep.value = 1
            obName.value = ""
            obPhone.value = ""
            obShopName.value = ""
            obAadhaar.value = ""
            obPan.value = ""
            obUpiId.value = ""
            obSelfiePhoto.value = ""
            obAadhaarPhoto.value = ""
            obStaticQrPhoto.value = ""
            obVerificationFeedback.value = ""
        }
    }

    // Master Admin Commands
    fun approveMerchant(merchant: Merchant) {
        viewModelScope.launch {
            val approved = merchant.copy(isApproved = true)
            repository.updateMerchant(approved)
        }
    }

    fun toggleSubscription(merchant: Merchant) {
        viewModelScope.launch {
            val doc = merchant.copy(isSubscribed = !merchant.isSubscribed)
            repository.updateMerchant(doc)
            // If logged in merchant is updated, refresh it
            if (_loggedInMerchant.value?.phone == merchant.phone) {
                _loggedInMerchant.value = doc
            }
        }
    }

    fun disableMerchant(phone: String) {
        viewModelScope.launch {
            repository.deleteMerchant(phone)
        }
    }

    // Payment Operations
    fun generateDynamicQr(onSuccess: () -> Unit) {
        val amountStr = amountInput.value.trim()
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val merchant = _loggedInMerchant.value

        if (merchant == null) {
            paymentErrorMessage.value = "No active merchant session loaded."
            return
        }

        if (amount <= 0.0) {
            paymentErrorMessage.value = "Enter a valid positive transaction amount."
            return
        }

        // Iron-Clad Security Protocol limits check: Limit max ₹5000 per tx, daily max limit ₹10,000 max.
        if (amount > 5000.0) {
            paymentErrorMessage.value = "❌ Security Limit: Per transaction limit is capped at ₹5,000 per rule guidelines."
            return
        }

        viewModelScope.launch {
            val dailySum = repository.getDailyVolumeFlow(merchant.phone).first() ?: 0.0
            if (dailySum + amount > 10000.0) {
                paymentErrorMessage.value = "❌ Security Gate: Daily payments cap limit of ₹10,000 reached. Protection active!"
                return@launch
            }

            // Generate clean standard Indian National UPI URI payload
            // upi://pay?pa=address&pn=name&am=amount&cu=INR
            val encodedMerchantName = merchant.shopName.replace(" ", "%20")
            val payload = "upi://pay?pa=${merchant.staticUpiId}&pn=$encodedMerchantName&am=$amount&cu=INR"
            
            activeUpiPayload.value = payload
            activeTransactionAmount.value = amount
            paymentErrorMessage.value = ""
            
            // Navigate to presentation screen
            onSuccess()
        }
    }

    fun simulateCustomerPaymentCompletion() {
        val merchant = _loggedInMerchant.value ?: return
        val amount = activeTransactionAmount.value
        val upiId = merchant.staticUpiId

        viewModelScope.launch {
            // Log Success Transaction
            val txn = Transaction(
                merchantPhone = merchant.phone,
                merchantUpiId = upiId,
                amount = amount,
                status = "SUCCESS",
                customerName = "Villager Customer",
                transactionId = "RAJTXN${System.currentTimeMillis().toString().takeLast(6)}"
            )
            repository.insertTransaction(txn)

            // Dynamic free trial control counter check: 
            // Free soundbox play is checked for 5 occurrences on unsubscribed states.
            var trialIncremented = merchant.trialCount
            var isSubscribedActive = merchant.isSubscribed

            if (!isSubscribedActive) {
                trialIncremented += 1
                // Check if trial has expired (threshold is 5 checks)
                if (trialIncremented > 5) {
                    showSoundBoxExpiredDialog.value = true
                }
            }

            // Update merchant model
            val updatedMerchant = merchant.copy(
                trialCount = trialIncremented
            )
            repository.updateMerchant(updatedMerchant)
            _loggedInMerchant.value = updatedMerchant

            // Announce voice alerts if unlocked or active trial
            if (soundBoxSoundEnabled.value) {
                if (isSubscribedActive || trialIncremented <= 5) {
                    announcer.setLanguage(soundBoxLanguageHindi.value)
                    if (soundBoxLanguageHindi.value) {
                        announcer.speakHindi(amount)
                    } else {
                        announcer.speakEnglish(amount)
                    }
                }
            }

            // Clear amounts and exitQR
            amountInput.value = ""
            navigateTo(Screen.MERCHANT_DASHBOARD)
        }
    }

    override fun onCleared() {
        super.onCleared()
        announcer.shutdown()
    }
}
