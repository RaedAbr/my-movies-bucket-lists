package mse.mobop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class GenericRecyclerViewAdapter<T>(
    private val listItems: List<T>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }

    internal interface Binder<T> {
        fun bind(data: T, position: Int, listener: OnItemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getViewHolder(
            LayoutInflater.from(parent.context).inflate(viewType, parent, false),
            viewType
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Binder<T>).bind(listItems[position], position, listener)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutId()
    }

    protected abstract fun getLayoutId(): Int

    abstract fun getViewHolder(view: View, viewType: Int):RecyclerView.ViewHolder
}