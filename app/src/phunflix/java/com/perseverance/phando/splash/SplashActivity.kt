package com.perseverance.phando.splash

import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.perseverance.phando.BuildConfig


class SplashActivity : BaseSplashActivity() {
    init {
      //  dynamicLink()

    }

    fun dynamicLink() {
        val uriPrefix = "https://phunflixqalink.page.link"
        val linkUrl = "https://phunflixqa.phando.com/watch/tvshow/episode/30"
        val shareTitle = "Cursed S01 E01"
        val shareThumbnail = "https://phunflixqa.phando.com/images/episodes/thumbnails/thumb_1596614806cursed.jpg"
        var dynamicLinkString = "N/A"

        val sortDynamicLink = Firebase.dynamicLinks.shortLinkAsync { // or Firebase.dynamicLinks.shortLinkAsync
            link = Uri.parse(linkUrl)
            domainUriPrefix = uriPrefix
            androidParameters(com.perseverance.phando.BuildConfig.APPLICATION_ID) {
                minimumVersion = 6
            }
            iosParameters(com.perseverance.phando.BuildConfig.APPLICATION_ID) {
                appStoreId = "1524436726"
                minimumVersion = "2.3"
            }
            googleAnalyticsParameters {
                source = "android"
                medium = "app"
            }

            socialMetaTagParameters {
                title = shareTitle
                imageUrl = Uri.parse(shareThumbnail)
            }
        }.addOnSuccessListener { result ->
            dynamicLinkString = result?.shortLink.toString()
            Log.e("sortDynamicLink  previewLink :", result?.previewLink.toString())
            Log.e("sortDynamicLink :" ,dynamicLinkString)

        }

        val dynamicLink = Firebase.dynamicLinks.dynamicLink { // or Firebase.dynamicLinks.shortLinkAsync
            link = Uri.parse(linkUrl)
            domainUriPrefix = uriPrefix
            androidParameters(BuildConfig.APPLICATION_ID) {
                minimumVersion = 6
            }
            iosParameters(BuildConfig.APPLICATION_ID) {
                appStoreId = "1524436726"
                minimumVersion = "2.3"
            }
            googleAnalyticsParameters {
                source = "android"
                medium = "app"
            }

            socialMetaTagParameters {
                title = shareTitle
                imageUrl = Uri.parse(shareThumbnail)
            }
        }
        Log.e("dynamicLink : ",dynamicLink.uri.toString())

    }
}
