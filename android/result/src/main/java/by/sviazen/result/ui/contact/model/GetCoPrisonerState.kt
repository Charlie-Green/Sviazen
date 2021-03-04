package by.sviazen.result.ui.contact.model

import by.sviazen.domain.entity.Contact


sealed class GetCoPrisonerState {

    object Idle: GetCoPrisonerState()
    object Loading: GetCoPrisonerState()

    class Success(
        val id: Int,
        val name: String,
        val contacts: List<Contact>,
        val info: String
    ): GetCoPrisonerState()

    sealed class Error: GetCoPrisonerState() {
        var dialogConsumed = false
        var containerConsumed = false

        class NoInternet: GetCoPrisonerState.Error()
        class Network: GetCoPrisonerState.Error()

        class NotConnected(
            val prisonerName: String
        ): GetCoPrisonerState.Error()
    }
}