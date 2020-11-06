package com.perseverance.phando;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewTest {

    @Test
    public String getOtpFromSMS(String message) {
        String OTP_REGEX = "(|^)\\d{4}";
        Pattern pattern  = Pattern.compile(OTP_REGEX);
        Matcher matcher  = pattern.matcher(message);
        String otp = "Not found";
        while (matcher.find()) {
            otp = matcher.group();
            System.out.println(otp);
        }
        return otp;
    }


}
