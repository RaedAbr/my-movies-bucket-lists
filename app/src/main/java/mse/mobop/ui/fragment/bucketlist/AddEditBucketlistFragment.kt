package mse.mobop.ui.fragment.bucketlist

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_add_edit_bucketlist.*
import mse.mobop.ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_ACTION
import mse.mobop.ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_TITLE
import mse.mobop.R
import mse.mobop.model.Bucketlist

class AddEditBucketlistFragment : Fragment() {
    private var action: AddEditBucketlistFragmentViewModel.Action? = null
    private var fragmentTitle: String? = "New List"
    private lateinit var bucketlistViewModel: AddEditBucketlistFragmentViewModel

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
        bucketlistViewModel = ViewModelProviders.of(this).get(AddEditBucketlistFragmentViewModel::class.java)
        bucketlistViewModel.bucketlist.observe(this, Observer {
            textview_add_edit_bucketlist_title.text = fragmentTitle
            edittext_add_edit_bucketlist_name.setText(it?.name)
        })

        bucketlistViewModel.loadArguments(arguments)
        action = arguments?.getSerializable(ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_ACTION) as AddEditBucketlistFragmentViewModel.Action?
        if (action == null) {
            action = AddEditBucketlistFragmentViewModel.Action.ADD
        }

        fragmentTitle = arguments?.getString(ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_TITLE)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity!!.menuInflater.inflate(R.menu.add_edit_bucketlist_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId) {
            R.id.save -> {
                when(action) {
                    AddEditBucketlistFragmentViewModel.Action.ADD -> {
                        saveNewBucketlist()
                    }
                    else -> {
                        editBucketlist()
                    }
                }
                activity!!.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editBucketlist() {
        val id = bucketlistViewModel.bucketlist.value!!.id
        val listName = edittext_add_edit_bucketlist_name.text.toString()
        val createdBy = bucketlistViewModel.bucketlist.value!!.createdBy

        if (listName.trim().isEmpty()){
            Toast.makeText(context, "List name can not be empty!", Toast.LENGTH_SHORT).show()
            return
        }
        bucketlistViewModel.update(
            Bucketlist(id, listName, createdBy)
        )
        Toast.makeText(context, "Movies bucketlist updated", Toast.LENGTH_SHORT).show()
    }

    private fun saveNewBucketlist() {
        val listName = edittext_add_edit_bucketlist_name.text.toString()
        val createdBy = "Raed"

        if (listName.trim().isEmpty()){
            Toast.makeText(context, "List name can not be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        bucketlistViewModel.insert(
            Bucketlist(null, listName, createdBy)
        )
        Toast.makeText(context, "New movies bucketlist saved", Toast.LENGTH_SHORT).show()
    }
}
