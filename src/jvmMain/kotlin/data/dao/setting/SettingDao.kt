package data.dao.setting

import model.Setting

interface SettingDao {

    fun getSetting():Setting?

    fun updateSetting(setting: Setting)
}