package ir.intoo.api.tracker.api

import retrofit2.http.GET
import ir.intoo.api.tracker.model.Configure
import ir.intoo.api.tracker.model.ResultConfigure
import retrofit2.http.POST
import ir.intoo.api.tracker.model.TrackerModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Query

interface APIService {
    @GET("audience/config")
    fun configure(@Query("packageName") packageName: String): Call<ResultConfigure>

    @POST("audience/track")
    fun recordLocation(@Body trackerModel: TrackerModel?): Call<Void?>?
}