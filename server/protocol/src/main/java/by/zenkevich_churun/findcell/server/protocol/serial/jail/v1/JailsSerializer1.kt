package by.zenkevich_churun.findcell.server.protocol.serial.jail.v1

import by.zenkevich_churun.findcell.entity.entity.Jail
import by.zenkevich_churun.findcell.serial.jail.v1.pojo.*
import by.zenkevich_churun.findcell.server.protocol.serial.jail.abstr.JailsSerializer
import by.zenkevich_churun.findcell.serial.util.protocol.ProtocolUtil


internal class JailsSerializer1: JailsSerializer {

    override fun serializeJails(jails: List<Jail>): String {
        val listPojo = JailsListPojo1.wrap(jails)
        val approxSize = 4 + 46*jails.size
        return ProtocolUtil.toJson(listPojo, approxSize)
    }


    override fun serializeCells(seatCounts: ShortArray): String {
        val pojo = SeatCountsListPojo()
        pojo.seatCounts = seatCounts
        return ProtocolUtil.toJson(pojo, 4 + 5*seatCounts.size)
    }
}