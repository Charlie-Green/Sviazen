package by.sviazen.prisoner.ui.root.vm

import android.content.Context
import androidx.lifecycle.*
import by.sviazen.core.injected.cp.CoPrisonersRepository
import by.sviazen.domain.entity.Prisoner
import by.sviazen.prisoner.repo.profile.ProfileRepository
import by.sviazen.prisoner.repo.profile.SavePrisonerResult
import by.sviazen.prisoner.ui.common.arest.ArestLiveDatasHolder
import by.sviazen.prisoner.ui.common.arest.ArestsListState
import by.sviazen.prisoner.ui.common.change.UnsavedChangesLiveDatasStorage
import by.sviazen.prisoner.ui.common.interrupt.InterruptLiveDataStorage
import by.sviazen.prisoner.ui.common.interrupt.EditInterruptState
import by.sviazen.prisoner.ui.common.sched.period.ScheduleCellsCrudState
import by.sviazen.prisoner.ui.common.sched.ld.ScheduleLiveDatasStorage
import by.sviazen.prisoner.ui.sched.model.ScheduleCrudState
import by.sviazen.result.ui.contact.model.GetCoPrisonerState
import by.sviazen.result.ui.contact.vm.CoPrisonerStateLDStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class PrisonerRootViewModel @Inject constructor(
    private val repo: ProfileRepository,
    private val cpRepo: CoPrisonersRepository,
    private val scheduleStore: ScheduleLiveDatasStorage,
    private val interruptStore: InterruptLiveDataStorage,
    unsavedChangesStore: UnsavedChangesLiveDatasStorage,
    private val arestHolder: ArestLiveDatasHolder,
    private val coPrisonerStore: CoPrisonerStateLDStorage
): ViewModel() {

    val prisonerLD: LiveData<out Prisoner>
        get() = repo.prisonerLD

    val savePrisonerResultLD: LiveData<SavePrisonerResult>
        get() = repo.savePrisonerResultLD

    val scheduleCrudStateLD: LiveData<ScheduleCrudState>
        get() = scheduleStore.scheduleCrudStateLD

    val cellCrudStateLD: LiveData<ScheduleCellsCrudState>
        get() = scheduleStore.cellsCrudStateLD

    val editInterruptStateLD: LiveData<EditInterruptState>
        get() = interruptStore.stateLD

    val unsavedChangesLD: LiveData<Boolean>
        = UnsavedPrisonerChangesLiveData(unsavedChangesStore, repo)

    val coPrisonerStateLD: LiveData<GetCoPrisonerState>
        get() = coPrisonerStore.stateLD


    var lastDestination: Int
        get() { return PrisonerRootVMStorage.lastDestination }
        set(value) { PrisonerRootVMStorage.lastDestination = value }


    fun notifySaveResultConsumed()
        = repo.notifySaveResultConsumed()

    fun notifyEditInterrupted(currentDest: Int, desiredDest: Int)
        = interruptStore.interrupt(currentDest, desiredDest)

    fun notifyInterruptConfirmationConsumed()
        = interruptStore.notifyConfirmationConsumed()

    fun navigateTo(currentDest: Int, desiredDest: Int)
        = interruptStore.navigate(currentDest, desiredDest)

    fun logOut() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.logOut()
            arestHolder.submitState(ArestsListState.Idle)
            scheduleStore.clearSchedule()
        }
    }


    companion object {

        fun get(
            appContext: Context,
            storeOwner: ViewModelStoreOwner
        ): PrisonerRootViewModel {

            val fact = PrisonerRootVMFactory.get(appContext)
            val provider = ViewModelProvider(storeOwner, fact)
            return provider.get(PrisonerRootViewModel::class.java)
        }
    }
}