package com.perseverance.phando.retrofit;

/**
 * Created by TrilokiNath on 25-01-2017.
 */

public class ServerResponseError extends Exception {

    public ServerResponseError(String message) {
        super(message);
    }
}
