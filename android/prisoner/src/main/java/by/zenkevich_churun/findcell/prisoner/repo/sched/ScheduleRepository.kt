package by.zenkevich_churun.findcell.prisoner.repo.sched

import android.util.Log
import by.zenkevich_churun.findcell.core.api.sched.ScheduleApi
import by.zenkevich_churun.findcell.entity.entity.Arest
import by.zenkevich_churun.findcell.entity.entity.Schedule
import by.zenkevich_churun.findcell.prisoner.repo.common.PrisonerStorage
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ScheduleRepository @Inject constructor(
    private val api: ScheduleApi,
    private val store: PrisonerStorage ) {

    private var arestId = Arest.INVALID_ID


    fun getSchedule(arestId: Int): GetScheduleResult {
        val prisoner = store.prisonerLD.value ?: return GetScheduleResult.NotAuthorized

        return try {
            val schedule = api.get(prisoner.id, prisoner.passwordHash, arestId)
            this.arestId = arestId
            GetScheduleResult.Success(schedule)
        } catch(exc: IOException) {
            Log.w(LOGTAG, "Failed to get schedule: ${exc.javaClass.name}: ${exc.message}")
            GetScheduleResult.Failed(exc)
        }
    }

    fun updateSchedule(schedule: Schedule): UpdateScheduleResult {
        val prisoner = store.prisonerLD.value ?: return UpdateScheduleResult.NotAuthorized

        try {
            api.update(prisoner.id, prisoner.passwordHash, schedule)
            return UpdateScheduleResult.Success
        } catch(exc: IOException) {
            Log.w(LOGTAG, "Failed to update schedule: ${exc.javaClass.name}: ${exc.message}")
            return UpdateScheduleResult.Failed(exc)
        }
    }


    fun addCell(
        jailId: Int,
        cellNumber: Short

    ) = crudCell { arestId, passwordHash ->
        api.addCell(arestId, passwordHash, jailId, cellNumber)
    }

    fun deleteCell(
        jailId: Int,
        cellNumber: Short

    ) = crudCell { arestId, passwordHash ->
        api.deleteCell(arestId, passwordHash, jailId, cellNumber)
    }

    fun updateCell(
        oldJailId: Int, oldCellNumber: Short,
        newJailId: Int, newCellNumber: Short

    ) = crudCell { arestId, passwordHash ->
        api.updateCell(
            arestId, passwordHash,
            oldJailId, oldCellNumber,
            newJailId, newCellNumber
        )
    }


    private inline fun crudCell(
        performNetworkCall: (arestId: Int, passwordHash: ByteArray) -> Unit
    ): Boolean {

        val arestId = this.arestId
        val prisoner = store.prisonerLD.value
        if(arestId == Arest.INVALID_ID || prisoner == null) {
            return false
        }

        try {
            performNetworkCall(arestId, prisoner.passwordHash)
            return true
        } catch(exc: IOException) {
            Log.w(LOGTAG, "Failed to add cell: ${exc.javaClass.name}: ${exc.message}")
            return false
        }
    }


    companion object {
        private const val LOGTAG = "FindCell-Schedule"
    }
}