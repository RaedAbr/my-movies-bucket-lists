package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.recycler_bucketlists_view.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.adapters.BucketlistAdapter
import mse.mobop.mymoviesbucketlists.firestore.BucketlistFirestore
import mse.mobop.mymoviesbucketlists.utils.hideKeyboardFrom
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.ui.recyclerview.SwipeController


class BucketlistFragment : Fragment() {

    private lateinit var recyclerAdapter: BucketlistAdapter

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
            findNavController().navigate(R.id.action_nav_home_to_nav_addEditBucketlistFragment)
        }

        setUpRecyclerView(root)

//        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_bucketlists_view)
//        recyclerView.layoutManager = LinearLayoutManager(root.context)
//        recyclerView.setHasFixedSize(true)
//        val recyclerAdapter = GenericRecyclerViewAdapter<Bucketlist>(R.layout.item_bucketlist)
//
//        bucketlistViewModel = ViewModelProviders.of(activity!!).get(BucketlistViewModel(activity!!.application)::class.java)
//        bucketlistViewModel.allBucketlist.observe(this, Observer {
//            recyclerAdapter.submitList(it)
//        })
//        recyclerView.adapter = recyclerAdapter
//
//        ItemTouchHelper(object: SwipeController(ItemTouchHelper.RIGHT) {
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                bucketlistViewModel.delete((viewHolder as RecyclerViewHoldersFactory.BucketlistViewHolder).dataObject)
////                recyclerAdapter.notifyItemRemoved(viewHolder.adapterPosition)
//                Toast.makeText(this@BucketlistFragment.context, "List deleted", Toast.LENGTH_SHORT).show()
//            }
//        }).attachToRecyclerView(recyclerView)
//
//        recyclerAdapter.setOnItemClickListener(object : GenericRecyclerViewAdapter.OnItemClickListener {
//            override fun onItemClick(dataObject: ModelInterface) {
//                val direction = BucketlistFragmentDirections.actionNavHomeToOneBucketlistFragment(
//                    bucketlistId = dataObject.modelId as Long
//                )
//                findNavController().navigate(direction)
//            }
//        })

        return root
    }

    private fun setUpRecyclerView(view: View) {
        val query = BucketlistFirestore.getOwnedBucketlistsQuery()
        val recyclerOptions = FirestoreRecyclerOptions.Builder<Bucketlist>()
            .setQuery(query, Bucketlist::class.java)
            .build()
        recyclerAdapter = BucketlistAdapter(recyclerOptions)
        val recyclerView = view.recycler_bucketlists_view
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerAdapter

        ItemTouchHelper(object: SwipeController(ItemTouchHelper.RIGHT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                recyclerAdapter.deleteItem(view, viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(recyclerView)

        recyclerAdapter.setOnItemClickListener(object: BucketlistAdapter.OnItemClickListener {
            override fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
                val bucketlist =  documentSnapshot.toObject(Bucketlist::class.java)
                if (bucketlist != null) {
                    val direction =
                        BucketlistFragmentDirections.actionNavHomeToOneBucketlistFragment(
                            bucketlistId = bucketlist.id!!
                        )
                    findNavController().navigate(direction)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        recyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        recyclerAdapter.stopListening()
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


}