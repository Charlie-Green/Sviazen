package by.zenkevich_churun.findcell.prisoner.ui.sched.vm

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.*
import by.zenkevich_churun.findcell.core.entity.sched.Schedule
import by.zenkevich_churun.findcell.core.util.android.AndroidUtil
import by.zenkevich_churun.findcell.prisoner.repo.sched.GetScheduleResult
import by.zenkevich_churun.findcell.prisoner.repo.sched.ScheduleRepository
import by.zenkevich_churun.findcell.prisoner.repo.sched.UpdateScheduleResult
import by.zenkevich_churun.findcell.prisoner.ui.common.vm.PrisonerLiveDatasStorage
import by.zenkevich_churun.findcell.prisoner.ui.sched.model.ScheduleModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class ScheduleViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    private val repo: ScheduleRepository,
    private val store: PrisonerLiveDatasStorage
): ViewModel() {

    private val mapping = ScheduleVMMapping(appContext)

    private val mldSelectedCellIndex = MutableLiveData<Int>()
    private val mldSchedule = MutableLiveData<ScheduleModel>()
    private val mldError = MutableLiveData<String?>()
    private val mldChanges = MutableLiveData<Boolean>()
    private val mldLoading = MutableLiveData<Boolean>()


    init {
        AndroidUtil.whenInternetAvailable(appContext) { netMan, callback ->
            if(mldSchedule.value == null) {
                getSchedule(netMan, callback)
            }
        }
    }


    val selectedCellIndexLD: LiveData<Int>
        get() = mldSelectedCellIndex

    val scheduleLD: LiveData<ScheduleModel>
        get() = mldSchedule

    val errorLD: LiveData<String?>
        get() = mldError

    val unsavedChangesLD: LiveData<Boolean>
        get() = mldChanges

    val loadingLD: LiveData<Boolean>
        get() = mldLoading


    fun selectCell(cellIndex: Int) {
        mldSelectedCellIndex.value = cellIndex
    }

    fun unselectCell() {
        mldSelectedCellIndex.value = -1
    }

    fun notifyErrorConsumed() {
        mldError.value = null
    }

    fun notifyScheduleChanged() {
        mldChanges.value = true
    }


    fun saveSchedule() {
        val scheduleModel = mldSchedule.value ?: return

        mldLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val schedule = scheduleModel.toSchedule()
            updateSchedule(schedule)
            mldLoading.postValue(false)
        }
    }


    private fun getSchedule(
        netMan: ConnectivityManager,
        callback: ConnectivityManager.NetworkCallback ) {

        mldLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repo.getSchedule()) {
                is GetScheduleResult.Success -> {
                    netMan.unregisterNetworkCallback(callback)

                    val scheduleModel = ScheduleModel.from(result.schedule)
                    mldSchedule.postValue(scheduleModel)
                }

                is GetScheduleResult.Failed -> {
                    mldError.postValue(mapping.getFailedMessage)
                }
            }

            mldLoading.postValue(false)
        }
    }

    private fun updateSchedule(schedule: Schedule) {
        val result = repo.updateSchedule(schedule)

        if(result is UpdateScheduleResult.Success) {
            store.submitUpdateScheduleSuccess()
            mldChanges.postValue(false)
        } else {
            mldError.postValue(mapping.updateFailedMessage)
        }
    }


    companion object {

        fun get(
            appContext: Context,
            storeOwner: ViewModelStoreOwner
        ): ScheduleViewModel {

            val fact = ScheduleVMFactory.get(appContext)
            val provider = ViewModelProvider(storeOwner, fact)
            return provider.get(ScheduleViewModel::class.java)
        }
    }
}