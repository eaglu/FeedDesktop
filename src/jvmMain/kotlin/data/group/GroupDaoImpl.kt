package data.group

import data.dao.model.Group
import data.dao.model.Groups
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import util.DBUtils

class GroupDaoImpl:GroupDao {

    override fun getAllGroups(): List<Group> =  transaction(DBUtils.db) {
        Groups.selectAll().map { it.toGroup() }
    }

    override fun getGroupById(id: Int) = transaction(DBUtils.db) {
        Groups.select { Groups.id.eq(id) }.mapNotNull { it.toGroup() }
            .singleOrNull()
    }

    override fun getGroupByName(name: String): Group?  = transaction(DBUtils.db) {
        Groups.select { Groups.name eq name }.mapNotNull { it.toGroup() }
            .singleOrNull()
    }

    override fun insertGroup(group: Group) = transaction(DBUtils.db) {
        Groups.insert { it[name] = group.name } get Groups.id
    }

    override fun updateGroup(group: Group): Int = transaction(DBUtils.db) {
        Groups.update({ Groups.id eq group.id }) {
            it[name] = group.name
        }
    }

    override fun deleteGroup(id: Int): Int = transaction(DBUtils.db) {
        Groups.deleteWhere { Groups.id.eq(id) }
    }

    private fun ResultRow.toGroup(): Group =
        Group(
            id = this[Groups.id],
            name = this[Groups.name],
        )

}