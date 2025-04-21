package com.example.resumeai

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.google.firebase.auth.FirebaseAuth

class WelcomeScreen : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is logged in, navigate to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Close the WelcomeScreen
        } else {
            // User is not logged in, show the welcome screen
            setContent {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    var showWelcomeScreen by remember { mutableStateOf(false) }

    // Show splash for 2 seconds before navigating to welcome screen
    LaunchedEffect(Unit) {
        delay(2000)
        showWelcomeScreen = true
    }

    if (showWelcomeScreen) {
        WelcomeScreenUI()
    } else {
        SplashScreen()
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFA070)), // Adjust splash background color
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.app_logo), // Replace with actual logo
                contentDescription = "Resume AI Logo",
                modifier = Modifier.size(350.dp), // Increased logo size
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "AI POWERED RESUME\nAPPLICATION",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF752101), // Adjusted text color
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WelcomeScreenUI() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top-Right "About Me" Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            TextButton(onClick = {
                val intent = Intent(context, AboutMeActivity::class.java)
                context.startActivity(intent)
            }) {
                Text(
                    text = "About Me",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Upper Half - Logo Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Resume AI Logo",
                modifier = Modifier.size(350.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Lower Half - Welcome Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFE1976D), shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "WELCOME",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "World’s most powerful AI Resume Builder App. Resume AI will help you build a professional resume or cover letter directly from your phone.",
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(context, SignInActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(text = "Sign In", fontSize = 18.sp, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(context, SignUpActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = "Sign Up", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}