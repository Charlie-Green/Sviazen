package by.sviazen.core.util.std


object CollectionUtil {

    fun <T> copyList(original: List<T>): MutableList<T> {
        return MutableList(original.size) { index ->
            original[index]
        }
    }
}