package repositoryset

import data.dao.setting.SettingDaoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Setting

class SettingRepository {
    private val settingDaoImpl = SettingDaoImpl()

    suspend fun loadSetting() = withContext(Dispatchers.IO){
        settingDaoImpl.getSetting()
    }

    suspend fun updateSetting(setting: Setting) = withContext(Dispatchers.IO){
        settingDaoImpl.updateSetting(setting)
    }
}