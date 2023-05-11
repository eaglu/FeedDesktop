package model

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Settings: Table("setting"){
    val id = integer("id").autoIncrement()
    val syncInLaunch = integer("sync_in_launch").default(0)
    val saveTime = integer("save_time").default(14)
    val pageInLaunch = integer("page_in_launch").default(0)
    override val primaryKey = PrimaryKey(Feeds.id, name = "id")
}


data class Setting (
    var syncInLaunch:Boolean,
    var saveTime:Int,
    var pageInLaunch:Int
)

fun Settings.toSetting(row: ResultRow) = Setting(
    syncInLaunch = row[syncInLaunch] == 1,
    saveTime = row[saveTime],
    pageInLaunch = row[pageInLaunch]
)