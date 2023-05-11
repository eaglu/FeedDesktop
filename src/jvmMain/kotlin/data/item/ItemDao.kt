package data.item

import data.dao.model.Item
import java.time.LocalDateTime
import java.util.*

interface ItemDao {
    suspend fun getAllItems(feedId: Int? = null): List<Item>
    suspend fun getItemById(id: Int): Item?
    suspend fun insertItem(item: Item): Int
    suspend fun updateItem(item: Item): Int
    suspend fun deleteItems(days: Int):Int

    fun deleteItemsByFeedId(feedId: Int):Int
    suspend fun getItemsByFeedId(feedId: Int): List<Item>

    suspend fun getUnreadItems(feedId: Int? = null): List<Item>

    suspend fun getStaredItems(feedId: Int? = null): List<Item>

    fun markAllAsRead(feedId: Int?,dateTime: LocalDateTime?)

    fun markAsRead(itemId: Int): Int
    fun markAsUnRead(itemId: Int): Int
    fun markAsStared(itemId: Int): Int
    fun markAsUnStared(itemId: Int): Int
    fun searchItems(query: String, feedId: Int?):List<Item>
    fun getAllCount(id: Int?):Int
    fun getUnreadCount(id: Int?):Int
    fun getStarCount(id:Int?):Int
    fun insertItems(items: List<Item>,fId: Int):Int

}