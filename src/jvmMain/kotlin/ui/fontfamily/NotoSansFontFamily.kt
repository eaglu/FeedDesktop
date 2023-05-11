package ui.fontfamily

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

object NotoSansFontFamily {
    val SC: FontFamily = FontFamily(listOf(
        Font(
            resource = "fonts/Noto Sans SC/NotoSansSC-Regular.otf",
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        ),
        Font(
            resource = "fonts/Noto Sans SC/NotoSansSC-Black.otf",
            weight = FontWeight.Black,
            style = FontStyle.Normal
        ),
        Font(
            resource = "fonts/Noto Sans SC/NotoSansSC-Bold.otf",
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        ),
        Font(
            resource = "fonts/Noto Sans SC/NotoSansSC-Light.otf",
            weight = FontWeight.Light,
            style = FontStyle.Normal
        ),
        Font(
            resource = "fonts/Noto Sans SC/NotoSansSC-Medium.otf",
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        ),
        Font(
            resource = "fonts/Noto Sans SC/NotoSansSC-Thin.otf",
            weight = FontWeight.Thin,
            style = FontStyle.Normal
        ),
    ))
}