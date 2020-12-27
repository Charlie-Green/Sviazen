package by.zenkevich_churun.findcell.serial.prisoner.common

import by.zenkevich_churun.findcell.entity.Prisoner
import by.zenkevich_churun.findcell.serial.prisoner.v1.serial.PrisonerSerializer1


interface PrisonerSerializer {
    fun serialize(prisoner: Prisoner): String


    companion object {
        fun forVersion(v: Int): PrisonerSerializer = when(v) {
            1 -> PrisonerSerializer1()
            else -> throw IllegalArgumentException("Unknown version $v")
        }
    }
}