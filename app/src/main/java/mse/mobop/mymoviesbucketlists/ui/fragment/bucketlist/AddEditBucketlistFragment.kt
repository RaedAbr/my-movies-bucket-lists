package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_add_edit_bucketlist.*
import kotlinx.android.synthetic.main.fragment_add_edit_bucketlist.view.*
import mse.mobop.mymoviesbucketlists.*
import mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters.SearchUserAdapter
import mse.mobop.mymoviesbucketlists.firestore.UserFirestore
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.model.User
import mse.mobop.mymoviesbucketlists.ui.fragment.OnNavigatingToFragmentListener
import mse.mobop.mymoviesbucketlists.utils.BucketlistAction
import kotlin.collections.ArrayList


class AddEditBucketlistFragment : Fragment() {
    private lateinit var action: BucketlistAction
    private lateinit var bucketlistViewModel: BucketlistViewModel
    private var titleListener: OnNavigatingToFragmentListener? = null
    private var usersList = ArrayList<SearchUserAdapter.UserForSearch>()
    private lateinit var simpleAdapterSearch: SearchUserAdapter

    private var selectedUsersList: ArrayList<SearchUserAdapter.UserForSearch> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // set the top left toolbar icon
        (activity!! as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close)

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_add_edit_bucketlist, container, false)

        // get bandle args from parent fragment
        val bandle = AddEditBucketlistFragmentArgs.fromBundle(arguments!!)
        val fragmentTitle = bandle.fragmentTitle
        val bucketlistId = bandle.bucketlistId
        action = bandle.action

        if (titleListener != null) {
            titleListener!!.onNavigatingToFragment(getString(fragmentTitle))
        }

        bucketlistViewModel = BucketlistViewModel(bucketlistId)
        if (action == BucketlistAction.EDIT) {
            bucketlistViewModel.bucketlist.observe(viewLifecycleOwner, Observer {
                bucketlist_name.setText(it.name)
                it.sharedWith.forEach {user ->
                    selectedUsersList.add(SearchUserAdapter.UserForSearch(
                        user.id,
                        user.name,
                        true
                    ))
                }
            })
        }

        // List view of list of users to share with
        simpleAdapterSearch = SearchUserAdapter(context!!, R.layout.item_user, usersList)
        simpleAdapterSearch.setOnCheckedChangeListener(object: SearchUserAdapter.OnCheckedChangeListener {
            override fun onCheckedChange(user: SearchUserAdapter.UserForSearch, isChecked: Boolean) {
                when {
                    isChecked -> {
                        user.isSelected = true
                        selectedUsersList.add(user)
                    }
                    else -> {
                        selectedUsersList.remove(user)
                    }
                }
            }
        })
        root.search_users_listview.adapter = simpleAdapterSearch

        // Search view for users
        setUpSearchView(root)

        // get the focus on the list name EditText view and open keyboard
        val imgr: InputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        return root
    }

    private fun setUpSearchView(view: View) {
        view.search_users.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateUsersList(query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateUsersList(newText!!)
                return false
            }
        })
    }

    private fun updateUsersList(txt: String) {
        val query = UserFirestore.searchUserQuery(txt)
        usersList.clear()
        if (query == null) {
            simpleAdapterSearch.notifyDataSetChanged()
            return
        } else {
            query.get().addOnSuccessListener {
                it.toObjects(SearchUserAdapter.UserForSearch::class.java).forEach { user ->
                    if (user.id != FirebaseAuth.getInstance().currentUser!!.uid) {
                        if (selectedUsersList.find { u -> u.id.equals(user.id) } != null) {
                            user.isSelected = true
                        }
                        usersList.add(user)
                    }
                }
                simpleAdapterSearch.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity!!.menuInflater.inflate(R.menu.save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_save -> {
                val ret: Boolean = when(action) {
                    BucketlistAction.ADD -> {
                        addNewBucketlist()
                    }
                    else -> {
                        updateBucketlist()
                    }
                }
                if (ret) {
                    activity!!.onBackPressed()
                }
                return ret
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateBucketlist(): Boolean {
        val bucketlistName = bucketlist_name.text.toString()

        if (bucketlistName.trim().isEmpty()){
            Toast.makeText(context, getString(R.string.bucketlist_name_cannor_be_empty), Toast.LENGTH_SHORT).show()
            return false
        }
        val updatedBucketlist: Bucketlist = bucketlistViewModel.bucketlist.value!!
        val selectedUsers = selectedUsersList.map { User(it.id, it.name) } as ArrayList
        val sharedWithIds = selectedUsersList.map { it.id!! } as ArrayList<String>

        updatedBucketlist.name = bucketlistName
        updatedBucketlist.sharedWith = selectedUsers
        updatedBucketlist.sharedWithIds = sharedWithIds
        bucketlistViewModel.update(updatedBucketlist)
        Toast.makeText(context, getString(R.string.bucketlist_updated), Toast.LENGTH_SHORT).show()
        return true
    }

    private fun addNewBucketlist(): Boolean {
        val listName = bucketlist_name.text.toString()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (listName.trim().isEmpty()){
            Toast.makeText(context, getString(R.string.bucketlist_name_cannor_be_empty), Toast.LENGTH_SHORT).show()
            return false
        }

        val selectedUsers = selectedUsersList.map { User(it.id, it.name) }
        val sharedWithIds = selectedUsersList.map { it.id!! } as ArrayList<String>

        bucketlistViewModel.insert(
            Bucketlist(name = listName, createdBy = User(currentUser), sharedWith = selectedUsers, sharedWithIds = sharedWithIds)
        )
        Toast.makeText(context, getString(R.string.new_movie_added), Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onPause() {
        bucketlistViewModel.stopSnapshotListener()
        super.onPause()
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
