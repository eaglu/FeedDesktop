package ui

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import data.dao.model.Group
import feedViewModel
import groupViewModel
import itemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import settingViewModel
import ui.common.FeedButton
import ui.dialog.*
import ui.fontfamily.NotoSansFontFamily
import util.OPMLUtil
import viewmodel.OPMLViewModel
import viewmodel.SettingViewModel
import java.io.File
import javax.swing.JFileChooser


val status = itemViewModel.status

@Composable
fun FeedsView(){

    Scaffold(
        topBar = {
            GroupTopBar()
        },
        backgroundColor = Color(189,189,189)
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()){
            Spacer(Modifier.size(16.dp))

            statusButton()

            allButton()

            if(showEditFeedDialog.value){
                EditFeedDialog()
            }
            LazyColumn {
                items(groupViewModel.groups) {group ->
                    feedCard(group)
                }
            }

        }


    }
}

@Composable
fun GroupTopBar(){
    var expanded by remember { mutableStateOf(false) }
    val scope = CoroutineScope(Dispatchers.IO)
    TopAppBar(
        backgroundColor = Color(0xf7f6f4),
        title = { Text("Feed", fontFamily = NotoSansFontFamily.SC) },
        elevation = 0.dp,
        actions = {

            if(showSettingDialog.value) SettingDialog()

            IconButton(
                onClick = {
                    showSettingDialog.value = true
                }
            ){
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = "Switch",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
            IconButton(
                onClick = {
                    expanded = true
                }
            ){
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Switch",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }

            val scope = rememberCoroutineScope()

            DropdownMenu(expanded = expanded, onDismissRequest = {
                expanded = false
            }) {
                DropdownMenuItem(onClick = {
                    scope.launch(Dispatchers.IO) {
                        importOpml()
                    }
                }) {
                    Text("导入OPML", fontFamily = NotoSansFontFamily.SC)
                }
                DropdownMenuItem(onClick = {
                    scope.launch(Dispatchers.IO) {
                        val opmlHandler = OPMLUtil()
                        val groups = OPMLViewModel().export()
                        val opmlString = opmlHandler.export(groups)
                        FileChooserDialog(opmlString)
                    }
                }) {
                    Text("导出OPML", fontFamily = NotoSansFontFamily.SC)
                }
                DropdownMenuItem(onClick = { showAddFeedDialog.value = true }) {
                    Text("添加订阅", fontFamily = NotoSansFontFamily.SC)
                }
            }
        }
    )

    if(showAddFeedDialog.value){
        AddFeedDialog()
    }
}

@Composable
fun statusButton() {
    val statusOptions = listOf("未读", "全部", "已加星标")
    val icon = when(status.value) {
        0 -> Icons.Outlined.CheckCircle
        1 -> Icons.Outlined.List
        2 -> Icons.Outlined.Star
        else -> Icons.Outlined.CheckCircle
    }


    Button(
        modifier = Modifier.size(width = 240.dp, height = 80.dp).padding(10.dp).clip(
            RoundedCornerShape(32.dp)
        ),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(213, 212, 210), contentColor = Color(63, 62, 60)),
        onClick = {
            status.value = (status.value + 1) % statusOptions.size
            runBlocking {
                itemViewModel.updateItems(feedViewModel.selectedFeed.value,status.value)
            }
        }
    ) {
        Icon(
            icon,
            contentDescription = "Switch",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.padding(8.dp))
        Text(statusOptions[status.value], modifier = Modifier.width(80.dp), textAlign = TextAlign.Center, maxLines = 1,fontFamily = NotoSansFontFamily.SC)
        Spacer(Modifier.padding(8.dp))
        Text(itemViewModel.items.size.toString(), fontFamily = NotoSansFontFamily.SC)
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))

        Icon(
            Icons.Outlined.ArrowForward,
            contentDescription = "Switch",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
    }
}

@Composable
fun allButton() {

    Button(
        modifier = Modifier.size(width = 240.dp, height = 80.dp).padding(10.dp).clip(
            RoundedCornerShape(32.dp)
        ),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(213, 212, 210), contentColor = Color(63, 62, 60)),
        onClick = {

            runBlocking {
                itemViewModel.updateItems(null,status.value)
            }
        }
    ) {
        Icon(
            painter = painterResource("icons/list_alt.svg"),
            contentDescription = "Switch",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.padding(8.dp))
        Text("所有订阅", modifier = Modifier.width(80.dp), textAlign = TextAlign.Center, maxLines = 1, fontFamily = NotoSansFontFamily.SC)
        Spacer(Modifier.padding(8.dp))

        Spacer(Modifier.size(ButtonDefaults.IconSpacing))

        Icon(
            Icons.Outlined.ArrowForward,
            contentDescription = "Switch",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
    }
}

