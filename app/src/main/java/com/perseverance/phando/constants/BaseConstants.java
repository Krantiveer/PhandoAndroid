package com.perseverance.phando.constants;


import org.jetbrains.annotations.NotNull;

/**
 * Created by TrilokiNath on 02-08-2016.
 */
public class BaseConstants {



    public static final String CONTACT_ACTIVITY = "CONTACT_ACTIVITY";

    public static final String LOGIN_WITH_MOBILE_SCREEN_SOCIAL = "LoginWithMobileSocial";

    public static final int LIMIT_VIDEOS = 100;
    public static final String FEATURED_VIDEO_CATEGORY = "-2";
    public static final String LATEST_VIDEO_CATEGORY = "-3";
    public static final String POPULAR_VIDEO_CATEGORY = "-4";
    public static final String MOST_VIEWED_VIDEO_CATEGORY = "-5";

    public static final int REQUEST_CODE_SEARCH = 11;
    public static final int REQUEST_CODE_SEARCH_RESULT = 12;
    public static final int FLAVOUR_NAME_240 = 240;
    public static final String MEDIA_TYPE_M3U8 = "m3u8";
    public static final String CONNECTION_ERROR = "Please check your internet connection and try again.";
    public static final String VIDEOS_NOT_FOUND_ERROR = "Videos is not available for this category.";
    public static final String RELATED_VIDEOS_NOT_FOUND_ERROR_RELATED = "Related videos are not available";
    public static final String RETRY_LABEL = "%S\n\nTouch here to refresh!";
    public static final String SERVER_CONNECTION_ERROR = "Failed to connect to the server, please try after some time.";
    public static final String NETWORK_ERROR = "Please check your internet connection and try again.";
    public static final String SERVER_ERROR = "Server error, please try again after some time.";
    public static final String SERVER_ERROR_JSON_EXCEPTION = "Server response is invalid. Please try again after some time.";
    public static final String SERVER_TIMEOUT_ERROR = "Server is taking too much time to load.";
    public static final String APPLICATION_ERROR = "Application Error. Please try again";


    //Screens for tracking
    public static final String SPLASH_SCREEN = "Splash";
    public static final String HOME_BROWSE_SCREEN = "Browse";
    public static final String HOME_SCREEN = "Home";
    public static final String SEARCH_SCREEN = "Search";
    public static final String SEARCH_RESULT = "Search";
    public static final String VIDEO_CATEGORY_SCREEN = "VideoCategory";
    public static final String SERIES_SCREEN = "Series";
    public static final String MY_LIST_SCREEN = "MyList";
    public static final String USER_LIST_SCREEN = "UserList";
    public static final String USER_LIST_PURCHASED_SCREEN = "UserPurchasedList";
    public static final String SETTINGS_SCREEN = "Settings";
    public static final String CATEGORY_SCREEN = "Category";
    public static final String MEDIA_DETAILS_SCREEN = "MediaDetails";
    public static final String NOTIFICATION_LIST_SCREEN = "NotificationList";

    public static final String PROFILE_SCREEN = "Profile";
    public static final String NOTIFICATIONS_ACTIVITY = "NOTIFICATIONS_ACTIVITY";
    public static final String PARENTAL_ACTIVITY = "PARENTAL_ACTIVITY";
    public static final String Dashboard_SCREEN = "DashboardList";
    public static final String LOGIN_SCREEN = "LoginScreen";
    public static final String SIGNUP_SCREEN = "SignupScreen";
    public static final String LOGIN_WITH_EMAIL_SCREEN = "LoginWithEmail";
    public static final String LOGIN_WITH_MOBILE_SCREEN = "LoginWithMobile";
    public static final String FORGOT_PASSWORD_SCREEN = "ForgotPassword";
    public static final String SET_PASSWORD_SCREEN = "SetPassword";
    public static final String GENRES_ACTIVITY = "GenreActivity";
    public static final String CHANGE_PASSWORD_SCREEN = "ChangePassword";
    public static final String OTP_VERIFICATION_SCREEN = "OTPVerification";

    public static final String PAYMENT_OPTIONS_SCREEN = "PaymentOptions";
    public static final String WALLWET_DETAILS_SCREEN = "WalletDetails";
    public static final String WALLWET_HISTORY_SCREEN = "WalletHistory";
    public static final String WALLWET_RECHARGE_SCREEN = "WalletRecharge";
    public static final String WALLWET_TC_SCREEN = "WalletT&C";


    public static final String PURCHASE_OPTION = "purchase_option";

    public enum Video {

        FEATURED(1), LATEST(2), POPULAR(3), MOSTVIEWED(4),
        CATEGORY(5), SERIESVIDEOS(6), RELATEDVIDEOS(7), CHANNELVIDEOS(8);

        private int type;

        Video(int sType) {
            type = sType;
        }

        public int getType() {
            return type;
        }
    }


}
