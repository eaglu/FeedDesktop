package viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.Group
import data.dao.model.Feed
import data.dao.model.Feeds
import data.dao.model.Group
import kotlinx.coroutines.flow.MutableStateFlow
import model.FeedWithItem
import repository.FeedRepository

class FeedViewModel {
    private val feedRepository = FeedRepository()

    val selectedFeed = MutableStateFlow<Feed?>(null)

    val editFeed = MutableStateFlow<Feed?>(null)

    val newFeed = mutableStateOf<FeedWithItem?>(null)

    val feeds = mutableStateListOf<Feed>()

    lateinit var feedsMap: Map<Int,String>


    suspend fun loadFeeds() {
        feeds.clear()
        feeds.addAll(feedRepository.loadFeeds())
        feedsMap = feeds.associateBy(Feed::id, Feed::title)
    }

    suspend fun updateFeed(feed: Feed, groupId: Int?){
        feedRepository.updateFeed(feed, groupId)
        loadFeeds()
    }


    suspend fun insertFeed(groupId: Int?){
        if(newFeed.value != null) {
            feedRepository.insertFeed(newFeed.value!!, groupId)
            loadFeeds()
        }
    }

    suspend fun deleteFeed(feedId: Int){
        feedRepository.deleteFeed(feedId)
        loadFeeds()
    }

    suspend fun loadFeed(link: String):Boolean{
        newFeed.value = feedRepository.loadFeed(link)
        newFeed.value?.feed?.link = link
        loadFeeds()
        return newFeed.value == null
    }

    suspend fun getFeedByLink(link: String)=
        feedRepository.getFeedByLink(link)

    suspend fun checkExistence(link: String):Boolean{
        return feedRepository.getFeedByLink(link) != null
    }
}
