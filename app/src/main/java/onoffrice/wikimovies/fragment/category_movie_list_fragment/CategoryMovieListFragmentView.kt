package onoffrice.wikimovies.fragment.category_movie_list_fragment


import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import onoffrice.wikimovies.R
import onoffrice.wikimovies.adapter.MoviesAdapter
import onoffrice.wikimovies.extension.getPreferenceKey
import onoffrice.wikimovies.extension.getPreferences
import onoffrice.wikimovies.extension.parseJson
import onoffrice.wikimovies.fragment.base_fragment.BaseFragment
import onoffrice.wikimovies.fragment.movie_detail_fragment.MovieDetailFragmentView
import onoffrice.wikimovies.model.Genre
import onoffrice.wikimovies.model.Movie
import onoffrice.wikimovies.model.MovieInterface
import onoffrice.wikimovies.model.MovieLongClickInterface


class CategoryMovieListFragmentView : BaseFragment(), CategoryMovieListFragmentContract.View{

    private var page                                     = 1
    private var genre            : Genre?                 = null
    private var adapter          : MoviesAdapter?        = null
    private var isLoading                                = false
    private var progressBar      : ProgressBar?          = null
    private var recyclerList     : RecyclerView?         = null

    private var listMovies       : ArrayList<Movie>      = ArrayList()
    private var presenter        : CategoryMovieListFragmentPresenter = CategoryMovieListFragmentPresenter()


    /**
     * Implementing interface to handle the click on the movie
     */
    private val movieClickListener = object: MovieInterface {
        override fun onMovieSelected(movie: Movie?) {
            openPopulatedFragment(movie,"movieJson",MovieDetailFragmentView())
        }
    }

    /**
     * Implementing interface to handle the Long click on the movie
     */
    private val movieLongClicListener = object : MovieLongClickInterface {
        override fun onMovieLongClickSelected(view: View, movie: Movie?) {
            openDropMenu(view, movie)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_category_movie_list, container, false)

            setUpViews(rootView!!)

            getSelectedGenre()

            presenter.bindTo(this)
            presenter.requestMoviesModel(genre?.id!!)

            setToolbarGoBackArrow(rootView!!,genre?.name.toString())

            setAdapter()

            setInfiniteScroll()
        }
        return rootView
    }

    /**
     * Get's the genre Selected saved on preferences
     */
    private fun  getSelectedGenre(){

        var genreSelected = (context?.getPreferences()?.getPreferenceKey("categoryChosen"))

        genre = genreSelected?.parseJson<Genre>()
    }

    /**
     * Set's the views and the progress bar
     */
    private fun setUpViews(view: View) {

        progressBar  = view.findViewById(R.id.progressBar)
        recyclerList = view.findViewById(R.id.category_list)

    }

    private fun setAdapter() {
        //Set's the adapter
        adapter = activity?.let { MoviesAdapter(it, listMovies, movieClickListener,movieLongClicListener) }
        recyclerList?.adapter = adapter
        setGridLayout(recyclerList)
    }

    /**
     * Make's new requests when user scrolls the list to the last item
     */
    private fun setInfiniteScroll() {
        recyclerList?.addOnScrollListener(object:RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                //direction = 1 = list ends
                if (!recyclerView.canScrollVertically(1) && !isLoading){
                    isLoading = true
                    page++
                    presenter.requestMoreMovies(page,genre?.id!!)
                    isLoading = false
                }
            }
        })
    }

    /**
     * Show's the progress bar
     */
    override fun showProgress() {
        progressBar?.visibility  = View.VISIBLE
    }

    /**
     * Hide's the progress bar
     */
    override fun hideProgress() {
        progressBar?.visibility  = View.GONE

    }

    /**
     * Update's the list with the request result
     */
    override fun setDataToList(movies: ArrayList<Movie>) {
        listMovies.addAll(movies)
        adapter?.notifyDataSetChanged()

    }

    /**
     * Show's an error if the response is troubleled
     */
    override fun onResponseError(error: Throwable) {
        Toast.makeText(context, error.toString(),Toast.LENGTH_LONG).show()
    }

    /**
     * Destroy's the connection with the presenter
     */
    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }
}
