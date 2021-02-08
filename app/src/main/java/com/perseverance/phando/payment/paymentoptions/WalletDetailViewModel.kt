package com.perseverance.phando.payment.paymentoptions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WalletDetailViewModel(application: Application) : AndroidViewModel(application) {

    var walletHistoryLiveData = MutableLiveData<WalletHistoryResponseData>()
    var tcResponseDataLiveData = MutableLiveData<TCResponseData>()
    var activateWalletLiveData = MutableLiveData<BaseResponse>()

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
        viewModelScope.launch(Dispatchers.IO) {
            val walletDetailResponseData= walletDetailRepository.refreshWallet()
            walletDetailResponseData.let {
                if (it.status=="success"){
                    it.data?.let {
                        walletDetailDao.insert(it.apply {
                            deactivate_wallet_msg=walletDetailResponseData.deactivate_wallet_msg
                            hint1=walletDetailResponseData.hint1
                            hint2=walletDetailResponseData.hint2
                            currency_code=walletDetailResponseData.currency_code?:""
                            currency_symbol=walletDetailResponseData.currency_symbol?:""
                            wallet_conversion_points=walletDetailResponseData.wallet_conversion_points
                        })
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
                activateWalletLiveData.postValue( walletDetailRepository.activateWallet(param))
            }
            refreshWallet()
        }
    }
    fun deActivateWallet() {
        viewModelScope.launch {
            val param = HashMap<String, String>()
            param.put("status","0")
            withContext(Dispatchers.IO) {
                walletDetailRepository.activateWallet(param)
            }
            refreshWallet()
        }
    }

}