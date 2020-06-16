package com.perseverance.phando.customtab

//import com.razorpay.Razorpay

import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.perseverance.phando.R
import com.perseverance.phando.payment.subscription.PaymentResponse
import kotlinx.android.synthetic.main.activity_webview.*


class WebviewActivity : AppCompatActivity() {
//var razorpay : Razorpay?=null
var uploadMessage: ValueCallback<Array<Uri?>?>? = null
    private var mUploadMessage: ValueCallback<Uri?>? = null
    val REQUEST_SELECT_FILE = 100
    private val FILECHOOSER_RESULTCODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val settings = webview?.settings
        settings?.javaScriptEnabled = true
        settings?.allowFileAccess=true

        settings?.domStorageEnabled = true;
        settings?.allowContentAccess = true;

        webview.webViewClient=MyWebViewClient()
        webview.webChromeClient=MyWebChromeClient()
        val url = intent.getStringExtra("url")
        webview.loadUrl(url)
       // razorpay = Razorpay(getString(R.string.rpkey), webview, this@WebviewActivity)
        webview.addJavascriptInterface( PaymentInterface(this@WebviewActivity,object :PaymentInterface.PaymentListener{
            override fun sendData(paymentResponse: PaymentResponse?) {
                Toast.makeText(this@WebviewActivity, paymentResponse?.message, Toast.LENGTH_SHORT).show()
                when(paymentResponse?.status){
                    1 ->{
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    0->{

                    }

                }

            }

        }),"PaymentInterface")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action === KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webview.canGoBack()) {
                        webview.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    inner class MyWebViewClient : WebViewClient() {



        @SuppressWarnings("deprecation")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            webview.loadUrl(url)
            return true
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            webview.loadUrl(request?.url.toString())
            return true
        }



    }

    inner class MyWebChromeClient : WebChromeClient() {


        protected fun openFileChooser(uploadMsg: ValueCallback<Uri?>, acceptType: String?) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE)
        }


        // For Lollipop 5.0+ Devices
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onShowFileChooser(mWebView: WebView?, filePathCallback: ValueCallback<Array<Uri?>?>, fileChooserParams: FileChooserParams): Boolean {
            if (uploadMessage != null) {
                uploadMessage!!.onReceiveValue(null)
                uploadMessage = null
            }
            uploadMessage = filePathCallback
            val intent = fileChooserParams.createIntent()
            try {
                startActivityForResult(intent, REQUEST_SELECT_FILE)
            } catch (e: ActivityNotFoundException) {
                uploadMessage = null
                Toast.makeText(this@WebviewActivity, "Cannot Open File Chooser", Toast.LENGTH_LONG).show()
                return false
            }
            return true
        }

        //For Android 4.1 only
        protected fun openFileChooser(uploadMsg: ValueCallback<Uri?>, acceptType: String?, capture: String?) {
            mUploadMessage = uploadMsg
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE)
        }

        protected fun openFileChooser(uploadMsg: ValueCallback<Uri?>) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE)
        }




    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == Razorpay.UPI_INTENT_REQUEST_CODE) {
//            razorpay?.onActivityResult(requestCode, resultCode, data)
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null) return
                uploadMessage!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent))
                uploadMessage = null
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            val result = if (intent == null || resultCode != RESULT_OK) null else intent.data
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        } else Toast.makeText(this@WebviewActivity, "Failed to Upload Image", Toast.LENGTH_LONG).show()
    }
}
