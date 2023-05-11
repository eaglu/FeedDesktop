package data.dao.feed

import model.Feed

interface FeedDao {
    // 获取所有 RSS 源
    fun getAllFeeds(): List<Feed>

    // 通过 ID 获取 RSS 源
    fun getFeedById(id: Int): Feed?

    // 添加一个 RSS 源
    fun insertFeed(feed: Feed):Int

    // 更新一个 RSS 源
    fun updateFeed(feed: Feed):Int

    // 删除一个 RSS 源
    fun deleteFeed(id: Int):Int

    fun getFeedsByGroupId(groupId: Int): List<Feed>

    fun insertFeeds(feeds: List<Feed>, groupId: Int): Int

    fun getFeedByLink(link: String): Feed?
}
