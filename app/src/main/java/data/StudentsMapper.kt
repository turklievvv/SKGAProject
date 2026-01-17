package data

import domain.entity.StudentItem

class StudentsMapper {

    fun mapEntityToDbModel(studentItem: StudentItem) = StudentItemDbModel(
        studentItem.id,
        studentItem.surName,
        studentItem.lastName,
        studentItem.middleName,
        studentItem.group,
        studentItem.phone,
        studentItem.email,
        studentItem.password
    )

    fun mapDbModelToEntity(studentItemDb: StudentItemDbModel) = StudentItem(
        studentItemDb.id,
        studentItemDb.surName,
        studentItemDb.lastName,
        studentItemDb.middleName,
        studentItemDb.group,
        studentItemDb.phone,
        studentItemDb.email,
        studentItemDb.password
    )

    fun mapDbModelListToEntityList(list: List<StudentItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }

}