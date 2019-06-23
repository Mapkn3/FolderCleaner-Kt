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

class IgnoreFragment : Fragment() {

    companion object {
        fun newInstance() = IgnoreFragment()
    }

    lateinit var ignore: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ignore = (activity as MainActivity).loadData(MainActivity.IGNORE_KEY)

        val fragment = inflater.inflate(R.layout.ignore_fragment, container, false)
        val ignoreListView = fragment.findViewById<ListView>(R.id.ignore)
        adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, ignore)
        ignoreListView.adapter = adapter

        ignoreListView.setOnItemLongClickListener { parent, view, position, id ->
            val selectedItem = (view as TextView).text.toString()
            val removedItem = ignore.removeAt(position)
            adapter.notifyDataSetChanged()
            (activity as MainActivity).toastShort("Ignore '$removedItem' unselected")
            removedItem == selectedItem
        }

        return fragment
    }

    override fun onDestroyView() {
        (activity as MainActivity).saveData(MainActivity.IGNORE_KEY, ignore)
        super.onDestroyView()
    }

    fun startChooseIgnore(view: View) {
        (activity as MainActivity).startChooseIgnore(view)
    }
}