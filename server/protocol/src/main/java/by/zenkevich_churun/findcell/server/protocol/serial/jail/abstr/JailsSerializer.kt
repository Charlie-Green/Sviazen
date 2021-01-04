package by.zenkevich_churun.findcell.server.protocol.serial.jail.abstr

import by.zenkevich_churun.findcell.entity.entity.Jail
import by.zenkevich_churun.findcell.server.protocol.serial.jail.v1.JailsSerializer1


interface JailsSerializer {
    fun serializeJails(jails: List<Jail>): String
    fun serializeCells(seatCounts: ShortArray): String


    companion object {

        fun forVersion(v: Int): JailsSerializer {
            if(v == 1) {
                return JailsSerializer1()
            }
            throw IllegalArgumentException("Unknown version $v")
        }
    }
}