package ir.intoo.api.tracker.api

import retrofit2.http.GET
import ir.intoo.api.tracker.model.Configure
import retrofit2.http.POST
import ir.intoo.api.tracker.model.TrackerModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Query

interface APIService {
    @GET("mobile-app/configure")
    fun configure(@Query("packageName") packageName: String): Call<Configure>

    @POST("mobile-app/record")
    fun recordLocation(@Body trackerModel: TrackerModel?): Call<Void?>?
}