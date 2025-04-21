package com.example.resumeai

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AboutMeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AboutMeScreen()
        }
    }
}

@Composable
fun AboutMeScreen() {
    val activity = LocalContext.current as? Activity

    Column(modifier = Modifier.fillMaxSize()) {
        // Orange Background (Top Section with Logo)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(451.dp)
                .background(Color(0xFFFFA87A))
                .padding(24.dp)
        ) {
            Column {
                // Back Icon Button
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { activity?.finish() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Logo
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Resume AI Logo",
                        modifier = Modifier.size(250.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // White Rounded Container (Overlapping Section)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(536.dp)
                .offset(y = (-50).dp)
                .background(Color.White, shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .padding(24.dp)
        ) {
            Column {
                // Title
                Text(
                    text = "About Me",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "ResumeAI is a powerful AI-driven resume builder app. It helps users create professional resumes and cover letters directly from their phones.",
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                // Back Button
                Button(
                    onClick = { activity?.finish() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text("Back", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}