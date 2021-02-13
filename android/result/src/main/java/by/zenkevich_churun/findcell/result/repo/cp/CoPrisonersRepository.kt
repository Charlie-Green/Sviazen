package by.zenkevich_churun.findcell.result.repo.cp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import by.zenkevich_churun.findcell.core.api.cp.CoPrisonersApi
import by.zenkevich_churun.findcell.core.common.prisoner.ExtendedPrisoner
import by.zenkevich_churun.findcell.core.common.prisoner.PrisonerStorage
import by.zenkevich_churun.findcell.entity.entity.CoPrisoner
import by.zenkevich_churun.findcell.result.db.CoPrisonersDatabase
import by.zenkevich_churun.findcell.result.db.dao.CoPrisonersDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CoPrisonersRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val prisonerStore: PrisonerStorage,
    private val cpApi: CoPrisonersApi ) {

    private var ldSuggested: LiveData< List<CoPrisoner> >? = null
    private var ldConnected: LiveData< List<CoPrisoner> >? = null
    private var ldRequests:  LiveData< List<CoPrisoner> >? = null


    /** [CoPrisoner]s with [CoPrisoner.Relation.SUGGESTED]
      * and [CoPrisoner.Relation.OUTCOMING_REQUEST]. **/
    fun suggestedLD(scope: CoroutineScope): LiveData< List<CoPrisoner> > {
        return ldSuggested ?: synchronized(this) {
            ldSuggested ?: CoPrisonersMediatorLiveData.Suggested(
                appContext,
                scope
            ).also { ldSuggested = it }
        }
    }


    /** [CoPrisoner]s with [CoPrisoner.Relation.CONNECTED]. **/
    fun connectedLD(scope: CoroutineScope): LiveData< List<CoPrisoner> > {
        return ldConnected ?: synchronized(this) {
            ldConnected ?: CoPrisonersMediatorLiveData.Connected(
                appContext,
                scope
            ).also { ldConnected = it }
        }
    }


    /** [CoPrisoner]s with [CoPrisoner.Relation.INCOMING_REQUEST]. **/
    fun requestsLD(scope: CoroutineScope): LiveData< List<CoPrisoner> > {
        return ldRequests ?: synchronized(this) {
            ldRequests ?: CoPrisonersMediatorLiveData.Requests(
                appContext,
                scope
            ).also { ldRequests = it }
        }
    }


    /** @return the new [CoPrisoner.Relation], or null if network request failed. **/
    fun sendConnectRequest(
        coPrisonerId: Int
    ): CoPrisoner.Relation? = sendRequest(coPrisonerId) { prisoner ->

        cpApi.connect(
            prisoner.id,
            prisoner.passwordHash,
            coPrisonerId
        )
    }

    /** @return the new [CoPrisoner.Relation], or null if network request failed. **/
    fun cancelConnectRequest(
        coPrisonerId: Int
    ): CoPrisoner.Relation? = sendRequest(coPrisonerId) { prisoner ->

        cpApi.disconnect(
            prisoner.id,
            prisoner.passwordHash,
            coPrisonerId
        )
    }


    private val dao: CoPrisonersDao
        get() = CoPrisonersDatabase.get(appContext).dao

    private inline fun sendRequest(
        cpId: Int,
        doNetworkCall: (ExtendedPrisoner) -> CoPrisoner.Relation
    ): CoPrisoner.Relation? {
        val prisoner = prisonerStore.prisonerLD.value ?: return null

        val newRelation = try {
            doNetworkCall(prisoner)
        } catch(exc: IOException) {
            Log.w(LOGTAG, "Failed to send connect request: ${exc.javaClass.name}: ${exc.message}")
            return null
        }

        dao.updateRelation(cpId, newRelation)
        return newRelation
    }


    companion object {
        private const val LOGTAG = "FindCell-CoPrisoner"
    }
}