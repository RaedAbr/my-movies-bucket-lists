package mse.mobop.mymoviesbucketlists.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.item_bucketlist.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.utils.dateConverter
import java.util.*


class BucketlistAdapter(options: FirestoreRecyclerOptions<Bucketlist>) :
    FirestoreRecyclerAdapter<Bucketlist, BucketlistAdapter.BucketlistHolder>(options) {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketlistHolder {
        return BucketlistHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_bucketlist, parent, false
        ))
    }

    override fun onBindViewHolder(holder: BucketlistHolder, position: Int, model: Bucketlist) {
        holder.bind(model)
    }

    fun deleteItem(view: View, position: Int) {
        snapshots.getSnapshot(position).reference.delete()
        Toast.makeText(view.context, "List deleted", Toast.LENGTH_SHORT).show()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int)
    }

    inner class BucketlistHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(bucketlist: Bucketlist) {
            itemView.bucketlist_name.text = bucketlist.name
            itemView.bucketlist_date.text = dateConverter(bucketlist.creationTimestamp ?: Timestamp(Date()))
            if (FirebaseAuth.getInstance().currentUser!!.uid == bucketlist.createdBy!!.name) {
                itemView.bucketlist_creator.text = bucketlist.createdBy!!.name
            } else {
                itemView.created_by_layout.visibility = View.GONE
            }

            if (listener != null) {
                itemView.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(snapshots.getSnapshot(adapterPosition), adapterPosition)
                    }
                }
            }
        }
    }
}