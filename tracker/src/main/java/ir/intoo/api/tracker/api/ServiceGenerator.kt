package ir.intoo.api.tracker.api

import android.content.Context
import ir.intoo.api.tracker.helper.StoreHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import kotlin.Throws
import ir.intoo.api.tracker.model.ResultConfigure
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.internal.Util
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException
import java.util.concurrent.TimeUnit

class ServiceGenerator(var context: Context) {
    private var baseurl = "https://api.intoo.ir/api/"
    var storeHelper: StoreHelper = StoreHelper(context)
    var stateRefreshToken: TypeRefresh? = null

    enum class TypeRefresh {
        FAIL, SUCCESS
    }

    private val builder = Retrofit.Builder().baseUrl(baseurl)
        .addConverterFactory(GsonConverterFactory.create())
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.MINUTES)
        .protocols(Util.immutableList(Protocol.HTTP_1_1))
        .readTimeout(1, TimeUnit.MINUTES).addInterceptor(Listener()).build()

    internal inner class Listener : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val request = original.newBuilder().method(original.method(), original.body())
                .header("app-token", storeHelper.getConfigure().accessToken)
                .build()
            val response = chain.proceed(request)
            if (response.code() == 401) {
                configure()
            }
            return response
        }
    }

    private fun configure() {
        val builder = Retrofit.Builder().baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create())
        builder.client(httpClient).build().create(APIService::class.java)
            .configure(context.packageName).enqueue(object : Callback<ResultConfigure?> {
                override fun onResponse(
                    call: Call<ResultConfigure?>,
                    response: retrofit2.Response<ResultConfigure?>
                ) {
                    if (response.isSuccessful) {
                        stateRefreshToken = TypeRefresh.SUCCESS
                        if (response.body() != null) {
                            storeHelper.saveConfigure(response.body()!!.data)
                        }
                    }
                }

                override fun onFailure(call: Call<ResultConfigure?>, t: Throwable) {
                    stateRefreshToken = TypeRefresh.FAIL
                }
            })
    }

    fun <S> createService(serviceClass: Class<S>?): S {
        return serviceClass?.let { builder.client(httpClient).build().create(it) }!!
    }

}