package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import feedViewModel
import itemViewModel
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Node
import util.HtmlToTextCom
import util.format
import java.awt.Desktop
import java.net.URI


@Composable
fun ItemView(){

    val item = itemViewModel.selectedItem.value


    Scaffold(
        topBar = {
            ArticleTopBar()
        },
        backgroundColor = Color(238, 238, 238),
        content = {
            Column (
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            ){
                Box(modifier = Modifier.clickable {
                    Desktop.getDesktop().browse(URI(item!!.link)) }.padding(20.dp).fillMaxWidth()) {
                    Column {
                        Text(text = item!!.pubDate.format(), style = MaterialTheme.typography.caption)
                        Text(text = item.title, style = MaterialTheme.typography.h6)
                        Text(text = item.author,style = MaterialTheme.typography.caption)
                        Text(text = feedViewModel.feedsMap.value[item.feedId]!!,style = MaterialTheme.typography.caption) }
                }
                Spacer(modifier = Modifier.height(8.dp))
                val stateVertical = rememberScrollState(0)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 12.dp, bottom = 12.dp)
                ) {
                    val nodes = Jsoup.parse(item!!.description).body().childNodes()
                    Column(
                                modifier = Modifier.padding(24.dp).verticalScroll(stateVertical)
                    ) {
                                HtmlToTextCom(nodes, skip = false)
                    }

                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(stateVertical)
                    )
                }
            }
        },
    )
}

@Composable
fun TextBox(text: String = "Item") {
    Box(
        modifier = Modifier.height(32.dp)
            .width(400.dp)
            .background(color = Color(200, 0, 0, 20))
            .padding(start = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text)
    }
}

@Composable
fun ArticleTopBar(){
    val isStared = mutableStateOf(itemViewModel.selectedItem.value!!.isStared)
    val isUnread = mutableStateOf(itemViewModel.selectedItem.value!!.isUnread)

    TopAppBar(
        backgroundColor = Color(0xf7f6f4),
        title = { },
        elevation = 0.dp,
        actions = {
            val scope = rememberCoroutineScope()

            IconButton(
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = if(isStared.value) {
                                painterResource("icons/star_cancel.svg")
                            }else{
                                painterResource("icons/star.svg")
                            },
                            contentDescription = "Switch",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }
                },

                onClick = {
                    scope.launch {
                        if(isStared.value) {
                            itemViewModel.markAsUnStared(selectedItem.value!!)
                        }else{
                            itemViewModel.markAsStared(selectedItem.value!!)
                        }
                        isStared.value = !isStared.value
                    }
                }
            )

            IconButton(
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = if(isUnread.value) {
                                painterResource("icons/done.svg")
                            }else{
                                painterResource("icons/done_cancel.svg")
                            },
                            contentDescription = "Switch",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }
                },

                onClick = {
                    scope.launch {
                        if(isUnread.value) {
                            itemViewModel.markAsRead(selectedItem.value!!)
                        }else{
                            itemViewModel.markAsUnRead(selectedItem.value!!)
                        }
                        isUnread.value = !isUnread.value
                    }
                }
            )
        }
    )
}

@Composable
fun ArticleBottomBar(){
    val isStared = mutableStateOf(itemViewModel.selectedItem.value!!.isStared)
    val isUnread = mutableStateOf(itemViewModel.selectedItem.value!!.isUnread)



    BottomAppBar(
        backgroundColor = Color(247, 246, 244),
        contentColor = Color(141, 141, 139),
    ) {
        Row(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val scope = rememberCoroutineScope()
            IconButton(
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.Star,
                            contentDescription = "Switch",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        if (isStared.value) {
                            Text("取消星标")
                        } else {
                            Text("添加星标")
                        }
                    }
                },

                onClick = {
                    scope.launch {
                        if(isStared.value) {
                            itemViewModel.markAsUnStared(selectedItem.value!!)
                        }else{
                            itemViewModel.markAsStared(selectedItem.value!!)
                        }
                        isStared.value = !isStared.value
                    }
                }
            )

            IconButton(
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Switch",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        if(isUnread.value) {
                            Text("标记已读")
                        }else{
                            Text("取消已读")
                        }
                    }
                },

                onClick = {
                    scope.launch {
                        if(isUnread.value) {
                            itemViewModel.markAsRead(selectedItem.value!!)
                        }else{
                            itemViewModel.markAsUnRead(selectedItem.value!!)
                        }
                        isUnread.value = !isUnread.value
                    }
                }
            )
            IconButton(
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.ArrowDropDown,
                            contentDescription = "Switch",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )

                        Text("下一个")
                    }
                },

                onClick = { shareText("Switch") }
            )
        }
    }
}

fun shareText(text: String) {
    Desktop.getDesktop().browse(URI.create("mailto:?body=$text"))
}