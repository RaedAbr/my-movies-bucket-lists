package mse.mobop.mymoviesbucketlists.ui.fragment.movie

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_add_movies.*
import kotlinx.android.synthetic.main.fragment_add_movies.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.Movie
import mse.mobop.mymoviesbucketlists.model.MoviesSearchResult
import mse.mobop.mymoviesbucketlists.tmdapi.MovieApi
import mse.mobop.mymoviesbucketlists.tmdapi.MovieService
import mse.mobop.mymoviesbucketlists.ui.fragment.OnNavigatingToFragmentListener
import mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist.BucketlistViewModel
import mse.mobop.mymoviesbucketlists.ui.recyclerview.MoviesPaginationScrollListener
import mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters.MoviesPaginationAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class AddMoviesFragment: Fragment() {
    private val bucketlistViewModel = BucketlistViewModel()

    private var titleListener: OnNavigatingToFragmentListener? = null

    private var adapter: MoviesPaginationAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null

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

        progressBar = root.main_progress

        setUpSearchView(root)

        setUpMoviesRecyclerView(root)

        return root
    }

    private fun setUpSearchView(root: View?) {
        val searchView: SearchView = root!!.movie_search

        val queryTextView: SearchView.SearchAutoComplete = searchView.findViewById(R.id.search_src_text)
        queryTextView.setTextColor(ContextCompat.getColor(searchView.context, R.color.white))
        queryTextView.setHintTextColor(ContextCompat.getColor(searchView.context, R.color.lightGray))

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                this@AddMoviesFragment.query = query ?: ""
                if (query != null && query.isNotEmpty()) {
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

        adapter = MoviesPaginationAdapter(activity!!)
        adapter!!.setOnItemLongClickListener(object: MoviesPaginationAdapter.OnItemLongClickListener{
            override fun onItemLongClick(position: Int) {
                Log.e("onItemLongClick", "$position")
                val movie = adapter!!.getItem(position)
                movie.isSelected = !movie.isSelected
                adapter!!.notifyDataSetChanged()
            }

        })

        linearLayoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = adapter

        moviesPaginationScrollListener = object: MoviesPaginationScrollListener(linearLayoutManager!!) {
            override fun loadMoreItems() {
                this@AddMoviesFragment.isLoading = true
                currentPage += 1

                loadNextPage()
            }
        }

        recyclerView!!.addOnScrollListener(moviesPaginationScrollListener)

//        val selectSwipeController = SelectSwipeController(ItemTouchHelper.LEFT)
//        selectSwipeController.setOnSwipeReleasedAction(object : OnSwipeReleasedAction {
//            override fun onSwipeReleased(position: Int) {
//                Log.e("onSwipeReleased", "$position")
//                val movie = adapter!!.getItem(position)
//                movie.isSelected = !movie.isSelected
//                adapter!!.notifyDataSetChanged()
//            }
//        })
//
//        ItemTouchHelper(selectSwipeController).attachToRecyclerView(recyclerView)

        //init service and load data
        movieService = MovieApi.client!!.create(MovieService::class.java)
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

                adapter!!.addAll(results)
                if (currentPage < totalPageCount) {
                    currentPage += 1
                    adapter!!.addLoadingFooter()
                } else {
                    isLastPage = true
                }
            }

            override fun onFailure(
                call: Call<MoviesSearchResult?>?,
                t: Throwable
            ) {
                t.printStackTrace()
                // TODO: handle failure
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
                adapter!!.removeLoadingFooter()
                isLoading = false
                val moviesSearchResult: MoviesSearchResult = response!!.body()!!
                val results: List<Movie>? = moviesSearchResult.results!!
                Log.e("currentPage", currentPage.toString())
                Log.e("totalPageCount", totalPageCount.toString())
                adapter!!.addAll(results!!)
                if (currentPage < totalPageCount) adapter!!.addLoadingFooter() else isLastPage = true
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
        adapter!!.clear()
        isLoading = false
        isLastPage = false
        currentPage = PAGE_START
        totalPageCount = TOTAL_PAGES
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity!!.menuInflater.inflate(R.menu.search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_add_movies -> {
                addMovies()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addMovies(): Boolean {
        val bandle = AddMoviesFragmentArgs.fromBundle(arguments!!)
        val bucketlistId = bandle.bucketlistId
        val selectedMovies = adapter!!.getSelectedItems() as ArrayList<Movie>

        bucketlistViewModel.addMoviesToBucketlist(bucketlistId, selectedMovies)

//        val direction = AddMoviesFragmentDirections
//            .actionAddMoviesFragmentToOneBucketlistFragment(bucketlistId)
        findNavController().popBackStack()
        return true
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
