import data.dao.model.Item
import data.item.ItemDao
import data.item.ItemDaoImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ItemDaoTest {

    lateinit var itemDao: ItemDao


    @Test
    fun testGetAllItems() {
        val itemDao = ItemDaoImpl()
        val items = itemDao.getAllItems()
        assertNotNull(items)
        assertEquals(100,items.size)
    }

    @Test
    fun testGetItemById() {
        val itemDao = ItemDaoImpl()
        // 插入测试数据
        val insertedItem = Item(-1, "Test Item", "http://test.com", "2022-01-01","", 1, true, false)
        val id = itemDao.insertItem(insertedItem)
        // 查询测试数据
        val item = itemDao.getItemById(id)
        assertNotNull(item)
        // 根据实际情况断言查询结果
    }

    @Test
    fun testGetItemById_NotExist() {
        val itemDao = ItemDaoImpl()
        val item = itemDao.getItemById(1080) // 假定ID为100的Item不存在
        assertNull(item)
    }

    @Test
    fun testInsertItem() {
        val itemDao = ItemDaoImpl()
        val item = Item(-1, "Test Item", "http://test.com2", "2022-01-01","", 1, true, false)
        val result = itemDao.insertItem(item)
        assertTrue(result > 0) // 断言插入成功
    }

    @Test
    fun testUpdateItem() {
        val itemDao = ItemDaoImpl()
        // 插入一条测试数据
        val insertedItem = Item(-1, "Test Item", "http://test3.com","", "2022-01-01", 1, true, false)
        val id = itemDao.insertItem(insertedItem)
        // 更新测试数据
        val updatedItem = Item(id, "Test Item Updated", "http://test-updated.com","", "2022-01-02", 2, false, true)
        val result = itemDao.updateItem(updatedItem)
        assertEquals(1, result) // 断言更新成功
        // 查询修改后的数据并断言
        val queriedItem = itemDao.getItemById(id)
        assertNotNull(queriedItem)
        assertEquals(updatedItem, queriedItem)
    }

    @Test
    fun testDeleteItem() {
        val itemDao = ItemDaoImpl()
        // 插入一条测试数据
        val insertedItem = Item(-1, "Test Item", "http://test5.com","", "2022-01-01", 1, true, false)
        val id = itemDao.insertItem(insertedItem)
        // 删除测试数据并断言
        val result = itemDao.deleteItem(id)
        assertEquals(1, result) // 断言删除成功
        val queriedItem = itemDao.getItemById(id)
        assertNull(queriedItem)
    }

    @Test
    fun testGetItemsByFeedId() {
        val itemDao = ItemDaoImpl()
        val feedId = 14 // 假定存在 FeedId 为1的 Item
        val items = itemDao.getItemsByFeedId(feedId)
        for (item in items){
            println(item.title)
        }
        // 根据实际情况断言查询结果
    }
}
