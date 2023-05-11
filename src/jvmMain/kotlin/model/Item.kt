package data.dao.model

import model.Feeds
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Items : Table("rss_item2") {
    val id = integer("id").autoIncrement()
    val title = text("title")
    val link = text("link")
    val description = text("description")
    val author = text("author")
    val pubDate = datetime("pub_date")
    val feedId = integer("feed_id").references(Feeds.id, onDelete = ReferenceOption.CASCADE, fkName = "item_feeds_id")
    val isUnread = integer("is_unread").default(1)
    val isStared = integer("is_stared").default(0)

    override val primaryKey = PrimaryKey(id, name = "id")
}

data class Item(
    val id: Int,
    val title: String,
    val link: String,
    val description: String,
    val author: String,
    val pubDate: LocalDateTime,
    var feedId: Int,
    var isUnread: Boolean = true,
    var isStared: Boolean = false
)

fun Items.toItem(row: ResultRow) = Item(
        id = row[id],
        title = row[title],
        link = row[link],
        description = row[description],
        author = row[author],
        pubDate = row[pubDate],
        feedId = row[feedId],
        isUnread = row[isUnread] != 0,
        isStared = row[isStared] != 0,
    )