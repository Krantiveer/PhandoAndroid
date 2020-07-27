package com.perseverance.phando.AdsUtil;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by trilokinathyadav on 28-12-2016.
 */

public interface AdMasterApiService {

    @Headers({"token:sandeshepaper@509"})
    @GET("adsunit/{applicationId}")
    Call<List<AdModel>> getAdModel(@Path("applicationId") String applicationId,
                                   @Query("os_name") String osName,
                                   @Query("device_model") String deviceModel,
                                   @Query("os_version") int osVersion,
                                   @Query("app_version") int appVersion);

}