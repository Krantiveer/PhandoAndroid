package com.perseverance.phando.home


import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.perseverance.phando.R
import kotlinx.android.synthetic.main.fragment_webview.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val URL = "url"

/**
 * A simple [Fragment] subclass.
 * Use the [WebviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class WebviewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(URL)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val settings = webview?.settings
        settings?.javaScriptEnabled = true
        //settings?.loadWithOverviewMode = true

        webview?.webViewClient = MyWebViewClient()
        webview?.webChromeClient = MyWebChromeClient()
        webview.loadUrl(url)
    }

    companion object {
        fun newInstance(url: String) =
                WebviewFragment().apply {
                    arguments = Bundle().apply {
                        putString(URL, url)
                    }
                }
    }

    inner class MyWebViewClient : WebViewClient() {


        @SuppressWarnings("deprecation")
        override fun shouldOverrideUrlLoading(view: WebView?, mUrl: String?): Boolean {

            val i = Intent(Intent.ACTION_VIEW, Uri.parse(mUrl))
            startActivity(i)
            return true


        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url.toString()

            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(i)
            return true


        }

    }

    internal inner class MyWebChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            try {
                pb.progress = newProgress
                if (newProgress > 95) {
                    pb.visibility = View.GONE
                }
            } catch (e: Exception) {

            }

        }
    }
}
