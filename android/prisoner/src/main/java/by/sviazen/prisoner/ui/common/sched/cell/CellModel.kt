package by.sviazen.prisoner.ui.common.sched.cell

import by.sviazen.domain.entity.Cell


class CellModel(
    override val jailId: Int,
    override val jailName: String,
    override val number: Short,
    override val seats: Short,
    val backColor: Int,
    val numberBackColor: Int,
    val textColor: Int
): Cell() {

    override fun toString(): String
        = toString(jailName, number)


    companion object {
        fun toString(jailName: String, cellNumber: Short)
            = "$jailName, $cellNumber"
    }
}