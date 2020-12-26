package by.zenkevich_churun.findcell.server.internal.entity.table

import by.zenkevich_churun.findcell.server.internal.entity.key.PeriodKey
import javax.persistence.*


@Entity
@Table(name = "Periods")
class PeriodEntity {

    @EmbeddedId
    lateinit var key: PeriodKey

    @Column(name = "jail")
    var jailId: Int = 0

    @Column(name = "cell")
    var cellNumber: Short = 0
}