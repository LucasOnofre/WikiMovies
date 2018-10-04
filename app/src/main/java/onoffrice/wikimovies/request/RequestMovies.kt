package onoffrice.wikimovies.request

import android.content.Context
import onoffrice.wikimovies.R
import onoffrice.wikimovies.model.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url
import java.util.HashMap

class RequestMovies(private val context:Context){

    private val service:Service

    init {
        this.service = RetrofitClient.instance(context).create(Service::class.java)
    }

    val API_KEY  = context.resources.getString(R.string.api_key)
    val params = HashMap<String,Any?>()

    /**
     * Return a movie list from the Discover
     */
    fun getPopularsMovies():Call<Result>{
        val endpoint = context.resources.getString(R.string.url_movies_popular)
        params.put("api_key",API_KEY)

        return service.getPopularsMovies(endpoint,params)
    }

    /**
     * Return the genres list
     */

    fun getGenres():Call<ResultGenre>{
        val endpoint = context.resources.getString(R.string.url_genres)
        params.put("api_key",API_KEY)
        return service.getGenres(endpoint,params)
    }

    private interface Service {

        @GET
        fun getPopularsMovies(@Url url: String, @QueryMap params: HashMap<String, Any?>):Call<Result>

        @GET
        fun getGenres(@Url url: String, @QueryMap params:HashMap<String, Any?>):Call<ResultGenre>
    }

}