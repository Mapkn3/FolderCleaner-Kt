package my.mapkn3.chooser.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import my.mapkn3.chooser.model.FileSystemModel

class ChooserViewModel : ViewModel() {
    private var fileSystemModel = FileSystemModel()
    var fileSystemModelLiveData = MutableLiveData<FileSystemModel>()

    init {
        fileSystemModelLiveData.value = fileSystemModel
    }

    fun setAllMode() {
        fileSystemModel.fileFilter = FileSystemModel.FILTER.ALL
        fileSystemModelLiveData.value = fileSystemModel
    }

    fun setFolderMode() {
        fileSystemModel.fileFilter = FileSystemModel.FILTER.ONLY_DIRECTORIES
        fileSystemModelLiveData.value = fileSystemModel
    }

    fun setPathType() {
        fileSystemModel.type = FileSystemModel.TYPE.PATH
        fileSystemModelLiveData.value = fileSystemModel
    }

    fun setNameType() {
        fileSystemModel.type = FileSystemModel.TYPE.NAME
        fileSystemModelLiveData.value = fileSystemModel
    }

    fun selectNextItem(item: String) {
        fileSystemModel.selectNextItem(item)
        fileSystemModelLiveData.value = fileSystemModel
    }

    fun selectPrevItem(): Boolean {
        val result = fileSystemModel.selectPrevItem()
        fileSystemModelLiveData.value = fileSystemModel
        return result
    }

    fun getChoice() = fileSystemModel.getChoice()
}