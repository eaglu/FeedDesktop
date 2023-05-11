package data.item

import model.Feeds
import data.dao.model.Item
import data.dao.model.Items
import data.dao.model.Items.author
import data.dao.model.Items.description
import data.dao.model.Items.feedId
import data.dao.model.Items.isStared
import data.dao.model.Items.isUnread
import data.dao.model.Items.link
import data.dao.model.Items.pubDate
import data.dao.model.Items.title
import data.dao.model.toItem
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import util.DBUtils
import java.time.LocalDateTime

class ItemDaoImpl : ItemDao {

    override suspend fun getAllItems(feedId: Int?) = transaction(DBUtils.db){
        if(feedId == null){
            Items.selectAll().orderBy(pubDate to SortOrder.DESC).map { Items.toItem(it) }
        }else{
            Items.innerJoin(Feeds).select { Feeds.id.eq(feedId) }.map { Items.toItem(it) }
        }
    }

    override suspend fun getItemById(id: Int) = transaction(DBUtils.db) {
        Items.select { Items.id eq id }
            .map { Items.toItem(it) }
            .singleOrNull()
    }

    override suspend fun insertItem(item: Item) = transaction(DBUtils.db){
            Items.insert {
                it[title] = item.title
                it[link] = item.link
                it[description] = item.description
                it[author] = item.author
                it[pubDate] = item.pubDate
                it[feedId] = item.feedId
                it[isUnread] = if (item.isUnread) 1 else 0
                it[isStared] = if (item.isStared) 1 else 0
            } get Items.id
    }

    override suspend fun updateItem(item: Item) = transaction(DBUtils.db){
            Items.update({ Items.id eq item.id }) {
                it[title] = item.title
                it[link] = item.link
                it[description] = item.description
                it[author] = item.author
                it[pubDate] = item.pubDate
                it[feedId] = item.feedId
                it[isUnread] = if (item.isUnread) 1 else 0
                it[isStared] = if (item.isStared) 1 else 0
            }
        }

    override suspend fun deleteItems(days: Int) = transaction(DBUtils.db) {
        val cutoff = LocalDateTime.now().minusDays(days.toLong())
            Items.deleteWhere { (pubDate less cutoff) and (isStared eq 0) }
        }

    override fun deleteItemsByFeedId(feedId: Int) = transaction(DBUtils.db) {
        Items.deleteWhere { Items.feedId eq feedId }
    }

    override suspend fun getItemsByFeedId(feedId: Int) = transaction(DBUtils.db) {
        Items.innerJoin(Feeds).select { Feeds.id.eq(feedId) }.orderBy(pubDate to SortOrder.DESC).map { Items.toItem(it) }
    }

    override fun searchItems(query: String, feedId: Int?): List<Item> {
        return transaction(DBUtils.db) {
            val searchQuery = "%$query%"
            val titleQuery = Items.title.like(searchQuery)
            val contentQuery = Items.description.like(searchQuery)

            feedId?.let { id ->
                val itemsWithFeedQuery =
                    (Items innerJoin Feeds).select {
                        Feeds.id eq id and (titleQuery or contentQuery)
                    }.orderBy(pubDate to SortOrder.DESC)

                itemsWithFeedQuery.map { Items.toItem(it) }
            } ?: let {
                val itemsWithoutFeedQuery =
                    Items.select { titleQuery.or(contentQuery) }
                        .orderBy(pubDate to SortOrder.DESC)

                itemsWithoutFeedQuery.map { Items.toItem(it) }
            }
        }
    }

    override suspend fun getUnreadItems(feedId: Int?) = transaction(DBUtils.db) {
        if(feedId == null){
            Items.select { Items.isUnread eq 1 }.orderBy(pubDate to SortOrder.DESC).map { Items.toItem(it) }
        }else{
            Items.innerJoin(Feeds).select { Feeds.id.eq(feedId) }.map { Items.toItem(it) }.filter { it.isUnread }
        }
    }

    override suspend fun getStaredItems(feedId: Int?) = transaction(DBUtils.db) {
        if(feedId == null){
            Items.select { Items.isStared eq 1 }.orderBy(pubDate to SortOrder.DESC).map { Items.toItem(it) }
        }else{
            Items.innerJoin(Feeds).select { Feeds.id.eq(feedId) }.map { Items.toItem(it) }.filter { it.isStared }
        }
    }

    override fun getAllCount(id: Int?) = transaction(DBUtils.db) {
        TransactionManager.currentOrNull()?.exec(
            "SELECT COUNT(*) FROM rss_item " + if(id == null) ";" else " id == $id;"
        ){ rs ->
            if(rs.next()) {
                rs.getInt(1)
            } else {
                0
            }
        } ?: 0
    }

    override fun markAllAsRead(feedId: Int?,dateTime: LocalDateTime? ) {
        val dateCondition = if(dateTime == null){
            Op.TRUE
        }else{
            Items.pubDate lessEq dateTime
        }
        val feedIdCondition = if(feedId == null){
            Op.TRUE
        }else{
            Items.feedId eq feedId
        }
        transaction(DBUtils.db) {

            Items.update({ feedIdCondition and dateCondition }) {
                it[isUnread] = 0
            }
        }
    }

    override fun markAsRead(itemId: Int)  = transaction(DBUtils.db){
        Items.update({ Items.id eq itemId}) {
            it[isUnread] = 0
        }
    }

    override fun markAsUnRead(itemId: Int)  = transaction(DBUtils.db){
        Items.update({ Items.id eq itemId}) {
            it[isUnread] = 1
        }
    }

    override fun markAsStared(itemId: Int)  = transaction(DBUtils.db){
        Items.update({ Items.id eq itemId}) {
            it[isStared] = 1
        }
    }

    override fun markAsUnStared(itemId: Int)  = transaction(DBUtils.db){
        Items.update({ Items.id eq itemId}) {
            it[isStared] = 0
        }
    }

    override fun getUnreadCount(id: Int?) = transaction(DBUtils.db) {
        TransactionManager.currentOrNull()?.exec(
            "SELECT COUNT(*) FROM rss_item where is_unread == " + if(id == null) ";" else " id == $id;"
        ){ rs ->
            if(rs.next()) {
                rs.getInt(1)
            } else {
                0
            }
        } ?: 0
    }

    override fun getStarCount(id: Int?) = transaction(DBUtils.db) {
        TransactionManager.currentOrNull()?.exec(
            "SELECT COUNT(*) FROM rss_item where is_stared == " + if(id == null) ";" else " id == $id;"
        ){ rs ->
            if(rs.next()) {
                rs.getInt(1)
            } else {
                0
            }
        } ?: 0
    }

    override fun insertItems(items: List<Item>,fId: Int) = transaction(DBUtils.db) {
        Items.batchInsert(items) { item ->
            this[title] = item.title
            this[link] = item.link
            this[description] = item.description
            this[author] = item.author
            this[pubDate] = item.pubDate
            this[feedId] = fId
            this[isUnread] = if (item.isUnread) 1 else 0
            this[isStared] = if (item.isStared) 1 else 0
        }

        items.size
    }
}
