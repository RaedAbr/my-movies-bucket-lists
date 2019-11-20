package mse.mobop.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        moviesBucketlistsViewModel =
            ViewModelProviders.of(this).get(MoviesBucketlistsViewModel(activity!!.application)::class.java)

        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_lists_view)
        moviesBucketlistsViewModel.allMoviesBucketlist.observe(this, Observer {
            val myAdapter = object : GenericRecyclerViewAdapter<MoviesBucketlist>(
                it,
                object : OnItemClickListener {
                    override fun onItemClick(position: Int, view: View) {
                        val list = it[position]
                        Log.v("RecyclerView", "Title: " + list.name)
//                    val intent = Intent(applicationContext, EditTodoActivity::class.java)
//                    startActivityForResult(intent, TODO_INDEX_RESULT, null)
                    }
                }) {
                override fun getLayoutId(): Int {
                    return  R.layout.recycler_list_item
                }

                override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
                    return RecyclerViewHoldersFactory.create(view,viewType)
                }
            }
            recyclerView.layoutManager = LinearLayoutManager(root.context)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = myAdapter
            Toast.makeText(this@MoviesBucketlistsFragment.context, "hahahahaha", Toast.LENGTH_SHORT).show()
            Log.e("Model changed", "hahahahahaha")
        })

//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(this, Observer {
//            textView.text = it
//        })

        // populate RecyclerView
//        val recyclerView = root!!.findViewById<RecyclerView>(R.id.recycler_lists_view)
//        val lists = ListOfMovies.listForTest
//
//        val myAdapter = object : GenericRecyclerViewAdapter<ListOfMovies>(
//            lists,
//            object : OnItemClickListener {
//                override fun onItemClick(position: Int, view: View) {
//                    val list = lists[position]
//                    Log.v("RecyclerView", "Title: " + list.name)
////                    val intent = Intent(applicationContext, EditTodoActivity::class.java)
////                    startActivityForResult(intent, TODO_INDEX_RESULT, null)
//                }
//            }) {
//            override fun getLayoutId(): Int {
//                return  R.layout.recycler_list_item
//            }
//
//            override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
//                return RecyclerViewHoldersFactory.create(view,viewType)
//            }
//        }
//        recyclerView.layoutManager = LinearLayoutManager(root.context)
//        recyclerView.setHasFixedSize(true)
//        recyclerView.adapter = myAdapter

        return root
    }
}