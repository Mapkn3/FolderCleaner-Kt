package my.mapkn3.foldercleaner.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.ignore_fragment.view.*
import kotlinx.android.synthetic.main.text_list_item.view.*
import my.mapkn3.foldercleaner.R

class IgnoreFragment : Fragment() {

    companion object {
        fun newInstance() = IgnoreFragment()
    }

    private lateinit var ignoreFragmentListener: IgnoreFragmentListener
    lateinit var ignoreList: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IgnoreFragmentListener) {
            ignoreFragmentListener = context
        } else {
            throw ClassCastException("$context must implement IgnoreFragmentListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ignoreList = ignoreFragmentListener.loadData(IgnoreFragmentListener.IGNORE_KEY)

        val fragment = inflater.inflate(R.layout.ignore_fragment, container, false)

        adapter = ArrayAdapter(context, R.layout.text_list_item, ignoreList)
        fragment.ignore.adapter = adapter

        fragment.ignore.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, position, id ->
                val selectedItem = view.textItem.text.toString()
                val removedItem = ignoreList.removeAt(position)
                adapter.notifyDataSetChanged()
                ignoreFragmentListener.notify("Ignore '$removedItem' unselected")
                removedItem == selectedItem
            }

        fragment.selectIgnoreButton.setOnClickListener { ignoreFragmentListener.onChooseIgnoreClick() }

        return fragment
    }

    override fun onDestroyView() {
        ignoreFragmentListener.saveData(IgnoreFragmentListener.IGNORE_KEY, ignoreList)
        super.onDestroyView()
    }

    fun addIgnore(item: String) {
        ignoreList.add(item)
        adapter.notifyDataSetChanged()
    }

    interface IgnoreFragmentListener : SaveLoadData<String, ArrayList<String>>, NotifyUser {
        companion object {
            const val IGNORE_KEY = "ignoreList"
        }

        fun onChooseIgnoreClick()
    }
}