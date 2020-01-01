package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_one_bucketlist.*
import kotlinx.android.synthetic.main.fragment_one_bucketlist.view.*
import kotlinx.android.synthetic.main.recycler_bucketlist_movies.*
import kotlinx.android.synthetic.main.recycler_bucketlist_movies.view.*
import mse.mobop.mymoviesbucketlists.utils.BucketlistAction

import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.firestore.BucketlistFirestore
import mse.mobop.mymoviesbucketlists.model.Movie
import mse.mobop.mymoviesbucketlists.ui.alrertdialog.DeleteBucketlistAlertDialog
import mse.mobop.mymoviesbucketlists.ui.fragment.BaseFragment
import mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters.BucketlistMoviesAdapter
import mse.mobop.mymoviesbucketlists.utils.dateConverter
import mse.mobop.mymoviesbucketlists.utils.hideKeyboardFrom
import java.lang.StringBuilder

@SuppressLint("SetTextI18n", "DefaultLocale", "RestrictedApi")
class OneBucketlistFragment : BaseFragment() {
    private lateinit var bucketlistViewModel: BucketlistViewModel
    private lateinit var bucketlistId: String
    private lateinit var bucketlistOwnerId: String
    private lateinit var bucketlistMoviesAdapter: BucketlistMoviesAdapter
    private var isInDeleteMode = false
    private var optionsMenu: Menu? = null
    private var toggleIsMovieWatchedAction: BucketlistMoviesAdapter.OnItemClickListener? = null
    private lateinit var addMoviesFab: FloatingActionButton
    private lateinit var deleteMoviesFab: FloatingActionButton
    private var noMovies: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // set the top left toolbar icon
        (activity!! as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        setHasOptionsMenu(true)

        // get bandle args from parent fragment
        val bandle = OneBucketlistFragmentArgs.fromBundle(arguments!!)
        bucketlistId = bandle.bucketlistId
        bucketlistOwnerId = bandle.owner

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_one_bucketlist, container, false)

        addMoviesFab = root.findViewById(R.id.add_movies_fab)
        addMoviesFab.setOnClickListener {
            val direction =
                OneBucketlistFragmentDirections.actionOneBucketlistFragmentToAddMoviesFragment(
                    bucketlistId = bucketlistId
                )
            findNavController().navigate(direction)
        }

        deleteMoviesFab = root.delete_movies_fab
        deleteMoviesFab.setOnClickListener {
            enterDeleteMode(root)
        }

        setUpRecyclerMoviesList(root)

