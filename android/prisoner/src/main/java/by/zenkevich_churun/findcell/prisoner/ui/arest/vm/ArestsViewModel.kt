package by.zenkevich_churun.findcell.prisoner.ui.arest.vm

import android.content.Context
import androidx.lifecycle.*
import by.zenkevich_churun.findcell.prisoner.repo.arest.ArestsRepository
import by.zenkevich_churun.findcell.prisoner.repo.arest.GetArestsResult
import by.zenkevich_churun.findcell.prisoner.ui.arest.state.ArestsListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class ArestsViewModel @Inject constructor(
    private val repo: ArestsRepository
): ViewModel() {

    private val mldListState = MutableLiveData<ArestsListState>().apply {
        value = ArestsListState.Idle
    }


    val listStateLD: LiveData<ArestsListState>
        get() = mldListState


    fun loadData() {
        if(mldListState.value !is ArestsListState.Idle) {
            return
        }
        mldListState.value = ArestsListState.Loading

        // TODO: Check internet.

        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.arestsList()
            applyResult(result)
        }
    }


    private fun applyResult(result: GetArestsResult) {

        when(result) {
            is GetArestsResult.Success -> {
                val newState = ArestsListState.Loaded(result.arests)
                mldListState.postValue(newState)
            }

            is GetArestsResult.NetworkError -> {
                mldListState.postValue( ArestsListState.NetworkError() )
            }

            is GetArestsResult.NotAuthorized -> {
                // TODO: Navigate to authorization screen.
                mldListState.postValue(ArestsListState.Idle)
            }
        }
    }


    companion object {

        fun get(
            appContext: Context,
            storeOwner: ViewModelStoreOwner
        ): ArestsViewModel {

            val fact = ArestsVMFactory.get(appContext)
            val provider = ViewModelProvider(storeOwner, fact)
            return provider.get(ArestsViewModel::class.java)
        }
    }
}