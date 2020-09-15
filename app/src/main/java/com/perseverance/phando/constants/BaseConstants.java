package com.perseverance.phando.constants;


/**
 * Created by TrilokiNath on 02-08-2016.
 */
public class BaseConstants {


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

    public static final String HOME = "HomeScreen";
    public static final String SEARCH = "SearchScreen";
    public static final String CATEGORY_VIDEO = "CategoryVideoScreen";
    public static final String MY_LIST = "MyListScreen";
    public static final String SETTINGS = "SettingsScreen";
    public static final String MEDIA_DETAILS = "MediaDetailsScreen";

    public static final String LOGIN = "LoginScreen";
    public static final String LOGIN_WITH_EMAIL = "LoginWithEmailScreen";
    public static final String LOGIN_WITH_OTP = "LoginWithOTPScreen";
    public static final String SIGNUP = "SignupScreen";
    public static final String FORGOT_PASSWORD = "ForgotPasswordScreen";
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
