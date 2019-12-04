package mse.mobop.mymoviesbucketlists.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_bucketlist.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import java.time.format.DateTimeFormatter

object RecyclerViewHoldersFactory {

    fun create(view: View, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.recycler_bucketlists_view -> BucketlistViewHolder(view)
//            R.layout.bus_layout -> BusViewHolder(view)
            else -> {
                BucketlistViewHolder(view)
            }
        }
    }

    class BucketlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), GenericRecyclerViewAdapter.Binder<Bucketlist> {
        private var bucketlistName: TextView = itemView.bucketlist_name
        private var bucketlistCreator: TextView = itemView.bucketlist_creator
        private var bucketlistDate: TextView = itemView.bucketlist_date
        private var bucketlistTime: TextView = itemView.bucketlist_time
        lateinit var dataObject: Bucketlist

        override fun bind(dataObject: Bucketlist, position: Int, listener: GenericRecyclerViewAdapter.OnItemClickListener) {
            this.dataObject = dataObject
            bucketlistName.text = dataObject.name
            bucketlistCreator.text = dataObject.createdBy
            var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            bucketlistDate.text = dataObject.creationDateTime!!.format(formatter)
            formatter = DateTimeFormatter.ofPattern("KK:mm a")
            bucketlistTime.text = dataObject.creationDateTime!!.format(formatter)

            itemView.setOnClickListener {
                if (position != RecyclerView.NO_POSITION)
                    listener.onItemClick(dataObject)
            }
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