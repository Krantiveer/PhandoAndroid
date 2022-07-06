package com.perseverance.phando.retrofit;


import com.perseverance.phando.data.BaseResponse;
import com.perseverance.phando.data.NotificationsSettingsModel;
import com.perseverance.phando.data.ParentSettingPost;
import com.perseverance.phando.data.ParentalControlData;
import com.perseverance.phando.db.Category;
import com.perseverance.phando.db.Filter;
import com.perseverance.phando.db.Language;
import com.perseverance.phando.db.Video;
import com.perseverance.phando.home.dashboard.models.BrowseData;
import com.perseverance.phando.home.dashboard.models.CategoryTab;
import com.perseverance.phando.home.dashboard.mylist.MyPurchaseListResponse;
import com.perseverance.phando.home.dashboard.mylist.UpdateMyListResponse;
import com.perseverance.phando.home.mediadetails.MediaMetadata;
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadataResponse;
import com.perseverance.phando.home.mediadetails.downloads.MediaUrlResponse;
import com.perseverance.phando.home.mediadetails.payment.MediaplaybackData;
import com.perseverance.phando.home.profile.UserProfileData;
import com.perseverance.phando.home.profile.login.SocialLoggedInUser;
import com.perseverance.phando.home.profile.model.CountryCode;
import com.perseverance.phando.home.series.TVSeriesResponseData;
import com.perseverance.phando.home.series.TVSeriesResponseDataNew;
import com.perseverance.phando.payment.paymentoptions.TCResponseData;
import com.perseverance.phando.payment.paymentoptions.WalletDetailResponseData;
import com.perseverance.phando.payment.paymentoptions.WalletHistoryResponseData;
import com.perseverance.phando.payment.subscription.CreateOrderResponse;
import com.perseverance.phando.payment.subscription.Package;
import com.perseverance.phando.splash.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by TrilokiNath on 28-12-2016.
 */

public interface ApiService {

    @GET("userappversion")
    Call<AppInfo> getAppInfo(@Query("app_type") String appType,@Query("device_id") String device_id);

    @GET("package")
    Call<Package> getPackageDetails();

    @POST("razorpay/order/create")
    @FormUrlEncoded
    Call<CreateOrderResponse> createOrder(@FieldMap Map<String, String> bodyMap);

    @POST("razorpay/order/status")
    @FormUrlEncoded
    Call<BaseResponse> updateOrderStatus(@FieldMap Map<String, String> bodyMap);

    @GET("notificationsettings")
    Call<NotificationsSettingsModel> notificationsettings();

    @GET("parental-control")
    Call<ParentalControlData> parentalControlSettings();

    @POST("notificationsettings")
    @FormUrlEncoded
    Call<BaseResponse> setNotificationsSettings(@FieldMap Map<String, Boolean> bodyMap);

    @POST("updateUserPin")
    @FormUrlEncoded
    Call<BaseResponse> updateUserPin(@FieldMap Map<String, String> bodyMap);

    @POST("validateUserPin")
    @FormUrlEncoded
    Call<BaseResponse> validateUserPin(@FieldMap Map<String, String> bodyMap);

    @POST("parentalSetting")
    Call<BaseResponse> parentalSetting(@Body ParentSettingPost bodyMap);

    @GET("paymenttoken")
    Call<PaymentToken> getPaymentToken();

    @GET("userProfile")
    Call<UserProfileData> getUserProfile();

    @POST("login")
    Call<LoginResponse> doLogin(@Body Cred cred);

    @POST("sociallogin")
    Call<LoginResponse> doSocialLogin(@Body SocialLoggedInUser socialLoggedInUser);

    @POST("register")
    @FormUrlEncoded
    Call<LoginResponse> doRegister(@FieldMap Map<String, String> bodyMap);

    @POST("send-otp")
    @FormUrlEncoded
    Call<BaseResponse> getOTP(@FieldMap Map<String, String> bodyMap);

    @POST("verify-otp")
    @FormUrlEncoded
    Call<LoginResponse> verifyOTP(@FieldMap Map<String, String> bodyMap);

    @GET("genres")
    Call<ArrayList<Category>> getGeneres();

    @GET("filters")
    Call<ArrayList<Filter>> getFilters();

    @GET("languages")
    Call<ArrayList<Language>> getLanguage();

    @POST("forgotpassword")
    Call<ForgotPasswordModel> sendEmailForPasswordReset(@Query("email") String email);


    @GET("latestcategory")
    Call<ArrayList<Category>> getLatestCategories();


    @GET("mediabygenresid")
    Call<List<Video>> getVideosByCategory(@Query("genres_id") String categoryId,
                                          @Query("limit") String limit, @Query("content_type") String type);

