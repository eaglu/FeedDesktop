package repository

import data.dao.feed.FeedDaoImpl
import model.Feed
import data.dao.model.Item
import data.item.ItemDaoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import util.Parser
import java.time.LocalDateTime

class ItemRepository {
    val itemDaoImpl = ItemDaoImpl()

    suspend fun loadAllItems(feed: Feed?) = withContext(Dispatchers.IO) {
        if(feed == null) {
            itemDaoImpl.getAllItems()
        }else{
            itemDaoImpl.getAllItems(feed.id)
        }
    }

    suspend fun loadUnreadItems(feed: Feed?) = withContext(Dispatchers.IO) {
        if(feed == null) {
            itemDaoImpl.getUnreadItems()
        }else{
            itemDaoImpl.getUnreadItems(feed.id)
        }
    }


    suspend fun loadStaredItems(feed: Feed?) = withContext(Dispatchers.IO) {
        if(feed == null) {
            itemDaoImpl.getStaredItems()
        }else{
            itemDaoImpl.getStaredItems(feed.id)
        }
    }

    suspend fun refreshItems(feed: Feed?, days:Int) = withContext(Dispatchers.IO) {
        val feeds: List<Feed> = if (feed == null) {
            FeedDaoImpl().getAllFeeds()
        } else {
            listOf(feed)
        }

        val cutoff = if(days != Int.MAX_VALUE){
            LocalDateTime.now().minusDays(days.toLong())
        }else{
            LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0)
        }
        val parser = Parser()
        //并发获取新的订阅源内容
        val deferred = feeds.map { feed ->
            async {
                //过滤已存在于数据库的内容
                val items = parser.parseFeed(feed.link)?.items ?: emptyList()
                val existingItems = itemDaoImpl.getAllItems(feed.id).map { it.link }.toSet()
                val itemsToInsert = items.filter { it.link !in existingItems && it.pubDate > cutoff}
                itemDaoImpl.insertItems(itemsToInsert, feed.id)

            }
        }
        deferred.awaitAll()
    }

    suspend fun deleteItems(days: Int) = withContext(Dispatchers.IO) {
        if(days ==  Int.MAX_VALUE) return@withContext
        itemDaoImpl.deleteItems(days)
    }


    suspend fun loadItemsByFeed(feed: Feed) = withContext(Dispatchers.IO) {
        itemDaoImpl.getItemsByFeedId(feed.id)
    }

    fun markAllAsRead(feed: Feed?, date:String){
        val feedId = feed?.id
        val now = LocalDateTime.now()
        val day = when(date){
            "1" -> 1
            "3" -> 3
            "7" -> 7
            else -> null
        }
        if(day != null){
            val readDateTime = now.minusDays(day.toLong())
            itemDaoImpl.markAllAsRead(feedId, readDateTime)
        }else{
            itemDaoImpl.markAllAsRead(feedId, null)
        }
    }

    suspend fun markAsUnRead(item: Item) = withContext(Dispatchers.IO){
        item.isUnread = true
        itemDaoImpl.updateItem(item)
    }

    suspend fun markAsRead(item: Item) = withContext(Dispatchers.IO){
        item.isUnread = false
        itemDaoImpl.updateItem(item)
    }

    suspend fun markAsStared(item: Item) = withContext(Dispatchers.IO){
        item.isStared = true
        itemDaoImpl.updateItem(item)
    }

    suspend fun markAsUnStared(item: Item) = withContext(Dispatchers.IO){
        item.isStared = false
        itemDaoImpl.updateItem(item)
    }

    suspend fun searchItems(query: String, feedId: Int?) = itemDaoImpl.searchItems(query, feedId)
}