package ir.intoo.api.tracker.helper

import android.content.Context
import android.util.Log
import ir.intoo.api.tracker.api.ServiceGenerator
import ir.intoo.api.tracker.api.APIService
import ir.intoo.api.tracker.model.Configure
import ir.intoo.api.tracker.model.TrackerModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallApi {
    fun getConfigure(context: Context?) {
        val storeHelper = StoreHelper(context!!)
        val serviceGenerator = ServiceGenerator(context)
        serviceGenerator.createService(APIService::class.java).configure(context.packageName)!!
            .enqueue(object :
                Callback<Configure?> {
                override fun onResponse(call: Call<Configure?>, response: Response<Configure?>) {
                    if (response.isSuccessful) {
                        assert(response.body() != null)
                        storeHelper.saveConfigure(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<Configure?>, t: Throwable) {
                    Log.i("LOG", t.message!!)
                }
            })
    }

    fun sendLocation(context: Context, trackerModel: TrackerModel?) {
        val serviceGenerator = ServiceGenerator(context)
        serviceGenerator.createService(APIService::class.java).recordLocation(trackerModel)!!
            .enqueue(object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    Log.i("LOG", "OK")
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    Log.i("LOG", t.message!!)
                }
            })
    }
}