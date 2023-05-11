package util

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import model.Feed
import data.dao.model.Item
import model.FeedWithItem
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.ProxySelector
import java.net.URI
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class Parser {

    //解析为Rome提供的SyncFeed类
    private fun parseSyncFeed(rssUrl: String): SyndFeed? {

        val proxySelector = ProxySelector.getDefault()
        val proxy = proxySelector.select(URI("https://www.google.com"))
        println(proxy)
        //超时时间为10秒
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()

        try{
            val request = Request.Builder()
            .url(rssUrl)
            .build()

            val response = client.newCall(request).execute()
            val input = SyndFeedInput()

            return input.build(XmlReader(response.body?.byteStream()))
        }catch (e: Exception){
            println(e)
        }
        return null
    }

    //若返回的为null，则表示用户需要检查其输入的链接或网络连接
    fun parseFeed(link: String): FeedWithItem?{
        val syncFeed = parseSyncFeed(link) ?: return null
        val feedTitle = syncFeed.title
        val feedLink = syncFeed.link


        val feed = Feed(-1,feedTitle,link, 1)
        var items: MutableList<Item> = mutableListOf()
        val entries = syncFeed.entries
        entries.forEach{ entry ->
            val pubDate = if(entry.publishedDate!= null){
                entry.publishedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            }else{
                LocalDateTime.now()
            }
            val rssItem = Item(
                id = -1,
                title = entry.title,
                link = entry.link,
                description = entry.description.value,
                author = entry.author,
                pubDate = pubDate,
                feedId = 0,
                isUnread = true,
                isStared = false,
            )
            items.add(rssItem)
        }
        return FeedWithItem(feed, items)
    }
}
