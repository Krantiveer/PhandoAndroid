package com.perseverance.phando.home.profile.login

//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.GoogleAuthProvider

import android.content.Intent
import android.os.Bundle
import com.facebook.*
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.perseverance.patrikanews.utils.log
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.BaseLoginFragment
import com.perseverance.phando.R
import com.perseverance.phando.utils.MyLog
import org.json.JSONException
import java.util.*


open abstract class BaseSocialLoginFragment : BaseLoginFragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    abstract fun onSocialUserLoginSuccess(loggedInUser: SocialLoggedInUser)
    protected fun showLoader(show: Boolean) {
        if (show) {
            showProgress()
        } else {
            dismissProgress()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(appCompatActivity, gso)
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()


    }

    override fun onStart() {
        super.onStart()
        gmailSignOut()
        facebookSignOut()
    }

    protected fun initFacebook() {
        FacebookSdk.sdkInitialize(appCompatActivity);
        //buttonFacebookLogin.setPermissions(Arrays.asList("email", "public_profile", "user_friends"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                useLoginInformation(loginResult.accessToken)
                log("facebook:onSuccess:$loginResult")
            }

            override fun onCancel() {
                log("facebook:onCancel")
                toast("Facebook sign in canceled")
                showLoader(false)
                // ...
            }

            override fun onError(error: FacebookException) {
                log("facebook:onError" + error)
                showLoader(false)
                toast("Facebook sign in error")
                // ...
            }
        })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        showLoader(false)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            showProgress()
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    val loggedInUser = SocialLoggedInUser(it.id, it.displayName, it.email, "google")
                    onSocialUserLoginSuccess(loggedInUser)
                }
                log("${account?.displayName} ${account?.email} ${account?.id}")
                showLoader(false)
                // toast("sign success")
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                MyLog.d(TAG, "Google sign in failed")
                toast("Google sign in failed")
                showLoader(false)
            }
        }
    }


    protected fun gmailSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    protected fun facebookSignIn() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            useLoginInformation(accessToken)
        } else {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));

        }

    }
    // [END signin]

    private fun gmailSignOut() {
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(appCompatActivity) {

        }
    }

    private fun facebookSignOut() {
        // Google sign out
        LoginManager.getInstance().logOut();

    }

    private fun useLoginInformation(accessToken: AccessToken) {
        showProgress()
        /**
         * Creating the GraphRequest to fetch user details
         * 1st Param - AccessToken
         * 2nd Param - Callback (which will be invoked once the request is successful)
         */
        val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
            dismissProgress()
            //OnCompleted is invoked once the GraphRequest is successful
            try {
                val name = `object`.getString("name")
                val email = `object`.getString("email")
                val id = `object`.getString("id")
                val loggedInUser = SocialLoggedInUser(id, name, email, "facebook")
                onSocialUserLoginSuccess(loggedInUser)

            } catch (e: JSONException) {
                e.printStackTrace()
                dismissProgress()
            }
        }
        // We set parameters to the GraphRequest using a Bundle.
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,picture.width(200)")
        request.parameters = parameters
        // Initiate the GraphRequest
        request.executeAsync()
    }


    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

}