package com.perseverance.phando;


/**
 * Created by TrilokiNath on 02-08-2016.
 */
public class Constants {

    public static final boolean BUILD_PROD = BuildConfig.BUILD_TYPE.equalsIgnoreCase("PROD");
//    public static final String  BASE_URL_PROD = "https://dflixott.com/";
//   public static final String  BASE_URL_PROD = "https://vp.mokshmargdharm.org/";
    public static final String  BASE_URL_PROD = "https://vyaspublication.mokshmargdharm.org/";

    public static final String BASE_URL_STAGE = "https://phunflixqa.phando.com/";

    public static final String GA_TRACKER_ID = "";

    public static final String URL_HELP = new FeatureConfigClass().getBaseUrl() + "page?type=faq";
    public static final String URL_ABOUT_US = new FeatureConfigClass().getBaseUrl() + "page?type=aboutus";
    public static final String URL_TC = new FeatureConfigClass().getBaseUrl() + "page?type=termconditions";
    public static final String URL_PRIVACY_POLICY = new FeatureConfigClass().getBaseUrl() + "page?type=privacy";

  public static final String URL_REFUND = new FeatureConfigClass().getBaseUrl() + "page?type=refundpolicy";


}