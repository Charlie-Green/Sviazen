package by.zenkevich_churun.findcell.result.ui.root

import by.zenkevich_churun.findcell.core.ui.common.SviazenActivity
import by.zenkevich_churun.findcell.result.databinding.ResultActivityBinding


class ResultActivity: SviazenActivity<ResultActivityBinding>() {

    override fun inflateViewBinding()
        = ResultActivityBinding.inflate(layoutInflater)
}