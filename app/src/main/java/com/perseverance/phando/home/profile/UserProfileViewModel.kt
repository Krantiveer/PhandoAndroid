package com.perseverance.phando.home.profile

import android.app.Application
import androidx.lifecycle.*
import com.perseverance.phando.base.BaseViewModel
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.profile.login.SocialLoggedInUser
import com.perseverance.phando.payment.paymentoptions.WalletDetailRepository
import com.perseverance.phando.retrofit.Cred
import com.perseverance.phando.retrofit.LoginResponse
import com.perseverance.phando.utils.PreferencesUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserProfileViewModel( application: Application) : BaseViewModel(application) {

    private var userProfileRepository: UserProfileRepository = UserProfileRepository(application)
    private val reloadTrigger = MutableLiveData<Boolean>()
    private val walletDetailDao by lazy {
        AppDatabase.getInstance(application).walletDetailDao()
    }
    val walletDetailRepository by lazy {
        WalletDetailRepository()
    }
    fun refreshWallet() {
        viewModelScope.launch(Dispatchers.IO) {
            val walletDetailResponseData= walletDetailRepository.refreshWallet()
            walletDetailResponseData?.let {
                if (it.status=="success"){
                    it.data?.let {  walletDetailDao.insert(it) }
                }
            }
        }

    }

    //profile
    private var data: LiveData<DataLoadingStatus<UserProfileData>> = Transformations.switchMap(reloadTrigger) {
        userProfileRepository.fetchProfileData()

    }

    fun getUserProfile(): LiveData<DataLoadingStatus<UserProfileData>> = data

    fun refreshUserProfile() {
        reloadTrigger.value = true
    }

    //login
    private val loginUserTrigger = MutableLiveData<Cred>()
    var loginUserData: LiveData<DataLoadingStatus<LoginResponse>> = Transformations.switchMap(loginUserTrigger) {
        it?.let {
            userProfileRepository.dologin(it)
        }

    }

    fun loginUser(cred: Cred) {
        loginUserTrigger.value = cred
    }

    //social login
    private val socialLoginUserTrigger = MutableLiveData<SocialLoggedInUser>()
    var socialLoginUserData: LiveData<DataLoadingStatus<LoginResponse>> = Transformations.switchMap(socialLoginUserTrigger) {
        it?.let {
            userProfileRepository.doSociallogin(it)
        }

    }

    fun socialLoginUser(socialLoggedInUser: SocialLoggedInUser) {
        socialLoginUserTrigger.value = socialLoggedInUser
    }


    //Registration
    private val registerUserTrigger = MutableLiveData<Map<String, String>>()
    var registerUserData: LiveData<DataLoadingStatus<LoginResponse>> = Transformations.switchMap(registerUserTrigger) {
        it?.let {
            userProfileRepository.doRegister(it)
        }

    }

    fun registerUser(map: Map<String, String>) {
        registerUserTrigger.value = map
    }

    //get OTP
    private val getOTPTrigger = MutableLiveData<Map<String, String>>()
    val getOTPTData: LiveData<DataLoadingStatus<BaseResponse>> = Transformations.switchMap(getOTPTrigger) {
        it?.let {
            userProfileRepository.getOTP(it)
        }

    }

    fun getOTP(map: Map<String, String>) {
        getOTPTrigger.postValue(map)
    }

    //verify OTP for login
    private val verifyOTPTrigger = MutableLiveData<Map<String, String>>()

    val verifyOTPTForLoginData: LiveData<DataLoadingStatus<LoginResponse>> = Transformations.switchMap(verifyOTPTrigger) {
        it?.let {
            userProfileRepository.verifyOTP(it)
        }

    }

    fun verifyOTP(map: Map<String, String>) {
        verifyOTPTrigger.postValue(map)
    }

    //verify OTP for login
    private val verifyOTPForForgotPasswordTrigger = MutableLiveData<Map<String, String>>()

    val verifyOTPForForgotPasswordData: LiveData<DataLoadingStatus<BaseResponse>> = Transformations.switchMap(verifyOTPForForgotPasswordTrigger) {
        it?.let {
            userProfileRepository.verifyOTPForForgotPassword(it)
        }

    }

    fun verifyOTPForForgotPassword(map: Map<String, String>) {
        verifyOTPForForgotPasswordTrigger.postValue(map)
    }

    fun removeUserDownload(param: ArrayList<String>) = liveData(Dispatchers.IO) {

        emit(userProfileRepository.removeUserDownload(param))
    }

}