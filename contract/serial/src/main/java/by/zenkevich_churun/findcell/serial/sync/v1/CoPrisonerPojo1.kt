package by.zenkevich_churun.findcell.serial.sync.v1

import by.zenkevich_churun.findcell.entity.entity.CoPrisoner
import by.zenkevich_churun.findcell.entity.entity.Contact
import by.zenkevich_churun.findcell.entity.entity.Prisoner
import by.zenkevich_churun.findcell.serial.prisoner.v1.pojo.ContactPojo1
import com.google.gson.annotations.SerializedName


class CoPrisonerPojo1: CoPrisoner() {

    @SerializedName("id")
    override var id: Int = Prisoner.INVALID_ID

    @SerializedName("name")
    override var name: String = ""

    @SerializedName("contacts")
    internal var contactPojos: List<ContactPojo1> = listOf()

    @SerializedName("rel")
    var relationOrdinal: Int = - 1


    override val contacts: List<Contact>
        get() = contactPojos

    override val relation: Relation
        get() = Relation.values()[relationOrdinal]
}