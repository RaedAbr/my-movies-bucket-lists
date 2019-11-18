package mse.mobop.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mse.mobop.R
import mse.mobop.adapters.GenericRecyclerViewAdapter
import mse.mobop.adapters.RecyclerViewHoldersFactory
import mse.mobop.data.OneList

class HomeFragment : Fragment() {

//    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        homeViewModel =
//            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(this, Observer {
//            textView.text = it
//        })

        // populate RecyclerView
        val recyclerView = root!!.findViewById<RecyclerView>(R.id.recycler_lists_view)
        val lists = OneList.myLists

        val myAdapter = object : GenericRecyclerViewAdapter<Any>(
            lists,
            object : OnItemClickListener {
                override fun onItemClick(position: Int, view: View) {
                    val list = lists[position]
                    Log.v("RecyclerView", "Title: " + list.name)
//                    val intent = Intent(applicationContext, EditTodoActivity::class.java)
//                    startActivityForResult(intent, TODO_INDEX_RESULT, null)
                }
            }) {
            override fun getLayoutId(position: Int, obj: Any): Int {
                return when(obj) {
                    is OneList -> R.layout.recycler_list_item
//                    is Bus->R.layout.bus_layout
                    else -> R.layout.recycler_list_item
                }
            }

            override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
                return RecyclerViewHoldersFactory.create(view,viewType)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = myAdapter

        return root
    }
}