package com.perseverance.phando.payment.subscription

import android.app.Application
import androidx.lifecycle.LiveData
import com.perseverance.phando.base.BaseViewModel
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus


class SubscriptionsViewModel(application: Application) : BaseViewModel(application) {

    private var subscriptionRepository: SubscriptionRepository = SubscriptionRepository(application)
    var data: LiveData<DataLoadingStatus<Package>> = subscriptionRepository.fetchPackage()

    fun createOrder(map: Map<String, String>): LiveData<DataLoadingStatus<CreateOrderResponse>> {
        return subscriptionRepository.createOrder(map)

    }

    fun updateOrderOnServer(map: Map<String, String>): LiveData<DataLoadingStatus<BaseResponse>> {
        return subscriptionRepository.updateOrderOnServer(map)

    }

}