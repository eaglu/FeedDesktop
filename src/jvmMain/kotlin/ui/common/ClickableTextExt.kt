package ui.common

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import java.awt.Desktop
import java.net.URI

@Composable
fun ClickableTextExt(
    text: AnnotatedString,
    uriMap: Map<IntRange, URI> = mapOf(),
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
){
    val uriMapStatic = uriMap


    ClickableText(text,modifier,style, softWrap,overflow,maxLines, onTextLayout
    ){
        offset ->
        uriMapStatic.keys.forEach {
            if (it.contains(offset)){
                Desktop.getDesktop().browse(uriMap[it]?.let { it1 -> it1})
            }
        }
    }
}