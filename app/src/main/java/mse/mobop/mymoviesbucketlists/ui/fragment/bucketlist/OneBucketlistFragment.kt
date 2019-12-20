package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist


import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_one_bucketlist.*
import mse.mobop.mymoviesbucketlists.utils.BucketlistAction

import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.ui.fragment.OnNavigatingToFragmentListener
import mse.mobop.mymoviesbucketlists.utils.dateConverter
import mse.mobop.mymoviesbucketlists.utils.hideKeyboardFrom

/**
 * A simple [Fragment] subclass.
 */
class OneBucketlistFragment : Fragment() {
    private lateinit var bucketlistViewModel: BucketlistViewModel
    private lateinit var bucketlistId: String
    private var titleListener: OnNavigatingToFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // set the top left toolbar icon
        (activity!! as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_one_bucketlist, container, false)

        val addMoviesFab: FloatingActionButton = root.findViewById(R.id.add_movies_fab)
        addMoviesFab.setOnClickListener {
            findNavController().navigate(R.id.action_OneBucketlistFragment_to_AddMoviesFragment)
            R.id.action_OneBucketlistFragment_to_AddEditBucketlistFragment
        }

        // get bandle args from parent fragment
        val bandle = OneBucketlistFragmentArgs.fromBundle(arguments!!)
        bucketlistId = bandle.bucketlistId

        bucketlistViewModel = BucketlistViewModel(bucketlistId)
        bucketlistViewModel.bucketlist.observe(this, Observer {
            if (it == null) { // this means that the data has been deleted by another user
                Toast.makeText(context, "Bucketlist has been deleted by the owner!", Toast.LENGTH_SHORT).show()
                activity!!.onBackPressed()
            } else {
                if (titleListener != null) {
                    titleListener!!.onNavigatingToFragment(it.name)
                }

                if (it.createdBy!!.id == FirebaseAuth.getInstance().currentUser!!.uid) {
                    bucketlist_creator.text = getString(R.string.me)
                    setHasOptionsMenu(true)
                } else {
                    bucketlist_creator.text = it.createdBy!!.name
                }

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
            R.id.action_edit -> {
                val direction = OneBucketlistFragmentDirections
                    .actionOneBucketlistFragmentToAddEditBucketlistFragment(
                        fragmentTitle = R.string.edit_bucketlist,
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
