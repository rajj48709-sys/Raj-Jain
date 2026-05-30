package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Merchant
import com.example.data.model.Transaction
import com.example.ui.components.StylizedQrCode
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Core Custom Themes and Gradients to avoid boring default looks
val CosmicSlateBg = Brush.verticalGradient(
    colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F172A))
)
val ActiveCrimsonAccent = Color(0xFFEF4444)
val VerifiedEmeraldGreen = Color(0xFF10B981)
val CoolIndigoBlue = Color(0xFF6366F1)
val WarmAmethystPurple = Color(0xFF8B5CF6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Display Sound Box subscription alert logic
    val showExpiredDialog by viewModel.showSoundBoxExpiredDialog.collectAsStateWithLifecycle()
    if (showExpiredDialog) {
        Dialog(onDismissRequest = { viewModel.showSoundBoxExpiredDialog.value = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(2.dp, Color(0xFFEF4444), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFFEF2F2), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Expired sound box lock icon",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Sound Box Expired",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your ₹49 monthly subscription for the polite Hindi payments announcer is completed or expired. Pay now to reactivate and avoid missing customer alerts.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.showSoundBoxExpiredDialog.value = false
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://api.whatsapp.com/send?phone=917987580921&text=Hello%20Raj%20Computer,%20my%20Sound%20Box%20is%20expired.%20Please%20reactivate%20my%20subscription.")
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // WhatsApp Color
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call/Message icon")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reactivate via WhatsApp", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { viewModel.showSoundBoxExpiredDialog.value = false }
                    ) {
                        Text("Maybe Later", color = Color(0xFF94A3B8))
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(CosmicSlateBg)) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                (fadeIn() + scaleIn(initialScale = 0.92f)) togetherWith (fadeOut() + scaleOut(targetScale = 0.92f))
            },
            label = "screen_navigation"
        ) { targetScreen ->
            when (targetScreen) {
                Screen.WELCOME -> WelcomeScreen(viewModel)
                Screen.ONBOARDING -> OnboardingScreen(viewModel)
                Screen.LOGIN_MERCHANT -> LoginMerchantScreen(viewModel)
                Screen.LOGIN_ADMIN -> LoginAdminScreen(viewModel)
                Screen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel)
                Screen.MERCHANT_DASHBOARD -> MerchantDashboardScreen(viewModel)
                Screen.GENERATE_QR -> GenerateQrScreen(viewModel)
                Screen.SCANNER_SIMULATOR -> ScannerSimulatorScreen(viewModel)
            }
        }
    }
}

