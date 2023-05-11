import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import repository.FeedRepository

class FeedRepoTest {

    @Test
    suspend fun InsertFeed(){
        val feedRepo = FeedRepository()
            feedRepo.insertFeed("http://www.people.com.cn/rss/world.xml")
    }
}