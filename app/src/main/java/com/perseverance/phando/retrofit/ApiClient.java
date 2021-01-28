package com.perseverance.phando.retrofit;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.google.gson.Gson;
import com.perseverance.phando.BuildConfig;
import com.perseverance.phando.Session;
import com.perseverance.phando.factory.FeatureConfigFactory;
import com.perseverance.phando.home.dashboard.HomeActivity;
import com.perseverance.phando.utils.PreferencesUtils;
import com.perseverance.phando.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.WIFI_SERVICE;
import static com.perseverance.phando.retrofit.SSLUtilKt.setSslClent;

/**
 * Created by TrilokiNath on 28-12-2016.
 */

public class ApiClient {

    private static final String REST_HOST = FeatureConfigFactory.getFeatureConfigInterfaceInstance().getBaseAPIUrl();
    private static final String REST_HOST_LOGIN = FeatureConfigFactory.getFeatureConfigInterfaceInstance().getBaseAPIUrl();
    private static Retrofit retrofit = null;
    private static Retrofit retrofitLogin = null;

//    public static Retrofit getLoginClient() {
//
//        if (retrofit == null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(REST_HOST)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(getRequestHeader())
//                    .build();
//        }
//
//        return retrofit;
//    }

    public static Retrofit getLoginClient() {
        if (retrofitLogin == null) {
            retrofitLogin = new Retrofit.Builder()
                    .baseUrl(REST_HOST_LOGIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getRequestHeader())
                    .build();
        }

        return retrofitLogin;
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
                .addInterceptor(new ResponseInterceptor())
                .addInterceptor(logging);
        setSslClent(okHttpClient);
        return okHttpClient.build();
    }

    public static Retrofit getClientDetail() {
        return getLoginClient();
    }

    static class ResponseInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());

            if (response.code() == 401) {
                ResponseBody body = response.body();
                String bodyString = body.string();
                MediaType contentType = body.contentType();
                ErrorModel errorModel = new Gson().fromJson(bodyString, ErrorModel.class);
                if (errorModel.getMessage().equalsIgnoreCase("You've been logged out because we have detected another login from your ID on a different device. You are not allowed to login on more than one device at a time.")
                        || errorModel.getStatus_code() != null && errorModel.getStatus_code().equalsIgnoreCase("E0002")) {
                    PreferencesUtils.setLoggedIn("");
                    PreferencesUtils.deleteAllPreferences();
                    Intent loginIntent = new Intent(Session.Companion.getInstance(), HomeActivity.class);
                    loginIntent.putExtra("msg", errorModel.getMessage());
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Session.Companion.getInstance().startActivity(loginIntent);
                    errorModel.setMessage(null);
                }
                return response.newBuilder().body(ResponseBody.create(contentType, new Gson().toJson(errorModel))).build();
            }
            return response;
        }
    }

    static class RequestInterceptor implements Interceptor {
        @SuppressWarnings("deprecation")
        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            if (!Utils.isNetworkAvailable(Session.Companion.getInstance())) {
                throw new NoConnectivityException();
            }
            String deviceIp = "";
            WifiManager wm = (WifiManager) Session.Companion.getInstance().getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wm != null)
                deviceIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

            final String token = PreferencesUtils.getLoggedStatus();
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Accept", "application/json; charset=utf-8")
                    .addHeader("IpAddress", deviceIp)
//                    .addHeader("IpAddress", "107.181.177.130")

                    .build();
            return chain.proceed(newRequest);
        }
    }

    public static class NoConnectivityException extends IOException {

        @Override
        public String getMessage() {
            return "No Internet Connection";

        }
    }

}
