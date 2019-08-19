package my.mapkn3.foldercleaner.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.list_with_control_fragment.view.*
import my.mapkn3.foldercleaner.R

class ListWithControlFragment(
    private val key: String,
    private val controlPosition: ControlPosition,
    private val buttonActionArray: Array<Pair<String, () -> Unit>>
) : Fragment() {
    companion object {
        fun newInstance(
            key: String,
            controlPosition: ControlPosition = ControlPosition.DOWN,
            buttonActionArray: Array<Pair<String, () -> Unit>> = arrayOf("Action" to {})
        ) =
            ListWithControlFragment(key, controlPosition, buttonActionArray)
    }

    enum class ControlPosition {
        DOWN,
        UP
    }

    private lateinit var listWithControlFragmentListener: ListWithControlFragmentListener

    lateinit var itemList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ListWithControlFragmentListener) {
            listWithControlFragmentListener = context
        } else {
            throw ClassCastException("$context must implement ListWithControlFragmentListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        itemList = listWithControlFragmentListener.loadData(key)

        val fragment = inflater.inflate(R.layout.list_with_control_fragment, container, false)

        val itemsLayoutParams = fragment.items.layoutParams as ConstraintLayout.LayoutParams
        val controlPanelLayoutParams =
            fragment.controlPanel.layoutParams as ConstraintLayout.LayoutParams

        when (controlPosition) {
            ControlPosition.UP -> {
                itemsLayoutParams.topToBottom = fragment.controlPanel.id
                itemsLayoutParams.bottomToBottom = fragment.listWithControl.id

                controlPanelLayoutParams.bottomToTop = fragment.items.id
                controlPanelLayoutParams.topToTop = fragment.listWithControl.id
            }
            ControlPosition.DOWN -> {
                itemsLayoutParams.bottomToTop = fragment.controlPanel.id
                itemsLayoutParams.topToTop = fragment.listWithControl.id

                controlPanelLayoutParams.topToBottom = fragment.items.id
                controlPanelLayoutParams.bottomToBottom = fragment.listWithControl.id
            }
        }

        fragment.items.requestLayout()
        fragment.controlPanel.requestLayout()

        adapter = ArrayAdapter(
            listWithControlFragmentListener as Context,
            R.layout.text_list_item,
            R.id.textItem,
            itemList
        )

        fragment.items.adapter = adapter

        fragment.items.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, position, id ->
                val removedItem = itemList.removeAt(position)
                adapter.notifyDataSetChanged()
                listWithControlFragmentListener.notify("Item '$removedItem' unselected")
                true
            }

        when (buttonActionArray.size) {
            1 -> {
                fragment.controlPanel.centerButton.apply {
                    visibility = View.VISIBLE
                    text = buttonActionArray[0].first
                    setOnClickListener { buttonActionArray[0].second() }
                }
            }
            2 -> {
                fragment.controlPanel.leftButton.apply {
                    visibility = View.VISIBLE
                    text = buttonActionArray[0].first
                    setOnClickListener { buttonActionArray[0].second() }
                }
                fragment.controlPanel.rightButton.apply {
                    visibility = View.VISIBLE
                    text = buttonActionArray[1].first
                    setOnClickListener { buttonActionArray[1].second() }
                }
            }
            3 -> {
                fragment.controlPanel.leftButton.apply {
                    visibility = View.VISIBLE
                    text = buttonActionArray[0].first
                    setOnClickListener { buttonActionArray[0].second() }
                }
                fragment.controlPanel.centerButton.apply {
                    visibility = View.VISIBLE
                    text = buttonActionArray[1].first
                    setOnClickListener { buttonActionArray[1].second() }
                }
                fragment.controlPanel.rightButton.apply {
                    visibility = View.VISIBLE
                    text = buttonActionArray[2].first
                    setOnClickListener { buttonActionArray[2].second() }
                }
            }
        }

        return fragment
    }

    override fun onDestroyView() {
        listWithControlFragmentListener.saveData(key, itemList)
        super.onDestroyView()
    }

    fun addItem(item: String) {
        itemList.add(item)
        adapter.notifyDataSetChanged()
    }

    interface ListWithControlFragmentListener : SaveLoadData<String, ArrayList<String>>, NotifyUser
}