package by.sviazen.prisoner.ui.celledit.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.updateLayoutParams
import by.sviazen.core.ui.common.SviazenDialog
import by.sviazen.core.util.android.AndroidUtil
import by.sviazen.prisoner.R
import by.sviazen.prisoner.databinding.CellEditDialogBinding
import by.sviazen.prisoner.ui.celledit.vm.CellEditorViewModel
import by.sviazen.prisoner.ui.common.sched.cell.CellEditFailureReason
import by.sviazen.prisoner.ui.common.sched.cell.CellModel
import by.sviazen.prisoner.ui.common.sched.period.ScheduleCellsCrudState


class CellEditorDialog: SviazenDialog<CellEditDialogBinding>() {
    private var vm: CellEditorViewModel? = null


    override fun inflateViewBinding(
        inflater: LayoutInflater
    ) = CellEditDialogBinding.inflate(inflater)

    override fun customizeDialog(view: View) {
        resizeDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val appContext = view.context.applicationContext
        val vm = CellEditorViewModel
            .get(appContext, this)
            .also { this.vm = it }

        vm.notifyUiShowing()

        vm.cellCrudStateLD.observe(viewLifecycleOwner, { state ->
            renderState(state)
        })

        vb.buSave.setOnClickListener {
            submitDraft()
            vm.save()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        submitDraft()
        if(activity?.isChangingConfigurations == false) {
            vm?.notifyUiDismissed()
        }

        super.onDismiss(dialog)
    }


    private fun resizeDialog() {
        val activitySize = AndroidUtil.activitySize(requireActivity())
        vb.vlltContent.updateLayoutParams {
            width = (activitySize.width*4)/5
        }
    }


    private fun renderState(state: ScheduleCellsCrudState) {
        if(state is ScheduleCellsCrudState.Processing) {
            dismiss()
            return
        }

        if(state !is ScheduleCellsCrudState.Editing) {
            return
        }

        renderEditor(state)
        when(state) {
            is ScheduleCellsCrudState.Editing.AddFailed -> {
                notifyError(
                    state.reason,
                    state.selectedJail?.name ?: "",
                    state.cellNumber,
                    R.string.add_cell_failed_msg
                )
            }

            is ScheduleCellsCrudState.Editing.UpdateFailed -> {
                notifyError(
                    state.reason,
                    state.selectedJail?.name ?: "",
                    state.cellNumber,
                    R.string.update_cell_failed_msg
                )
            }
        }
    }

    private fun renderEditor(state: ScheduleCellsCrudState.Editing) {
        vb.txtvError.visibility = View.GONE

        vb.spJail.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            state.jails
        )
        vb.spJail.setSelection(state.jailIndex)

        vb.numpCellNumber.apply {
            minValue = 1
            maxValue = state.jails[state.jailIndex].cellCount.toInt()
            value = state.cellNumber.toInt()
        }

        vb.spJail.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long ) {

                vb.numpCellNumber.maxValue = state.jails[position].cellCount.toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {   }
        }

        vb.buSave.setText(
            if(state is ScheduleCellsCrudState.Editing.Adding) R.string.add
            else R.string.save )
    }

    private fun notifyError(message: String) {
        vb.txtvError.visibility = View.VISIBLE
        vb.txtvError.text = message
    }

    private fun notifyError(messageRes: Int)
        = notifyError( getString(messageRes) )

    private fun notifyError(
        reason: CellEditFailureReason,
        jailName: String,
        cellNumber: Short,
        networkErrorMessageRes: Int ) {

        val msg = when(reason) {
            CellEditFailureReason.NETWORK_ERROR -> {
                getString(networkErrorMessageRes)
            }

            CellEditFailureReason.DUPLICATE -> {
                val cellString = CellModel.toString(jailName, cellNumber)
                getString(R.string.cell_duplicate_msg, cellString)
            }
        }

        notifyError(msg)
    }


    private fun submitDraft() {
        vm?.submitEditorState(
            vb.spJail.selectedItemPosition,
            vb.numpCellNumber.value.toShort()
        )
    }
}