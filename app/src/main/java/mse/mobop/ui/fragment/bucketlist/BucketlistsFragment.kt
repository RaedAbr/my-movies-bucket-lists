package mse.mobop.ui.fragment.bucketlist

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
import mse.mobop.R
import mse.mobop.adapters.GenericRecyclerViewAdapter
import mse.mobop.model.Bucketlist
import mse.mobop.ui.swipe.SwipeController

class BucketlistsFragment : Fragment() {

    private lateinit var bucketlistsViewModel: BucketlistsViewModel

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
            findNavController().navigate(
                R.id.action_nav_home_to_nav_addEditBucketlistFragment
            )
        }

        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_bucketlists_view)
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        recyclerView.setHasFixedSize(true)
        val recyclerAdapter = GenericRecyclerViewAdapter<Bucketlist>(R.layout.item_bucketlist)

        bucketlistsViewModel = ViewModelProviders.of(activity!!).get(BucketlistsViewModel(activity!!.application)::class.java)
        bucketlistsViewModel.allBucketlist.observe(this, Observer {
            recyclerAdapter.submitList(it)
//            recyclerAdapter.setListItems(it)
        })
        recyclerView.adapter = recyclerAdapter

        ItemTouchHelper(object: SwipeController(ItemTouchHelper.RIGHT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                bucketlistsViewModel.deleteAt(viewHolder.adapterPosition)
//                recyclerAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                Toast.makeText(this@BucketlistsFragment.context, "List deleted", Toast.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(recyclerView)

        recyclerAdapter.setOnItemClickListener(object : GenericRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, view: View) {
                // todo: replace this with navigating to the movies list
                findNavController().navigate(
                    R.id.action_nav_home_to_nav_addEditBucketlistFragment,
                    AddEditBucketlistFragmentViewModel.createArguments(bucketlistsViewModel.getBucketlistAt(position))
                )
            }
        })

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity!!.menuInflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}