package mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.item_bucketlist_shared.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.utils.dateConverter
import java.util.*


class BucketlistAdapter(options: FirestoreRecyclerOptions<Bucketlist>, private val type: Type) :
    FirestoreRecyclerAdapter<Bucketlist, BucketlistAdapter.BucketlistHolder>(options) {

    enum class Type { OWNED, SHARED}

    private var onItemClicklistener: OnItemClickListener? = null
    private var onDataChangedListener: OnDataChangedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketlistHolder {
        return when(type) {
            Type.OWNED -> BucketlistHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_bucketlist_owned, parent, false
                ),
                type
            )
            Type.SHARED -> BucketlistHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_bucketlist_shared, parent, false
                ),
                type
            )
        }
    }

    override fun onBindViewHolder(holder: BucketlistHolder, position: Int, model: Bucketlist) {
        holder.bind(model)
    }

    override fun onDataChanged() {
        if (onDataChangedListener != null) {
            onDataChangedListener!!.onDataChaneged(itemCount)
        }
        super.onDataChanged()
    }

    fun deleteItem(view: View, position: Int) {
        snapshots.getSnapshot(position).reference.delete()
        Toast.makeText(view.context, "List deleted", Toast.LENGTH_SHORT).show()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClicklistener = onItemClickListener
    }

    fun setOnDataChangeListener(onDataChangedListener: OnDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener
    }

    interface OnItemClickListener {
        fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int)
    }

    interface OnDataChangedListener {
        fun onDataChaneged(itemCount: Int)
    }

    inner class BucketlistHolder(private val view: View, private val type: Type): RecyclerView.ViewHolder(view) {
        fun bind(bucketlist: Bucketlist) {
            view.bucketlist_name.text = bucketlist.name
            view.bucketlist_date.text = dateConverter(bucketlist.creationTimestamp ?: Timestamp(Date()))
            if (type == Type.SHARED) {
                view.bucketlist_creator.text = bucketlist.createdBy!!.name
            }

            if (onItemClicklistener != null) {
                view.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onItemClicklistener!!.onItemClick(snapshots.getSnapshot(adapterPosition), adapterPosition)
                    }
                }
            }
        }
    }
}