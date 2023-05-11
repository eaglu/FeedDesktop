package viewmodel

import model.Feed
import data.dao.model.Group
import repository.OPMLRepository

class OPMLViewModel {
    private val opmlRepository = OPMLRepository()

    suspend fun insert(feedMap: Map<String, List<Feed>>, days: Int)
    = opmlRepository.insert(feedMap,days)
    suspend fun export() = opmlRepository.export()
}