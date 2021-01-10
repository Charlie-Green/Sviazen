package by.zenkevich_churun.findcell.result.ui.connect.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent


internal class ConnectedCoPrisonersVMFactory(
    private val appContext: Context
): ViewModelProvider.Factory {

    @EntryPoint
    @InstallIn(ApplicationComponent::class)
    interface SuggestedCoPrisonersEntryPoint {
        val connectedCoPrisonersViewModel: ConnectedCoPrisonersViewModel
    }


    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        val entryClass = SuggestedCoPrisonersEntryPoint::class.java
        val accessor = EntryPointAccessors.fromApplication(appContext, entryClass)
        return accessor.connectedCoPrisonersViewModel as T
    }


    companion object {
        private var instance: ConnectedCoPrisonersVMFactory? = null

        fun get(appContext: Context): ConnectedCoPrisonersVMFactory {
            return instance ?: synchronized(this) {
                instance ?: ConnectedCoPrisonersVMFactory(appContext).also {
                    instance = it
                }
            }
        }
    }
}