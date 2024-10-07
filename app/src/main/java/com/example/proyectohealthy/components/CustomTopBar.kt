package com.example.proyectohealthy.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectohealthy.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    navController: NavController,
    title: String,
    //onCalendarClick: () -> Unit,
    userPhotoUrl: String?
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate("profile") }) {
                when {
                    userPhotoUrl != null && userPhotoUrl.startsWith("http") -> {
                        AsyncImage(
                            model = userPhotoUrl,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.gojowin),
                            contentDescription = "Foto de perfil predeterminada",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }
        }
    )
}