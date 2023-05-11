package viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import data.dao.model.Item
import model.Feed
import repository.ItemRepository

class ItemViewModel(){
    private val itemRepository = ItemRepository()

    val items = mutableStateListOf<Item>()

    val status = mutableStateOf(0)

    var selectedItem = mutableStateOf<Item?>(null)

    suspend fun updateItems(selectedFeed: Feed?, status: Int){

        items.clear()
        when(status){
            0 -> items.addAll(itemRepository.loadUnreadItems(selectedFeed))
            1 -> items.addAll(itemRepository.loadAllItems(selectedFeed))
            2 -> items.addAll(itemRepository.loadStaredItems(selectedFeed))
        }
    }

    suspend fun markAllAsRead(selectedFeed: Feed?, date: String, status: Int){
        itemRepository.markAllAsRead(selectedFeed, date)
        updateItems(selectedFeed, status)
    }

    suspend fun markAsRead(selectedItem:Item) {
        items.let {
            // 在已知的列表中搜索需要更新的 Item 对象
            val item = it.find { it -> it.id == selectedItem.id }
            // 如果找到了该 Item 对象，将其标记为已读
            item?.isUnread = false
        }
        selectedItem.isUnread = false
        itemRepository.markAsRead(selectedItem)
    }

    suspend fun markAsUnRead(selectedItem:Item) {
        items.let {
            // 在已知的列表中搜索需要更新的 Item 对象
            val item = it.find { it.id == selectedItem.id }
            // 如果找到了该 Item 对象，将其标记为已读
            item?.isUnread = false
        }
        selectedItem.isUnread = true
        itemRepository.markAsUnRead(selectedItem)
    }

    suspend fun refreshItems(selectedFeed: Feed?, days: Int){
        itemRepository.refreshItems(selectedFeed,days)
        status.value = 0
        updateItems(selectedFeed,0)
    }

    suspend fun markAsStared(selectedItem:Item) {
        items.let {
            // 在已知的列表中搜索需要更新的 Item 对象
            val item = it.find { it.id == selectedItem.id }
            // 如果找到了该 Item 对象，将其标记为已读
            item?.isStared = true
        }
        selectedItem.isStared = true
        itemRepository.markAsStared(selectedItem)
    }

    suspend fun markAsUnStared(selectedItem:Item) {
        items.let {
            // 在已知的列表中搜索需要更新的 Item 对象
            val item = it.find { it.id == selectedItem.id }
            // 如果找到了该 Item 对象，将其标记为已读
            item?.isStared = true
        }
        selectedItem.isStared = false
        itemRepository.markAsUnStared(selectedItem)
    }

    suspend fun searchItems(query: String, selectedFeed: Feed?){
        items.clear()
        if(selectedFeed != null){
            items.addAll(itemRepository.searchItems(query, selectedFeed.id ))
        }else{
            items.addAll(itemRepository.searchItems(query, null))
        }
    }

}