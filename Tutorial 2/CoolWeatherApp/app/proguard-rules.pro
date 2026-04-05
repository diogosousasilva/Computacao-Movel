# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools proguard/proguard-android.txt

# Keep Gson data classes
-keep class com.diogo.coolweatherapp.data.model.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
