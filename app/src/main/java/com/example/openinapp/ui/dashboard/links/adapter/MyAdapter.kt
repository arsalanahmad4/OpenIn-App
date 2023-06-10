package com.example.openinapp.ui.dashboard.links.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.openinapp.R
import com.example.openinapp.data.model.RecentLink

class MyAdapter(private val dataList: MutableList<RecentLink>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private var showAllData = false
    private val MAX_ITEMS_DEFAULT = 4
    private val MAX_ITEMS_ALL = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.links_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return if (showAllData) dataList.size else MAX_ITEMS_DEFAULT
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.tvMainText)

        fun bind(item: RecentLink) {
            textView.text = item.smart_link
        }
    }

    fun setShowAllData(showAll: Boolean) {
        showAllData = showAll
        notifyDataSetChanged()
    }

    fun addItems(items: List<RecentLink>) {
        dataList.addAll(items)
        notifyDataSetChanged()
    }
}