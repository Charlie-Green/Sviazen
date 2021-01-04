package by.zenkevich_churun.findcell.prisoner.ui.sched.fragm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.zenkevich_churun.findcell.core.ui.common.SviazenFragment
import by.zenkevich_churun.findcell.core.util.android.AndroidUtil
import by.zenkevich_churun.findcell.core.util.android.NavigationUtil
import by.zenkevich_churun.findcell.core.util.recycler.autogrid.AutomaticGridLayoutManager
import by.zenkevich_churun.findcell.entity.entity.Cell
import by.zenkevich_churun.findcell.prisoner.R
import by.zenkevich_churun.findcell.prisoner.databinding.ScheduleFragmBinding
import by.zenkevich_churun.findcell.prisoner.ui.celledit.dialog.CellEditorDialog
import by.zenkevich_churun.findcell.prisoner.ui.cellopt.dialog.CellOptionsDialog
import by.zenkevich_churun.findcell.prisoner.ui.common.sched.CellUpdate
import by.zenkevich_churun.findcell.prisoner.ui.common.sched.ScheduleModel
import by.zenkevich_churun.findcell.prisoner.ui.sched.vm.ScheduleViewModel


/** Allows viewing interactive editing of the user's arest [Schedule]. **/
class ScheduleFragment: SviazenFragment<ScheduleFragmBinding>() {
    // ==================================================================================
    // Fields:

    private lateinit var vm: ScheduleViewModel
    private var selectedCellIndex = -1


    // ==================================================================================
    // Lifecycle:

    override fun inflateViewBinding(
        inflater: LayoutInflater
    ) = ScheduleFragmBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initFields()
        initCellsAdapter()
        initDaysAdapter()
        loadSchedule()

        vm.selectedCellIndexLD.observe(viewLifecycleOwner, Observer { cellIndex ->
            selectedCellIndex = cellIndex
            selectCell()
        })
        vm.scheduleLD.observe(viewLifecycleOwner, Observer { schedule ->
            schedule?.also {
                displaySchedule(schedule)
                selectCell()
                vb.buAddCell.isEnabled = true
            } ?: clearSchedule()
        })
        vm.errorLD.observe(viewLifecycleOwner, Observer { message ->
            message?.also { notifyError(it) }
        })
        vm.unsavedChangesLD.observe(viewLifecycleOwner, Observer { thereAreChanges ->
            vb.buSave.isEnabled = thereAreChanges
        })
        vm.loadingLD.observe(viewLifecycleOwner, Observer { isLoading ->
            vb.prBar.isVisible = isLoading
        })
        vm.cellUpdateLD.observe(viewLifecycleOwner, Observer { update ->
            updateCells(update)
        })
        vm.cellOptionsLD.observe(viewLifecycleOwner, Observer { cell ->
            cell?.also {
                suggestOptions(it)
                vm.notifyCellOptionsSuggested()
            }
        })
        vm.cellUpdateRequestLD.observe(viewLifecycleOwner, Observer { cell ->
            cell?.also {
                suggestUpdateCell(it)
                vm.notifyCellUpdateSuggested()
            }
        })

        view.setOnClickListener {
            vm.unselectCell()
        }
        vb.buAddCell.setOnClickListener {
            addCell()
        }
        vb.buSave.setOnClickListener {
            vm.saveSchedule()
        }
    }


    // ==================================================================================
    // Initialization:

    private fun initFields() {
        val appContext = requireContext().applicationContext
        vm = ScheduleViewModel.get(appContext, this)
    }

    private fun initCellsAdapter() {
        vb.recvCells.layoutManager = AutomaticGridLayoutManager(
            requireActivity(),
            dimen(R.dimen.cellview_width)
        )
    }

    private fun initDaysAdapter() {
        val activitySize = AndroidUtil.activitySize(requireActivity())
        vb.recvDays.pivotX = 0.5f*activitySize.width
        vb.recvDays.pivotY = 0.5f*activitySize.height

        val layoutMan = LinearLayoutManager(requireContext())
        val itemDecoration = DividerItemDecoration(requireContext(), layoutMan.orientation)
        vb.recvDays.apply {
            layoutManager = layoutMan
            addItemDecoration(itemDecoration)
        }
    }

    private fun loadSchedule() {
        val args = ScheduleArguments.of(this)
        vm.loadSchedule(args.arestId)
    }


    // ==================================================================================
    // Observers:

    private fun displaySchedule(scheduleModel: ScheduleModel) {
        vb.recvCells.adapter = CellsAdapter(scheduleModel.cells, vm)
        vb.recvDays.adapter = ScheduleDaysAdapter(scheduleModel, vm)
    }

    private fun clearSchedule() {
        vb.recvCells.adapter = CellsAdapter(listOf(), vm)
        daysAdapter?.isEnabled = false
    }

    private fun selectCell() {
        val adapter1 = vb.recvCells.adapter as CellsAdapter? ?: return
        val adapter2 = vb.recvDays.adapter as ScheduleDaysAdapter? ?: return
        val scale = if(selectedCellIndex < 0) 1.0f else 0.9f

        adapter1.selectCellAt(selectedCellIndex)
        adapter2.selectedCellIndex = selectedCellIndex
        vb.recvDays.scaleX = scale
        vb.recvDays.scaleY = scale
    }

    private fun notifyError(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_title)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.setOnDismissListener {
                vm.notifyErrorConsumed()
            }.show()
    }


    private fun addCell() {
        val fragmMan = parentFragmentManager
        CellEditorDialog().apply {
            arguments = CellEditorDialog.arguments(-1, -1)
            show(fragmMan, null)
        }
    }

    private fun updateCells(update: CellUpdate?) {
        val cellsAdapter = vb.recvCells.adapter as CellsAdapter? ?: return

        when(update) {
            is CellUpdate.Added -> {
                cellsAdapter.notifyCellProbablyAdded()
                vm.notifyCellUpdateConsumed()
                vb.recvCells.scrollToPosition(cellsAdapter.itemCount - 1)
            }

            is CellUpdate.Updated -> {
                changeDataset(cellsAdapter)
            }

            is CellUpdate.Deleted -> {
                changeDataset(cellsAdapter)
            }
        }
    }


    private fun suggestOptions(cell: Cell) {
        NavigationUtil.safeNavigate(
            findNavController(),
            R.id.fragmSchedule,
            R.id.dialogCellOptions
        ) { CellOptionsDialog.arguments(cell) }
    }

    private fun suggestUpdateCell(cell: Cell) {
        NavigationUtil.safeNavigate(
            findNavController(),
            R.id.fragmSchedule,
            R.id.dialogCellEdit
        ) { CellEditorDialog.arguments(cell.jailId, cell.number) }
    }

    private fun changeDataset(cellsAdapter: CellsAdapter) {
        cellsAdapter.notifyDataSetChanged()
        vm.notifyCellUpdateConsumed()
        daysAdapter?.notifyDataSetChanged()
    }


    // ==================================================================================
    // Help:

    private val daysAdapter
        get() = vb.recvDays.adapter as ScheduleDaysAdapter?

    private fun dimen(dimenRes: Int): Int
        = resources.getDimensionPixelSize(dimenRes)


    // ==================================================================================
    // Companion:

    companion object {
        fun arguments(arestId: Int)
            = ScheduleArguments.createBundle(arestId)
    }
}