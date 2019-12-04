package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.adapters.GenericRecyclerViewAdapter
import mse.mobop.mymoviesbucketlists.adapters.RecyclerViewHoldersFactory
import mse.mobop.mymoviesbucketlists.hideKeyboardFrom
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.model.ModelInterface
import mse.mobop.mymoviesbucketlists.ui.swipe.SwipeController


class BucketlistFragment : Fragment() {

    private lateinit var bucketlistViewModel: BucketlistViewModel

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

        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_bucketlists_view)
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        recyclerView.setHasFixedSize(true)
        val recyclerAdapter = GenericRecyclerViewAdapter<Bucketlist>(R.layout.item_bucketlist)

        bucketlistViewModel = ViewModelProviders.of(activity!!).get(BucketlistViewModel(activity!!.application)::class.java)
        bucketlistViewModel.allBucketlist.observe(this, Observer {
            recyclerAdapter.submitList(it)
        })
        recyclerView.adapter = recyclerAdapter

        ItemTouchHelper(object: SwipeController(ItemTouchHelper.RIGHT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                bucketlistViewModel.delete((viewHolder as RecyclerViewHoldersFactory.BucketlistViewHolder).dataObject)
//                recyclerAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                Toast.makeText(this@BucketlistFragment.context, "List deleted", Toast.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(recyclerView)

        recyclerAdapter.setOnItemClickListener(object : GenericRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(dataObject: ModelInterface) {
                val direction = BucketlistFragmentDirections.actionNavHomeToOneBucketlistFragment(
                    bucketlistId = dataObject.modelId as Long
                )
                findNavController().navigate(direction)
            }
        })

        return root
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