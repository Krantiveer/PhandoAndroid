package com.perseverance.phando.customtab;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.gson.Gson;
import com.perseverance.phando.payment.subscription.PaymentResponse;
import com.perseverance.phando.utils.MyLog;

class PaymentInterface{

  Context mContext;
  PaymentListener paymentListener;


  PaymentInterface(Context c,PaymentListener mPaymentListener) {
    mContext = c;
    paymentListener=mPaymentListener;
  }

  @JavascriptInterface
  public void success(String data){
    MyLog.e("PaymentInterface",data);
    PaymentResponse paymentResponse = new Gson().fromJson(data,PaymentResponse.class);
    paymentListener.sendData(paymentResponse);
    Toast.makeText(mContext, paymentResponse.getMessage(), Toast.LENGTH_LONG).show();


  }
  
  @JavascriptInterface
  public void error(String data){
    MyLog.e("PaymentInterface",data);
    PaymentResponse paymentResponse = new Gson().fromJson(data,PaymentResponse.class);
    paymentListener.sendData(paymentResponse);
    Toast.makeText(mContext, paymentResponse.getMessage(), Toast.LENGTH_LONG).show();

  }

  public interface PaymentListener {
    void sendData(PaymentResponse paymentResponse);
  }

}