package mse.mobop.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mse.mobop.R
import mse.mobop.data.OneList

object RecyclerViewHoldersFactory {

    fun create(view: View, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.recycler_lists_view -> ListViewHolder(view)
//            R.layout.bus_layout -> BusViewHolder(view)
            else -> {
                ListViewHolder(view)
            }
        }
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), GenericRecyclerViewAdapter.Binder<OneList> {

        private var listNameTextView: TextView = itemView.findViewById(R.id.list_name_textview)
        private var listCreatorTextView: TextView = itemView.findViewById(R.id.list_creator_textview)
        private var listCreationDateTextView: TextView = itemView.findViewById(R.id.list_date_textview)
        private var listCreationTimeTextView: TextView = itemView.findViewById(R.id.list_time_textview)

        override fun bind(data: OneList, position: Int, listener: GenericRecyclerViewAdapter.OnItemClickListener) {
            listNameTextView.text = data.name
            listCreatorTextView.text = data.createdBy
            listCreationDateTextView.text = data.creationDate
            listCreationTimeTextView.text = data.creationTime

            itemView.setOnClickListener { listener.onItemClick(position, itemView) }
        }
    }

//    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), GenericAdapter.Binder<Car> {
//
//        var textView: TextView
//        init {
//            textView = itemView.findViewById(R.id.carName)
//        }
//        override fun bind(car: Car) {
//            textView.text = car.name
//        }
//    }

//    class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), GenericAdapter.Binder<Bus> {
//
//        var textView: TextView
//        init {
//            textView = itemView.findViewById(R.id.busName)
//        }
//        override fun bind(bus: Bus) {
//            textView.setText(bus.name)
//        }
//    }
}