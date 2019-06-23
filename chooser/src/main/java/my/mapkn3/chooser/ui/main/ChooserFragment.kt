package my.mapkn3.chooser.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.fragment.app.ListFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import my.mapkn3.chooser.ChooserActivity
import my.mapkn3.chooser.R
import my.mapkn3.chooser.model.FileSystemModel

class ChooserFragment : ListFragment() {
    companion object {
        private val FOLDER_ICON = R.drawable.ic_folder_black
        private val FILE_ICON = R.drawable.ic_file_black
        private val ITEM_KEY_ICON = "icon"
        private val ITEM_KEY_NAME = "name"

        fun newInstance() = ChooserFragment()
    }

    private lateinit var chooserViewModel: ChooserViewModel
    private lateinit var pathTextView: TextView
    private lateinit var selectCurrentItemButton: Button
    private lateinit var adapter: SimpleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.chooser_fragment, container, false)
        pathTextView = view.findViewById(R.id.pathTextView)
        selectCurrentItemButton = view.findViewById(R.id.selectCurrentItemButton)
        selectCurrentItemButton.setOnClickListener { v -> (activity as ChooserActivity).onResult(getChoice()) }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        chooserViewModel = ViewModelProviders.of(this).get(ChooserViewModel::class.java)
        chooserViewModel.fileSystemModelLiveData.observe(this, Observer { fileSystemModel ->
            pathTextView.text = fileSystemModel.getPathString()

            val items = fileSystemModel.getItemsForCurrentItem()
            if (fileSystemModel.currentItemIsDirectory()) {
                val data = items.map { hashMapOf(
                    ITEM_KEY_ICON to if (it.isDirectory) FOLDER_ICON else FILE_ICON,
                    ITEM_KEY_NAME to it.name
                ) }
                val from = arrayOf(ITEM_KEY_ICON, ITEM_KEY_NAME)
                val to = intArrayOf(R.id.itemIcon, R.id.itemText)
                adapter = SimpleAdapter(this.context, data, R.layout.list_item, from, to)
                listAdapter = adapter
                adapter.notifyDataSetChanged()
            }
        })

        when ((activity as ChooserActivity).mode) {
            FileSystemModel.MODE.FOLDER -> setFolderMode()
            FileSystemModel.MODE.FILE -> setAllMode()
        }

        when ((activity as ChooserActivity).type) {
            FileSystemModel.TYPE.PATH -> setPathType()
            FileSystemModel.TYPE.NAME -> setNameType()
        }
    }

    fun setAllMode() = chooserViewModel.setAllMode()

    fun setFolderMode() = chooserViewModel.setFolderMode()

    fun setPathType() = chooserViewModel.setPathType()

    fun setNameType() = chooserViewModel.setNameType()

    fun selectNext(item: String) = chooserViewModel.selectNextItem(item)

    fun selectPrev() = chooserViewModel.selectPrevItem()

    fun getChoice() = chooserViewModel.getChoice()

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val item: Map<String, Any> = l?.getItemAtPosition(position) as Map<String, Any>
        selectNext(item[ITEM_KEY_NAME] as String)
    }
}