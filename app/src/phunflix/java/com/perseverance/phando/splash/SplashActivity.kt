package com.perseverance.phando.splash

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.videoplayer.VideoSdkUtil
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : BaseSplashActivity() {


    private fun printKeyHash() {
        // Add code to print out the key hash
        try {
            val info: PackageInfo = packageManager.getPackageInfo("com.qait.sadhna", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                // Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("KeyHash:", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("KeyHash:", e.toString())
        }
    }
}
