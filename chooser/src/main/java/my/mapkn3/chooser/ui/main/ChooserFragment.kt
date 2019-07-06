package my.mapkn3.chooser.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.fragment.app.ListFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.chooser_fragment.*
import kotlinx.android.synthetic.main.chooser_fragment.view.*
import my.mapkn3.chooser.R
import my.mapkn3.chooser.model.FileSystemModel

class ChooserFragment(
    private val mode: FileSystemModel.MODE,
    private val type: FileSystemModel.TYPE
) : ListFragment() {
    companion object {
        private val FOLDER_ICON = R.drawable.ic_folder_black
        private val FILE_ICON = R.drawable.ic_file_black
        private val ITEM_KEY_ICON = "icon"
        private val ITEM_KEY_NAME = "name"

        fun newInstance(mode: FileSystemModel.MODE, type: FileSystemModel.TYPE) =
            ChooserFragment(mode, type)
    }

    private lateinit var chooserFragmentListener: ChooserFragmentListener
    private lateinit var chooserViewModel: ChooserViewModel
    private lateinit var adapter: SimpleAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ChooserFragmentListener) {
            chooserFragmentListener = context
        } else {
            throw ClassCastException("$context must implement ChooserFragmentListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.chooser_fragment, container, false)
        fragment.selectCurrentItemButton.setOnClickListener {
            chooserFragmentListener.onSelectClick(
                getChoice()
            )
        }
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        chooserViewModel = ViewModelProviders.of(this).get(ChooserViewModel::class.java)
        chooserViewModel.fileSystemModelLiveData.observe(this, Observer { fileSystemModel ->
            pathTextView.text = fileSystemModel.getPathString()

            if (fileSystemModel.currentItemIsDirectory()) {
                val data = fileSystemModel.getItemsForCurrentItem().map {
                    hashMapOf(
                        ITEM_KEY_ICON to if (it.isDirectory) FOLDER_ICON else FILE_ICON,
                        ITEM_KEY_NAME to it.name
                    )
                }
                val from = arrayOf(ITEM_KEY_ICON, ITEM_KEY_NAME)
                val to = intArrayOf(R.id.itemIcon, R.id.itemText)
                adapter = SimpleAdapter(context, data, R.layout.chooser_list_item, from, to)
                listAdapter = adapter
                adapter.notifyDataSetChanged()
            }
        })

        when (mode) {
            FileSystemModel.MODE.FOLDER -> setFolderMode()
            FileSystemModel.MODE.FILE -> setAllMode()
        }

        when (type) {
            FileSystemModel.TYPE.PATH -> setPathType()
            FileSystemModel.TYPE.NAME -> setNameType()
        }
    }

    private fun setAllMode() = chooserViewModel.setAllMode()

    private fun setFolderMode() = chooserViewModel.setFolderMode()

    private fun setPathType() = chooserViewModel.setPathType()

    private fun setNameType() = chooserViewModel.setNameType()

    private fun selectNext(item: String) = chooserViewModel.selectNextItem(item)

    fun selectPrev() = chooserViewModel.selectPrevItem()

    private fun getChoice() = chooserViewModel.getChoice()

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val item: Map<String, Any> = l?.getItemAtPosition(position) as Map<String, Any>
        selectNext(item[ITEM_KEY_NAME] as String)
    }

    interface ChooserFragmentListener {
        fun onSelectClick(result: String)
    }
}