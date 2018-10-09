package onoffrice.wikimovies.activity

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import onoffrice.wikimovies.R
import onoffrice.wikimovies.adapter.MoviesAdapter
import onoffrice.wikimovies.extension.toast
import onoffrice.wikimovies.model.*
import onoffrice.wikimovies.request.RequestMovies
import retrofit2.Call
import retrofit2.Response
import kotlin.collections.ArrayList
import android.view.animation.AnimationUtils.loadLayoutAnimation
import android.view.animation.LayoutAnimationController
import android.view.animation.AnimationUtils.loadLayoutAnimation





class ActivityPopulars : ActivityBase() {

    private var page                                     = 1
    private var isLoading                                = true

    private var manager     : GridLayoutManager?         = null
    private var listMovies  : ArrayList<Movie>           = ArrayList()
    private var progressBar : ProgressBar?               = null
    private var recyclerList: RecyclerView?              = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_populars)

        setUpViews()
        requestMovies()
        setInfiniteScroll()
        setupToolbar("Popular")

    }

    /**
     * Set's the views and the progress bar
     */
    private fun setUpViews() {
        progressBar  = findViewById(R.id.progressBar)
        recyclerList = findViewById(R.id.lista)
        progressBar?.visibility  = View.VISIBLE

        recyclerList?.adapter = MoviesAdapter(this@ActivityPopulars,listMovies)
        setOrientationLayoutManager()
    }

    /**
     * Set's the layout manager of the recycler view according to the device's orientation
     */
    private fun setOrientationLayoutManager() {

        val orientation = resources.configuration.orientation

        manager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)
        }
        else
            GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)

        recyclerList?.layoutManager = manager

     //   spanSizeIfHeader()
    }

//    private fun spanSizeIfHeader() {
//        manager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                if (position == 0) {
//                    return manager?.spanCount ?: 0
//                }
//                return 1
//            }
//        }
//    }

    /**
     * Return a movie list from the Discover, passing page as a param for the request
     * Also its done a recursive function
     */
    private fun requestMovies(page:Int = 1){

        RequestMovies(this).getPopularsMovies(page).enqueue(object : retrofit2.Callback<Result> {

            override fun onResponse(call: Call<Result>, response: Response<Result>?) {
                progressBar?.visibility = View.GONE
                response?.body()?.movies?.let { movies ->
                    listMovies.addAll(movies)
                 //   listMovies.get(0).isHeader = true
                    recyclerList?.adapter?.notifyDataSetChanged()
                    isLoading = false

                }

            }
            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.i("Resposta: ", t.message)
            }
        })
    }

    /**
     * Make's new requests when user scrolls the list to the last item
     */
    private fun setInfiniteScroll() {
        recyclerList?.addOnScrollListener(object:RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                //direction = 1 = list ends
                if (!recyclerView.canScrollVertically(1 ) && !isLoading){
                    isLoading = true
                    page++
                    requestMovies(page)
                }
            }
        })
    }

//
//    /**
//     * Return a movie list from the Discover
//     */
//    fun requestGenres(){
//        RequestMovies(this).getGenres().enqueue(object : retrofit2.Callback<ResultGenre> {
//            override fun onResponse(call: Call<ResultGenre>, response: Response<ResultGenre>) {
//
//                var removedSectionsList = filterSectionsGenres(response)
//                removedSectionsList?.let { genres ->
//
//                    filterGenres(genres)
//                }?:run {
//                    Toast.makeText(this@ActivityPopulars, "Not possible to load list", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ResultGenre>, t: Throwable) {
//                Toast.makeText(this@ActivityPopulars, "Not possible to load list", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    /**
//     * Make's the filter to exclude some genres
//     */
//    private fun filterSectionsGenres(response: Response<ResultGenre>) =
//            response.body()?.genres?.filter { it.name != "TV Movie" && it.name != "Music" }
//
//    /**
//     * Make's the filter by genre with all the movies and send to adapter
//     */
//    private fun filterGenres(genres:List<Genre>){
//
//        for (genre in genres){
//            val movies =  listMovies.filter {
//                it.genres!!.contains(genre!!.id)
//            }
//
//            if (!movies.isEmpty()){
//                listSections.add(MovieListGenre(genre, movies))
//            }
//        }
//        recyclerList?.adapter = MovieListAdapter(this@ActivityPopulars, listSections)
//        progressBar?.visibility = View.GONE
//    }
}
