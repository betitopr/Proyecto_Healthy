package com.example.proyectohealthy.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var isLoading by remember { mutableStateOf(true) }
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = backgroundColor.copy(alpha = 0.6f)  // Color sutilmente más oscuro/claro

    LaunchedEffect(Unit) {
        delay(1500)
        isLoading = false
        onSplashFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.food_image),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(
                "Healthy",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                "by Marte-sama",
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}