package com.perseverance.phando.contactus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.utils.PreferencesUtils
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_contactus.*
import kotlinx.android.synthetic.main.layout_header_new.*

class Contactus : BaseScreenTrackingActivity() {

    override var screenName: String = BaseConstants.CONTACT_ACTIVITY
    private val contactUsViewModel by lazy {
        ViewModelProvider(this@Contactus).get(ContactUsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactus)
        txtTitle.text = "Contact Us"

        imgBack.setOnClickListener {
            finish()
        }

        linkMobile.setOnClickListener{
            openWhatsApp()
        }

        linkEmail.setOnClickListener{
            openEmail()
        }

        contactUsViewModel.responseString.observe(this, Observer {
            progressBarCircle.gone()
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        finish()

        })

        btnSubmit.setOnClickListener {
            Utils.hideKeyboard(this)
            if (tvFirstName.text.toString().isNullOrBlank()) {
                Toast.makeText(this, "Please enter First Name", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
                else if(tvLastName.text.toString().isNullOrBlank()) {
                    Toast.makeText(this@Contactus, "Please enter Last Name", Toast.LENGTH_LONG).show()
                   return@setOnClickListener
                }
            else if(tvEmailId.text.toString().isNullOrBlank()) {
                    Toast.makeText(this@Contactus, "Please enter Email", Toast.LENGTH_LONG).show()
                   return@setOnClickListener
                }

            else if(!isValidEmail(tvEmailId.text.toString())) {
                    Toast.makeText(this@Contactus, "Please enter Valid Email Address", Toast.LENGTH_LONG).show()
                   return@setOnClickListener
                }
            else if (inputMobileContact.text.toString().isNullOrBlank()) {
                Toast.makeText(this, "Please enter Mobile Number", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            else if (inputMobileContact.text.toString().length>10) {
                Toast.makeText(this, "Please enter 10 digits Mobile Number", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            else if(tvComment.text.toString().isNullOrBlank()) {
                Toast.makeText(this@Contactus, "Please enter Comment", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else  {
                progressBarCircle.visible()
                contactUsViewModel.contactUs(tvFirstName.text.toString(),tvEmailId.text.toString(),
                    inputMobileContact.text.toString(),tvComment.text.toString() )

            }



        }




    }



    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun openWhatsApp() {
        val smsNumber = PreferencesUtils.getWhatsappnumber() //without '+'
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("http://api.whatsapp.com/send?phone=" + smsNumber.toString() + "&text=" + PreferencesUtils.getWhatsappText())
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error/n$e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openEmail() {
        if (!PreferencesUtils.getEmailContactUs().toString().isEmpty()
        ) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(PreferencesUtils.getEmailContactUs()))
            intent.data = Uri.parse("mailto:")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(
                    this@Contactus, "There is no application that support this action",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this@Contactus, "Email is not Available Now",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}