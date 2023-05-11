package model

import data.dao.model.Group

data class GroupWithFeed(
    var group: Group,
    var feeds: List<Feed>
)