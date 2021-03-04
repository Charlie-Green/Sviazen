package by.sviazen.server.internal.repo.sched

import by.sviazen.domain.contract.sched.*
import by.sviazen.server.internal.dao.arest.ArestsDao
import by.sviazen.server.internal.dao.scell.ScheduleCellsDao
import by.sviazen.server.internal.dao.speriod.SchedulePeriodsDao
import by.sviazen.server.internal.entity.key.PeriodKey
import by.sviazen.server.internal.entity.table.*
import by.sviazen.server.internal.repo.common.SviazenRepositiory
import org.springframework.beans.factory.annotation.Autowired


class ScheduleRepository: SviazenRepositiory() {

    @Autowired
    private lateinit var periodsDao: SchedulePeriodsDao

    @Autowired
    private lateinit var cellsDao: ScheduleCellsDao

    @Autowired
    private lateinit var arestsDao: ArestsDao


    fun get(
        arestId: Int,
        passwordHash: ByteArray
    ): ScheduleFetchedPojo {

        validateCredentialsByArestId(arestId, passwordHash)

        val cellEntries = cellsDao.get(arestId)
        val cellPojos = cellEntries.map { entry ->
            val fullCell = cellsDao.getFull(entry.key.jailId, entry.key.cellNumber)
            CellPojo(
                fullCell.jail.id,
                fullCell.jail.name,
                fullCell.number,
                fullCell.seats
            )
        }

        val periodEntities = periodsDao.get(arestId)
        val periodPojos = periodEntities.map { entity ->
            SchedulePeriodFetchedPojo(
                cellIndex(cellEntries, entity.jailId, entity.cellNumber),
                entity.key.start,
                entity.key.end
            )
        }

        return ScheduleFetchedPojo(cellPojos, periodPojos)
    }


    fun save(
        data: ScheduleUpdatedPojo,
        passwordHash: ByteArray ) {

        val arest = arestsDao
            .findById(data.arestId)
            .get()
        validateCredentials(arest.prisonerId, passwordHash)

        val entities = data.periods.map { period ->
            periodEntityFrom(period, arest)
        }

        periodsDao.apply {
            deletePeriodsForArests( listOf(data.arestId) )
            saveAll(entities)
        }
    }


    private fun validateCredentialsByArestId(arestId: Int, passwordHash: ByteArray) {
        val prisonerId = arestsDao.prisonerId(arestId)
        validateCredentials(prisonerId, passwordHash)
    }

    private fun periodEntityFrom(
        period: SchedulePeriodUpdatedPojo,
        arest: ArestEntity
    ): PeriodEntity {
        if(period.start > period.end) {
            throw IllegalArgumentException(
                "Period.start (${period.start}) is greater than Period.end (${period.end})" )
        }

        if(period.start < arest.start ||
            period.end > arest.end ) {

            val periodRange = "${period.start} - ${period.end}"
            val arestRange = "${arest.start} - ${arest.end}"
            throw IllegalArgumentException(
                "Period ($periodRange) is out of its Arest's bounds ($arestRange)" )
        }

        return PeriodEntity(
            PeriodKey(arest.id, period.start, period.end),
            period.jailId,
            period.cellNumber
        )
    }

    private fun cellIndex(
        cells: List<ScheduleCellEntryEntity>,
        jailId: Int,
        cellNumber: Short
    ): Int {
        val index = cells.indexOfFirst { c ->
            c.key.jailId == jailId &&
            c.key.cellNumber == cellNumber
        }

        if(index !in cells.indices) {
            throw Error("No cell ($jailId, $cellNumber) found in Schedule")
        }

        return index
    }
}