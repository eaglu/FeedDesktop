package util

import model.Feed
import data.dao.model.Group
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class OPMLUtil {
    fun parse(file: File):Map<String, MutableList<Feed>> {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(file)

        document.documentElement.normalize()

        val feedMap: MutableMap<String, MutableList<Feed>> = mutableMapOf()
        //获取所有outline元素
        val outlines = document.getElementsByTagName("outline")
        for (i in 0 until outlines.length) {
            val outline = outlines.item(i) as Element
            //处理订阅源
            if (outline.hasAttribute("xmlUrl")) {
                val title = outline.getAttribute("title")
                val xmlUrl = outline.getAttribute("xmlUrl")
                //获取父结点，判断是否未独立订阅源
                val groupElement = outline.parentNode as? Element
                val groupName = if (groupElement != null && groupElement.hasAttribute("title")) {
                    groupElement.getAttribute("title")
                } else {
                    "默认"
                }
                //建立Feed对象，存储订阅源，添加至feedMap中
                val feeds = feedMap.getOrDefault(groupName, mutableListOf())
                feeds.add(Feed(-1,title,xmlUrl, -1))
                feedMap[groupName] = feeds
            }else {
                //处理分组
                val title = outline.getAttribute("title")
                if (!outline.hasChildNodes()) {
                    feedMap[title] = mutableListOf()
                }
            }
        }

        return feedMap
    }

     fun export(groups: List<Group>):String {
        // 查询数据库中的所有分组和订阅源信息

        // 生成 OPML 格式的字符串
        val docBuilderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = docBuilderFactory.newDocumentBuilder()
        val doc = docBuilder.newDocument()

        val opml = doc.createElement("opml")
        opml.setAttribute("version", "2.0")
        doc.appendChild(opml)

        val head = doc.createElement("head")
        opml.appendChild(head)

        val title = doc.createElement("title")
        title.textContent = "RSS Feeds"
        head.appendChild(title)

        val body = doc.createElement("body")
        opml.appendChild(body)

        groups.forEach { group ->
            // 添加分组
            val groupNode = doc.createElement("outline")
            groupNode.setAttribute("text", group.name)
            groupNode.setAttribute("title", group.name)
            body.appendChild(groupNode)

            // 添加分组下的订阅源
            group.feeds.forEach { feed ->
                val feedNode = doc.createElement("outline")
                feedNode.setAttribute("text", feed.title)
                feedNode.setAttribute("title", feed.title)
                feedNode.setAttribute("type", "rss")
                feedNode.setAttribute("xmlUrl", feed.link)
                feedNode.setAttribute("htmlUrl", feed.link)
                groupNode.appendChild(feedNode)
            }
        }

        return docToString(doc)
    }

    // 将 Document 对象转化为字符串
    fun docToString(doc: Document): String {
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        val writer = StringWriter()
        transformer.transform(DOMSource(doc), StreamResult(writer))
        return writer.toString()
    }

}
