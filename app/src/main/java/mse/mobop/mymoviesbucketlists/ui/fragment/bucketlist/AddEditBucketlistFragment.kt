package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_add_edit_bucketlist.*
import mse.mobop.mymoviesbucketlists.*
import mse.mobop.mymoviesbucketlists.model.Bucketlist


class AddEditBucketlistFragment : Fragment() {
    private lateinit var action: BucketlistAction
    private lateinit var addEditBucketlistViewModel: AddEditBucketlistViewModel

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
        addEditBucketlistViewModel = ViewModelProviders.of(this).get(AddEditBucketlistViewModel::class.java)
        addEditBucketlistViewModel.bucketlist.observe(this, Observer {
            bucketlist_name.setText(it?.name)
            fragment_title.text = BucketListActionStrings[action]
        })

        addEditBucketlistViewModel.loadArguments(arguments)
        action = arguments?.getSerializable(ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_ACTION) as BucketlistAction

        // get the focus on the list name EditText view
        val imgr: InputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity!!.menuInflater.inflate(R.menu.save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId) {
            R.id.save -> {
                val ret: Boolean = when(action) {
                    BucketlistAction.ADD -> {
                        saveNewBucketlist()
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
            Toast.makeText(context, "List name can not be empty!", Toast.LENGTH_SHORT).show()
            return false
        }
        val updatedBucketlist: Bucketlist = addEditBucketlistViewModel.bucketlist.value!!
        updatedBucketlist.name = bucketlistName
        addEditBucketlistViewModel.update(updatedBucketlist)
        Toast.makeText(context, "Movies bucketlist updated", Toast.LENGTH_SHORT).show()
        return true
    }

    private fun saveNewBucketlist(): Boolean {
        val listName = bucketlist_name.text.toString()
        val createdBy = "Raed"

        if (listName.trim().isEmpty()){
            Toast.makeText(context, "List name can not be empty!", Toast.LENGTH_SHORT).show()
            return false
        }

        addEditBucketlistViewModel.insert(
            Bucketlist(null, listName, createdBy)
        )
        Toast.makeText(context, "New movies bucketlist saved", Toast.LENGTH_SHORT).show()
        return true
    }
}
