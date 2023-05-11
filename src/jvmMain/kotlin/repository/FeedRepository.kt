package repository

import data.dao.feed.FeedDaoImpl
import model.Feed
import data.item.ItemDaoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.FeedWithItem
import util.Parser

class FeedRepository {
    private val feedDao = FeedDaoImpl()
    suspend fun loadFeeds() = withContext(Dispatchers.IO) {
        FeedDaoImpl().getAllFeeds()
    }
    suspend fun insertFeed(feedWithItem: FeedWithItem, groupId: Int?) = withContext(Dispatchers.IO) {
        if(groupId != null){
            feedWithItem.feed.groupId = groupId
        }else{
            feedWithItem.feed.groupId = 1
        }
        val feedId = FeedDaoImpl().insertFeed(feedWithItem.feed)
        ItemDaoImpl().insertItems(feedWithItem.items,feedId)
        return@withContext feedWithItem
    }


    fun getFeedByLink(link: String)=feedDao.getFeedByLink(link)

    suspend fun updateFeed(feed: Feed) = withContext(Dispatchers.IO) {
        FeedDaoImpl().updateFeed(feed)
    }

    suspend fun deleteFeed(feedId: Int) = withContext(Dispatchers.IO) {
        FeedDaoImpl().deleteFeed(feedId)
    }

    suspend fun loadFeed(link: String) = withContext(Dispatchers.IO){
        return@withContext Parser().parseFeed(link)
    }

}