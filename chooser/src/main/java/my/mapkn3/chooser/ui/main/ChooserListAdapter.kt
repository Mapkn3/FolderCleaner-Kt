package my.mapkn3.chooser.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.mapkn3.chooser.R
import java.io.File

class ChooserListAdapter(private val data: List<File>) : RecyclerView.Adapter<ChooserListAdapter.ChooserItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooserItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChooserItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ChooserItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    class ChooserItemViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.chooser_list_item, parent, false)) {
        private val folderIcon = R.drawable.ic_folder_black
        private val fileIcon = R.drawable.ic_file_black

        private var itemIcon: ImageView? = null
        private var itemText: TextView? = null

        init {
            itemIcon = itemView.findViewById(R.id.itemIcon)
            itemText = itemView.findViewById(R.id.itemText)
        }

        fun bind(item: File) {
            itemIcon?.setImageResource(
                if (item.isDirectory) folderIcon else fileIcon)
            itemText?.text = item.name
        }
    }
}