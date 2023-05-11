package util

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import data.dao.model.Groups
import data.dao.model.Items
import model.Feeds
import model.Settings
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DBUtils {
    val ds by lazy {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:sqlite:rss.db"
        config.driverClassName = "org.sqlite.JDBC"
        HikariDataSource(config)
    }
    val db by lazy { Database.connect(ds)}

    init {
        initTable()
    }

    fun initTable(){
        transaction(db) {
            SchemaUtils.create(Groups)
            SchemaUtils.create(Feeds)
            SchemaUtils.create(Items)
            SchemaUtils.create(Settings)
        }
    }
}