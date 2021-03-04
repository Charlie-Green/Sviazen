package by.sviazen.domain.contract.sched

import by.sviazen.domain.entity.Cell
import by.sviazen.domain.entity.Jail
import com.google.gson.annotations.SerializedName


class CellPojo(

    @SerializedName("jailId")
    override var jailId: Int,

    @SerializedName("jailName")
    override var jailName: String,

    @SerializedName("number")
    override var number: Short,

    @SerializedName("seats")
    override var seats: Short

): Cell() {

    constructor(): this(Jail.UNKNOWN_ID, "", 0, 0)


    companion object {

        fun from(
            c: Cell
        ) = CellPojo(c.jailId, c.jailName, c.number, c.seats)
    }
}