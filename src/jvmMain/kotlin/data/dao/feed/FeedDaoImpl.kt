package data.dao.feed

import data.dao.model.*
import data.item.ItemDaoImpl
import model.Feed
import model.Feeds
import model.toFeed
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import util.DBUtils

class FeedDaoImpl : FeedDao {
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
            it[groupId] = feed.groupId
        } get Feeds.id
    }


    override fun updateFeed(feed: Feed) = transaction(DBUtils.db) {
            Feeds.update({ Feeds.id eq feed.id }) {
                it[title] = feed.title
                it[link] = feed.link
                it[groupId] = feed.groupId
            }
        }

    override fun deleteFeed(id: Int) =  transaction(DBUtils.db) {
            Feeds.deleteWhere { Feeds.id eq id }
            ItemDaoImpl().deleteItemsByFeedId(id)
        }


    override fun getFeedsByGroupId(groupId: Int) = transaction(DBUtils.db) {
        Feeds.innerJoin(Groups)
            .select { Groups.id.eq(groupId) }
            .map { Feeds.toFeed(it) }
    }


    override fun insertFeeds(feeds: List<Feed>, groupId: Int) = transaction(DBUtils.db) {
        Feeds.batchInsert(feeds){ feed ->
            this[Feeds.title] = feed.title
            this[Feeds.link] = feed.link
            this[Feeds.groupId] = groupId
        }
        feeds.size
    }

    override fun getFeedByLink(link: String) = transaction(DBUtils.db) {
            Feeds.select{ Feeds.link eq link}.map { Feeds.toFeed(it) }.singleOrNull()
    }
}

