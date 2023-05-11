package viewmodel

import androidx.compose.runtime.mutableStateOf
import model.Setting
import repositoryset.SettingRepository

class SettingViewModel {
    private val settingRepository = SettingRepository()

    val setting = mutableStateOf<Setting?>(null)

    suspend fun loadSetting(){
        setting.value = settingRepository.loadSetting()
    }

    suspend fun updateSyncInLaunch(){
        setting.value!!.syncInLaunch = !setting.value!!.syncInLaunch
        settingRepository.updateSetting(setting.value!!)
    }


    suspend fun updateSaveTime(saveTime: Int){
        setting.value?.saveTime = saveTime
        settingRepository.updateSetting(setting.value!!)
    }

    suspend fun updatePageInLaunch(pageInLaunch: Int){
        setting.value?.pageInLaunch = pageInLaunch
        settingRepository.updateSetting(setting.value!!)
    }
}