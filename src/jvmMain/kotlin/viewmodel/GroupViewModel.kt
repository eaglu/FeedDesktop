package viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import model.Feed
import data.dao.model.Group
import repository.FeedRepository
import repository.GroupRepository

class GroupFeedViewModel {
    private val groupRepository = GroupRepository()
    private val feedRepository = FeedRepository()

    val groups = mutableStateListOf<Group>()

    lateinit var feeds:List<Feed>

    val selectedGroup = mutableStateOf<Group?>(null)

    val selectedFeed = mutableStateOf<Feed?>(null)
    suspend fun loadGroup(){
        groups.clear()
        groups.addAll(groupRepository.loadGroup())
        feeds = feedRepository.loadFeeds()
        for(group in groups){
            group.feeds = feeds.filter { it.groupId ==  group.id}
        }
    }



    suspend fun deleteGroup(groupId: Int) {
        groupRepository.deleteGroup(groupId)
        loadGroup()
    }

    suspend fun updateGroup(group: Group){
        groupRepository.updateGroup(group)
        loadGroup()
    }

    suspend fun addGroup(groupName: String){
        groupRepository.addGroup(Group(name = groupName))
        loadGroup()
    }

    suspend fun checkExistence(groupName: String):Boolean{
        return groups.any { it.name == groupName }
    }

}