package com.perseverance.phando.payment.subscription;

import com.google.gson.annotations.SerializedName;

public class PackageDetails {
    @SerializedName("name")
    private String packageName;

    @SerializedName("amount")
    private String packagePrice;

    @SerializedName("interval")
    private String packageInterval;

    @SerializedName("interval_count")
    private String interval_count;

    @SerializedName("id")
    private String packageId;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(String packagePrice) {
        this.packagePrice = packagePrice;
    }

    public String getPackageInterval() {
        return packageInterval;
    }

    public void setPackageInterval(String packageInterval) {
        this.packageInterval = packageInterval;
    }

    @SerializedName("currency")
    private String currency;

    @SerializedName("currency_symbol")
    private String currencySymbol;

    public String getCurrency() {
        return currency;
    }


    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getInterval_count() {
        return interval_count;
    }

    public void setInterval_count(String interval_count) {
        this.interval_count = interval_count;
    }

    public PackageDetails(String packageName, String packagePrice, String packageInterval, String packageId) {
        this.packageName = packageName;
        this.packagePrice = packagePrice;
        this.packageInterval = packageInterval;
        this.packageId = packageId;
    }
}