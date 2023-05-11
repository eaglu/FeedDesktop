package model

import data.dao.model.Groups
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Feeds : Table("rss_feed") {
    val id = integer("id").autoIncrement()
    val title = text("title")
    val link = text("link")
    val groupId = integer("group_id").references(Groups.id, onDelete = ReferenceOption.CASCADE, fkName = "feed_group_id")
    override val primaryKey = PrimaryKey(id, name = "id")
}

data class Feed(
    var id: Int,
    var title: String,
    var link: String,
    var groupId: Int
)

fun Feeds.toFeed(row: ResultRow) = Feed(
    id = row[id],
    title = row[title],
    link = row[link],
    groupId = row[groupId]
)
