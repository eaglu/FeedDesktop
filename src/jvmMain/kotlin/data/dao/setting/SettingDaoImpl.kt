package data.dao.setting

import model.Setting
import model.Settings
import model.toSetting
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import util.DBUtils

class SettingDaoImpl: SettingDao {
    override fun getSetting() = transaction(DBUtils.db) {
        Settings.select{Settings.id eq 1}.map { Settings.toSetting(it) }.singleOrNull()
    }

    override fun updateSetting(setting: Setting) {
        transaction(DBUtils.db) {
            Settings.update({Settings.id eq 1}){
                it[syncInLaunch] = if(setting.syncInLaunch) 1 else 0
                it[saveTime] = setting.saveTime
                it[pageInLaunch] = setting.pageInLaunch
            }
        }
    }
}