package com.perseverance.phando

import android.net.Uri
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import org.junit.Test

//import org.junit.Test;
/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun dynamicLink() {
        val uriPrefix = "https://phunflixqalink.page.link"
        val linkUrl = "https://phunflixqa.phando.com/watch/tvshow/episode/30"
        val shareTitle = "Cursed S01 E01"
        val shareThumbnail = "https://imstool.phando.com/?image_url=https://phunflixqa.phando.com/images/episodes/thumbnails/thumb_1596614806cursed.jpg&height=180&width=300&service=resize&quality=100"
        var dynamicLinkString = "N/A"

        val sortDynamicLink = Firebase.dynamicLinks.shortLinkAsync { // or Firebase.dynamicLinks.shortLinkAsync
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
        }.addOnSuccessListener { result ->
            dynamicLinkString = result?.shortLink.toString()
            print("sortDynamicLink  previewLink : ${result?.previewLink.toString()}")
            print("sortDynamicLink : $dynamicLinkString")

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
        print("dynamicLink : ${dynamicLink.uri}")

    }
}