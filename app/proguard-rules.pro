# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature, InnerClasses, EnclosingMethod


# Application classes that will be serialized/deserialized over Gson
-keep class vn.truongnnt.atmpro.trafficlight.api.request.** { <fields>; }
-keep class vn.truongnnt.atmpro.trafficlight.api.response.** { <fields>; }


-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**


## keep Enum in Response Objects
-keepclassmembers enum com.android.services.** { *; }


## Note not be needed unless some model classes don't implement Serializable interface
## Keep model classes used by ORMlite
-keep class com.android.model.**


## keep classes and class members that implement java.io.Serializable from being removed or renamed
## Fixes "Class class com.twinpeek.android.model.User does not have an id field" execption
#-keep class * implements java.io.Serializable {
#    *;
#}

## Rules for Retrofit2
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions


## Rules for Gson
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
-keep class sun.misc.** { *; }
-keep class com.google.gson.stream.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Rules for OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

# Rules for Javamail
-keep class javax.** {*;}
-keep class com.sun.** {*;}
#-keep class myjava.** {*;}
-keep class org.apache.harmony.** {*;}
#-keep public class MailClient {*;}
-dontwarn com.sun.mail.**
-dontwarn java.awt.**
-dontwarn java.beans.Beans
-dontwarn javax.security.**

# Otto Library
#-keepclassmembers class ** {
    #@com.squareup.otto.Subscribe public *;
    #@com.squareup.otto.Produce public *;
#}

# Remove logs, don't forget to use 'proguard-android-optimize.txt' file in build.gradle
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    #public static int e(...);
    public static int wtf(...);
}
