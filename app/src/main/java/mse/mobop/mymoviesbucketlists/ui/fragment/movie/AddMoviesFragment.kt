package mse.mobop.mymoviesbucketlists.ui.fragment.movie

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_add_movies.*
import kotlinx.android.synthetic.main.fragment_add_movies.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.Movie
import mse.mobop.mymoviesbucketlists.model.MoviesSearchResult
import mse.mobop.mymoviesbucketlists.model.User
import mse.mobop.mymoviesbucketlists.tmdapi.MovieApi
import mse.mobop.mymoviesbucketlists.tmdapi.MovieService
import mse.mobop.mymoviesbucketlists.ui.alrertdialog.DisplayMovieTrailerAlertDialog
import mse.mobop.mymoviesbucketlists.ui.fragment.OnNavigatingToFragmentListener
import mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist.BucketlistViewModel
import mse.mobop.mymoviesbucketlists.ui.recyclerview.MoviesPaginationScrollListener
import mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters.MoviesPaginationAdapter
import mse.mobop.mymoviesbucketlists.utils.hideKeyboardFrom
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class AddMoviesFragment: Fragment() {
    private lateinit var bucketlistViewModel: BucketlistViewModel
    private var selectedMovies: ArrayList<Movie> = ArrayList()

    private var titleListener: OnNavigatingToFragmentListener? = null

    private var recyclerAdapter: MoviesPaginationAdapter? = null
    private var optionsMenu: Menu? = null

//    private var viewPagerAdapter: ViewPagerAdapter? = null

    private var query: String = ""

    private var recyclerView: RecyclerView? = null
    private lateinit var moviesPaginationScrollListener: MoviesPaginationScrollListener
    private var progressBar: ProgressBar? = null

    private var isLoading = false
        set(value) {
            moviesPaginationScrollListener.isLoading = value
            field = value
        }
    private var isLastPage = false
        set(value) {
            moviesPaginationScrollListener.isLastPage = value
            field = value
        }
    private var currentPage = PAGE_START
    private var totalPageCount = TOTAL_PAGES
        set(value) {
            moviesPaginationScrollListener.totalPageCount = value
            field = value
        }

    private var movieService: MovieService? = null
    private var apiCall = ::callGetTopRatedMovies

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // set the top left toolbar icon
        (activity as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        val root = inflater.inflate(R.layout.fragment_add_movies, container, false)

        if (titleListener != null) {
            titleListener!!.onNavigatingToFragment(getString(R.string.add_movies))
        }

        val bandle = AddMoviesFragmentArgs.fromBundle(arguments!!)
        val bucketlistId = bandle.bucketlistId
        bucketlistViewModel = BucketlistViewModel(bucketlistId)
        bucketlistViewModel.bucketlist.observe(viewLifecycleOwner, Observer {
            if (it == null) { // this means that the data has been deleted by another user
                Toast.makeText(context, "Bucket list has been deleted by the owner!", Toast.LENGTH_LONG).show()
                activity!!.onBackPressed()
            } else {
                if (recyclerAdapter != null) {
                    recyclerAdapter!!.setMoviesAlreadyAdded(it.movies as ArrayList<Movie>)
                }
            }
        })

//        val viewPager: ViewPager = root.viewpager
//        setupViewPager(viewPager)
//        val tabLayout: TabLayout = root.tabs
//        tabLayout.setupWithViewPager(viewPager)

        movieService = MovieApi.client!!.create(MovieService::class.java)

        progressBar = root.main_progress

        setUpSearchView(root)

        setUpMoviesRecyclerView(root)

        return root
    }

//    private fun setupViewPager(viewPager: ViewPager) {
//        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
//        viewPagerAdapter!!.addFragment(BucketlistFragment(), "Fragment1")
//        viewPagerAdapter!!.addFragment(BucketlistFragment(), "Fragment2")
//        viewPager.adapter = viewPagerAdapter
//    }

    private fun setUpSearchView(root: View?) {
        val searchView: SearchView = root!!.movie_search

        val queryTextView: SearchView.SearchAutoComplete = searchView.findViewById(R.id.search_src_text)
        queryTextView.setTextColor(ContextCompat.getColor(searchView.context, R.color.white))
        queryTextView.setHintTextColor(ContextCompat.getColor(searchView.context, R.color.lightGray))

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                this@AddMoviesFragment.query = query ?: ""
                if (query != null && query.isNotEmpty()) {
                    hideKeyboardFrom(context!!, searchView)
                    resetRecyclerView()
                    apiCall = ::callSearchMoviesApi

                    if (movie_header.visibility == View.VISIBLE) {
                        movie_header.visibility = View.GONE
                    }

                    loadFirstPage()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setUpMoviesRecyclerView(root: View) {
        recyclerView = root.main_recycler

        recyclerAdapter = MoviesPaginationAdapter(activity!!)
        recyclerAdapter!!.setOnItemLongClickListener(object: MoviesPaginationAdapter.ItemListener{
            override fun onItemLongClick(position: Int) {
                Log.e("onItemLongClick", "$position")
                val movie = recyclerAdapter!!.getItem(position)
                movie.isSelected = !movie.isSelected
                if (movie.isSelected) {
                    selectedMovies.add(movie)
                } else {
                    selectedMovies.remove(movie)
                }
                recyclerAdapter!!.notifyDataSetChanged()

                if (selectedMovies.isEmpty()) {
                    optionsMenu?.setGroupVisible(0, false)
                } else {
                    optionsMenu?.setGroupVisible(0, true)
                }

                Snackbar.make(root, "${selectedMovies.size} movies selected", Snackbar.LENGTH_SHORT)
                    .show()
            }

            override fun onItemClick(movieId: Int) {
                DisplayMovieTrailerAlertDialog(context!!, movieId, R.layout.dialog_movie_trailer)
                    .create()
            }
        })

        val linearLayoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = recyclerAdapter

        moviesPaginationScrollListener = object: MoviesPaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                this@AddMoviesFragment.isLoading = true
                currentPage += 1

                loadNextPage()
            }
        }

        recyclerView!!.addOnScrollListener(moviesPaginationScrollListener)

        //load data
        loadFirstPage()
    }

    private fun loadFirstPage() {
        Log.e(TAG, "loadFirstPage: ")
        currentPage = PAGE_START
        Log.e("loadFirstPage", "currentPage: $currentPage\ttotalPageCount: $totalPageCount\tisLastPage: $isLastPage")

        apiCall()!!.enqueue(object : Callback<MoviesSearchResult?> {
            override fun onResponse(
                call: Call<MoviesSearchResult?>?,
                response: Response<MoviesSearchResult?>?
            ) { // Got data.
                // Send it to recycler adapter
                // Update currentPage and totalPages
                val moviesSearchResult: MoviesSearchResult = response!!.body()!!
                currentPage = moviesSearchResult.page!!
                totalPageCount = moviesSearchResult.totalPages!!

                val results: List<Movie> = moviesSearchResult.results!!
                progressBar!!.visibility = View.GONE

                recyclerAdapter!!.addAll(results)
                if (currentPage < totalPageCount) {
                    currentPage += 1
                    recyclerAdapter!!.addLoadingFooter()
                } else {
                    isLastPage = true
                }
            }

            override fun onFailure(call: Call<MoviesSearchResult?>?, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun loadNextPage() {
        Log.e(TAG, "loadNextPage: $currentPage")
        Log.e("loadFirstPage", "currentPage: $currentPage\ttotalPageCount: $totalPageCount\tisLastPage: $isLastPage")

        apiCall()!!.enqueue(object : Callback<MoviesSearchResult?> {
            override fun onResponse(
                call: Call<MoviesSearchResult?>?,
                response: Response<MoviesSearchResult?>?
            ) {
                recyclerAdapter!!.removeLoadingFooter()
                isLoading = false
                val moviesSearchResult: MoviesSearchResult = response!!.body()!!
                val results: List<Movie>? = moviesSearchResult.results!!
                Log.e("currentPage", currentPage.toString())
                Log.e("totalPageCount", totalPageCount.toString())
                recyclerAdapter!!.addAll(results!!)
                if (currentPage < totalPageCount) recyclerAdapter!!.addLoadingFooter() else isLastPage = true
            }

            override fun onFailure(
                call: Call<MoviesSearchResult?>?,
                t: Throwable?
            ) {
                t!!.printStackTrace()
                // TODO: handle failure
            }
        })
    }

    private fun addMovies(): Boolean {
        if (selectedMovies.size == 0) {
            Toast.makeText(context, "Please select one or more movies!", Toast.LENGTH_SHORT).show()
            return false
        }
        selectedMovies = selectedMovies.map {
            val currenUser = FirebaseAuth.getInstance().currentUser
            it.addedBy = User(currenUser!!.uid, currenUser.displayName!!)
            it.addedTimestamp = Timestamp(Date().time / 1000, 0)
            it
        } as ArrayList<Movie>

        bucketlistViewModel.addMoviesToBucketlist(bucketlistViewModel.bucketlist.value!!, selectedMovies)

        findNavController().popBackStack()
        return true
    }

    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As [.currentPage] will be incremented automatically
     * by @[moviesPaginationScrollListener] to load next page.
     */
    private fun callSearchMoviesApi(): Call<MoviesSearchResult?>? {
        return movieService!!.searchForMovies(query, currentPage)
    }
    private fun callGetTopRatedMovies(): Call<MoviesSearchResult?>? {
        return movieService!!.getTopRatedMovies(currentPage)
    }

    private fun resetRecyclerView() {
        recyclerAdapter!!.clear()
        isLoading = false
        isLastPage = false
        currentPage = PAGE_START
        totalPageCount = TOTAL_PAGES
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity!!.menuInflater.inflate(R.menu.save_menu, menu)
        optionsMenu = menu
        menu.setGroupVisible(0, false)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_save -> {
                addMovies()
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    companion object {
        private const val PAGE_START = 1
        private const val TOTAL_PAGES = 1
        private const val TAG = "AddMoviesFragment"
    }
}
