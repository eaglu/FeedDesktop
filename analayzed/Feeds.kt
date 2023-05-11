package data.dao.model

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Feeds : Table("rss_feed") {
    val id = integer("id").autoIncrement()
    val title = text("title")
    val link = text("link")
    val description = text("description")
    val groupId = integer("group_id").references(Groups.id).nullable()
    override val primaryKey = PrimaryKey(id, name = "id")
}

data class Feed(
    var id: Int,
    var title: String,
    var link: String,
    val description: String = "",
    var groupId: Int? = 1
)

fun Feeds.toFeed(row: ResultRow) = Feed(
    id = row[id],
    title = row[title],
    link = row[link],
    description = row[description],
    groupId = row[groupId]
)
