package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_one_bucketlist.*
import mse.mobop.mymoviesbucketlists.BucketlistAction

import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.hideKeyboardFrom
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */
class OneBucketlistFragment : Fragment() {
    private lateinit var bucketlistViewModel: BucketlistViewModel
    private var bucketlistId: Long = -1

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
        val root = inflater.inflate(R.layout.fragment_one_bucketlist, container, false)

        // get bandle args from parent fragment
        val bandle = AddEditBucketlistFragmentArgs.fromBundle(arguments!!)
        bucketlistId = bandle.bucketlistId

        bucketlistViewModel = ViewModelProviders.of(this).get(BucketlistViewModel::class.java)
        bucketlistViewModel.loadBucketlist(bucketlistId)
        bucketlistViewModel.bucketlist.observe(this, Observer {
            bucketlist_name.text = it?.name
            bucketlist_creator.text = it?.createdBy
            var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            bucketlist_date.text = it?.creationDateTime!!.format(formatter)
            formatter = DateTimeFormatter.ofPattern("KK:mm a")
            bucketlist_time.text = it.creationDateTime!!.format(formatter)
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
}
