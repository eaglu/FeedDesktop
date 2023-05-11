package repository

import data.dao.feed.FeedDaoImpl
import model.Feed
import data.dao.model.Group
import data.group.GroupDaoImpl

class OPMLRepository {
    private val groupDaoImpl = GroupDaoImpl()
    private val feedDaoImpl = FeedDaoImpl()
    suspend fun insert(feedMap: Map<String, List<Feed>>, days: Int){
        val groups = groupDaoImpl.getAllGroups().map { it.name }.toSet()
        val feedLinks = feedDaoImpl.getAllFeeds().map { it.link }.toSet()

        feedMap.forEach{ (name, feeds) ->
            //若分组当前不存在，则先执行插入操作，获取插入后的id
            val id = if(name !in groups){
                groupDaoImpl.insertGroup(Group(-1,name))
            }else{
                groupDaoImpl.getGroupByName(name)!!.id
            }
            //过滤掉已存在的订阅源
            feedDaoImpl.insertFeeds(feeds.filter { it.link !in feedLinks }, id)
        }

        ItemRepository().refreshItems(null,days)
    }

    suspend fun export():List<Group>{
        var groups = groupDaoImpl.getAllGroups()
        val feeds = feedDaoImpl.getAllFeeds()
        groups.forEach{group ->
           group.feeds = feeds.filter { it.groupId == group.id }
        }
        return groups
    }
}