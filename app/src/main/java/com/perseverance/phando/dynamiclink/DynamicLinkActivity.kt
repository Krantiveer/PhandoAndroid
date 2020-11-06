package com.perseverance.phando.dynamiclink

import android.content.Intent
import android.net.UrlQuerySanitizer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.perseverance.phando.R
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.dashboard.HomeActivity
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.utils.Utils

class DynamicLinkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_link)
        checkForDynamicLink()
    }

    fun checkForDynamicLink() {
        Firebase.dynamicLinks
                .getDynamicLink(intent)
                .addOnSuccessListener(this) {
                    it?.let {
                        it.link?.let {
                            if (Utils.isNetworkAvailable(this@DynamicLinkActivity)) {
                                val sanitizer = UrlQuerySanitizer()
                                sanitizer.allowUnregisteredParamaters = true
                                sanitizer.parseUrl(it.toString())
                                val screen = sanitizer.getValue("screen")
                                val id = sanitizer.getValue("id")
                                val type = sanitizer.getValue("type")
                                val image = sanitizer.getValue("thumbnail")
                                val trailerId = sanitizer.getValue("trailerid")

                                id?.let {
                                    val video = Video()
                                    video.id = it.toInt()
                                    video.type = type
                                    video.thumbnail = image
                                    when(screen){

                                        "tvseries"->{
                                            val intent = Intent(this@DynamicLinkActivity, SeriesActivity::class.java)
                                            intent.putExtra(Key.CATEGORY, video)
                                            startActivity(intent)
                                        }
                                        "player" ->{
                                            startActivity(MediaDetailActivity.getDetailIntent(this@DynamicLinkActivity, video,trailerId))
                                        }
                                    }



                                }?: startActivity(Intent(this@DynamicLinkActivity, HomeActivity::class.java))


                            } else {
                                startActivity(Intent(this@DynamicLinkActivity, HomeActivity::class.java))
                            }
                        }
                                ?: startActivity(Intent(this@DynamicLinkActivity, HomeActivity::class.java))

                    } ?: startActivity(Intent(this@DynamicLinkActivity, HomeActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    startActivity(Intent(this@DynamicLinkActivity, HomeActivity::class.java))
                    finish()
                }

    }
}