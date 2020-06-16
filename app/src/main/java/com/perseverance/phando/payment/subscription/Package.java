package com.perseverance.phando.payment.subscription;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Package {
    @SerializedName("package")
    private List<PackageDetails> packageDetails;

    public List<PackageDetails> getPackageDetails() {
        return packageDetails;
    }
}