package com.perseverance.phando

import org.junit.Test
import java.util.regex.Pattern

//import org.junit.Test;
/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class ExampleUnitTest {
    val OTP_REGEX = "(|^)\\d{4}"
    val message = "[#] 3153 is the One-time Password (OTP) to complete your transaction. Please do not share this with anyone. nKo5TwH9MJc"
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        val OTP_REGEX = "(|^)\\d{4}"
        val message = "[#] 3153 is the One-time Password (OTP) to complete your transaction. Please do not share this with anyone. nKo5TwH9MJc"
        val pattern  = Pattern.compile(OTP_REGEX)
        val matcher  = pattern.matcher(message)
        var otp = ""
        while (matcher.find()) {
            otp = matcher.group()
            print(otp)
        }
    }
}