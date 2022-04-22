package ir.intoo.api.tracker.api;

import static android.telephony.PhoneNumberUtils.WAIT;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ir.intoo.api.tracker.helper.StoreHelper;
import ir.intoo.api.tracker.model.Configure;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    public String API_BASE_URL = "https://api.intoo.ir/api/";
    StoreHelper storeHelper;
    TypeRefresh stateRefreshToken;
    Context context;

    enum TypeRefresh {
         FAIL, SUCCESS
    }

    public ServiceGenerator(Context context) {
        this.context = context;
        storeHelper = new StoreHelper(context);
    }

    private final Builder builder = new Builder().baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.MINUTES)
            .protocols(Util.immutableList(Protocol.HTTP_1_1))

            .readTimeout(1, TimeUnit.MINUTES).addInterceptor(new listener()).build();

    class listener implements Interceptor {
        listener() {
        }

        @NonNull
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original.newBuilder().method(original.method(), original.body())
                    .header("app-token", storeHelper.getConfigure().getAccessToken())
                    .build();
            Response response = chain.proceed(request);
            if (response.code() == 401) {
                configure();
            }
            return response;

        }
    }

    private void configure() {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create());
        builder.client(httpClient).build().create(APIService.class).configure(this.context.getPackageName()).enqueue(new Callback<Configure>() {
            @Override
            public void onResponse(Call<Configure> call, final retrofit2.Response<Configure> response) {
                if (response.isSuccessful()) {
                    stateRefreshToken = TypeRefresh.SUCCESS;
                    if (response.body() != null) {
                        storeHelper.saveConfigure(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<Configure> call, Throwable t) {
                stateRefreshToken = TypeRefresh.FAIL;
            }
        });
    }


    public <S> S createService(Class<S> serviceClass) {
        return builder.client(httpClient).build().create(serviceClass);
    }

}
