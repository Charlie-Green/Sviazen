package by.zenkevich_churun.findcell.prisoner.ui.arest.fragm

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.zenkevich_churun.findcell.prisoner.R
import by.zenkevich_churun.findcell.prisoner.ui.arest.state.ArestsListState
import by.zenkevich_churun.findcell.prisoner.ui.arest.vm.ArestsViewModel
import kotlinx.android.synthetic.main.arests_fragm.*
import javax.inject.Inject


class ArestsFragment: Fragment(R.layout.arests_fragm) {

    @Inject
    lateinit var vm: ArestsViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initFields()
        initRecycler()

        vm.loadData()
        vm.listStateLD.observe(viewLifecycleOwner, Observer { state ->
            renderState(state)
        })
    }


    private fun initFields() {
        val appContext = requireContext().applicationContext
        vm = ArestsViewModel.get(appContext, this)
    }

    private fun initRecycler() {
        recvArests.layoutManager = LinearLayoutManager(requireContext())
        recvArests.adapter = ArestsAdapter()
    }


    private fun renderState(state: ArestsListState) {
        if(state is ArestsListState.Loading) {
            prBar.visibility = View.VISIBLE
            return
        }
        prBar.visibility = View.GONE

        when(state) {
            is ArestsListState.Loaded -> {
                val adapter = recvArests.adapter as ArestsAdapter
                adapter.submitList(state.arests)
            }

            is ArestsListState.NoInternet -> {
                if(!state.consumed) {
                    notifyError(R.string.no_internet_title, R.string.arests_need_internet_msg) {
                        state.consumed = true
                    }
                }
            }

            is ArestsListState.NetworkError -> {
                if(!state.consumed) {
                    notifyError(R.string.error_title, R.string.get_arests_failed_msg) {
                        state.consumed = true
                    }
                }
            }
        }
    }


    private fun notifyError(
        titleRes: Int,
        messageRes: Int,
        onDismiss: (DialogInterface) -> Unit ) {

        AlertDialog.Builder(requireContext())
            .setTitle(titleRes)
            .setMessage(messageRes)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.setOnDismissListener(onDismiss)
            .show()
    }
}