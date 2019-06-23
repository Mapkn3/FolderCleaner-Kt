package my.mapkn3.chooser.model

import android.os.Environment
import java.io.File

class FileSystemModel {
    enum class FILTER {ALL, ONLY_DIRECTORIES}
    companion object {
        val ROOT: String = Environment.getExternalStorageDirectory().absolutePath
    }

    enum class MODE {FOLDER, FILE}
    enum class TYPE {PATH, NAME}

    var fileFilter: FILTER
    var type: TYPE
    private var path: ArrayList<String>
    private var currentItem: File

    init {
        fileFilter = FILTER.ONLY_DIRECTORIES
        type = TYPE.PATH
        path = ArrayList()
        currentItem = File(ROOT)
        selectNextItem(ROOT)
    }

    fun getChoice(): String = when (type) {
        TYPE.PATH -> currentItem.absolutePath
        TYPE.NAME -> currentItem.name
    }

    fun getPathString() = getPathStringFromList(path)

    private fun getPathStringFromList(path: List<String>) = path.joinToString(separator = "/")

    fun selectNextItem(item: String) {
        if (!currentItem.isDirectory) {
            selectPrevItem()
        }
        path.add(item)
        currentItem = File(getPathStringFromList(path))
    }

    fun selectPrevItem(): Boolean {
        if (path.size == 1) {
            return false
        }
        path.removeAt(path.size - 1)
        currentItem = File(getPathStringFromList(path))
        return true
    }

    fun getItemsForCurrentItem(): List<File> {
        val listFiles = currentItem.listFiles(
            when(fileFilter) {
                FILTER.ALL -> File::exists
                FILTER.ONLY_DIRECTORIES -> File::isDirectory
            }
        )
        val items = listFiles?.toList() ?: emptyList<File>()
        return items.sortedWith(Comparator { o1, o2 ->
            var result: Int
            if ((o1.isDirectory && o2.isDirectory) || (o1.isFile && o2.isFile)) {
                result = o1.name.compareTo(o2.name, true)
                if (result == 0) {
                    result = o1.name.compareTo(o2.name)
                }
            } else {
                result = if (o1.equals(o2)) 0 else if (o1.isDirectory) -1 else 1
            }
            result
        })
    }

    fun currentItemIsDirectory(): Boolean = currentItem.isDirectory
}