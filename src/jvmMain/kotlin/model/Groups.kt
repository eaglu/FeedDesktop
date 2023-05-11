package data.dao.model

import model.Feed
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Groups : Table("rss_group") {
    val id = integer("id").autoIncrement()
    val name = text("name")

    override val primaryKey = PrimaryKey(id, name = "id")
}

data class Group(
    val id: Int = -1,
    val name: String,
    var feeds: List<Feed> = listOf()
)

fun Groups.toGroup(row: ResultRow) = Group(
    id = row[Groups.id],
    name = row[Groups.name],
)

