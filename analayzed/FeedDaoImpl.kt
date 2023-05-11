package data.dao.feed

import data.dao.model.Feed
import data.dao.model.Feeds
import data.dao.model.Groups
import data.dao.model.toFeed
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import util.DBUtils

class FeedDaoImpl : FeedDao {
    // 使用 Koin 注入 Database 对象
    private val db by lazy {
        Database.connect(
            "jdbc:sqlite:rss.db",
            "org.sqlite.JDBC",
        )
    }

    override fun getAllFeeds(): List<Feed> {
        return transaction(DBUtils.db) {
            Feeds.selectAll().map { Feeds.toFeed(it) }
        }
    }

    override fun getFeedById(id: Int): Feed? {
        return transaction(DBUtils.db) {
            Feeds.select { Feeds.id eq id }
                .map { Feeds.toFeed(it) }
                .singleOrNull()
        }
    }


    override fun insertFeed(feed: Feed) = transaction(DBUtils.db){
        Feeds.insert {
            it[title] = feed.title
            it[link] = feed.link
            it[description] = feed.description
            it[groupId] = feed.groupId
        } get Feeds.id
    }


    override fun updateFeed(feed: Feed) = transaction(DBUtils.db) {
            Feeds.update({ Feeds.id eq feed.id }) {
                it[title] = feed.title
                it[link] = feed.link
                it[description] = feed.description
                it[groupId] = feed.groupId
            }
        }

    override fun deleteFeed(id: Int) =  transaction(DBUtils.db) {
            Feeds.deleteWhere { Feeds.id eq id }
        }

    override fun getFeedsByGroupId(groupId: Int) = transaction(DBUtils.db) {
        Feeds.innerJoin(Groups)
            .select { Groups.id.eq(groupId) }
            .map { Feeds.toFeed(it) }
    }


    override fun insertFeeds(feeds: List<Feed>,groupId: Int) = transaction(DBUtils.db) {
        Feeds.batchInsert(feeds){ feed ->
            this[Feeds.title] = feed.title
            this[Feeds.link] = feed.link
            this[Feeds.description] = feed.description
            this[Feeds.groupId] = groupId
        }
        feeds.size
    }

    override fun getFeedByLink(link: String) = transaction(DBUtils.db) {
            Feeds.select{Feeds.link eq link}.map { Feeds.toFeed(it) }.singleOrNull()
    }
}

