package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist


import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_one_bucketlist.*
import mse.mobop.mymoviesbucketlists.utils.BucketlistAction

import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.utils.dateConverter
import mse.mobop.mymoviesbucketlists.utils.hideKeyboardFrom

/**
 * A simple [Fragment] subclass.
 */
class OneBucketlistFragment : Fragment() {
    private lateinit var bucketlistViewModel: BucketlistViewModel
    private lateinit var bucketlistId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // set the top left toolbar icon
        (activity!! as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        setHasOptionsMenu(false)

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_one_bucketlist, container, false)

        // get bandle args from parent fragment
        val bandle = OneBucketlistFragmentArgs.fromBundle(arguments!!)
        bucketlistId = bandle.bucketlistId

        bucketlistViewModel = BucketlistViewModel(bucketlistId)
        bucketlistViewModel.bucketlist.observe(this, Observer {
            if (it == null) { // this means that the data has been deleted by another user
                Toast.makeText(context, "Bucketlist has been deleted by the owner!", Toast.LENGTH_SHORT).show()
                activity!!.onBackPressed()
            } else {
                if (it.createdBy!!.id == FirebaseAuth.getInstance().currentUser!!.uid) {
                    bucketlist_creator.text = getString(R.string.me)
                    setHasOptionsMenu(true)
                } else {
                    bucketlist_creator.text = it.createdBy!!.name
                }
                bucketlist_name.text = it.name
                bucketlist_date.text = dateConverter(it.creationTimestamp!!)

                if (it.sharedWith.isEmpty()) {
                    bucketlist_shared_with.text = getString(R.string.bucketlist_not_shared)
                } else {
                    val sharedWithString = StringBuilder(getString(R.string.shared_with).plus(" "))
                    it.sharedWith.forEach { user ->
                        run {
                            if (FirebaseAuth.getInstance().currentUser!!.uid != user.name) {
                                sharedWithString.append(user.name + ", ")
                            }
                            bucketlist_shared_with.text = sharedWithString.dropLast(2)
                        }
                    }
                }
            }
        })

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity!!.menuInflater.inflate(R.menu.edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.edit -> {
                val direction = OneBucketlistFragmentDirections
                    .actionOneBucketlistFragmentToNavAddEditBucketlistFragment(
                        fragmentTitle = getString(R.string.edit),
                        bucketlistId = bucketlistId,
                        action = BucketlistAction.EDIT
                    )
                findNavController().navigate(direction)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        // hide the keyboard if opened (after finishing editing for example)
        hideKeyboardFrom(activity!!, view!!)
        super.onResume()
    }

    override fun onPause() {
        bucketlistViewModel.stopSnapshotListener()
        super.onPause()
    }
}
