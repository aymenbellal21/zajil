package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R

@Composable
fun MediaDisplay(
    mediaKey: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    colorMatrix: ColorMatrix? = null
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (mediaKey.startsWith("content://") || mediaKey.startsWith("file://") || mediaKey.startsWith("http://") || mediaKey.startsWith("https://")) {
            AsyncImage(
                model = mediaKey,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                colorFilter = colorMatrix?.let { ColorFilter.colorMatrix(it) }
            )
        } else {
            val resId = when (mediaKey) {
                "res_cyber_city" -> R.drawable.post_cyber_city_1791806521527
                "res_avatar", "avatar_founders" -> R.drawable.avatar_cyber_gamer_1791806539609
                "alzajel_logo", "app_logo" -> R.drawable.alzajel_logo_1791823760889
                "avatar_sara" -> R.drawable.avatar_cyber_gamer_1791806539609
                "avatar_omar" -> R.drawable.post_cyber_city_1791806521527
                "avatar_nouf" -> R.drawable.avatar_cyber_gamer_1791806539609
                else -> R.drawable.post_cyber_city_1791806521527
            }
            Image(
                painter = painterResource(id = resId),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                colorFilter = colorMatrix?.let { ColorFilter.colorMatrix(it) }
            )
        }
    }
}
