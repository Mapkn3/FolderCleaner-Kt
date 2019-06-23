package my.mapkn3.foldercleaner.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import my.mapkn3.foldercleaner.MainActivity
import my.mapkn3.foldercleaner.R

class FoldersFragment : Fragment() {

    companion object {
        fun newInstance() = FoldersFragment()
    }

    lateinit var folders: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        folders = (activity as MainActivity).loadData(MainActivity.FOLDERS_KEY)

        val fragment = inflater.inflate(R.layout.folders_fragment, container, false)
        val folderListView = fragment.findViewById<ListView>(R.id.folders)
        adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, folders)
        folderListView.adapter = adapter

        folderListView.setOnItemLongClickListener { parent, view, position, id ->
            val selectedItem = (view as TextView).text.toString()
            val removedItem = folders.removeAt(position)
            adapter.notifyDataSetChanged()
            (activity as MainActivity).toastShort("Folder '$removedItem' unselected")
            removedItem == selectedItem
        }

        return fragment
    }

    override fun onDestroyView() {
        (activity as MainActivity).saveData(MainActivity.FOLDERS_KEY, folders)
        super.onDestroyView()
    }

    fun startChooseFolder(view: View) {
        (activity as MainActivity).startChooseFolder(view)
    }

    fun startClearFolders(view: View) {
        (activity as MainActivity).startClearFolders(view)
    }
}