@Composable
fun WelcomeScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .navigationBarsPadding()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // High-fidelity branding card
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF6366F1))),
                        RoundedCornerShape(32.dp)
                    )
                    .border(2.dp, Color.White, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Raj Computer POS Launcher icon",
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Raj Computer POS",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "India's First Verified Hyper-Local Fintech Platform\nBamhani, Balaghat",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Verified security badge
            Surface(
                color = Color(0xFF1E293B),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield Security Active Icon",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Iron-clad Security Protocols Enforced (FLAG_SECURE)",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Beautiful Interactive Actions area
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                onClick = { viewModel.navigateTo(Screen.LOGIN_MERCHANT) },
                color = Color(0xFF1E293B),
                border = BorderStroke(2.dp, Color(0xFF334155)),
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth().testTag("retailer_login_action")
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF312E81), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Storefront,
                            contentDescription = "Retailer Login icon",
                            tint = Color(0xFF818CF8)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Retailer Login",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Instant checkout payments, Sound Box alerts",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = "Enter icon", tint = Color(0xFF64748B))
                }
            }

            Surface(
                onClick = { viewModel.navigateTo(Screen.ONBOARDING) },
                color = Color(0xFF1E293B),
                border = BorderStroke(2.dp, Color(0xFF2E1065)),
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth().testTag("retailer_register_action")
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF3B0764), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = "Join platform icon",
                            tint = Color(0xFFC084FC)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Register Shop & Get Verified",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Aadhaar, PAN, and Static QR verification stepper",
                            color = Color(0xFFC084FC),
                            fontSize = 12.sp
                        )
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = "Enter icon", tint = Color(0xFF64748B))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.navigateTo(Screen.LOGIN_ADMIN) },
                    modifier = Modifier.testTag("admin_portal_button")
                ) {
                    Icon(
                        Icons.Default.SettingsInputComponent,
                        contentDescription = "Security admin gate key logo",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Master Admin Control Mode", color = Color(0xFF94A3B8), fontSize = 13.sp)
                }

                Text(
                    text = "v2026.1",
                    color = Color(0xFF475569),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val step by viewModel.currentOnboardStep.collectAsStateWithLifecycle()

    val name by viewModel.obName.collectAsStateWithLifecycle()
    val phone by viewModel.obPhone.collectAsStateWithLifecycle()
    val shopName by viewModel.obShopName.collectAsStateWithLifecycle()
    val village by viewModel.obVillage.collectAsStateWithLifecycle()
    val district by viewModel.obDistrict.collectAsStateWithLifecycle()
    val state by viewModel.obState.collectAsStateWithLifecycle()
    val aadhaar by viewModel.obAadhaar.collectAsStateWithLifecycle()
    val pan by viewModel.obPan.collectAsStateWithLifecycle()
    val upiId by viewModel.obUpiId.collectAsStateWithLifecycle()

    val selfiePhoto by viewModel.obSelfiePhoto.collectAsStateWithLifecycle()
    val docPhoto by viewModel.obAadhaarPhoto.collectAsStateWithLifecycle()
    val qrPhoto by viewModel.obStaticQrPhoto.collectAsStateWithLifecycle()

    val feedbackMsg by viewModel.obVerificationFeedback.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verified Onboarding", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.WELCOME) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back back icon")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Stepper Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..4) {
                        val active = step >= i
                        val current = step == i
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (current) Color(0xFF6366F1)
                                    else if (active) Color(0xFF10B981)
                                    else Color(0xFF334155)
                                )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Step $step of 4", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = when (step) {
                            1 -> "Basic Details"
                            2 -> "Shop & Location"
                            3 -> "KYC Verification"
                            else -> "Static Standee Setup"
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Step Forms
                when (step) {
                    1 -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Primary Contact Information",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Enter your legal name and phone. This links securely to the National Village Ledger index.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.obName.value = it },
                            label = { Text("Full Legal Name (as in Aadhaar)") },
                            modifier = Modifier.fillMaxWidth().testTag("ob_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 10) viewModel.obPhone.value = it },
                            label = { Text("Contact Phone Number") },
                            modifier = Modifier.fillMaxWidth().testTag("ob_phone_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )
                    }

                    2 -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Establishment Location details",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Your POS is geo-fenced for regional safety to preventing outside district fraud.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )

                        OutlinedTextField(
                            value = shopName,
                            onValueChange = { viewModel.obShopName.value = it },
                            label = { Text("Shop / Business Name") },
                            modifier = Modifier.fillMaxWidth().testTag("ob_shop_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = village,
                            onValueChange = { viewModel.obVillage.value = it },
                            label = { Text("Village / City") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = district,
                                onValueChange = { viewModel.obDistrict.value = it },
                                label = { Text("District") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF6366F1),
                                    unfocusedBorderColor = Color(0xFF334155)
                                ),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = state,
                                onValueChange = { viewModel.obState.value = it },
                                label = { Text("State") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF6366F1),
                                    unfocusedBorderColor = Color(0xFF334155)
                                ),
                                singleLine = true
                            )
                        }
                    }

                    3 -> Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                        Text(
                            "KYC & Zero-Leak Security Check",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Both PAN and Aadhaar records are verified carefully with mandatory identity tracking.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )

                        OutlinedTextField(
                            value = aadhaar,
                            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 12) viewModel.obAadhaar.value = it },
                            label = { Text("12-Digit Aadhaar Card Number") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = pan,
                            onValueChange = { if (it.length <= 10) viewModel.obPan.value = it.uppercase() },
                            label = { Text("10-Character PAN Card (Capitalized)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.captureSimulatedSelfie() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selfiePhoto.isNotEmpty()) Color(0xFF10B981) else Color(0xFF3B82F6)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Face, contentDescription = "Camera Icon")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (selfiePhoto.isNotEmpty()) "Selfie ✓" else "Take Selfie", overflow = TextOverflow.Ellipsis, maxLines = 1)
                            }

                            Button(
                                onClick = { viewModel.captureSimulatedDocuments() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (docPhoto.isNotEmpty()) Color(0xFF10B981) else Color(0xFF6366F1)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = "Document camera icon")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (docPhoto.isNotEmpty()) "Doc Photos ✓" else "Upload Docs", overflow = TextOverflow.Ellipsis, maxLines = 1)
                            }
                        }
                    }

                    else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Physical GPay/PhonePe Sticker Check",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "This is the 'Final Verification Wall'. Doublechecking your typed UPI ID against the QR sticker removes payment errors.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // High contrast visual card for scanning sticker simulator
                        Surface(
                            onClick = { viewModel.navigateTo(Screen.SCANNER_SIMULATOR) },
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(2.dp, if (qrPhoto.isNotEmpty()) Color(0xFF10B981) else Color(0xFFEF4444)),
                            modifier = Modifier.fillMaxWidth().testTag("scan_sticker_button")
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(Color(0xFF450A0A), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "Scan QR icon decoration",
                                        tint = Color(0xFFF87171)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (qrPhoto.isNotEmpty()) " ✓ Physical STICKER SCANNED" else "SCAN PHYSICAL UPI QR STANDEE",
                                    color = if (qrPhoto.isNotEmpty()) Color(0xFF10B981) else Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Scan your GPay/PhonePe business sticker at your counter.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "Or typing manual UPI Address (checks validity)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = upiId,
                            onValueChange = { viewModel.obUpiId.value = it },
                            label = { Text("Retailer Counter UPI Address (Manual)") },
                            modifier = Modifier.fillMaxWidth().testTag("ob_upi_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            placeholder = { Text("e.g. shopname@okaxis") },
                            singleLine = true
                        )
                    }
                }

                // Dynamic Interactive Verification Feedback panel
                if (feedbackMsg.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF475569), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = feedbackMsg,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Standard Bottom Control Stepper Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { viewModel.prevOnboardStep() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Text("Back", fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        if (step < 4) {
                            viewModel.nextOnboardStep()
                        } else {
                            viewModel.submitOnboarding(
                                onSuccess = {
                                    viewModel.navigateTo(Screen.WELCOME)
                                    viewModel.obVerificationFeedback.value = "Registration Submitted! Direct Login code: RAJ[last 4 digits of phone]"
                                },
                                onError = { error ->
                                    viewModel.obVerificationFeedback.value = "❌ $error"
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (step == 4) Color(0xFFEF4444) else Color(0xFF6366F1)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.5f).testTag("ob_next_action")
                ) {
                    Text(
                        text = if (step == 4) "Submit Verification" else "Continue",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginMerchantScreen(viewModel: MainViewModel) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorFeedback by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Retailer Verification Login", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.WELCOME) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back back icon")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Welcome back to Raj QR POS",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    "Enter your phone credentials below. Seeded login test: 7987580921, password: RAJY.",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Registered Mobile Phone (10 digits)") },
                    modifier = Modifier.fillMaxWidth().testTag("merchant_phone_field"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Secure App Override PIN") },
                    modifier = Modifier.fillMaxWidth().testTag("merchant_pass_field"),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    singleLine = true
                )

                if (errorFeedback.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF450A0A), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(errorFeedback, color = Color(0xFFFCA5A5), fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        viewModel.merchantLogin(
                            phone = phone,
                            passcode = password,
                            onFail = { errorFeedback = it }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoolIndigoBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("merchant_login_submit")
                ) {
                    Text("Secure Login Gate", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                // Help Trigger button
                TextButton(
                    onClick = {
                        val uri = Uri.parse("https://api.whatsapp.com/send?phone=917987580921&text=Help%20me%20login%20to%20Raj%20Computer%20POS%20app!")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContactSupport,
                        contentDescription = "WhatsApp customer help desk logo",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Contact Help Support Desk (7987580921)", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAdminScreen(viewModel: MainViewModel) {
    var adminId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Console Gate", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.WELCOME) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back back icon")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Master Command Center",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    "Authorize using hardcoded master system parameters to verify rural registration requests.",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = adminId,
                    onValueChange = { adminId = it },
                    label = { Text("Master Admin ID") },
                    modifier = Modifier.fillMaxWidth().testTag("admin_id_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFEF4444),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Master Admin Safety Key") },
                    modifier = Modifier.fillMaxWidth().testTag("admin_pass_field"),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFEF4444),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    singleLine = true
                )

                if (hasError) {
                    Text(
                        text = "Authentication failed! Credentials mismatch.",
                        color = Color(0xFFFCA5A5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = {
                    val ok = viewModel.adminLogin(adminId, password)
                    hasError = !ok
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActiveCrimsonAccent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("admin_login_submit")
            ) {
                Text("Unlock Admin Hub", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(viewModel: MainViewModel) {
    val pending by viewModel.pendingMerchants.collectAsStateWithLifecycle()
    val approved by viewModel.approvedMerchants.collectAsStateWithLifecycle()
    val payments by viewModel.allPayments.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) } // 0: Pending Onboardings, 1: Approved Merchants, 2: System Logs

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Raj POS Central Panel", fontWeight = FontWeight.ExtraBold) },
                actions = {
                    IconButton(onClick = { viewModel.adminLogout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Log out of dashboard", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            // Stats card for Madhya Pradesh - India hyper coverage
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("National Village Ledger Metrics", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Outlets", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            val totalCount = pending.size + approved.size
                            Text("$totalCount Registered", color = CoolIndigoBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Active Soundboxes", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            val subCount = approved.count { it.isSubscribed }
                            Text("$subCount Terminals", color = VerifiedEmeraldGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Daily Platform Tx", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            val successTxSum = payments.filter { it.status == "SUCCESS" }.sumOf { it.amount }
                            Text("₹${successTxSum.toInt()}", color = Color.Yellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Tabs Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF6366F1)
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Pending (${pending.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Verified Merchants (${approved.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Platform Ledger", fontWeight = FontWeight.Bold) }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (pending.isEmpty()) {
                        item {
                            EmptyStateComponent(
                                icon = Icons.Default.AllInbox,
                                title = "Zero Pending Reviews",
                                description = "All onboarding requests of PAN, Aadhaar standee checks, and GPay sticker alignments are thoroughly processed!"
                            )
                        }
                    } else {
                        items(pending) { merchant ->
                            PendingMerchantVerificationCard(
                                merchant = merchant,
                                onApprove = { viewModel.approveMerchant(merchant) },
                                onReject = { viewModel.disableMerchant(merchant.phone) }
                            )
                        }
                    }
                }

                1 -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (approved.isEmpty()) {
                        item {
                            EmptyStateComponent(
                                icon = Icons.Default.Storefront,
                                title = "No Active Terminals",
                                description = "No approved retailers are live yet. Visit pending reviews to activate your rural outreach network."
                            )
                        }
                    } else {
                        items(approved) { merchant ->
                            ApprovedMerchantManagerRow(
                                merchant = merchant,
                                onToggleSub = { viewModel.toggleSubscription(merchant) },
                                onDelete = { viewModel.disableMerchant(merchant.phone) }
                            )
                        }
                    }
                }

                2 -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (payments.isEmpty()) {
                        item {
                            EmptyStateComponent(
                                icon = Icons.Default.ReceiptLong,
                                title = "No Transaction Traffic",
                                description = "Platform history is blank. Live merchants generating dynamic checkout QRs will appear here."
                            )
                        }
                    } else {
                        items(payments) { payment ->
                            FintechTxLedgerRow(payment = payment)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateComponent(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Empty state icon indicator",
            tint = Color(0xFF475569),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun PendingMerchantVerificationCard(
    merchant: Merchant,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFF475569)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF3B0764), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PersonSearch, contentDescription = "Review icon", tint = Color(0xFFC084FC))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(merchant.shopName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Owner: ${merchant.name} | Phone: ${merchant.phone}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color(0xFF334155))

            Spacer(modifier = Modifier.height(12.dp))

            // Geographic Coordinates mapping details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("GPS REGION CONTROL", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    Text("${merchant.village}, ${merchant.district}, ${merchant.state}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Lat: ${String.format("%.4f", merchant.latitude)} | Lon: ${String.format("%.4f", merchant.longitude)}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("STANDEE CHECKED ID", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    Text(merchant.staticUpiId, color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Security pass: ${merchant.password}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Simulated document previews row
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0F172A), RoundedCornerShape(8.dp)).padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Verification asset status icon", tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Selfie, PAN, Aadhaar files verified", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                }
                Text("Zero Fraud ✓", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Deny", fontSize = 13.sp)
                }

                Button(
                    onClick = onApprove,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerifiedEmeraldGreen),
                    modifier = Modifier.weight(1.5f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Check verify approve logo", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Approve Retailer", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ApprovedMerchantManagerRow(
    merchant: Merchant,
    onToggleSub: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(merchant.shopName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("ID: ${merchant.phone} | Pin: ${merchant.password}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteForever, contentDescription = "Disable/Kill switch", tint = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0F172A), RoundedCornerShape(10.dp)).padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("SOUNDBOX SUBSCRIPTION", color = Color(0xFFCBD5E1), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (merchant.isSubscribed) "Active Billing (₹49/mo)" else "Trial Status / Unpaid",
                        color = if (merchant.isSubscribed) VerifiedEmeraldGreen else Color(0xFFF59E0B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Switch(
                    checked = merchant.isSubscribed,
                    onCheckedChange = { onToggleSub() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = VerifiedEmeraldGreen,
                        uncheckedThumbColor = Color(0xFF94A3B8),
                        uncheckedTrackColor = Color(0xFF475569)
                    )
                )
            }
        }
    }
}

@Composable
fun FintechTxLedgerRow(payment: Transaction) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.5f)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF065F46), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Transaction Inward Icon", tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Inflow via QR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("UPI: ${payment.merchantUpiId}", color = Color(0xFF94A3B8), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("+₹${payment.amount.toInt()}", color = Color.Yellow, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH)
                Text(sdf.format(Date(payment.timestamp)), color = Color(0xFF64748B), fontSize = 9.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantDashboardScreen(viewModel: MainViewModel) {
    val merchant by viewModel.loggedInMerchant.collectAsStateWithLifecycle()
    val dailyTotal by viewModel.currentMerchantDailyVolume.collectAsStateWithLifecycle()
    val historyLog by viewModel.currentMerchantPayments.collectAsStateWithLifecycle()
    val inputAmount by viewModel.amountInput.collectAsStateWithLifecycle()
    val errorMsg by viewModel.paymentErrorMessage.collectAsStateWithLifecycle()

    val ttsLanguageHindi by viewModel.soundBoxLanguageHindi.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundBoxSoundEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (merchant == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(merchant?.shopName ?: "Workspace Mobile POS", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp) },
                actions = {
                    IconButton(onClick = { viewModel.merchantLogout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Merchant log out", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A), titleContentColor = Color.White)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live status & Daily targets summary card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(VerifiedEmeraldGreen, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LIVE VERIFIED TERMINAL", color = VerifiedEmeraldGreen, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            }

                            // Subscribed Status tag
                            Surface(
                                color = if (merchant!!.isSubscribed) Color(0xFF065F46) else Color(0xFF451A03),
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text(
                                    text = if (merchant!!.isSubscribed) "Sound Box PRO ✓" else "Sound Box Trial ${merchant!!.trialCount}/5",
                                    color = if (merchant!!.isSubscribed) Color(0xFF34D399) else Color(0xFFFBBF24),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(merchant?.name ?: "", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("${merchant?.village}, ${merchant?.district} (${merchant?.state})", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Medium)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("TODAY'S RECEIVED", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("₹${dailyTotal.toInt()}", color = Color.Yellow, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("OUTLET DAILY REVENUE LIMIT", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("₹10,000 LIMIT", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Single Check caps: ₹5,000 max", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // Keyboard Numeric checkout input card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Instant Customer Payment Invoice", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        OutlinedTextField(
                            value = inputAmount,
                            onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 4) viewModel.amountInput.value = it },
                            label = { Text("Enter Amount (₹)") },
                            modifier = Modifier.fillMaxWidth().testTag("payment_amount_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = "Currency Symbol logo", tint = Color.Yellow) },
                            singleLine = true
                        )

                        if (errorMsg.isNotEmpty()) {
                            Text(errorMsg, color = Color(0xFFFCA5A5), fontSize = 11.sp, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.generateDynamicQr(
                                    onSuccess = { viewModel.navigateTo(Screen.GENERATE_QR) }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("generate_qr_btn")
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = "Qr logo")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Dynamic Checkout QR", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Active Voice sound box controls
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Virtual Paytm Sound Box Speaker", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Polite payment confirmation voice", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }

                            Switch(
                                checked = soundEnabled,
                                onCheckedChange = { viewModel.soundBoxSoundEnabled.value = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = VerifiedEmeraldGreen)
                            )
                        }

                        if (soundEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFF334155))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Announcement Language", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ElevatedFilterChip(
                                        selected = ttsLanguageHindi,
                                        onClick = { viewModel.soundBoxLanguageHindi.value = true },
                                        label = { Text("Hindi (हिंदी)") },
                                        colors = FilterChipDefaults.elevatedFilterChipColors(
                                            selectedContainerColor = CoolIndigoBlue,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                    ElevatedFilterChip(
                                        selected = !ttsLanguageHindi,
                                        onClick = { viewModel.soundBoxLanguageHindi.value = false },
                                        label = { Text("English") },
                                        colors = FilterChipDefaults.elevatedFilterChipColors(
                                            selectedContainerColor = CoolIndigoBlue,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Outlet transaction logs list
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Inward Credit History", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Live feed ✓", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }

            if (historyLog.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No active payment logs yet.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                }
            } else {
                items(historyLog) { tx ->
                    FintechTxLedgerRow(payment = tx)
                }
            }

            // Contact Help desk WhatsApp CTA
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            val uri = Uri.parse("https://api.whatsapp.com/send?phone=917987580921&text=Hello%20Raj%20Computer%20Support!%20My%20Merchant%20ID%20is%20${merchant?.phone}")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContactSupport, contentDescription = "Support logo chat", tint = Color.Green)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Raj Computer Support Desk: 7987580921", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQrScreen(viewModel: MainViewModel) {
    val payload by viewModel.activeUpiPayload.collectAsStateWithLifecycle()
    val amount by viewModel.activeTransactionAmount.collectAsStateWithLifecycle()
    val merchant by viewModel.loggedInMerchant.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan and Pay Customer Invoice", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.MERCHANT_DASHBOARD) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back icon button")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Invoice Amount",
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "₹${amount.toInt()}",
                    color = Color.Yellow,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = merchant?.shopName ?: "",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "UPI: ${merchant?.staticUpiId}",
                    color = Color(0xFF6366F1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // White backdrop board for high contrast QR readability
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .border(4.dp, Color(0xFF334155), RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (payload.isNotEmpty()) {
                        StylizedQrCode(payload = payload)
                    } else {
                        CircularProgressIndicator(color = Color.DarkGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time countdown indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = "Timer logo icon", tint = Color.Red, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simulated checkout active • 03:00 mins", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Simulation trigger buttons so testers can experience the Sound Box speech!
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.simulateCustomerPaymentCompletion() },
                    colors = ButtonDefaults.buttonColors(containerColor = VerifiedEmeraldGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("simulate_payment_success")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Validate payments success")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIMULATE PAYMENT SUCCESS (TRIGGERS SOUNDBOX)", fontWeight = FontWeight.Black)
                }

                TextButton(
                    onClick = { viewModel.navigateTo(Screen.MERCHANT_DASHBOARD) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel Transaction Invoice", color = Color(0xFF94A3B8))
                }
            }
        }
    }
}

@Composable
fun ScannerSimulatorScreen(viewModel: MainViewModel) {
    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // Simulated Camera viewfinder screen overlay lines
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateTo(Screen.ONBOARDING) }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel camera feed logo", tint = Color.White)
                    }
                    Text(
                        "SCAN GPay / PHONEPE STICKER",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                    Icon(Icons.Default.FlashOn, contentDescription = "Flash icon", tint = Color.White)
                }

                // Interactive target scan zone
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .border(3.dp, Color.Green, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Moving scanning bar indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color.Green)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Align GPay, PhonePe or Paytm QR Standee to secure your terminal routing details automatically.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            viewModel.simulateBarcodeScan()
                            viewModel.navigateTo(Screen.ONBOARDING)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("simulator_trigger_scan")
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan click logo", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SIMULATE QR SCAN SUCCESS", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
