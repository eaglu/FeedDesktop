package util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import ui.common.ClickableTextExt
import ui.fontfamily.NotoSansFontFamily
import ui.html.applyHtmlSpanStyle
import java.net.URI


//移除HTML标签，显示纯文本
fun removeHtmlTags(htmlString: String): String {
    val document = Jsoup.parse(htmlString)
    val text = document.text()
    return if(text.length > 100) text.substring(0,100) else text
}

@Composable
fun ShowImg(resource: Resource<Painter>, caption: String? = null){
    Box(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KamelImage(
                resource = resource,
                contentDescription = null,
                modifier = Modifier.clip(shape = RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Fit,
                onLoading = {
                    Image(
                        painter = painterResource("icons/hourglass.svg"),
                        contentDescription = "Sample",
                        contentScale = ContentScale.FillBounds
                    )
                },
                crossfade = true,
                onFailure = {
                    Image(
                        painter = painterResource("/pic/image_alt.svg"),
                        contentDescription = "Sample",
                        contentScale = ContentScale.FillBounds
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            caption?.let {
                Text(caption, fontFamily = NotoSansFontFamily.SC, fontStyle = FontStyle.Normal)
            }
        }
    }
}


@Composable
fun HtmlToTextCom(nodes: List<Node>,elements:MutableList<@Composable () -> Unit> = mutableListOf(),skip:Boolean = true){

    //存储文本中的哪一段应该跳转至哪一个网址
    var uriMap = mutableMapOf<IntRange,URI>()
    var offset = 0

    var builder = AnnotatedString.Builder()
    builder.pushStyle(SpanStyle(fontFamily = NotoSansFontFamily.SC))

    nodes.forEach{ node ->
        when(node){
            is TextNode -> {
                builder.append(node.text())
                offset += node.text().length
            }
            is Element -> {
                when(node.tag().normalName().lowercase()) {
                    "strong" -> {
                        builder.pushTextWithSpanStyle(
                            node.text(), SpanStyle(fontWeight = FontWeight.Bold)
                        )
                        offset += node.text().length

                    }
                    "a" -> {
                        val href = node.attr("href")
                        uriMap[IntRange(offset+1,offset+node.text().length)] = URI(href)
                        builder.pushTextWithSpanStyle(
                            node.text(),SpanStyle(textDecoration = TextDecoration.Underline, color = Color.Blue)
                        )
                        offset += node.text().length
                        //node.childNodes()内包含node.text()
                        if(node.childNodes().any { it.toString() != node.text() }){
                            HtmlToTextCom(node.childNodes(), elements)
                        }
                    }
                    "h1" -> {
                        builder.pushParagraph(
                            node.text(),
                            spanStyle = applyHtmlSpanStyle("h1")
                        )
                        offset += node.text().length
                    }
                    "h2" -> {
                        builder.pushParagraph(
                            node.text(),
                            spanStyle = applyHtmlSpanStyle("h2")
                        )
                        offset += node.text().length
                    }
                    "h3" -> {
                        builder.pushParagraph(
                            node.text(),
                            spanStyle = applyHtmlSpanStyle("h3")
                        )
                        offset += node.text().length
                    }
                    "i","em" -> {
                        builder.pushTextWithSpanStyle(
                            node.text(),
                            style = applyHtmlSpanStyle("i")
                        )
                        offset += node.text().length
                    }
                    "s" -> {
                        builder.pushTextWithSpanStyle(
                            node.text(),
                            style = SpanStyle(textDecoration = TextDecoration.LineThrough)
                        )
                    }
                    "b" -> {
                        builder.pushTextWithSpanStyle(
                            node.text(),
                            style = SpanStyle(fontWeight = FontWeight.Bold)
                        )
                        offset += node.text().length
                    }
                    "br" -> {
                        builder.append("\n")
                    }
                    "img" ->{
                        var uri = node.attr("src")
                        if(uri.isEmpty()){
                            uri = node.attr("data-src")
                        }
                        val resource = lazyPainterResource(data = uri)
                        elements.add {
                            //图片组件
                           ShowImg(resource)
                        }
                    }
                    "figure" ->{
                        var uri:String?
                        lateinit var resource:Resource<Painter>
                        var caption: String? = null
                        node.childNodes().forEach {childNode ->
                            when(childNode){
                                is Element ->{
                                    when(childNode.tag().normalName().lowercase()){
                                        "img" -> {
                                            uri = childNode.attr("src")
                                            if(uri == null) {
                                                uri = childNode.attr("data-src")
                                            }
                                            resource = lazyPainterResource(data = uri!!)
                                        }
                                        "figcaption" -> {
                                            caption = childNode.text()
                                        }

                                    }
                                }
                            }
                        }

                        elements.add {
                            ShowImg(resource, caption)
                        }
                    }
                    "p","div","iframe","blockquote" -> {
                        if(builder.length != 0) {
                            val annotatedString = builder.toAnnotatedString()
                            elements.add {
                                ClickableTextExt(annotatedString,uriMap)
                            }
                            builder = AnnotatedString.Builder()
                            offset = 0
                        }
                        HtmlToTextCom(node.childNodes(),elements)
                    }
                    else -> {
                        builder.append(node.text())
                        offset += node.text().length
                    }
                }
            }
        }
    }

    elements.add {
        ClickableTextExt(builder.toAnnotatedString(),uriMap){
        }
    }

    if(!skip) {
        elements.forEach{ it() }
    }
}

fun AnnotatedString.Builder.pushTextWithSpanStyle(text: String,style: SpanStyle ){
    append(buildAnnotatedString {
        withStyle(style = style){
            append(text)
        }
    })
}

fun AnnotatedString.Builder.pushParagraph(text: String, paragraphStyle: ParagraphStyle = ParagraphStyle(TextAlign.Center), spanStyle: SpanStyle = SpanStyle()){
    append(buildAnnotatedString {
        withStyle(paragraphStyle){
            withStyle(spanStyle){
                append(text)
            }
        }
    })
}
