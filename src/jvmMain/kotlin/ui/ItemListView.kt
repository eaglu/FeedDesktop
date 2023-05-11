package ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import data.dao.model.Item
import feedViewModel
import itemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import settingViewModel
import ui.common.FeedButton
import ui.fontfamily.NotoSansFontFamily
import util.format
import util.removeHtmlTags

val selectedItem = itemViewModel.selectedItem
val selectedItems = itemViewModel.items
val selectedFeed = feedViewModel.selectedFeed

@Composable
fun ItemListView(){

    var searchExpanded by remember { mutableStateOf(false) }
    var markExpanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            ItemListTopBar(searchExpanded,markExpanded,{searchExpanded = !searchExpanded},{markExpanded = !markExpanded})
        },
        backgroundColor = Color(224, 224, 224),
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                SearchBar(searchExpanded)

                MarkAsReadBar(markExpanded)

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {

                    val state = rememberLazyListState()

                    LazyColumn(
                        state = state
                    ) {
                        items(itemViewModel.items) { feedItem ->
                            //内容样式卡片
                            ItemCard(feedItem)
                        }
                    }

                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(
                            scrollState = state
                        )
                    )
                }
            }
        }
    )
}

@Composable
fun ItemListTopBar(searchExpanded: Boolean,markExpanded: Boolean,onSearchExpandedClick: () -> Unit,onMarkExpandedClick: () -> Unit){

    TopAppBar(
        backgroundColor = Color(0xf7f6f4),
        title = {
        },
        elevation = 0.dp,
        actions = {
            IconButton(
                onClick = { onSearchExpandedClick()},
            ){
                Icon(
                    if(searchExpanded) Icons.Outlined.Close else Icons.Outlined.Search,
                    contentDescription = "Switch",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }


            val scope = CoroutineScope(Dispatchers.IO)

            var isRefreshing by remember{ mutableStateOf(settingViewModel.setting.value!!.syncInLaunch) }
            if(isRefreshing) {
                scope.launch {
                    itemViewModel.refreshItems(feedViewModel.selectedFeed.value,settingViewModel.setting.value!!.saveTime)
                    isRefreshing = false
                }
            }

            val infiniteTransition = rememberInfiniteTransition()
            val degrees by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing)
                )
            )


            IconButton(
                onClick = {
                    scope.launch {
                        isRefreshing = true
                    }
                }
            ){
                Icon(
                    Icons.Outlined.Refresh,
                    contentDescription = "Switch",
                    modifier = if(isRefreshing) Modifier.size(ButtonDefaults.IconSize).rotate(degrees) else Modifier.size(
                        ButtonDefaults.IconSize)
                )
            }

            IconButton(
                onClick = {
                    onMarkExpandedClick()
                }
            ){
                if(markExpanded) {
                    Icon(
                       Icons.Outlined.Close,
                        contentDescription = "Switch",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }else{
                    Icon(
                        painter = painterResource("icons/done_all.svg"),
                        contentDescription = "Switch",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }
            }


        }
    )
}

@Composable
fun SearchBar(searchExpanded: Boolean) {
    var isHintDisplayed by remember { mutableStateOf(true) }
    var searchText by remember { mutableStateOf("") }
    AnimatedVisibility(
        visible = searchExpanded,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        val scope = CoroutineScope(Dispatchers.IO)
        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
                isHintDisplayed = it.isEmpty()
                scope.launch {
                    itemViewModel.searchItems(searchText, feedViewModel.selectedFeed.value)
                } },
            modifier = Modifier
                .onFocusChanged {
                    isHintDisplayed = it.isFocused && searchText.isEmpty()
                }.clip(RoundedCornerShape(24.dp)).width(300.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = "搜索",
                    color = Color.LightGray,
                    fontStyle = FontStyle.Italic
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = if (isHintDisplayed) Color.LightGray else Color.Gray
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }

    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
fun MarkAsReadBar(expanded: Boolean) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val scope = CoroutineScope(Dispatchers.IO)

           FeedButton(onClick = {
               val selectedFeed = feedViewModel.selectedFeed.value
               scope.launch{
                   itemViewModel.markAllAsRead(selectedFeed,"1", status.value)
               }
           }){
               Text("1天")
           }


            FeedButton(
                onClick = {
                    val selectedFeed = feedViewModel.selectedFeed.value
                    scope.launch{
                        itemViewModel.markAllAsRead(selectedFeed,"3", status.value)
                    }
                }
            ){
                Text("3天")
            }

            FeedButton(
                onClick = {
                    val selectedFeed = feedViewModel.selectedFeed.value
                    scope.launch{
                        itemViewModel.markAllAsRead(selectedFeed,"7", status.value)
                    }
                }
            ){
                Text("7天")
            }

            FeedButton(
                onClick = {
                    val selectedFeed = feedViewModel.selectedFeed.value
                    scope.launch{
                        itemViewModel.markAllAsRead(selectedFeed,"all", status.value)
                    }
                }
            ){
                Text("全部标记为已读")
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
fun ItemCard(feedItem: Item){

    var color = if(feedItem.isUnread){
        Color.White
    }else{
        Color.LightGray
    }
    var background = mutableStateOf(color)

    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.width(500.dp).height(180.dp).padding(24.dp).clip(RoundedCornerShape(24.dp)).clickable {
            itemViewModel.selectedItem.value = feedItem
            background.value = Color.LightGray
            scope.launch {
                itemViewModel.markAsRead(feedItem)
            }
        },
        elevation = 8.dp,
        backgroundColor = Color.Yellow

    ) {
        Column(
            modifier = Modifier.background(background.value).padding(16.dp).fillMaxSize(),
            ){
            Row(modifier = Modifier.fillMaxWidth().height(25.dp)){
                println(feedItem.feedId)
                feedViewModel.feedsMap.value[feedItem.feedId]?.let { Text(it, modifier = Modifier.weight(1f), maxLines = 1, color = Color.Gray,fontFamily = NotoSansFontFamily.SC) }
                Spacer(modifier = Modifier.weight(0.3f))
                Text(feedItem.pubDate.format(),modifier = Modifier.weight(1f), color = Color.Gray, maxLines = 1, fontFamily = NotoSansFontFamily.SC)
            }
                Text(feedItem.title, fontWeight = FontWeight.Bold, overflow = TextOverflow.Ellipsis, maxLines = 1, fontFamily = NotoSansFontFamily.SC)
                Spacer(modifier = Modifier.height(8.dp))
                Text(removeHtmlTags(feedItem.description), overflow = TextOverflow.Ellipsis, maxLines = 2, fontFamily = NotoSansFontFamily.SC)
//            }
        }
    }
}