    @GET("showwishlist")
    Call<List<Video>> getMyVideoList(@Query("limit") String limit);

    @GET("subscribedmedia")
    Call<MyPurchaseListResponse> getMyPurchasedVideoList(@Query("limit") String limit);

//    @POST("addwishlist")
//    Call<UpdateMyListResponse> updateMyList(@Query("id") String id, @Query("type") String type, @Query("value") String value);

    @FormUrlEncoded
    @POST("addwishlist")
    Call<UpdateMyListResponse> updateMyList(@Field("id") String id, @Field("type") String type, @Field("value") String value);

    @FormUrlEncoded
    @POST("likedislike")
    Call<UpdateMyListResponse> likedislike(@Field("id") String id, @Field("type") String type, @Field("action") String action,
                                           @Field("value") String value);


    @GET("featuredmedia")
    Call<List<Video>> getFeaturedVideos(@Query("limit") String limit,
                                        @Query("lang_id") int langId,
                                        @Query("pid") int pid);

    @GET("medialist")
    Call<List<Video>> getLatestVideos(
            @Query("order") String order,
            @Query("limit") String limit);

    @GET("popularvideos")
    Call<List<Video>> getPopularVideos(
            @Query("interval") String interval);

    @GET("popularvideos")
    Call<List<Video>> getMostViewedVideos(
            @Query("interval") String interval);

    @GET("mediabyseriesid")
    Call<List<Video>> getVideosBySeriesId(
            @Query("series_id") String series_id,
            @Query("limit") String limit);

    @GET("mediabychannelid")
    Call<List<Video>> getVideosByChannel(
            @Query("channel_id") String channelId,
            @Query("limit") String limit);


    @GET("recommendedvideos")
    Call<List<Video>> getRelatedVideos(@Query("limit") String limit,
                                       @Query("id") String mediaId,
                                       @Query("type") String type);


    @GET("tvseries")
    Call<TVSeriesResponseDataNew> getSeriesDetail(@Query("id") String id);

    @GET("episodes/{id}")
    Call<List<Video>> getEpisodesBySeason(@Path("id") String id);

    @GET("mediasearch")
    Call<List<Video>> searchVideo(
            @Query("term") String search,
            @Query("genre_id") String genre_id,
            @Query("language_id") String filter,
            @Query("limit") String limit);


    @GET("mediaplayback")
    Call<MediaMetadata> getVideoMetadataWithFlavour(@Header("Authorization") String authorization,
                                                    @Query("type") String type,
                                                    @Query("id") String id);

    @GET("mediaplayback")
    Call<MediaplaybackData> getMediaMatadata(@Query("id") String id,
                                             @Query("type") String type
    );

    @GET("dashboard")
    Call<List<BrowseData>> getBrowseDataList(
            @Query("type") String type,
            @Query("genre_id") String genre_id,
            @Query("filter") String filter,
            @Query("filter_type") String filter_type,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("category_v1")
    Call<List<CategoryTab>> getCategoryTabList();

    @GET("setContinueWatchingTime")
    Call<UpdateMyListResponse> setContinueWatchingTime(@Query("document_id") String documentId, @Query("time") String time);

    @POST("forgotpassword/otp")
    @FormUrlEncoded
    Call<BaseResponse> verifyPasswordOTP(@FieldMap Map<String, String> bodyMap);

    @GET("countrycodes")
    Call<List<CountryCode>> getCountrycodes();


    @POST("saveUserDownload")
    @FormUrlEncoded
    Call<BaseResponse> saveUserDownload(@FieldMap Map<String, String> bodyMap);

    @POST("removeUserDownload")
    @FormUrlEncoded
    Call<BaseResponse> removeUserDownload(@Field("document_id[]") ArrayList<String> document_id);

    @GET("userDownload")
    Call<DownloadMetadataResponse> getUserDownload();

    @GET("mediaUrl")
    Call<MediaUrlResponse> getMediaUrl(@Query("document_id") String documentId);

    @GET("walletdetails")
    Call<WalletDetailResponseData> getWalletDetails();

    @GET("wallethistory?limit=0,20")
    Call<WalletHistoryResponseData> getWalletHistory();

    @POST("walletactivate")
    @FormUrlEncoded
    Call<BaseResponse> activateWallet(@FieldMap Map<String, String> bodyMap);

    @GET("wallettnc")
    Call<TCResponseData> getTC();

    @POST("setUserLngPreferances")
    @FormUrlEncoded
    Call<BaseResponse> updateLanguagePreference(@FieldMap Map<String, String> bodyMap);

    @POST("mediaplaystarttime")
    @FormUrlEncoded
    Call<BaseResponse> mediaplaystarttime(@FieldMap Map<String, String> bodyMap);
}