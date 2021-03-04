package by.sviazen.prisoner.ui.common.model

import by.sviazen.entity.entity.Cell
import by.sviazen.entity.entity.SchedulePeriod
import by.sviazen.prisoner.ui.common.sched.*
import by.sviazen.prisoner.ui.common.sched.cell.CellModel
import by.sviazen.prisoner.ui.common.sched.period.ScheduleModel
import by.sviazen.prisoner.ui.common.sched.period.SchedulePeriodModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*


class ScheduleModelTest {

    @Test
    fun fromSchedule() {
        val cell1 = CellModel(1, "Жодино", 7, 0, 0, 0, 0)
        val cell2 = CellModel(2, "Окрестина", 14, 0, 0, 0, 0)

        val schedule = TestSchedule(
            1,
            calendar(2020, 11, 18),
            calendar(2020, 11, 22),
            listOf(cell1, cell2),
            listOf(
                SchedulePeriodModel(
                    calendar(2020, 11, 18),
                    calendar(2020, 11, 21),
                    1
                ),

                SchedulePeriodModel(
                    calendar(2020, 11, 21),
                    calendar(2020, 11, 22),
                    0
                )
            )
        )

        val model = ScheduleModel.from(schedule)

        assertDay(model, 0, cell2)
        assertDay(model, 3, cell1, cell2)
        assertDay(model, 4, cell1)
    }


    @Test
    fun toSchedule() {
        val schedule = TestSchedule(
            1,
            calendar(2020, 11, 18),
            calendar(2020, 11, 22),
            listOf( CellModel(1, "Жодино", 31, 0, 0, 0, 0) ),
            listOf()
        )

        val model = ScheduleModel.from(schedule)
        model.markDayWithCell(0, calendar(2020, 11, 19))
        model.markDayWithCell(0, calendar(2020, 11, 21))
        model.markDayWithCell(0, calendar(2020, 11, 22))

        val result = model.toSchedule()
        Assertions.assertEquals(2, result.periods.size)
        assertPeriodExists(result.periods, 2020, 11, 19, 2020, 11, 19)
        assertPeriodExists(result.periods, 2020, 11, 21, 2020, 11, 22)
    }


    private fun calendar(year: Int, month: Int, date: Int): Calendar {
        return Calendar.getInstance().apply {
            set(year, month, date)
        }
    }

    private fun assertDay(
        where: ScheduleModel,
        dayIndex: Int,
        vararg cells: Cell ) {

        val day = where.dayAt(dayIndex)
        val actualCellCount = day.dayData.count { it == '.' } + 1
        Assertions.assertEquals(cells.size, actualCellCount)

        for(cell in cells) {
            Assertions.assertTrue( day.dayData.contains(cell.toString()) )
        }
    }

    private fun calendarIs(cal: Calendar, year: Int, month: Int, day: Int): Boolean {
        return (cal[Calendar.YEAR] == year) &&
            (cal[Calendar.MONTH] == month) &&
            (cal[Calendar.DATE] == day)
    }

    private fun assertPeriodExists(
        periods: List<SchedulePeriod>,
        year1: Int, month1: Int, date1: Int,
        year2: Int, month2: Int, date2: Int ) {

        val period = periods.find { p ->
            calendarIs(p.startDate, year1, month1, date1) &&
            calendarIs(p.endDate, year2, month2, date2)
        }

        Assertions.assertNotNull(period)
    }
}