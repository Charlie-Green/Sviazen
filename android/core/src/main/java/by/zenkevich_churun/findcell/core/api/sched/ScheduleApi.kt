package by.zenkevich_churun.findcell.core.api.sched

import by.zenkevich_churun.findcell.core.entity.sched.Schedule


/** Performs CRUD operations on user's [Schedule]. **/
interface ScheduleApi {
    fun get(prisonerId: Int, passwordHash: ByteArray): Schedule
    fun update(prisonerId: Int, passwordHash: ByteArray, schedule: Schedule)

    fun addCell(
        prisonerId: Int, passwordHash: ByteArray,
        jailId: Int, cellNumber: Short
    )

    fun deleteCell(
        prisonerId: Int, passwordHash: ByteArray,
        jailId: Int, cellNumber: Short
    )

    fun updateCell(
        prisonerId: Int, passwordHash: ByteArray,
        oldJailId: Int, oldCellNumber: Short,
        newJailId: Int, newCellNumber: Short
    )
}