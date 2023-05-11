package repository

import data.dao.model.Group
import data.group.GroupDaoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroupRepository {

    private val groupDaoImpl = GroupDaoImpl()
    suspend fun loadGroup() = withContext(Dispatchers.IO) {
        groupDaoImpl.getAllGroups()
    }

    suspend fun deleteGroup(groupId: Int) = withContext(Dispatchers.IO) {
        groupDaoImpl.deleteGroup(groupId)
    }

    suspend fun updateGroup(group: Group) = withContext(Dispatchers.IO){
        groupDaoImpl.updateGroup(group)
    }

    suspend fun addGroup(group: Group) = withContext(Dispatchers.IO){
        groupDaoImpl.insertGroup(group)
    }
}