        bucketlistViewModel = BucketlistViewModel(bucketlistId)
        bucketlistViewModel.bucketlist.observe(viewLifecycleOwner, Observer {
            if (it == null) { // this means that the data has been deleted by another user
                Toast.makeText(context, getString(R.string.bucketlist_deleted_by_owner), Toast.LENGTH_LONG).show()
                activity!!.onBackPressed()
            } else {
                fragmentTitle = it.name

                if (it.createdBy!!.id == FirebaseAuth.getInstance().currentUser!!.uid) {
                    bucketlist_creator_layout.text = getString(R.string.me)
                } else {
                    bucketlist_creator_layout.text = it.createdBy!!.name
                }

                bucketlist_date.text = dateConverter(it.creationTimestamp!!)

                if (it.sharedWith.isEmpty()) {
                    bucketlist_shared_with.text = getString(R.string.bucketlist_not_shared)
                    bucketlist_shared_with.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_group_off,
                        0, 0, 0
                    )
                } else {
                    bucketlist_shared_with.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_group,
                        0, 0, 0
                    )
                    var sharedWithString = StringBuilder()
                    it.sharedWith.forEach { user ->
                        run {
                            if (FirebaseAuth.getInstance().currentUser!!.uid == user.id) {
                                sharedWithString.insert(0, " " + getString(R.string.me))
                            } else {
                                sharedWithString.append(", " + user.name)
                            }
                        }
                    }
                    if (it.createdBy!!.id == FirebaseAuth.getInstance().currentUser!!.uid) {
                        sharedWithString = StringBuilder(sharedWithString.drop(1))
                    }
                    bucketlist_shared_with.text = sharedWithString.insert(0, getString(R.string.shared_with))
                }
                if (it.movies.isEmpty()) {
                    noMovies = true
                    deleteMoviesFab.visibility = View.GONE
                    bucketlist_no_movies.visibility = View.VISIBLE
                    recycler_bucketlist_movies.visibility = View.GONE
                } else {
                    noMovies = false
                    if (!isInDeleteMode) {
                        deleteMoviesFab.visibility = View.VISIBLE
                    }
                    bucketlist_no_movies.visibility = View.GONE
                    recycler_bucketlist_movies.visibility = View.VISIBLE
                }
                if (bucketlist_movies_progressbar.visibility == View.VISIBLE) {
                    bucketlist_movies_progressbar.visibility = View.GONE
                }
                bucketlist_movies_summary.text = "${it.movies.count { m -> m.isWatched }} " +
                        "${getString(R.string.watched).toLowerCase()} / " +
                        "${it.movies.count()}"
                bucketlistMoviesAdapter.setMoviesList(it.movies)
            }
        })

        return root
    }

    private fun enterDeleteMode(view: View) {
        isInDeleteMode = true
        updateControlView(view)
    }

    private fun exitDeleteMode(view: View) {
        isInDeleteMode = false
        updateControlView(view)
    }

    private fun updateControlView(view: View) {
        val actionBar = (activity!! as AppCompatActivity).supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(!isInDeleteMode)
        actionBar.setDisplayShowTitleEnabled(!isInDeleteMode)
        actionBar.dispatchMenuVisibilityChanged(!isInDeleteMode)
        optionsMenu?.findItem(R.id.action_delete)?.isVisible = !isInDeleteMode
        optionsMenu?.findItem(R.id.action_edit)?.isVisible = !isInDeleteMode
        optionsMenu?.findItem(R.id.action_finish)?.isVisible = isInDeleteMode
        addMoviesFab.visibility = if (isInDeleteMode) View.GONE else View.VISIBLE
        if (!noMovies) {
            deleteMoviesFab.visibility = if (isInDeleteMode) View.GONE else View.VISIBLE
        }
        view.movies_delete_hint.visibility = if (isInDeleteMode) View.VISIBLE else View.GONE
        view.header_layout.visibility = if (!isInDeleteMode) View.VISIBLE else View.GONE
        if (isInDeleteMode) {
            bucketlistMoviesAdapter.setOnItemClickListener(object: BucketlistMoviesAdapter.OnItemClickListener {
                override fun itemClickListener(movie: Movie) {
                    BucketlistFirestore.deleteBucketlistMovie(bucketlistId, movie)
                }
            })
        } else {
            bucketlistMoviesAdapter.setOnItemClickListener(toggleIsMovieWatchedAction!!)
        }
    }

    private fun setUpRecyclerMoviesList(root: View) {
        bucketlistMoviesAdapter = BucketlistMoviesAdapter()
        val recyclerView = root.recycler_bucketlist_movies
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = bucketlistMoviesAdapter

        toggleIsMovieWatchedAction = object: BucketlistMoviesAdapter.OnItemClickListener {
            override fun itemClickListener(movie: Movie) {
                recyclerView.isVerticalScrollBarEnabled = false
                BucketlistFirestore.toggleIsMovieWatched(bucketlistId, movie)
                bucketlistMoviesAdapter.notifyDataSetChanged()
                recyclerView.isVerticalScrollBarEnabled = true
            }
        }

        bucketlistMoviesAdapter.setOnItemClickListener(toggleIsMovieWatchedAction!!)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.one_bucketlist_menu, menu)

        if (FirebaseAuth.getInstance().currentUser!!.uid != bucketlistOwnerId) {
            menu.removeItem(R.id.action_edit)
            menu.removeItem(R.id.action_delete)
        }

        optionsMenu = menu
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
                DeleteBucketlistAlertDialog(
                    this@OneBucketlistFragment.context!!,
                    DialogInterface.OnClickListener { _, _ ->
                        bucketlistViewModel.bucketlist.removeObservers(viewLifecycleOwner)
                        bucketlistViewModel.delete(bucketlistId)
                        activity!!.onBackPressed()
                    })
                    .create()
                    .show()
                true
            }
            R.id.action_finish -> {
                exitDeleteMode(view!!)
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
