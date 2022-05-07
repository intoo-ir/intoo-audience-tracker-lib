package ir.intoo.api.tracker.helper

import android.content.Context
import ir.intoo.api.tracker.api.ServiceGenerator
import ir.intoo.api.tracker.api.APIService
import ir.intoo.api.tracker.model.ResultConfigure
import ir.intoo.api.tracker.model.TrackerModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallApi {
    fun getConfigure(context: Context?) {
        val storeHelper = StoreHelper(context!!)
        val serviceGenerator = ServiceGenerator(context)
        serviceGenerator.createService(APIService::class.java).configure(context.packageName)
            .enqueue(object :
                Callback<ResultConfigure?> {
                override fun onResponse(
                    call: Call<ResultConfigure?>,
                    response: Response<ResultConfigure?>
                ) {
                    if (response.isSuccessful) {
                        assert(response.body() != null)
                        response.body()?.let { storeHelper.saveConfigure(it.data) }
                    }
                }

                override fun onFailure(call: Call<ResultConfigure?>, t: Throwable) {
                }
            })
    }

    fun sendLocation(context: Context, trackerModel: TrackerModel?) {
        val serviceGenerator = ServiceGenerator(context)
        serviceGenerator.createService(APIService::class.java).recordLocation(trackerModel)!!
            .enqueue(object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    if (response.isSuccessful) {

                    }

                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {

                }
            })
    }
}