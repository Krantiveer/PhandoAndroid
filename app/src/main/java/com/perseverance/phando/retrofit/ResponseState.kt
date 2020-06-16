package com.perseverance.phando.retrofit

/**
 * Created by QAIT\TrilokiNath on 7/3/18.
 */


data class ResponseState private constructor(
        val status: Status,
        val msg:String?=null) {
    companion object {
        val LOADED=ResponseState(Status.SUCCESS)
        val LOADING=ResponseState(Status.RUNNING)
        fun error(msg:String?)=ResponseState(Status.FAILED,msg)
    }
}

enum class Status {
    RUNNING,
    FAILED,
    SUCCESS
}