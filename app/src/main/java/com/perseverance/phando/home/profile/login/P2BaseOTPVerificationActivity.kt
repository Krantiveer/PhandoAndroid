package com.perseverance.phando.home.profile.login

import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.perseverance.phando.R
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.utils.MyLog
import kotlinx.android.synthetic.main.activity_p2_otp_verification.*
import java.util.concurrent.TimeUnit


abstract class P2BaseOTPVerificationActivity : BaseUserLoginActivity() {
    var countDownTimer: CountDownTimer? = null
    protected lateinit var mobileNo: String
    protected lateinit var countryCode: String

    abstract fun verifyOTP()
    var smsBroadcastReceiver: MySMSBroadcastReceiver? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_p2_otp_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verifyOTP.setOnClickListener {
            verifyOTP()

        }
        resendOTP.setOnClickListener {
            val map: MutableMap<String, String> = HashMap()
            map["country_code"] = countryCode
            map["mobile"] = mobileNo
            userProfileViewModel.getOTP(map)
        }
        mobileNo = arguments?.getString("MOBILE")!!
        countryCode = arguments?.getString("COUNTRY_CODE")!!
        mobile.text = "+$countryCode$mobileNo"
        startSmsRetriever()
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_p2_otp_verification)
//        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)
//        verifyOTP.setOnClickListener {
//            verifyOTP()
//
//        }
//        resendOTP.setOnClickListener {
//            val map: MutableMap<String, String> = HashMap()
//            map["country_code"] = countryCode
//            map["mobile"] = mobileNo
//            userProfileViewModel.getOTP(map)
//        }
//        mobileNo = intent.getStringExtra("MOBILE")
//        countryCode = intent.getStringExtra("COUNTRY_CODE")
//        mobile.text = "+$countryCode$mobileNo"
//
//    }


    protected fun startTimer() {
        otpHint?.setText(getString(R.string.otp_hint_for_timer))
        resendOTP.isEnabled = false
        resendOTP.alpha = 0.5f
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                resendOTP?.setText("" + String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                // txtTimer.setText(getFormatedDuration(millisUntilFinished))

            }

            override fun onFinish() {
                otpHint?.setText(getString(R.string.otp_hint_for_resent))
                resendOTP?.setText("")
                resendOTP?.setText("Resend")
                resendOTP?.isEnabled = true
                resendOTP?.alpha = 1.0f

            }
        }
        countDownTimer?.start()
    }


    override fun onGetOtpSuccess(baseResponse: BaseResponse) {
        inputOTP.setOTP("")
        startTimer()


    }

    private fun startSmsRetriever() {
        SmsRetriever.getClient(appCompatActivity).also {
            it.startSmsRetriever()
                    .addOnSuccessListener {
                        MyLog.e("LISTENING_SUCCESS")
                    }
                    .addOnFailureListener {
                        MyLog.e("LISTENING_FAILURE")
                    }
        }
    }

    override fun onResume() {
        super.onResume()
        registerBroadcastReceiver()
    }

    override fun onPause() {
        super.onPause()
        appCompatActivity.unregisterReceiver(smsBroadcastReceiver)
    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = MySMSBroadcastReceiver().also {
            it.smsBroadcastReceiverListener = object : MySMSBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(otp: String?) {
                    otp?.let {
                        inputOTP.setOTP(it)
                    }

                }

                override fun onFailure() {
                }
            }
        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        appCompatActivity.registerReceiver(smsBroadcastReceiver, intentFilter)

    }

}
