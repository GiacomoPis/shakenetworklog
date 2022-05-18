package it.icemangp.shakenetworklog.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.icemangp.shakenetworklog.R
import it.icemangp.shakenetworklog.data.NetworkCall

class NetworkCallListAdapter(var list: List<NetworkCall>, val listener: ItemClickListener) : RecyclerView.Adapter<NetworkCallListAdapter.NetworkCallListViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(item: NetworkCall)
    }

    class NetworkCallListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val requestMethodTv    : TextView = view.findViewById(R.id.requestMethodTv)
        val responseCodeTv     : TextView = view.findViewById(R.id.requestStatusTv)
        val requestSpeedTv     : TextView = view.findViewById(R.id.requestSpeedTv)
        val requestUrlTv       : TextView = view.findViewById(R.id.requestUrlTv)
        val requestExceptionTv : TextView = view.findViewById(R.id.requestExceptionTv)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NetworkCallListViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.view_networklog_list_item, viewGroup, false)
        val holder =  NetworkCallListViewHolder(view)

        view.setOnClickListener {
            val position = holder.adapterPosition
            val item = list[position]
            listener.onItemClick(item)
        }

        return holder
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: NetworkCallListViewHolder, position: Int) {
        val item = list[position]
        holder.requestMethodTv.text = item.method
        holder.requestSpeedTv.text = item.duration
        holder.responseCodeTv.text = item.responseCode?.toString() ?: " "
        holder.requestUrlTv.text = item.url
        holder.requestExceptionTv.text = item.exceptionMessage

        val color = when (item.responseCode) {
            in 0..399 -> R.color.snl_green
            in 400..499 -> R.color.snl_orange
            else -> R.color.snl_red
        }

        holder.responseCodeTv.setTextColor(ContextCompat.getColor(holder.itemView.context, color))
    }

    fun updateData(data: List<NetworkCall>) {
        list = data
        notifyDataSetChanged()
    }
}