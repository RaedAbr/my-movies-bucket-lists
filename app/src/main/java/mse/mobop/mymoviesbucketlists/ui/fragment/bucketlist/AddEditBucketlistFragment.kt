package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_add_edit_bucketlist.*
import mse.mobop.mymoviesbucketlists.*
import mse.mobop.mymoviesbucketlists.database.Converters
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import java.time.OffsetDateTime


class AddEditBucketlistFragment : Fragment() {
    private lateinit var action: BucketlistAction
    private lateinit var bucketlistViewModel: BucketlistViewModel

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

        bucketlistViewModel = ViewModelProviders.of(this).get(BucketlistViewModel::class.java)
        if (bucketlistId != -1L) {
            bucketlistViewModel.loadBucketlist(bucketlistId)
            bucketlistViewModel.bucketlist.observe(this, Observer {
                fragment_title.text = fragmentTitle
                bucketlist_name.setText(it?.name)
            })
        }

        // get the focus on the list name EditText view and open keyboard
        val imgr: InputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity!!.menuInflater.inflate(R.menu.save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
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
        val updatedBucketlist: Bucketlist = bucketlistViewModel.bucketlist.value!!
        updatedBucketlist.name = bucketlistName
        bucketlistViewModel.update(updatedBucketlist)
        Toast.makeText(context, "Movies bucketlist updated", Toast.LENGTH_SHORT).show()
        return true
    }

    private fun saveNewBucketlist(): Boolean {
        val listName = bucketlist_name.text.toString()
        val createdBy = FirebaseAuth.getInstance().currentUser!!.uid

        if (listName.trim().isEmpty()){
            Toast.makeText(context, "List name can not be empty!", Toast.LENGTH_SHORT).show()
            return false
        }

        bucketlistViewModel.insert(
            Bucketlist(null, listName, createdBy ?: "UNKNOUN", Converters.fromOffsetDateTime(OffsetDateTime.now()))
        )
        Toast.makeText(context, "New movies bucketlist saved", Toast.LENGTH_SHORT).show()
        return true
    }
}
