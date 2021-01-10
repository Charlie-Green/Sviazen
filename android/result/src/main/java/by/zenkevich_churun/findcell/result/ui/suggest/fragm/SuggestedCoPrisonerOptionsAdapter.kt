package by.zenkevich_churun.findcell.result.ui.suggest.fragm

import android.util.Log
import by.zenkevich_churun.findcell.entity.entity.CoPrisoner
import by.zenkevich_churun.findcell.result.R
import by.zenkevich_churun.findcell.result.ui.shared.cps.CoPrisonerOptionsAdapter


internal class SuggestedCoPrisonerOptionsAdapter: CoPrisonerOptionsAdapter {
    override fun label1(relation: CoPrisoner.Relation): Int {
        if(relation == CoPrisoner.Relation.OUTCOMING_REQUEST) {
            return R.string.cpoption_cancel_request
        }
        return R.string.cpoption_connect
    }

    override fun label2(relation: CoPrisoner.Relation): Int {
        return 0  // Only 1 option here.
    }

    override fun onSelected1(relation: CoPrisoner.Relation, position: Int) {
        if(relation == CoPrisoner.Relation.OUTCOMING_REQUEST) {
            Log.v("CharlieDebug", "Cancel request. Position = $position")
        } else {
            Log.v("CharlieDebug", "Connect. Position = $position")
        }
    }
}