@Composable
fun feedCard(group: Group){
    val feeds = group.feeds
    var isExpanded by remember { mutableStateOf(false) }

    if(showEditGroupDialog.value && groupViewModel.selectedGroup.value?.name == group.name) {
        EditGroupDialog(group)
    }

    Column(
        modifier = Modifier.padding(10.dp).clip(
            RoundedCornerShape(32.dp)).background(Color(213, 212, 210)),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 40.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {

            // 显示文本
            Text(group.name, fontSize = 20.sp, style = MaterialTheme.typography.caption, overflow = TextOverflow.Ellipsis, maxLines = 1, fontFamily = NotoSansFontFamily.SC)

            Spacer(modifier = Modifier.weight(1f))
            // 显示编辑按钮
            IconButton(
                onClick = {
                    showEditGroupDialog.value = true
                    groupViewModel.selectedGroup.value = group
                }
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "编辑",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
            IconButton(
                onClick = {
                    isExpanded = !isExpanded
                }
            ) {
                Icon(
                    if (!isExpanded)
                        Icons.Outlined.KeyboardArrowUp
                    else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "展开",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }



        }
        if(isExpanded) {

            feeds.forEach{ feed ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.padding(start = 40.dp, bottom = 8.dp, top = 8.dp).weight(2f).clickable {
                            feedViewModel.selectedFeed.value = feed
                            runBlocking {
                                itemViewModel.updateItems(feed, status.value)
                            }
                        },
                        text = feed.title
                    )

                    IconButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            feedViewModel.editFeed.value = feed
                            showEditFeedDialog.value = true
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "编辑",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }


                }
            }
        }

    }
}





@Composable
fun LazyScrollable(){
    var selectedGroupId = mutableStateOf(-1)
    var isSelected = mutableStateOf(false)

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text("移动到组", style = MaterialTheme.typography.subtitle1)
        Spacer(modifier = Modifier.fillMaxWidth().height(8.dp))

        if(showAddGroupDialog.value) AddGroupDialog()

        Box(
            modifier = Modifier.height(80.dp)
        ) {
            val state = rememberLazyListState()

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), state = state) {
                items(groupViewModel.groups) { group ->
                    FeedButton(
                        onClick = {
                            groupViewModel.selectedGroup.value = group
                            selectedGroupId.value = groupViewModel.selectedGroup.value!!.id
                            isSelected.value = !isSelected.value
                        }, isSelected = (selectedGroupId.value == group.id && isSelected.value)
                    ) {
                        if (selectedGroupId.value == group.id && isSelected.value)
                            Icon(
                                Icons.Outlined.Done,
                                contentDescription = "确定",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                        Text(group.name, maxLines = 1, style = MaterialTheme.typography.button)
                    }
                }
                item {
                    FeedButton(
                        onClick = {
                            showAddGroupDialog.value = true
                        }
                    ){
                        Text("添加分组", maxLines = 1, style = MaterialTheme.typography.button)
                    }
                }
            }
            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomStart).height(8.dp),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
    }
}




