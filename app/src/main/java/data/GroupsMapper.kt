package data

import domain.entity.GroupsItem

class GroupsMapper {
    fun mapDbModelToEntity(groupsDbModel: GroupsDbModel): GroupsItem{
        return GroupsItem(
            group = groupsDbModel.group
        )
    }
    fun mapEntityToDbModel(groupItem: GroupsItem): GroupsDbModel{
        return GroupsDbModel(
            group = groupItem.group
        )
    }
}