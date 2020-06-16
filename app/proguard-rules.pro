# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Development-Tools\adt-bundle-windows-x86_64-20130917\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Platform calls Class.forName on types which do not exist on Android to determine platform.
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**
-dontwarn javax.annotation.**
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn org.joda.convert.**
-dontwarn com.google.gms.**

# Retain generic type information for use by reflection by converters and adapters.
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Signature
-keepattributes Exceptions
-keepattributes Annotation

-keep class com.squareup.okhttp3.** { public *; }
-keep interface com.squareup.okhttp3.** { public *; }
-keep class android.support.** { public *; }

-keep class com.perseverance.phando.** { *; }

-keep public class com.google.gms.* { public *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep public class * extends android.support.v4.*
-keep public class * extends android.app.Fragment
