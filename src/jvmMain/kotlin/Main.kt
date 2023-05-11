import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import repository.ItemRepository
import ui.FeedsView
import ui.ItemListView
import ui.ItemView
import viewmodel.FeedViewModel
import viewmodel.GroupFeedViewModel
import viewmodel.ItemViewModel
import viewmodel.SettingViewModel
import kotlin.system.exitProcess


val settingViewModel = SettingViewModel()
val groupViewModel = GroupFeedViewModel()
val feedViewModel = FeedViewModel()
val itemViewModel = ItemViewModel()
@Composable
@Preview
fun App() {
    MaterialTheme{
        Row(modifier = Modifier.fillMaxSize())
       {
           Column(modifier = Modifier.weight(400f / 1920f).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
           )
            {
               FeedsView()
           }
           Column(modifier = Modifier.weight(520f / 1920f).fillMaxSize().border(1.dp, Color.LightGray)) {
               ItemListView() // 控件2
            }
           Column(modifier = Modifier.weight(1080f / 1920f)) {
               if(itemViewModel.selectedItem.value == null){
                    Column(
                       horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.Center,
                      modifier = Modifier.fillMaxSize()
                  ) {
                        Text("Nothing Selected", style = MaterialTheme.typography.h2)
                    }
               }else {
                   ItemView()
               }
           }
       }
    }

}

suspend fun loadData() = withContext(Dispatchers.IO){
    settingViewModel.loadSetting()
    groupViewModel.loadGroup()
    feedViewModel.loadFeeds()
    settingViewModel.loadSetting()
    itemViewModel.updateItems(null,settingViewModel.setting.value!!.pageInLaunch)
}


fun main() = application {
    runBlocking {
        loadData()
    }

    Window(
        onCloseRequest = {
                        //应用退出时删除需要删除的项目，
                         runBlocking {
                             ItemRepository().deleteItems(settingViewModel.setting.value!!.saveTime)
                             exitProcess(0)
                         }
        },
        title = "Feed",
        state = WindowState(width = 1920.dp, height = 1080.dp)
    ) {
        App()
    }
}

