package my.mapkn3.chooser.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.mapkn3.chooser.R
import java.io.File

class ChooserListAdapter(private val data: List<File>, private val fragment: ChooserFragment) :
    RecyclerView.Adapter<ChooserListAdapter.ChooserItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooserItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chooser_list_item, parent, false)
        return ChooserItemViewHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: ChooserItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    class ChooserItemViewHolder(view: View, private val fragment: ChooserFragment) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private val folderIcon = R.drawable.ic_folder_black
        private val fileIcon = R.drawable.ic_file_black

        private var itemIcon: ImageView? = null
        private var itemText: TextView? = null

        init {
            itemIcon = itemView.findViewById(R.id.itemIcon)
            itemText = itemView.findViewById(R.id.itemText)
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            fragment.selectNext(itemText?.text as String)
        }

        fun bind(item: File) {
            itemIcon?.setImageResource(
                if (item.isDirectory) folderIcon else fileIcon
            )
            itemText?.text = item.name
        }
    }
}