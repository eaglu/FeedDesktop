package model

import data.dao.model.Item

data class FeedWithItem(
    var feed: Feed,
    var items:List<Item>
)

