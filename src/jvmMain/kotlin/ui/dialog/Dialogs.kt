package ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
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
import ui.LazyScrollable
import ui.common.FeedButton
import ui.common.JFilerChooserExt
import ui.fontfamily.NotoSansFontFamily
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

val showAddFeedDialog = mutableStateOf(false)
val showAddGroupDialog = mutableStateOf(false)
val showEditGroupDialog = mutableStateOf(false)
val showEditFeedDialog = mutableStateOf(false)

@Composable
fun AddFeedDialog() {
    var status by remember { mutableStateOf("添加订阅源")}
    var isEnabled by remember { mutableStateOf(false)}
    var isSearched by remember { mutableStateOf(false)}

    Dialog(
        onCloseRequest = {
            showAddFeedDialog.value = false
        },
        visible = true,
        state = rememberDialogState(size = DpSize(400.dp, 500.dp)),
        title = "添加订阅源",
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().background(Color(238, 238, 238))
        ) {
            var feedLink by remember{ mutableStateOf("") }
            Icon(Icons.Outlined.Add, contentDescription = "RSS icon")
            Spacer(modifier = Modifier.height(16.dp))
            Text(status, style = MaterialTheme.typography.button)
            Spacer(modifier = Modifier.height(16.dp))
            var isHintDisplayed by remember { mutableStateOf(true) }

            TextField(
                value = feedLink,
                onValueChange = {
                    feedLink = it
                    isEnabled = feedLink.isNotEmpty()
                    isHintDisplayed = it.isEmpty()
                },
                modifier = Modifier
                    .onFocusChanged {
                        isHintDisplayed = it.isFocused && feedLink.isEmpty()
                    }.clip(RoundedCornerShape(24.dp)).width(300.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "搜索",
                        color = Color.LightGray,
                        fontStyle = FontStyle.Italic
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            if(isSearched) LazyScrollable()

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                if (!isSearched) {

                    val scope = CoroutineScope(Dispatchers.IO)


                    FeedButton(
                        enabled = isEnabled,
                        onClick = {
                            status = "搜索中..."
                            isEnabled = false
                            scope.launch {
                                if(feedViewModel.checkExistence(feedLink)) {
                                    status = "订阅源已存在"
                                }else if (feedViewModel.loadFeed(feedLink)){
                                    status = "请检查链接或网络连接"
                                }else {
                                    val feed = feedViewModel.newFeed.value
                                    status = feed!!.feed.title
                                    isSearched = true
                                }
                                isEnabled = true
                            }
                        }
                    ) {
                        Text("搜索")
                    }
                }else{
                    val scope = CoroutineScope(Dispatchers.IO)
                    FeedButton(
                        enabled = isEnabled,
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                feedViewModel.insertFeed(groupViewModel.selectedGroup.value?.id)
                                groupViewModel.loadGroup()
                                feedViewModel.loadFeeds()
                                itemViewModel.updateItems(null,0)
                                groupViewModel.selectedGroup.value = null
                                showAddFeedDialog.value = false
                            }
                        }
                    ){
                        Text("确定")
                    }
                }
            }
        }

    }
}

@Composable
fun AddGroupDialog(){

    val status = remember {mutableStateOf("添加分组")}
    val groupName = remember{mutableStateOf("")}
    val scope = CoroutineScope(Dispatchers.IO)

    Dialog(
        onCloseRequest = { showAddGroupDialog.value = false},
        visible = true,
        state = rememberDialogState(size = DpSize(400.dp, 300.dp)),
        title = "添加分组",
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().background(Color(238, 238, 238))
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "Setting icon")
            Spacer(modifier = Modifier.height(16.dp))
            Text(status.value, style = MaterialTheme.typography.button)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = groupName.value,
                onValueChange = {
                    groupName.value = it
                },
                modifier = Modifier.clip(RoundedCornerShape(24.dp)).width(300.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "分组名",
                        color = Color.LightGray,
                        fontStyle = FontStyle.Italic
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            FeedButton(
                onClick = {
                    scope.launch {
                        if(groupViewModel.checkExistence(groupName.value)){
                            status.value = "分组名已存在"
                        }else{
                            groupViewModel.addGroup(groupName.value)
                            showAddGroupDialog.value = false
                        }
                    }
                },
                enabled = (groupName.value != "")
            ){
                Text("确定", style = MaterialTheme.typography.button)
            }
        }
    }
}

