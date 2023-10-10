package com.perseverance.phando.AdsUtil;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.perseverance.phando.BuildConfig;
import com.perseverance.phando.db.AppDatabase;
import com.perseverance.phando.utils.PreferencesUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.perseverance.phando.retrofit.SSLUtilKt.setSslClent;

public class AdNetworkService extends IntentService {

    private static final String ACTION_AD_NETWORK = "com.sandesh.epaper.splash.action.AD_NETWORK";

    public AdNetworkService() {
        super("AdNetworkService");
    }

    public static void startAdNetworkAction(Context context) {
        Intent intent = new Intent(context, AdNetworkService.class);
        intent.setAction(ACTION_AD_NETWORK);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_AD_NETWORK.equals(action)) {
                handleActionAdNetwork();
            }
        }
    }

    private void handleActionAdNetwork() {
        Retrofit retrofitForAdNetwork = new Retrofit.Builder()
                .baseUrl(BuildConfig.AD_NETWORK_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getRequestHeader())
                .build();
        AdMasterApiService apiService = retrofitForAdNetwork.create(AdMasterApiService.class);
        Call<List<AdModel>> call = apiService.getAdModel(BuildConfig.APPLICATION_ID, "android", Build.MODEL, Build.VERSION.SDK_INT, BuildConfig.VERSION_CODE);
        try {
            Response<List<AdModel>> response = call.execute();
            if (response != null || response.body() != null) {
                List<AdModel> adModelList = response.body();
                if (adModelList != null && adModelList.size() > 0) {
                    AdModelDao adModelDao = AppDatabase.Companion.getInstance(getApplicationContext()).adModelDao();
                    adModelDao.deleteAll();
                    adModelDao.insertAll(adModelList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static OkHttpClient getRequestHeader() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }


        final OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new RequestInterceptor())
                .addInterceptor(logging);

        setSslClent(okHttpClient);
        return okHttpClient.build();
    }

    static class RequestInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            final String token = PreferencesUtils.getLoggedStatus();

            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("publisherid", "80")
                    .addHeader("devicetype", "android")
                    .addHeader("appversion", "12")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Accept", "application/json; charset=utf-8")
                    .build();
            return chain.proceed(newRequest);

        }

    }
}
