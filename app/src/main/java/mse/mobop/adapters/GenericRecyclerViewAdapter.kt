package mse.mobop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mse.mobop.model.ModelInterface

class GenericRecyclerViewAdapter<T: ModelInterface>(private val layoutId: Int
) : ListAdapter<T, RecyclerView.ViewHolder>(DiffCallback<T>()) {
//) :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class DiffCallback<T: ModelInterface>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(
            oldItem: T,
            newItem: T
        ): Boolean = oldItem.modelId == newItem.modelId

        override fun areContentsTheSame(
            oldItem: T,
            newItem: T
        ): Boolean = oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }

    interface Binder<T> {
        fun bind(data: T, position: Int, listener: OnItemClickListener)
    }

//    private var listItems: List<T> = emptyList()

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getViewHolder(
            LayoutInflater.from(parent.context).inflate(viewType, parent, false),
            viewType
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        (holder as Binder<T>).bind(listItems[position], position, listener!!)
        (holder as Binder<T>).bind(getItem(position), position, listener!!)
    }

    // to remove
//    override fun getItemCount(): Int {
//        return listItems.size
//    }

    override fun getItemViewType(position: Int): Int {
        return layoutId
    }

    private fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return RecyclerViewHoldersFactory.create(view, viewType)
    }

    // to remove
//    fun setListItems(listItems: List<T>) {
//        this.listItems = listItems
//        notifyDataSetChanged()
//    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}