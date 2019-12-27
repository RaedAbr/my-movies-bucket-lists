package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_one_bucketlist.*
import kotlinx.android.synthetic.main.recycler_bucketlist_movies.*
import kotlinx.android.synthetic.main.recycler_bucketlist_movies.view.*
import mse.mobop.mymoviesbucketlists.utils.BucketlistAction

import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.firestore.BucketlistFirestore
import mse.mobop.mymoviesbucketlists.model.Movie
import mse.mobop.mymoviesbucketlists.ui.DeleteBucketlistAlertDialog
import mse.mobop.mymoviesbucketlists.ui.fragment.OnNavigatingToFragmentListener
import mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters.BucketlistMoviesAdapter
import mse.mobop.mymoviesbucketlists.utils.dateConverter
import mse.mobop.mymoviesbucketlists.utils.hideKeyboardFrom

/**
 * A simple [Fragment] subclass.
 */
class OneBucketlistFragment : Fragment() {
    private lateinit var bucketlistViewModel: BucketlistViewModel
    private lateinit var bucketlistId: String
    private var titleListener: OnNavigatingToFragmentListener? = null
    private lateinit var bucketlistMoviesAdapter: BucketlistMoviesAdapter

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

        // get bandle args from parent fragment
        val bandle = OneBucketlistFragmentArgs.fromBundle(arguments!!)
        bucketlistId = bandle.bucketlistId

        val addMoviesFab: FloatingActionButton = root.findViewById(R.id.add_movies_fab)
        addMoviesFab.setOnClickListener {
            val direction =
                OneBucketlistFragmentDirections.actionOneBucketlistFragmentToAddMoviesFragment(
                    bucketlistId = bucketlistId
                )
            findNavController().navigate(direction)
        }

        setUpRecyclerMoviesList(root)

        bucketlistViewModel = BucketlistViewModel(bucketlistId)
        bucketlistViewModel.bucketlist.observe(viewLifecycleOwner, Observer {
            if (it == null) { // this means that the data has been deleted by another user
                Toast.makeText(context, "Bucket list has been deleted by the owner!", Toast.LENGTH_LONG).show()
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
                if (it.movies.isEmpty()) {
                    bucketlist_no_movies.visibility = View.VISIBLE
                    recycler_bucketlist_movies.visibility = View.GONE
                } else {
                    bucketlist_no_movies.visibility = View.GONE
                    recycler_bucketlist_movies.visibility = View.VISIBLE
                }
                if (bucketlist_movies_progressbar.visibility == View.VISIBLE) {
                    bucketlist_movies_progressbar.visibility = View.GONE
                }
                bucketlistMoviesAdapter.setMovies(it.movies)
                bucketlistMoviesAdapter.setOnItemClickListener(
                    object: BucketlistMoviesAdapter.OnItemClickListener {
                        override fun itemClickListener(movie: Movie) {
                            BucketlistFirestore.toggleIsMovieWatched(bucketlistId, movie)
                        }

                    })
            }
        })

        return root
    }

    private fun setUpRecyclerMoviesList(root: View) {
        bucketlistMoviesAdapter = BucketlistMoviesAdapter()
        val recyclerView = root.recycler_bucketlist_movies
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = bucketlistMoviesAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity!!.menuInflater.inflate(R.menu.one_bucketlist_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_edit -> {
                val direction = OneBucketlistFragmentDirections
                    .actionOneBucketlistFragmentToAddEditBucketlistFragment(
                        fragmentTitle = R.string.edit_bucket_list,
                        bucketlistId = bucketlistId,
                        action = BucketlistAction.EDIT
                    )
                findNavController().navigate(direction)
                true
            }
            R.id.action_delete -> {
                DeleteBucketlistAlertDialog(this@OneBucketlistFragment.context!!,
                    DialogInterface.OnClickListener { _, _ ->
                        bucketlistViewModel.bucketlist.removeObservers(viewLifecycleOwner)
                        bucketlistViewModel.delete(bucketlistId)
                        activity!!.onBackPressed()
                    })
                    .create()
                    .show()
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
