package com.perseverance.phando.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.perseverance.phando.BuildConfig
import com.perseverance.phando.Session

/**
 * Created by trilokinathyadav on 1/11/2018.
 */

object FirebaseEventUtil {
    private const val APP_NOTIFICATION = "APP_NOTIFICATION"



    fun selectContent(contentType: String?, itemId: String?) {
        if (BuildConfig.DEBUG) {
            MyLog.infoAnalytic("event:id ${itemId} name ${contentType}")


        }
        try {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId)
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
            Session.instance.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        } catch (e: Exception) {

        }

    }
    fun adToCart(value: Long,itemId:String,itemCategory:String,itemName:String) {
        if ( BuildConfig.DEBUG) {
            // MyLog.infoAnalytic("event:id $searchString ")
        }
        Session.instance.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE){
            param(FirebaseAnalytics.Param.VALUE, value)
            param(FirebaseAnalytics.Param.CURRENCY, "Rs")
            param(FirebaseAnalytics.Param.TRANSACTION_ID, itemId)
            val item1 = Bundle()
            item1.putString(FirebaseAnalytics.Param.ITEM_ID, itemCategory)
            item1.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, itemCategory)
            item1.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName)
            param(FirebaseAnalytics.Param.ITEMS,item1)

        }
    }
    fun purchaseCompleted(transactionId:String,value: Long,itemId:String,itemCategory:String,itemName:String) {
        if ( BuildConfig.DEBUG) {
           // MyLog.infoAnalytic("event:id $searchString ")
        }
        Session.instance.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE){
            param(FirebaseAnalytics.Param.PRICE, value)
            param(FirebaseAnalytics.Param.TRANSACTION_ID, transactionId)
            val item1 = Bundle()
            item1.putString(FirebaseAnalytics.Param.ITEM_ID, itemId)
            item1.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, itemCategory)
            item1.putString(FirebaseAnalytics.Param.PAYMENT_TYPE, itemCategory)
            item1.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName)
            param(FirebaseAnalytics.Param.ITEMS,item1)

        }
    }
    fun viewItem(category: String?, id: String?, name: String?) {
        if (BuildConfig.DEBUG) {
            MyLog.infoAnalytic("category ${category} id ${id} name ${name}")

        }
        try {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
            Session.instance.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
        } catch (e: Exception) {

        }

    }

}
