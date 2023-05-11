package ui.html

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun applyHtmlSpanStyle(heading:String):SpanStyle{
    return when(heading){
        "h1" -> SpanStyle(
            fontWeight = FontWeight.Light,
            fontSize = 32.sp,
            letterSpacing = (-1.5).sp
        )
        "h2" -> SpanStyle(
            fontWeight = FontWeight.Light,
            fontSize = 26.sp,
            letterSpacing = (-0.5).sp
        )
        "h3" -> SpanStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            letterSpacing = 0.sp
        )
        "h4" -> SpanStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            letterSpacing = 0.25.sp
        )
        "h5" -> SpanStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            letterSpacing = 0.sp
        )

        "h6" -> SpanStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 0.15.sp
        )

        "i" -> SpanStyle(
            fontStyle = FontStyle.Italic
        )
        else -> SpanStyle()
    }
}
