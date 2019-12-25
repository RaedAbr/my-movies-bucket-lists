package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.fragment_bucketlists.view.*
import kotlinx.android.synthetic.main.recycler_bucketlists_owned.view.*
import kotlinx.android.synthetic.main.recycler_bucketlists_shared.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters.BucketlistAdapter
import mse.mobop.mymoviesbucketlists.firestore.BucketlistFirestore
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.ui.fragment.OnNavigatingToFragmentListener
import mse.mobop.mymoviesbucketlists.ui.recyclerview.DeleteSwipeController
import mse.mobop.mymoviesbucketlists.utils.hideKeyboardFrom


class BucketlistFragment : Fragment() {

    private lateinit var recyclerViewOwned: RecyclerView
    private lateinit var recyclerViewShared: RecyclerView

    private lateinit var recyclerAdapterOwned: BucketlistAdapter
    private lateinit var recyclerAdapterShared: BucketlistAdapter

    private var titleListener: OnNavigatingToFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // set the top left toolbar icon
        (activity!! as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_open_nav_menu)

        val root = inflater.inflate(R.layout.fragment_bucketlists, container, false)

        val addBucketlistFab: FloatingActionButton = root.findViewById(R.id.add_bucketlist_fab)
        addBucketlistFab.setOnClickListener {
            findNavController().navigate(R.id.action_BucketlistsFragment_to_AddEditBucketlistFragment)
        }

        if (titleListener != null) {
            titleListener!!.onNavigatingToFragment(getString(R.string.app_name))
        }

        setUpRecyclerViewOwned(root)

        setUpRecyclerViewShared(root)

        setUpRecyclerViewHeaders(root)

        return root
    }

    private fun setUpRecyclerViewOwned(view: View) {
        val query = BucketlistFirestore.getOwnedBucketlistsQuery()

        val recyclerOptions = FirestoreRecyclerOptions.Builder<Bucketlist>()
            .setQuery(query, Bucketlist::class.java)
            .build()

        recyclerAdapterOwned = BucketlistAdapter(recyclerOptions, BucketlistAdapter.Type.OWNED)

        recyclerViewOwned = view.recycler_bucketlists_owned
        recyclerViewOwned.layoutManager = LinearLayoutManager(view.context)
        recyclerViewOwned.setHasFixedSize(false)
        recyclerViewOwned.adapter = recyclerAdapterOwned
        recyclerViewOwned.isNestedScrollingEnabled = false

        ItemTouchHelper(object: DeleteSwipeController(ItemTouchHelper.RIGHT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                recyclerAdapterOwned.deleteItem(view, viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(recyclerViewOwned)

        recyclerAdapterOwned.setOnItemClickListener(OnBucketlistItemClickListener())

        recyclerAdapterOwned.setOnDataChangeListener(OnBucketlistChangeDataListener(
            view.owned_header, view.owned_header_nbr
        ))
    }

    private fun setUpRecyclerViewShared(view: View) {
        val query = BucketlistFirestore.getSharedBucketlistsQuery()

        val recyclerOptions = FirestoreRecyclerOptions.Builder<Bucketlist>()
            .setQuery(query, Bucketlist::class.java)
            .build()

        recyclerAdapterShared = BucketlistAdapter(recyclerOptions, BucketlistAdapter.Type.SHARED)

        recyclerViewShared = view.recycler_bucketlists_shared
        recyclerViewShared.layoutManager = LinearLayoutManager(view.context)
        recyclerViewShared.setHasFixedSize(false)
        recyclerViewShared.adapter = recyclerAdapterShared
        recyclerViewShared.isNestedScrollingEnabled = false

        recyclerAdapterShared.setOnItemClickListener(OnBucketlistItemClickListener())

        recyclerAdapterShared.setOnDataChangeListener(OnBucketlistChangeDataListener(
            view.shared_header, view.shared_header_nbr
        ))
    }

    private inner class OnBucketlistItemClickListener: BucketlistAdapter.OnItemClickListener {
        override fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
            val bucketlist = documentSnapshot.toObject(Bucketlist::class.java)
            if (bucketlist != null) {
                val direction =
                    BucketlistFragmentDirections.actionBucketlistsFragmentToOneBucketlistFragment(
                        bucketlistId = bucketlist.id!!
                    )
                findNavController().navigate(direction)
            }
        }
    }

    private inner class OnBucketlistChangeDataListener(
        private val recyclerHeader: LinearLayout,
        private val headerNbr: TextView
    ): BucketlistAdapter.OnDataChangedListener {
        override fun onDataChaneged(itemCount: Int) {
            if (itemCount == 0) {
                recyclerHeader.visibility = View.GONE
            } else {
                recyclerHeader.visibility = View.VISIBLE
            }
            headerNbr.text = itemCount.toString()
        }
    }

    private fun setUpRecyclerViewHeaders(view: View) {
        view.owned_header.setOnClickListener {
            if (recyclerViewOwned.visibility == View.VISIBLE) {
                recyclerViewOwned.visibility = View.GONE
                view.owned_header_arrow.visibility = View.GONE
                view.owned_header_nbr.visibility = View.VISIBLE
            } else {
                recyclerViewOwned.visibility = View.VISIBLE
                view.owned_header_arrow.visibility = View.VISIBLE
                view.owned_header_nbr.visibility = View.GONE
            }
        }
        view.shared_header.setOnClickListener {
            if (recyclerViewShared.visibility == View.VISIBLE) {
                recyclerViewShared.visibility = View.GONE
                view.shared_header_arrow.visibility = View.GONE
                view.shared_header_nbr.visibility = View.VISIBLE
            } else {
                recyclerViewShared.visibility = View.VISIBLE
                view.shared_header_arrow.visibility = View.VISIBLE
                view.shared_header_nbr.visibility = View.GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        recyclerAdapterOwned.startListening()
        recyclerAdapterShared.startListening()
    }

    override fun onStop() {
        super.onStop()
        recyclerAdapterOwned.stopListening()
        recyclerAdapterShared.stopListening()
    }

    override fun onResume() {
        // hide the keyboard if opened (after finishing editing for example)
        hideKeyboardFrom(activity!!, view!!)
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity!!.menuInflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            titleListener = context as OnNavigatingToFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$context must implement OnNavigatingToFragmentListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        titleListener = null
    }
}