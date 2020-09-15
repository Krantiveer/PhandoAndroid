package com.perseverance.phando.payment.paymentoptions

import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.isSuccess
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.payment.PurchaseOption
import com.perseverance.phando.payment.subscription.CreateOrderResponse
import com.perseverance.phando.payment.subscription.SubscriptionRepository
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_payment_options.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class PaymentActivityViewModel(application: Application) : AndroidViewModel(application) {

    var purchaseOption: PurchaseOption?=null
    var walletHistoryLiveData = MutableLiveData<WalletHistoryResponseData>()
    var tcResponseDataLiveData = MutableLiveData<TCResponseData>()
    var activateWalletLiveData = MutableLiveData<BaseResponse>()
    var updateOrderOnServerLiveData = MutableLiveData<BaseResponse>().apply {
        null
    }
    var createOrderResponseLiveData = MutableLiveData<CreateOrderResponse>().apply {
        null
    }
    var loaderLiveData = MutableLiveData<Boolean>().apply {
        false
    }
    private val walletDetailRepository by lazy {
        WalletDetailRepository()
    }

    private val walletDetailDao by lazy {
        AppDatabase.getInstance(application).walletDetailDao()
    }
    val walletDetailLiveData= MutableLiveData<WalletDetail>()

    fun getWallet(): WalletDetail? {
        return  walletDetailDao.getWalletDetail()
    }

    fun refreshWallet() {
        loaderLiveData.value=true
        viewModelScope.launch(Dispatchers.IO) {
            val walletDetailResponseData= walletDetailRepository.refreshWallet()
            walletDetailResponseData.let {
                loaderLiveData.postValue(false)
                if (it.status=="success"){
                    it.data?.let {
                        walletDetailDao.insert(it)
                        walletDetailLiveData.postValue(walletDetailDao.getWalletDetail())
                    }
                }
            }
        }

    }

    fun getWalletHistory() {
        viewModelScope.launch(Dispatchers.IO)  {
            walletHistoryLiveData.postValue(walletDetailRepository.getWalletHistory())
        }
    }
    fun getTC() {
        viewModelScope.launch(Dispatchers.IO)  {
            tcResponseDataLiveData.postValue(walletDetailRepository.getTC())
        }
    }


    fun activateWallet() {
        viewModelScope.launch {
            val param = HashMap<String, String>()
            param.put("status","1")
            withContext(Dispatchers.IO) {
                val response = walletDetailRepository.activateWallet(param)
                val walletDetailResponseData= walletDetailRepository.refreshWallet()
                walletDetailResponseData.let {
                    loaderLiveData.postValue(false)
                    if (it.status=="success"){
                        it.data?.let {
                            walletDetailDao.insert(it)
                            walletDetailLiveData.postValue(walletDetailDao.getWalletDetail())
                        }
                    }
                }
                activateWalletLiveData.postValue(response)

            }

        }
    }
    fun deActivateWallet() {
        viewModelScope.launch {
            val param = HashMap<String, String>()
            param.put("status","0")
            withContext(Dispatchers.IO) {
                val response = walletDetailRepository.activateWallet(param)
                activateWalletLiveData.postValue(response.apply {
                    message="deactivated"
                })
            }
        }
    }
    //Payment
     suspend fun createOrder(map: Map<String, String?>): CreateOrderResponse {
        return walletDetailRepository.createOrder(map)

    }

    suspend fun updateOrderOnServer(map: Map<String, String>): BaseResponse {
        return walletDetailRepository.updateOrderOnServer(map)

    }

}

