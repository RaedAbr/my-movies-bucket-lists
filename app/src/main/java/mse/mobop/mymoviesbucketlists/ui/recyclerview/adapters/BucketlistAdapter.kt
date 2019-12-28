package mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_bucketlist.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.utils.dateConverter
import java.lang.StringBuilder


class BucketlistAdapter(options: FirestoreRecyclerOptions<Bucketlist>, private val type: Type) :
    FirestoreRecyclerAdapter<Bucketlist, BucketlistAdapter.BucketlistHolder>(options) {

    enum class Type { OWNED, SHARED}

    private var onItemClicklistener: OnItemClickListener? = null
    private var onDataChangedListener: OnDataChangedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketlistHolder {
        return BucketlistHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_bucketlist, parent, false
                ),
                type
            )
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
        Toast.makeText(view.context, "Bucket list deleted", Toast.LENGTH_SHORT).show()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClicklistener = onItemClickListener
    }

    fun setOnDataChangeListener(onDataChangedListener: OnDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener
    }

    interface OnItemClickListener {
        fun onItemClick(bucketlistId: String)
    }

    interface OnDataChangedListener {
        fun onDataChaneged(itemCount: Int)
    }

    inner class BucketlistHolder(private val view: View, private val type: Type): RecyclerView.ViewHolder(view) {
        fun bind(bucketlist: Bucketlist) {
            view.bucketlist_name.text = bucketlist.name
            if (bucketlist.creationTimestamp != null) {
                view.bucketlist_date.text = dateConverter(bucketlist.creationTimestamp!!)
            }
            view.bucketlist_movies_count.text = bucketlist.movies.size.toString()

            if (bucketlist.sharedWith.isEmpty()) {
                view.bucketlist_shared_with.text = view.context.getString(R.string.bucketlist_not_shared)
                view.bucketlist_shared_with.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_group_off,
                    0, 0, 0
                )
            } else {
                view.bucketlist_shared_with.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_group,
                    0, 0, 0
                )
                var sharedWithString = StringBuilder()
                bucketlist.sharedWith.forEach { user ->
                    run {
                        if (FirebaseAuth.getInstance().currentUser!!.uid == user.id) {
                            sharedWithString.insert(0, " me")
                        } else {
                            sharedWithString.append(", " + user.name)
                        }
//                        view.bucketlist_shared_with.text = sharedWithString.dropLast(2)
                    }
                }
                if (bucketlist.createdBy!!.id == FirebaseAuth.getInstance().currentUser!!.uid) {
                    sharedWithString = StringBuilder(sharedWithString.drop(1))
                }
                view.bucketlist_shared_with.text = sharedWithString.insert(0, view.context.getString(R.string.shared_with))
            }

            if (type == Type.SHARED) {
                view.bucketlist_creator_name.text = StringBuilder()
                    .append(view.context.getString(R.string.by) + " " + bucketlist.createdBy!!.name)
            } else {
                view.bucketlist_creator_layout.visibility = View.GONE
            }

            if (onItemClicklistener != null) {
                view.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onItemClicklistener!!.onItemClick(snapshots[adapterPosition].id!!)
                    }
                }
            }
        }
    }
}