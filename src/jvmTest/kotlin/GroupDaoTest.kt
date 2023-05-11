import data.dao.model.Group
import data.group.GroupDao
import data.group.GroupDaoImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class GroupDaoTest {
    class GroupDaoTest {

        lateinit var groupDao: GroupDao

        @Test
        fun testGetAllGroups() {
            val groupDao = GroupDaoImpl()
            val groups = groupDao.getAllGroups()
            assertNotNull(groups)
            assertEquals(11, groups.size) // 假定一共有3个Group
        }

        @Test
        fun testGetGroupById() {
            val groupDao = GroupDaoImpl()
            val group = groupDao.getGroupById(1)
            assertNotNull(group)
            assertEquals("默认", group!!.name)
        }

        @Test
        fun testGetGroupById_NotExist() {
            val groupDao = GroupDaoImpl()
            val group = groupDao.getGroupById(20) // 假定ID为20的Group不存在
            assertNull(group)
        }

        @Test
        fun testInsertGroup() {
            val groupDao = GroupDaoImpl()
            val group = Group(-1, "Group 5")
            val result = groupDao.insertGroup(group)
            assertEquals(13, result) // 假定插入成功
        }

        @Test
        fun testUpdateGroup() {
            val groupDao = GroupDaoImpl()
            val group = Group(7, "fun")
            val result = groupDao.updateGroup(group)
            assertEquals(1, result) // 假定更新成功
            val updatedGroup = groupDao.getGroupById(7)
            assertNotNull(updatedGroup)
            assertEquals("fun", updatedGroup!!.name)
        }

        @Test
        fun testDeleteGroup() {
            val groupDao = GroupDaoImpl()
            val result = groupDao.deleteGroup(12)
            assertEquals(1, result) // 假定删除成功
            val deletedGroup = groupDao.getGroupById(12)
            assertNull(deletedGroup)
        }


    }

}