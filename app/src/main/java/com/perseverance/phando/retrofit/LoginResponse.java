package com.perseverance.phando.retrofit;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    public String accessToken;
    //    @SerializedName("error")
//    public String error;
    @SerializedName("message")
    public String message;
    @SerializedName("hint")
    public String hint;

    @SerializedName("mobile")
    public String mobile;


    /*
    {
    "error": "invalid_request",
    "message": "The request is missing a required parameter, includes an invalid parameter value, includes a parameter more than once, or is otherwise malformed.",
    "hint": "Check the `password` parameter"
}
     */
}
