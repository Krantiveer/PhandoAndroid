package com.perseverance.phando.AdsUtil;


public interface IAdConstants {

    int AD_PARTNER_FB = 1;//0;
    int AD_PARTNER_DFP = 0;//1;
    int LIST_ITEM_TYPE_SMALL = 0;
    int LIST_ITEM_TYPE_BIG = 1;
    int BLOCKED_POSITION = 0;
    String DFP_NAME = "Partner :Dfp";
    String FB_NAME = "Partner :FB";

    String TAG_QA = "QA_NATIVE :";

    //NativeCustomAdTemplateField description
    String HEADLINE = "Headline";
    String CAPTION = "Caption";
    String MAINIMAGE = "MainImage";

    int IS_ACTIVE = 1;


    String DEFAULT_NATIVE_JSON = "{\n" +
            "  \"detailArray\": [\n" +
            "    {\n" +
            "      \"isLoadOtherAdPartnerOnFails\": 0,\n" +
            "      \"preferredAdPartner\": 1,\n" +
            "      \"isActive\": 1,\n" +
            "      \"placementPosition\": 2,\n" +
            "      \"fbAdId\": \"1093413124123870_1167188246746357\",\n" +
            "      \"dfpAdId\": \"\",\n" +
            "      \"adType\": 1\n" +
            "    },\n" +
            "{\n" +
            "\"isLoadOtherAdPartnerOnFails\": 0,\n" +
            "      \"preferredAdPartner\": 1,\n" +
            "      \"isActive\": 1,\n" +
            "      \"placementPosition\": 6,\n" +
            "      \"fbAdId\": \"1093413124123870_1167602183371630\",\n" +
            "      \"dfpAdId\": \"\",\n" +
            "      \"adType\": 1\n" +
            "    },\n" +
            "{\n" +
            "\"isLoadOtherAdPartnerOnFails\": 0,\n" +
            "      \"preferredAdPartner\": 1,\n" +
            "      \"isActive\": 1,\n" +
            "      \"placementPosition\": 10,\n" +
            "      \"fbAdId\": \"1093413124123870_1167657913366057\",\n" +
            "      \"dfpAdId\": \"\",\n" +
            "      \"adType\": 1\n" +
            "    },\n" +
            "{\n" +
            "\"isLoadOtherAdPartnerOnFails\": 0,\n" +
            "      \"preferredAdPartner\": 1,\n" +
            "      \"isActive\": 0,\n" +
            "      \"placementPosition\": 14,\n" +
            "      \"fbAdId\": \"1093413124123870_1167658010032714\",\n" +
            "      \"dfpAdId\": \"\",\n" +
            "      \"adType\": 1\n" +
            "    },\n" +
            "{\n" +
            "\"isLoadOtherAdPartnerOnFails\": 0,\n" +
            "      \"preferredAdPartner\": 1,\n" +
            "      \"isActive\": 0,\n" +
            "      \"placementPosition\": 18,\n" +
            "      \"fbAdId\": \"1093413124123870_1167658086699373\",\n" +
            "      \"dfpAdId\": \"\",\n" +
            "      \"adType\": 1\n" +
            "    }\n" +
            "  ]\n" +
            "}";

}

