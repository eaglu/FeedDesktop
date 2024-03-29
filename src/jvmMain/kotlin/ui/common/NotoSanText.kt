package ui.common

import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import ui.fontfamily.NotoSansFontFamily

@Composable
fun NotoSansText(text: String,
                 modifier: Modifier = Modifier,
                 color: Color = Color.Unspecified,
                 fontSize: TextUnit = TextUnit.Unspecified,
                 fontStyle: FontStyle? = FontStyle.Normal,
                 fontWeight: FontWeight? = FontWeight.Normal,
                 fontFamily: FontFamily? = NotoSansFontFamily.SC,
                 letterSpacing: TextUnit = TextUnit.Unspecified,
                 textDecoration: TextDecoration? = null,
                 textAlign: TextAlign? = null,
                 lineHeight: TextUnit = TextUnit.Unspecified,
                 overflow: TextOverflow = TextOverflow.Clip,
                 softWrap: Boolean = true,
                 maxLines: Int = Int.MAX_VALUE,
                 onTextLayout: (TextLayoutResult) -> Unit = {},
                 style: TextStyle = LocalTextStyle.current){

}

//fun with