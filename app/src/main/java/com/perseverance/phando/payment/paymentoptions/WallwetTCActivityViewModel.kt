package com.perseverance.phando.payment.paymentoptions

import android.app.Activity
import android.app.Application
import android.widget.Toast
import com.perseverance.phando.BaseScreenTrackingActivity
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

class WallwetTCActivityViewModel(application: Application) : AndroidViewModel(application) {

    var tcResponseDataLiveData = MutableLiveData<TCResponseData>()

    private val walletDetailRepository by lazy {
        WalletDetailRepository()
    }

    fun getTC() {
        viewModelScope.launch(Dispatchers.IO)  {
            tcResponseDataLiveData.postValue(walletDetailRepository.getTC())
        }
    }

}

