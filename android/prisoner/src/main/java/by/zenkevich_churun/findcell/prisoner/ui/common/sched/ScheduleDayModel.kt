
package by.zenkevich_churun.findcell.prisoner.ui.common.sched

import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.*


class ScheduleDayModel(
    val date: Calendar,
    val dayData: String,
    val textColor: Int,
    val backColors: List<Int> ) {

    /** [date] and [dayData] merged and formatted using HTML tags.
      * The result is to be displayed on UI. **/
    val fullHtml by lazy { buildFullHtml() }


    private fun buildFullHtml(): String {
        val dateString = dateFormat.format(date.time)
        return "<b>$dateString</b>: $dayData"
    }


    companion object {
        /** In this case, use the theme-defined text color. **/
        const val UNDEFINED_COLOR = Color.TRANSPARENT

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    }
}