package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_add_edit_bucketlist.*
import kotlinx.android.synthetic.main.fragment_add_edit_bucketlist.view.*
import mse.mobop.mymoviesbucketlists.*
import mse.mobop.mymoviesbucketlists.firestore.UserFirestore
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.model.User
import mse.mobop.mymoviesbucketlists.ui.fragment.BaseFragment
import mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters.SearchUserAdapter
import mse.mobop.mymoviesbucketlists.utils.BucketlistAction
import mse.mobop.mymoviesbucketlists.viewmodel.BucketlistViewModel
import kotlin.collections.ArrayList


class AddEditBucketlistFragment : BaseFragment() {
    private lateinit var action: BucketlistAction
    private lateinit var bucketlistViewModel: BucketlistViewModel
    private lateinit var usersListAdapter: SearchUserAdapter
    private val searchUsersResult = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // set the top left toolbar icon
        (activity!! as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close)

        setHasOptionsMenu(true)

        // get bandle args from parent fragment
        val bandle = AddEditBucketlistFragmentArgs.fromBundle(arguments!!)
        val fragmentTitle = bandle.fragmentTitle
        val bucketlistId = bandle.bucketlistId
        action = bandle.action

        this.fragmentTitle = getString(fragmentTitle)

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_add_edit_bucketlist, container, false)

        bucketlistViewModel =
            BucketlistViewModel(
                bucketlistId
            )
        if (action == BucketlistAction.EDIT) {
            bucketlistViewModel.bucketlist.observe(viewLifecycleOwner, Observer {
                bucketlist_name.setText(it.name)
                usersListAdapter.addAllUsers(it.sharedWith as ArrayList<User>)
            })
        }

        usersListAdapter = SearchUserAdapter(context!!, R.layout.item_user, ArrayList())

        usersListAdapter.setOnCheckedChangeListener(object: SearchUserAdapter.OnCheckedChangeListener {
            override fun onCheckedChange(user: User) {
                usersListAdapter.removeUser(user)
            }
        })

        root.users_listview.adapter = usersListAdapter

        // Search view for users
        setUpSearchView(root)

        // get the focus on the list name EditText view and open keyboard
        val imgr: InputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        return root
    }

    private fun setUpSearchView(view: View) {
        val searchAutoComplete = view.search_users

        val searchTextViewAdapter = ArrayAdapter<String>(
            context!!,
            android.R.layout.simple_list_item_1,
            ArrayList()
        )

        searchAutoComplete.threshold = 1
        searchAutoComplete.setAdapter(searchTextViewAdapter)

        searchAutoComplete.setOnItemClickListener { _, _, position, _ ->
            searchAutoComplete.setText("")
            val user = searchUsersResult[position]
            usersListAdapter.addUser(user)
        }


        searchAutoComplete.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(newText: CharSequence, start: Int, before: Int, count: Int) {
                if (newText.isNotEmpty()) {
                    UserFirestore.searchUserQuery(newText.toString())!!
                        .get().addOnSuccessListener {
                            val searchList = it.toObjects(User::class.java)
                                .filter { user ->
                                    user.id != FirebaseAuth.getInstance().currentUser!!.uid &&
                                            usersListAdapter.countWhere { u: User -> user.id == u.id } == 0
                                }

                            searchUsersResult.clear()
                            searchUsersResult.addAll(searchList)
                            searchTextViewAdapter.clear()
                            searchTextViewAdapter.addAll(searchList.map { u -> u.name })
                        }
                }
            }
        })
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
        val usersList = usersListAdapter.items()
        val sharedWithIds = usersList.map { it.id!! } as ArrayList<String>

        updatedBucketlist.name = bucketlistName
        updatedBucketlist.sharedWith = usersList
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

        val usersList = usersListAdapter.items()
        val sharedWithIds = usersList.map { it.id!! } as ArrayList<String>

        bucketlistViewModel.insert(
            Bucketlist(name = listName, createdBy = User(currentUser), sharedWith = usersList, sharedWithIds = sharedWithIds)
        )
        Toast.makeText(context, getString(R.string.new_movie_added), Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onPause() {
        bucketlistViewModel.stopSnapshotListener()
        super.onPause()
    }
}