@Composable
fun EditGroupDialog(group: Group) {
    var groupName by remember { mutableStateOf(group.name)}
    val status = remember {mutableStateOf("修改分组")}

    Dialog(
        onCloseRequest = {
            showEditGroupDialog.value = false
        },
        visible = showEditGroupDialog.value,
        state = rememberDialogState(size = DpSize(400.dp, 400.dp)),
        title = "修改分组",
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().background(Color(238, 238, 238))
        ) {

            var isHintDisplayed by remember { mutableStateOf(true) }
            Icon(Icons.Outlined.Edit, contentDescription = "RSS icon")
            Spacer(modifier = Modifier.height(16.dp))
            Text(status.value, style = MaterialTheme.typography.h6, fontFamily = NotoSansFontFamily.SC)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = groupName,
                onValueChange = {
                    groupName = it
                    isHintDisplayed = it.isEmpty()
                },
                modifier = Modifier.clip(RoundedCornerShape(24.dp)).width(300.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "编辑",
                        color = Color.LightGray,
                        fontStyle = FontStyle.Italic
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            val scope = CoroutineScope(Dispatchers.IO)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
            ) {
                FeedButton(
                    onClick = {
                        showEditGroupDialog.value = false
                        scope.launch {
                            groupViewModel.deleteGroup(group.id)
                            feedViewModel.loadFeeds()
                            itemViewModel.updateItems(null, 0)
                            showEditGroupDialog.value = false
                        }
                    },
                ) {
                    Text("删除分组", style = MaterialTheme.typography.button)
                }
                FeedButton(
                    onClick = {
                        scope.launch {
                            if(groupViewModel.checkExistence(groupName)) {
                                status.value = "分组名已存在"
                            }else{
                                groupViewModel.updateGroup(Group(group.id, groupName))
                                showEditGroupDialog.value = false
                            }
                        }
                    },
                    enabled = groupName.isNotEmpty()
                ) {
                    Text("修改", style = MaterialTheme.typography.button)
                }
            }
        }
    }
}

@Composable
fun EditFeedDialog(){
    val feed = feedViewModel.editFeed.value
    var title by remember { mutableStateOf(feed!!.title)}
    Dialog(
        onCloseRequest = {
            feedViewModel.editFeed.value = null
            showEditFeedDialog.value = false
        },
        visible = true,
        state = rememberDialogState(size = DpSize(400.dp, 400.dp)),
        title = "修改订阅源",
    ) {
        var status by remember { mutableStateOf(feed!!.link)}

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().background(Color(238, 238, 238))
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = "RSS icon")
            Spacer(modifier = Modifier.height(16.dp))
            SelectionContainer {
                Text(status, style = MaterialTheme.typography.caption, fontFamily = NotoSansFontFamily.SC)
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = title,
                onValueChange = {
                    title = it
                },
                modifier = Modifier.clip(RoundedCornerShape(24.dp)).width(300.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "编辑",
                        color = Color.LightGray,
                        fontStyle = FontStyle.Italic
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            LazyScrollable()

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
            ) {
                val scope = rememberCoroutineScope()

                FeedButton(
                    onClick = {
                        showAddFeedDialog.value = false
                        scope.launch {
                            runBlocking {
                                feedViewModel.deleteFeed(feed!!.id)
                            }
                            groupViewModel.loadGroup()
                            feedViewModel.loadFeeds()
                            itemViewModel.updateItems(null, 0)
                            showEditFeedDialog.value = false
                        }
                    },
                ) {
                    Text("取消订阅", style = MaterialTheme.typography.button)
                }
                FeedButton(
                    onClick = {
                        scope.launch {
                            val groupId = groupViewModel.selectedGroup.value?.id
                            feed!!.title = title
                            if(groupId != null) feed.groupId = groupId
                            feedViewModel.updateFeed(feed)
                            groupViewModel.loadGroup()
                            showEditFeedDialog.value = false
                        }
                    }
                ) {
                    Text("修改", style = MaterialTheme.typography.button)
                }
            }
        }
    }
}

fun FileChooserDialog(string:String){
    // 使用系统文件选择器打开对话框，让用户选择保存文件的路径和文件名
    val fileChooser = JFilerChooserExt("feeds.opml")
    fileChooser.dialogTitle = "导出为OPML"
    fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
    fileChooser.isMultiSelectionEnabled = false
    fileChooser.fileFilter = FileNameExtensionFilter("OPML文件", "opml")
    val result = fileChooser.showSaveDialog(null)

    // 将生成的 OPML 字符串写入文件
    if (result == JFileChooser.APPROVE_OPTION) {
        val file = fileChooser.selectedFile
        file.writeText(string)
    }
}
