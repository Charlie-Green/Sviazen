package by.sviazen.prisoner.repo.arest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.sviazen.domain.entity.Arest
import by.sviazen.domain.entity.LightArest
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Comparator
import kotlin.collections.HashSet


/** Keeps current list of [Arest]s in RAM.
  * Provides methods to update cache after changes were made remotely.
  * Thread-safe. **/
@Singleton
class ArestsCache @Inject constructor() {

    private val mldArests = MutableLiveData< List<Arest> >().apply {
        value = mutableListOf()
    }

    val arestsLD: LiveData< List<Arest> >
        get() = mldArests

    val cachedList: List<Arest>
        get() = synchronized(arests) {
            arests
        }


    fun getById(
        arestId: Int
    ): Arest? = arestsLD.value?.find { arest ->
        arest.id == arestId
    }

    fun submit(list: List<Arest>) {
        updateArests {
            arests.clear()
            arests.addAll(list)
            arests.sortWith(ArestsComparator)
        }
    }


    /** @return index in the list where the [Arest] was inserted. **/
    fun insert(newArest: Arest): Int {
        updateArests {
            val position = positionFor(newArest)
            arests.add(position, newArest)
            return position
        }
    }

    /** @return pair of old index and new index **/
    fun update(updatedArest: Arest): Pair<Int, Int> {
        updateArests {
            val oldPosition = findArest(updatedArest.id)
            arests.removeAt(oldPosition)

            val newPosition = positionFor(updatedArest)
            arests.add(newPosition, updatedArest)

            return oldPosition to newPosition
        }
    }


    /** @return [LinkedList] of list positions which the items have been deleted from. **/
    fun delete(ids: HashSet<Int>): LinkedList<Int> {
        if(ids.isEmpty()) {
            return LinkedList()
        }

        val deletedPositions = LinkedList<Int>()

        updateArests {
            val oldArests = List(arests.size) { index ->
                arests[index]
            }
            arests.clear()

            for(j in oldArests.indices) {
                val arest = oldArests[j]

                if(ids.contains(arest.id)) {
                    // Delete this arest.
                    deletedPositions.add(j)
                } else {
                    // Leave it.
                    arests.add(arest)
                }
            }
        }

        return deletedPositions
    }

    /** Makes the [Arest]s list empty. **/
    fun clear() {
        updateArests {
            arests.clear()
        }
    }


    private val arests: MutableList<Arest>
        get() = mldArests.value as MutableList<Arest>


    private fun findArest(id: Int): Int {
        val position = arests.indexOfFirst { existingArest ->
            existingArest.id == id
        }

        if(position !in arests.indices) {
            throw IllegalArgumentException("No Arest with ID $id")
        }
        return position
    }

    private fun positionFor(newArest: Arest): Int {
        val position = arests.indexOfFirst { existingArest ->
            ArestsComparator.compare(newArest, existingArest) < 0
        }

        if(position in arests.indices) {
            return position
        }
        return arests.size
    }

    private inline fun <T> updateArests(
        updateList: () -> T
    ): T {
        synchronized(mldArests) {
            val result = updateList()
            mldArests.postValue(mldArests.value)  // Re-emit
            return result
        }
    }


    private object ArestsComparator: Comparator<LightArest> {

        override fun compare(a: LightArest, b: LightArest): Int {
            if(a.start != b.start) {
                return a.start.compareTo(b.start)
            }
            return a.end.compareTo(b.end)
        }
    }
}