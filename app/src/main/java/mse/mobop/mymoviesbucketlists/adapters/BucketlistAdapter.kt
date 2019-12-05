package mse.mobop.mymoviesbucketlists.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.item_bucketlist.view.*
import kotlinx.android.synthetic.main.recycler_bucketlists_view.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.database.Converters
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import java.time.format.DateTimeFormatter

class BucketlistAdapter(options: FirestoreRecyclerOptions<Bucketlist>) :
    FirestoreRecyclerAdapter<Bucketlist, BucketlistAdapter.BucketlistHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketlistHolder {
        return BucketlistHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_bucketlist, parent, false
        ))
    }

    override fun onBindViewHolder(holder: BucketlistHolder, position: Int, model: Bucketlist) {
        holder.bind(model)
    }

    class BucketlistHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(bucketlist: Bucketlist) {
            itemView.bucketlist_name.text = bucketlist.name
            itemView.bucketlist_creator.text = bucketlist.createdBy
            var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            itemView.bucketlist_date.text = Converters
                .toOffsetDateTime(bucketlist.creationDateTime!!)!!.format(formatter)
            formatter = DateTimeFormatter.ofPattern("KK:mm a")
            itemView.bucketlist_time.text = Converters
                .toOffsetDateTime(bucketlist.creationDateTime!!)!!.format(formatter)
        }
    }
}