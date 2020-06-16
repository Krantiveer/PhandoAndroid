package com.perseverance.phando.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by QAIT\TrilokiNath on 1/2/18.
 */

public class SuperModel implements Serializable {

    /*
    {
  "error": {
    "code": 500,
    "context": null,
    "message": "Failed to retrieve records from 'horoscope_details'.\nSQLSTATE[42000]: Syntax error or access violation: 1064 You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near '' at line 1 (SQL: select count(1) as aggregate from `sadhnaapp`.`horoscope_details` where (`type_id` = 1) AND (`signs_id` = 1)",
    "status_code": 500
  }
}
    */

    @SerializedName("error")
    private Error error;

    private Throwable throwable;

    public SuperModel() {
    }

    public SuperModel(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public String toString() {
        return "SuperModel{" +
                "error=" + error +
                ", throwable=" + throwable +
                '}';
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public static class Error {

        @SerializedName("code")
        private int code;

        @SerializedName("message")
        private String message;

        @SerializedName("status_code")
        private int statusCode;

        @Override
        public String toString() {
            return "Error{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", statusCode=" + statusCode +
                    '}';
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }
    }

    /*@SerializedName("status")
    private boolean status;

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("error_msg")
    private String errorMessage;

    @Override
    public String toString() {
        return "SuperModel{" +
                "status=" + status +
                ", statusCode=" + statusCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }*/
}
