package viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import model.Feed
import kotlinx.coroutines.flow.MutableStateFlow
import model.FeedWithItem
import repository.FeedRepository

class FeedViewModel {
    private val feedRepository = FeedRepository()

    val selectedFeed = MutableStateFlow<Feed?>(null)

    val editFeed = MutableStateFlow<Feed?>(null)

    val newFeed = mutableStateOf<FeedWithItem?>(null)

    val feeds = mutableStateListOf<Feed>()

    val feedsMap = mutableStateOf<Map<Int,String>>(mapOf())


    suspend fun loadFeeds() {
        feeds.clear()
        feeds.addAll(feedRepository.loadFeeds())
        feedsMap.value = feeds.associateBy(Feed::id, Feed::title)
    }

    suspend fun updateFeed(feed: Feed){
        feedRepository.updateFeed(feed)
        editFeed.value = null
        loadFeeds()
    }

    suspend fun insertFeed(groupId: Int?):Int{
        val feedId = feedRepository.insertFeed(newFeed.value!!, groupId).feed.id
        loadFeeds()
        return feedId
    }

    suspend fun deleteFeed(feedId: Int){
        feedRepository.deleteFeed(feedId)
        loadFeeds()
        editFeed.value = null
    }

    suspend fun loadFeed(link: String):Boolean{
        newFeed.value = feedRepository.loadFeed(link)
        newFeed.value?.feed?.link = link
        loadFeeds()
        return newFeed.value == null
    }

    suspend fun checkExistence(link: String):Boolean{
        return feedRepository.getFeedByLink(link) != null
    }

}
