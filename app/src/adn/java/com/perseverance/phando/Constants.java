package com.perseverance.phando;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by TrilokiNath on 02-08-2016.
 */
public class Constants {

    public static final boolean BUILD_PROD = BuildConfig.BUILD_TYPE.equalsIgnoreCase("PROD");

    public static final String  BASE_URL_PROD = "http://ott.adnbroadband.com/";
    public static final String  BASE_URL_STAGE = "https://phunflixqa.phando.com/";

    public static final String GA_TRACKER_ID = "UA-152515869-1";

    public static final String URL_HELP=new FeatureConfigClass().getBaseUrl()+"page?type=help";
    public static final String URL_ABOUT_US=new FeatureConfigClass().getBaseUrl()+"page?type=aboutus";
    public static final String URL_TC=new FeatureConfigClass().getBaseUrl()+"page?type=termconditions";
    public static final String URL_PRIVACY_POLICY=new FeatureConfigClass().getBaseUrl()+"page?type=privacy";



}