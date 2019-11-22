package mse.mobop.ui.bucketlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import mse.mobop.R
import mse.mobop.adapters.GenericRecyclerViewAdapter
import mse.mobop.adapters.RecyclerViewHoldersFactory
import mse.mobop.model.MoviesBucketlist

class MoviesBucketlistsFragment : Fragment() {

    private lateinit var moviesBucketlistsViewModel: MoviesBucketlistsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_movies_bucketlists, container, false)

        val fab: FloatingActionButton = root.findViewById(R.id.add_bucketlist_fab)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_addEditBucketlistFragment)
//            val amountTv = "Raed"
//            val action = AddEditBucketlistFragment.confirmationAction(amountTv)
//            v.findNavController().navigate(action)
            Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        moviesBucketlistsViewModel =
            ViewModelProviders.of(activity!!).get(MoviesBucketlistsViewModel(activity!!.application)::class.java)

        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_bucketlists_view)
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        recyclerView.setHasFixedSize(true)

        moviesBucketlistsViewModel.allMoviesBucketlist.observe(this, Observer {
            val recyclerAdapter = object : GenericRecyclerViewAdapter<MoviesBucketlist>(
                it,
                object : OnItemClickListener {
                    override fun onItemClick(position: Int, view: View) {
                        val list = it[position]
                        findNavController().navigate(
                            R.id.action_nav_home_to_nav_addEditBucketlistFragment,
                            AddEditBucketlistFragmentViewModel.createArguments(it[position])
                        )
                        Log.v("RecyclerView", "Title: " + list.name)
                    }
                }) {
                override fun getLayoutId(): Int {
                    return  R.layout.item_bucketlist
                }

                override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
                    return RecyclerViewHoldersFactory.create(view,viewType)
                }
            }
            recyclerView.adapter = recyclerAdapter
        })
        return root
    }
}