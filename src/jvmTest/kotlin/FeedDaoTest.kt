import data.dao.feed.FeedDaoImpl
import model.Feed
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
class FeedDaoTest {

    @Test
    fun testGetAllFeeds() {
        val feedDao = FeedDaoImpl()
        val feeds = feedDao.getAllFeeds()
        assertNotNull(feeds)
        assertEquals(22, feeds.size) // 假定一共有5个Feed
    }

    @Test
    fun testGetFeedById() {
        val feedDao = FeedDaoImpl()
        val feed = feedDao.getFeedById(1)
        assertNotNull(feed)
        assertEquals("exp", feed!!.title)
    }

    @Test
    fun testGetFeedById_NotExist() {
        val feedDao = FeedDaoImpl()
        val feed = feedDao.getFeedById(100) // 假定ID为20的Feed不存在
        assertNull(feed)
    }

    @Test
    fun testInsertFeed() {
        val feedDao = FeedDaoImpl()
        val feed = Feed(6, "Feed 6", "https://example.com/feed7","",1)
        val result = feedDao.insertFeed(feed)
        assertEquals(35, result) // 假定插入成功
    }

    @Test
    fun testUpdateFeed() {
        val feedDao = FeedDaoImpl()
        val feed = Feed(34, "Feed One", "https://example.com/feed34")
        val result = feedDao.updateFeed(feed)
        assertEquals(1, result) // 假定更新成功
        val updatedFeed = feedDao.getFeedById(34)
        assertNotNull(updatedFeed)
        assertEquals("Feed One", updatedFeed!!.title)
        assertEquals("https://example.com/feed34", updatedFeed.link)
    }

    @Test
    fun testDeleteFeed() {
        val feedDao = FeedDaoImpl()
        val result = feedDao.deleteFeed(34)
        assertEquals(1, result) // 假定删除成功
        val deletedFeed = feedDao.getFeedById(34)
        assertNull(deletedFeed)
    }

    @Test
    fun testGetFeedsByGroupId() {
        val feedDao = FeedDaoImpl()
        val groupId = 6
        val feeds = feedDao.getFeedsByGroupId(groupId)
        feeds.forEach {
            println(it.title) // 确认返回的每个Feed都属于该Group
        }
    }
}