package my.mapkn3.chooser.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.chooser_fragment.*
import kotlinx.android.synthetic.main.chooser_fragment.view.*
import my.mapkn3.chooser.R
import my.mapkn3.chooser.model.FileSystemModel

class ChooserFragment(
    private val mode: FileSystemModel.MODE,
    private val type: FileSystemModel.TYPE
) : Fragment() {
    companion object {
        fun newInstance(mode: FileSystemModel.MODE, type: FileSystemModel.TYPE) =
            ChooserFragment(mode, type)
    }

    private lateinit var chooserFragmentListener: ChooserFragmentListener
    private lateinit var chooserViewModel: ChooserViewModel

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chooserViewModel = ViewModelProviders.of(this).get(ChooserViewModel::class.java)
        chooserViewModel.fileSystemModelLiveData.observe(this, Observer { fileSystemModel ->
            pathTextView.text = fileSystemModel.getPathString()

            if (fileSystemModel.currentItemIsDirectory()) {
                val data = fileSystemModel.getItemsForCurrentItem()
                chooser_list.layoutManager = LinearLayoutManager(activity)
                chooser_list.adapter = ChooserListAdapter(data, this)
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

    fun selectNext(item: String) = chooserViewModel.selectNextItem(item)

    fun selectPrev() = chooserViewModel.selectPrevItem()

    private fun getChoice() = chooserViewModel.getChoice()

    interface ChooserFragmentListener {
        fun onSelectClick(result: String)
    }
}