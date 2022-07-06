package com.perseverance.phando.home.profile

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import com.perseverance.phando.base.BaseViewModel
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.data.NotificationsSettingsModel
import com.perseverance.phando.data.ParentSettingPost
import com.perseverance.phando.data.ParentalControlData
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.profile.login.SocialLoggedInUser
import com.perseverance.phando.payment.paymentoptions.WalletDetailRepository
import com.perseverance.phando.retrofit.Cred
import com.perseverance.phando.retrofit.LoginResponse
import com.perseverance.phando.utils.PreferencesUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserProfileViewModel(application: Application) : BaseViewModel(application) {

    private var userProfileRepository: UserProfileRepository = UserProfileRepository(application)
    private val reloadTrigger = MutableLiveData<Boolean>()
    private val notificationsTrigger = MutableLiveData<Boolean>()

    private val walletDetailDao by lazy {
        AppDatabase.getInstance(application).walletDetailDao()
    }
    private val walletDetailRepository by lazy {
        WalletDetailRepository()
    }
    val languageList by lazy {
        AppDatabase.getInstance(application).languageDao().allLanguage()
    }


    fun refreshWallet() {
        viewModelScope.launch(Dispatchers.IO) {
            val walletDetailResponseData = walletDetailRepository.refreshWallet()
            walletDetailResponseData?.let {
                if (it.status == "success") {
                    it.data?.let {
                        walletDetailDao.insert(it.apply {
                            deactivate_wallet_msg = walletDetailResponseData.deactivate_wallet_msg
                            hint1 = walletDetailResponseData.hint1
                            hint2 = walletDetailResponseData.hint2
                            currency_code = walletDetailResponseData.currency_code ?: ""
                            currency_symbol = walletDetailResponseData.currency_symbol ?: ""
                            wallet_conversion_points =
                                walletDetailResponseData.wallet_conversion_points
                        })
                    }
                }
            }
        }

    }

    //profile
    private var data: LiveData<DataLoadingStatus<UserProfileData>> =
        Transformations.switchMap(reloadTrigger) {
            userProfileRepository.fetchProfileData()

        }

    fun getUserProfile(): LiveData<DataLoadingStatus<UserProfileData>> = data

    fun refreshUserProfile() {
        reloadTrigger.value = true
    }

    //notifcations settings
    private var notifcationData: LiveData<DataLoadingStatus<NotificationsSettingsModel>> =
        Transformations.switchMap(notificationsTrigger) {
            userProfileRepository.notificationSettings()
        }

    fun getNotificationsSettings(): LiveData<DataLoadingStatus<NotificationsSettingsModel>> =
        notifcationData

    fun refreshNotificationsSettings() {
        notificationsTrigger.value = true
    }

    //parentalControlData settings
    private val parentalControlTrigger = MutableLiveData<Boolean>()

    private var parentalControlData: LiveData<DataLoadingStatus<ParentalControlData>> =
        Transformations.switchMap(parentalControlTrigger) {
            userProfileRepository.parentalControlSettings()
        }

    fun getparentalControl(): LiveData<DataLoadingStatus<ParentalControlData>> =
        parentalControlData

    fun refreshparentalControl() {
        parentalControlTrigger.value = true
    }

    //login
    private val loginUserTrigger = MutableLiveData<Cred>()
    var loginUserData: LiveData<DataLoadingStatus<LoginResponse>> =
        Transformations.switchMap(loginUserTrigger) {
            it?.let {
                userProfileRepository.doLogin(it)
            }

        }

    fun loginUser(cred: Cred) {
        loginUserTrigger.value = cred
    }

    //social login
    private val socialLoginUserTrigger = MutableLiveData<SocialLoggedInUser>()
    var socialLoginUserData: LiveData<DataLoadingStatus<LoginResponse>> =
        Transformations.switchMap(socialLoginUserTrigger) {
            it?.let {
                userProfileRepository.doSocialLogin(it)
            }

        }

    fun socialLoginUser(socialLoggedInUser: SocialLoggedInUser) {
        socialLoginUserTrigger.value = socialLoggedInUser
    }


    //Registration
    private val registerUserTrigger = MutableLiveData<Map<String, String>>()
    var registerUserData: LiveData<DataLoadingStatus<LoginResponse>> =
        Transformations.switchMap(registerUserTrigger) {
            it?.let {
                userProfileRepository.doRegister(it)
            }
        }

    fun registerUser(map: Map<String, String>) {
        registerUserTrigger.value = map
    }

    //NotificationsSettings
    private val setNotificationsTrigger = MutableLiveData<Map<String, Boolean>>()
    var setNotificationsSettings: LiveData<DataLoadingStatus<BaseResponse>> =
        Transformations.switchMap(setNotificationsTrigger) {
            it?.let {
                userProfileRepository.setNotificationsSettings(it)
            }

        }

    fun setNotificationsSetting(map: Map<String, Boolean>) {
        setNotificationsTrigger.value = map
    }

    //updateUserPinTrigger
    private val updateUserPinTrigger = MutableLiveData<Map<String, String>>()
    var updateUserPin: LiveData<DataLoadingStatus<BaseResponse>> =
        Transformations.switchMap(updateUserPinTrigger) {
            it?.let {
                userProfileRepository.updateUserPin(it)
            }

        }

    fun setupdateUserPin(map: Map<String, String>) {
        updateUserPinTrigger.value = map
    }

    // validateUserPinTrigger
    private val validateUserPinTrigger = MutableLiveData<Map<String, String>>()
    var validateUserPin: LiveData<DataLoadingStatus<BaseResponse>> =
        Transformations.switchMap(validateUserPinTrigger) {
            it?.let {
                userProfileRepository.validateUserPin(it)
            }

        }

    fun setValidateUserPin(map: Map<String, String>) {
        validateUserPinTrigger.value = map
    }

 // parentalSettingTrigger
    private val parentalSettingTrigger = MutableLiveData<ParentSettingPost>()
    var parentalSetting: LiveData<DataLoadingStatus<BaseResponse>> =
        Transformations.switchMap(parentalSettingTrigger) {
            it?.let {
                userProfileRepository.parentalSetting(it)
            }

        }

    fun setParentalSetting(map: ParentSettingPost) {
        parentalSettingTrigger.value = map
    }

    //get OTP
    private val getOTPTrigger = MutableLiveData<Map<String, String>>()
    val getOTPTData: LiveData<DataLoadingStatus<BaseResponse>> =
        Transformations.switchMap(getOTPTrigger) {
            it?.let {
                userProfileRepository.getOTP(it)
            }

        }

    fun getOTP(map: Map<String, String>) {
        getOTPTrigger.postValue(map)
    }

    //verify OTP for login
    private val verifyOTPTrigger = MutableLiveData<Map<String, String>>()

    val verifyOTPTForLoginData: LiveData<DataLoadingStatus<LoginResponse>> =
        Transformations.switchMap(verifyOTPTrigger) {
            it?.let {
                userProfileRepository.verifyOTP(it)
            }

        }

    fun verifyOTP(map: Map<String, String>) {
        verifyOTPTrigger.postValue(map)
    }

    //verify OTP for login
    private val verifyOTPForForgotPasswordTrigger = MutableLiveData<Map<String, String>>()

    val verifyOTPForForgotPasswordData: LiveData<DataLoadingStatus<BaseResponse>> =
        Transformations.switchMap(verifyOTPForForgotPasswordTrigger) {
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

    fun getSavesUserProfile(): UserProfileData? {
        val strProfile = PreferencesUtils.getStringPreferences("profile")
        return Gson().fromJson(strProfile, UserProfileData::class.java)
    }

    suspend fun updateLanguagePreference(map: Map<String, String>): BaseResponse {
        return userProfileRepository.updateLanguagePreference(map)
    }

}