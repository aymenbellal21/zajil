package com.example.data.filter

import androidx.compose.ui.graphics.ColorMatrix
import com.example.data.model.PhotoFilter

object FilterEngine {

    val availableFilters = listOf(
        PhotoFilter(
            id = "normal",
            nameAr = "الأصلي",
            nameEn = "Original",
            brightness = 0f,
            contrast = 1f,
            saturation = 1f
        ),
        PhotoFilter(
            id = "ai_enhance",
            nameAr = "تحسين ذكي AI",
            nameEn = "AI Enhance",
            brightness = 0.08f,
            contrast = 1.25f,
            saturation = 1.35f
        ),
        PhotoFilter(
            id = "cyber_neon",
            nameAr = "نيون سايبر",
            nameEn = "Cyber Neon",
            brightness = 0.05f,
            contrast = 1.35f,
            saturation = 1.6f,
            tintColorHex = 0xFF802FFF
        ),
        PhotoFilter(
            id = "golden_hour",
            nameAr = "ساعة ذهبية",
            nameEn = "Golden Hour",
            brightness = 0.1f,
            contrast = 1.15f,
            saturation = 1.4f,
            tintColorHex = 0xFFFF9E00
        ),
        PhotoFilter(
            id = "noir",
            nameAr = "أبيض وأسود سينمائي",
            nameEn = "Noir B&W",
            brightness = 0.02f,
            contrast = 1.4f,
            saturation = 0f
        ),
        PhotoFilter(
            id = "vintage",
            nameAr = "ريترو كلاسيك",
            nameEn = "Vintage Film",
            brightness = 0.04f,
            contrast = 1.05f,
            saturation = 0.85f,
            tintColorHex = 0xFFE0C097
        ),
        PhotoFilter(
            id = "aurora",
            nameAr = "أورورا زمردي",
            nameEn = "Emerald Aurora",
            brightness = 0.05f,
            contrast = 1.2f,
            saturation = 1.3f,
            tintColorHex = 0xFF00F5D4
        ),
        PhotoFilter(
            id = "pastel",
            nameAr = "باستيل حالم",
            nameEn = "Pastel Dream",
            brightness = 0.15f,
            contrast = 0.95f,
            saturation = 1.1f
        )
    )

    fun createColorMatrix(
        filter: PhotoFilter,
        brightnessAdj: Float = 0f,
        contrastAdj: Float = 0f,
        saturationAdj: Float = 0f
    ): ColorMatrix {
        val matrix = ColorMatrix()

        val effSaturation = (filter.saturation + saturationAdj).coerceIn(0f, 2.5f)
        matrix.setToSaturation(effSaturation)

        val effContrast = (filter.contrast + contrastAdj).coerceIn(0.4f, 2.5f)
        val effBrightness = ((filter.brightness + brightnessAdj) * 255f).coerceIn(-100f, 100f)

        // Custom contrast and brightness transform
        val contrastMatrix = ColorMatrix(
            floatArrayOf(
                effContrast, 0f, 0f, 0f, effBrightness,
                0f, effContrast, 0f, 0f, effBrightness,
                0f, 0f, effContrast, 0f, effBrightness,
                0f, 0f, 0f, 1f, 0f
            )
        )

        val combined = ColorMatrix()
        // Combine saturation then contrast
        combined.setToSaturation(effSaturation)

        if (filter.tintColorHex != null) {
            val tint = filter.tintColorHex
            val r = ((tint shr 16) and 0xFF) / 255f
            val g = ((tint shr 8) and 0xFF) / 255f
            val b = (tint and 0xFF) / 255f

            val tintMatrix = ColorMatrix(
                floatArrayOf(
                    effContrast * (0.8f + r * 0.4f), 0f, 0f, 0f, effBrightness,
                    0f, effContrast * (0.8f + g * 0.4f), 0f, 0f, effBrightness,
                    0f, 0f, effContrast * (0.8f + b * 0.4f), 0f, effBrightness,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            return tintMatrix
        }

        return contrastMatrix
    }
}
