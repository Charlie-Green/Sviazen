package by.zenkevich_churun.findcell.server.internal.entity.table

import by.zenkevich_churun.findcell.server.internal.entity.key.CellKey
import javax.persistence.*


@Entity
@Table(name = "Cells")
class CellEntity {

    @EmbeddedId
    var key: CellKey? = null

    @Column(name = "seats")
    var seatCount: Short = 0
}