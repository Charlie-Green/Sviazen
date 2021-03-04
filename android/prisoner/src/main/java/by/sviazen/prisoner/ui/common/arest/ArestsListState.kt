package by.sviazen.prisoner.ui.common.arest

import by.sviazen.domain.entity.Arest


sealed class ArestsListState {

    object Idle: ArestsListState()
    object Loading: ArestsListState()

    class Loaded(
        val arests: List<Arest>,
    ): ArestsListState() {

        var animated = false

        /** Contains IDs of those [Arest]s which the user marked.
          * Later, these items can be deleted. **/
        var checkedIds = hashSetOf<Int>()
    }

    class NetworkError: ArestsListState() {
        var notified = false
    }

    class NoInternet: ArestsListState() {
        var notified = false
    }
}