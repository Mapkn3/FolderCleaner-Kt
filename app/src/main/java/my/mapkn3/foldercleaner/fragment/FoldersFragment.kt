package my.mapkn3.foldercleaner.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.folders_fragment.view.*
import my.mapkn3.foldercleaner.R

class FoldersFragment : Fragment() {

    companion object {
        fun newInstance() = FoldersFragment()
    }

    private lateinit var folderFragmentListener: FolderFragmentListener
    lateinit var foldersList: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FolderFragmentListener) {
            folderFragmentListener = context
        } else {
            throw ClassCastException("$context must implement FolderFragmentListener")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foldersList = folderFragmentListener.loadData(FolderFragmentListener.FOLDERS_KEY)

        val fragment = inflater.inflate(R.layout.folders_fragment, container, false)
        context?.let {
            adapter = ArrayAdapter(it, R.layout.text_list_item, foldersList)
            fragment.folders.adapter = adapter

            fragment.folders.onItemLongClickListener =
                AdapterView.OnItemLongClickListener { parent, view, position, id ->
                    val selectedItem = (view as TextView).text.toString()
                    val removedItem = foldersList.removeAt(position)
                    adapter.notifyDataSetChanged()
                    folderFragmentListener.notify("Folder '$removedItem' unselected")
                    removedItem == selectedItem
                }
        }

        fragment.selectFolderButton.setOnClickListener { folderFragmentListener.onChooseFolderClick() }
        fragment.clearFolderButton.setOnClickListener { folderFragmentListener.onClearFolderClick() }

        return fragment
    }

    override fun onDestroyView() {
        folderFragmentListener.saveData(FolderFragmentListener.FOLDERS_KEY, foldersList)
        super.onDestroyView()
    }

    interface FolderFragmentListener : SaveLoadData<String, ArrayList<String>>, NotifyUser {
        companion object {
            const val FOLDERS_KEY = "foldersList"
        }

        fun onChooseFolderClick()

        fun onClearFolderClick()
    }
}