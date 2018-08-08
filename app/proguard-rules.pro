# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/sukesh/Android/Sdk/tools/proguard/proguard-android.txt
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

-dontskipnonpubliclibraryclasses
-dontobfuscate
-forceprocessing
-optimizationpasses 5

-keep class * extends android.app.Activity
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

-keep class com.facebook.** { *; }
-keep class com.google.** { *; }
-keep class org.acra.** { *; }
-keep class org.apache.** { *; }
-keep class android.support.** { *; }
-keep class com.patientz.VO.** { *; }
-keep class com.google.gson.** { *; }
-keep class org.ksoap2.** { *; }
-keep class com.android.volley.** { *; }
-keep class com.squareup.picasso.** { *; }

 -keepattributes Signature

# Suppress warnings
 -dontwarn com.androidquery.**
 -dontwarn com.google.**
 -dontwarn org.acra.**
 -dontwarn org.apache.**
 -dontwarn com.android.support.**
 -dontwarn com.google.android.gms.**
 -dontwarn com.squareup.picasso.**
 -dontwarn org.xmlpull.v1.**

