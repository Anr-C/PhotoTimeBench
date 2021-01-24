package com.lckiss.photobench.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lckiss.photobench.databinding.ItemBinding
import com.lckiss.photobench.transTime
import java.io.File

class FileListAdapter : RecyclerView.Adapter<FileListAdapter.VH>() {

    var data: Array<File> = emptyArray()
        set(value) {
            value.sortBy {
                it.lastModified()
            }
            field = value
            notifyDataSetChanged()
        }

    var onItemClickListener: ((file: File) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val vh = VH(view)
        view.root.setOnClickListener {
            data.getOrNull(vh.adapterPosition)?.also {
                onItemClickListener?.invoke(it)
            }
        }
        return vh
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val orNull = data.getOrNull(position)
        orNull?.also {
            holder.view.name.text = it.name
            holder.view.type.text = if (it.isDirectory) "目录" else "文件"
            holder.view.modifyTime.text = it.lastModified().transTime()
            val isIMG = it.name.endsWith(".jpg") || it.name.endsWith(".jpeg") || it.name.endsWith(".png")
            holder.view.preview.visibility = if (it.isDirectory.not() && isIMG) {
                Glide.with(holder.itemView).load(it).into(holder.view.preview)
                View.VISIBLE
            } else {
                View.GONE
            }

        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class VH(val view: ItemBinding) : RecyclerView.ViewHolder(view.root)
}
