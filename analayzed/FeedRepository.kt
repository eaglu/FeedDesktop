package repository

import data.dao.feed.FeedDaoImpl
import data.dao.model.Feed
import data.dao.model.Group
import data.item.ItemDaoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.FeedWithItem
import model.GroupWithFeed
import util.Parser

class FeedRepository {
    val feedDao = FeedDaoImpl()

    suspend fun loadFeeds() = withContext(Dispatchers.IO) {
        FeedDaoImpl().getAllFeeds()
    }


    suspend fun insertFeed(feedWithItem: FeedWithItem,groupId: Int?) = withContext(Dispatchers.IO) {
        if(groupId != null){
            feedWithItem.feed.groupId = groupId
        }
        val feedId = FeedDaoImpl().insertFeed(feedWithItem.feed)
        ItemDaoImpl().insertItems(feedWithItem.items,feedId)
        return@withContext feedWithItem
    }

    suspend fun findFeedById(feedId: Int)=feedDao.getFeedById(feedId)

    suspend fun getFeedByLink(link: String)=feedDao.getFeedByLink(link)

    suspend fun updateFeed(feed: Feed,groupId: Int? = null) = withContext(Dispatchers.IO) {
        feed.groupId = groupId
        FeedDaoImpl().updateFeed(feed)
    }

    suspend fun deleteFeed(feedId: Int) = withContext(Dispatchers.IO) {
        FeedDaoImpl().deleteFeed(feedId)
    }

    suspend fun loadFeed(link: String) = withContext(Dispatchers.IO){
        return@withContext Parser().parseFeed(link)
    }


    fun loadFeedsByGroup(group: Group):GroupWithFeed {
        val feeds = feedDao.getFeedsByGroupId(group.id)
        return GroupWithFeed(group, feeds)
    }
}