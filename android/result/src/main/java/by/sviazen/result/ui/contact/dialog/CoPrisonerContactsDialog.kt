package by.sviazen.result.ui.contact.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import by.sviazen.core.ui.common.SviazenDialog
import by.sviazen.core.util.view.contact.ContactView
import by.sviazen.domain.entity.Contact
import by.sviazen.result.R
import by.sviazen.result.databinding.CoprisonerContactsDialogBinding
import by.sviazen.result.ui.contact.model.GetCoPrisonerState
import by.sviazen.result.ui.contact.vm.CoPrisonerContactsViewModel


class CoPrisonerContactsDialog: SviazenDialog<CoprisonerContactsDialogBinding>() {

    private lateinit var vm: CoPrisonerContactsViewModel


    override fun inflateViewBinding(
        inflater: LayoutInflater
    ) = CoprisonerContactsDialogBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFields()

        val args = CoPrisonerContactsArguments(requireArguments())

        vm.loadPrisoner(args.coprisonerId, args.coprisonerName)
        vm.stateLD.observe(viewLifecycleOwner) { state ->
            displayData(state)
        }
    }


    private fun initFields() {
        val appContext = requireContext().applicationContext
        vm = CoPrisonerContactsViewModel.get(appContext, this)
    }


    private fun displayData(state: GetCoPrisonerState) {
        if(state is GetCoPrisonerState.Loading) {
            vb.prBar.visibility = View.VISIBLE
            return
        }
        if(state is GetCoPrisonerState.Error) {
            state.dialogConsumed = true
            findNavController().navigateUp()
            return
        }
        if(state !is GetCoPrisonerState.Success) {
            return
        }
        vb.prBar.visibility = View.GONE

        // Remove previous ContactViews, if any:
        while(vb.root.childCount > 2) {
            vb.root.removeViewAt(1)
        }

        // Add the new ones:
        for(contact in state.contacts) {
            addContactView(contact)
        }

        // Update name and info:
        vb.txtvName.text = state.name
        vb.txtvInfo.text = state.info
    }

    private fun addContactView(contact: Contact) {
        val gap = resources.getDimensionPixelSize(R.dimen.coprisoner_contacts_gap)

        val params = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            topMargin = gap
            bottomMargin = gap
        }

        val view = ContactView(requireContext())
        vb.root.addView(view, 1, params)
        view.show(contact)
    }


    companion object {

        fun arguments(
            coPrisonerId: Int,
            coPrisonerName: String

        ): Bundle = CoPrisonerContactsArguments.createBundle(
            coPrisonerId,
            coPrisonerName
        )
    }
}