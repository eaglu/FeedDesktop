package data.group

import data.dao.model.*
import org.jetbrains.exposed.sql.select

interface GroupDao {
    // 获取所有分组
    fun getAllGroups(): List<Group>

    // 通过 ID 获取分组
    fun getGroupById(id: Int): Group?

    fun getGroupByName(name: String):Group?

    // 添加一个分组
    fun insertGroup(group: Group): Int

    // 更新一个分组
    fun updateGroup(group: Group): Int

    // 删除一个分组
    fun deleteGroup(id: Int): Int

    // 获得分组下的所有 RSS 源


}