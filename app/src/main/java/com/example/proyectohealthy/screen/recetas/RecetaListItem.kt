package com.example.proyectohealthy.screen.recetas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.RecetaApi
import com.example.proyectohealthy.data.local.entity.RecetaGuardada

sealed class RecetaListItem {
    data class ApiReceta(val receta: RecetaApi) : RecetaListItem()
    data class GuardadaReceta(val receta: RecetaGuardada) : RecetaListItem()
}

@Composable
fun RecetaListItem(
    item: RecetaListItem,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (item) {
                is RecetaListItem.ApiReceta -> {
                    ApiRecetaContent(
                        receta = item.receta,
                        onClick = onClick
                    )
                }
                is RecetaListItem.GuardadaReceta -> {
                    GuardadaRecetaContent(
                        receta = item.receta,
                        onClick = onClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ApiRecetaContent(
    receta: RecetaApi,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = receta.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.RestaurantMenu,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Porciones: ${receta.servings}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${receta.ingredients.size} ingredientes",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun GuardadaRecetaContent(
    receta: RecetaGuardada,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = receta.nombre,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalDining,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${receta.valoresNutricionales.calorias} kcal/porción",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = receta.porciones,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (receta.tiempoPreparacion.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = receta.tiempoPreparacion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (onDeleteClick != null) {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar receta",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}