val showSettingDialog = mutableStateOf(false)
val showSaveDateDialog = mutableStateOf(false)
val showPageDialog = mutableStateOf(false)
@Composable
fun SettingDialog(){
    val saveDateMap = mapOf(
        1 to "一天", 3 to "三天", 7 to "一周", 14 to "两周",30 to "一个月", 90 to "三个月",Int.MAX_VALUE to "永久"
    )
    val pageMap = mapOf(
        0 to "未读", 1 to "全部", 2 to "已加星标"
    )

    Dialog(
        onCloseRequest = {
                         showSettingDialog.value = false
        },
        visible = true,
        state = rememberDialogState(size = DpSize(360.dp, 400.dp)),
        title = "设置",
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().background(Color(238, 238, 238))
        ) {
            Icon(Icons.Outlined.Settings, contentDescription = "Setting icon")
            Spacer(modifier = Modifier.height(16.dp))
            Text("设置", style = MaterialTheme.typography.button)
            Spacer(modifier = Modifier.height(16.dp))

            val syncInLaunch = remember { mutableStateOf(settingViewModel.setting.value!!.syncInLaunch) }

            if(showSaveDateDialog.value) DateDialog()

            if(showPageDialog.value) PageDialog()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.width(260.dp)
            ){
                val scope = CoroutineScope(Dispatchers.IO)
                Text("启动时刷新", fontFamily = NotoSansFontFamily.SC, modifier = Modifier.width(200.dp))

                Spacer(modifier = Modifier.width(24.dp))

                Switch(
                    checked = syncInLaunch.value,
                    onCheckedChange = {
                        syncInLaunch.value = it
                        scope.launch {
                            settingViewModel.updateSyncInLaunch()
                        }
                    },
                    colors = SwitchDefaults.colors(Color.Gray)
                    )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.width(260.dp).clickable {
                    showSaveDateDialog.value = true
                }
            ){
                CoroutineScope(Dispatchers.IO)
                Text(buildAnnotatedString {
                    withStyle(SpanStyle(fontFamily = NotoSansFontFamily.SC)){
                        append("内容保存期限\n")
                    }
                    withStyle(SpanStyle(fontFamily = NotoSansFontFamily.SC, fontWeight = FontWeight.Light)){
                        append(saveDateMap[settingViewModel.setting.value!!.saveTime]!!)
                    }
                }, maxLines =  2)
                Spacer(modifier = Modifier.width(24.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.width(260.dp).clickable {
                    showPageDialog.value = true
                }
            ){
                Text(buildAnnotatedString {
                    withStyle(SpanStyle(fontFamily = NotoSansFontFamily.SC)){
                        append("启动时内容状态\n")
                    }
                    withStyle(SpanStyle(fontFamily = NotoSansFontFamily.SC, fontWeight = FontWeight.Light)){
                        append(pageMap[settingViewModel.setting.value!!.pageInLaunch]!!)
                    }
                }, maxLines =  2)
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@Composable
fun DateDialog(){
    val saveDateMap = mapOf(
         1 to "一天", 3 to "三天", 7 to "一周", 14 to "两周",30 to "一个月", 90 to "三个月",Int.MAX_VALUE to "永久"
    )

    val saveDateList = saveDateMap.keys.toList()

    Dialog(
        onCloseRequest = {
            showSaveDateDialog.value = false
        },
        visible = true,
        state = rememberDialogState(size = DpSize(400.dp, 500.dp)),
        title = "设置",
    ){
       Column(
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.Center,
           modifier = Modifier.fillMaxSize().background(Color(238, 238, 238))
       )
        {
            val scope = CoroutineScope(Dispatchers.IO)

            Icon(Icons.Outlined.DateRange, contentDescription = "Setting icon")
            Spacer(modifier = Modifier.height(16.dp))
            Text("设置", style = MaterialTheme.typography.button)
            Spacer(modifier = Modifier.height(16.dp))
                saveDateList.forEach { k ->
                    Row(
                        modifier = Modifier.width(320.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settingViewModel.setting.value?.saveTime == k,
                            onClick = {
                                scope.launch {
                                    settingViewModel.updateSaveTime(k)
                                    showSaveDateDialog.value = false
                                }
                            },
                            colors = RadioButtonDefaults.colors(Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(200.dp))
                        Text(
                            text = saveDateMap[k]!!
                        )
                    }
                }
        }
    }
}

@Composable
fun PageDialog(){
    val pageMap = mapOf(
        0 to "未读", 1 to "全部", 2 to "已加星标"
    )

    val pageList = pageMap.keys.toList()



    Dialog(
        onCloseRequest = {
            showPageDialog.value = false
        },
        visible = true,
        state = rememberDialogState(size = DpSize(360.dp, 300.dp)),
        title = "设置",
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().background(Color(238, 238, 238))
        )
        {
            val scope = CoroutineScope(Dispatchers.IO)

            Icon(Icons.Outlined.DateRange, contentDescription = "Setting icon")
            Spacer(modifier = Modifier.height(16.dp))
            Text("设置", style = MaterialTheme.typography.button)
            Spacer(modifier = Modifier.height(16.dp))
            pageList.forEach { k ->
                Row(
                    modifier = Modifier.width(280.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = settingViewModel.setting.value?.pageInLaunch == k,
                        onClick = {
                            scope.launch {
                                settingViewModel.updatePageInLaunch(k)
                                showPageDialog.value = false
                            }
                        },
                        colors = RadioButtonDefaults.colors(Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(160.dp))
                    Text(
                        text = pageMap[k]!!
                    )
                }
            }
        }
    }
}



suspend fun importOpml(){
    val opmlFile = openFileChooser()
    if(opmlFile != null) {
        val opmlUtil = OPMLUtil()
        val feedMap =  opmlUtil.parse(opmlFile)
        OPMLViewModel().insert(feedMap,settingViewModel.setting.value!!.saveTime)
    }
}

fun openFileChooser(): File? {
    val fileChooser = JFileChooser()
    fileChooser.showOpenDialog(null)
    return fileChooser.selectedFile
}