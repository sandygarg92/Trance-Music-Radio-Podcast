# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Volumes/Data/AndroidTools/adt-bundle-mac-x86_64/sdk/tools/proguard/proguard-android.txt
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

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-dontpreverify
-dontwarn androidx.**
-dontwarn org.apache.**
-dontwarn org.apache.http.**

-repackageclasses ''

-keep class org.apache.http.*
-keep interface org.apache.http.*

-keepattributes Signature


-dontwarn com.google.android.gms.**
-dontwarn com.google.ads.*
-dontwarn oauth.signpost.jetty.*

-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *Annotation*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class com.google.* {*;}

-keep class com.google.android.gms.common.api.GoogleApiClient {
    void connect();
    void disconnect();
}

-keep public class custom.components.package.and.name.*
 -keepclassmembers public class * extends android.view.View {
  void set*(***);
  *** get*();
}
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** w(...);
}
-keepclassmembers class * {
 	public void onClick(android.view.View);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#To keep parcelable classes (to serialize - deserialize objects to sent through Intents)
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#Keep the R
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep public class com.google.ads.* {
    public protected *;
}

-keep public class com.google.gson.* {
    public protected *;
}
-keep public class cmn.ProguardKeepMembers.*
-keep public class * implements cmn.Proguard$KeepMembers.*
-keepclassmembers class * implements cmn.Proguard$KeepMembers.* {
   <methods>;
}
-keepattributes *Annotation*
-dontwarn android.webkit.JavascriptInterface

-keepclassmembers class fqcn.of.javascript.interface.for.webview.* {
   public *;
}
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep public class androidx.appcompat.widget.* { *; }
-keep public class androidx.preference.internal.* { *; }

-keep public class * extends androidx.core.view.ActionProvider {
    public <init>(android.content.Context);
}
-keepattributes Exceptions, Signature, InnerClasses


-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,LineNumberTable, *Annotation*, EnclosingMethod

# Add this global rule
-keepattributes Signature

-dontwarn com.google.android.material.**
-keep class com.google.android.material.* { *; }
-keep interface com.google.android.material.* { *; }
-keep public class com.google.android.material.R$* { *; }
-keep public class * extends androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.* {
    public <init>(android.content.Context, android.util.AttributeSet);
}


-keep public class * extends androidx.recyclerview.widget.RecyclerView$ItemDecoration
-keep class androidx.recyclerview.widget.RecyclerView

-keep public class * extends androidx.core.view.ActionProvider {
    public <init>(android.content.Context);
}

-keep class androidx.core.app.* { *; }
-keep interface androidx.core.app.* { *; }

-keep class androidx.appcompat.* { *; }
-keep interface androidx.appcompat.* { *; }


#Okio
-keep class okio.* { *; }
-dontwarn okio.**


-keep class com.google.android.gms.common.api.GoogleApiClient {
    void connect();
    void disconnect();
}

#retrolambada
-dontwarn java.lang.invoke.*
-dontwarn '**$$Lambda$*'

# Keep public classes and methods mopub
-keep public class android.webkit.JavascriptInterface {}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#Okhttp
-keep class okhttp3.* { *;  }
-keep interface okhttp3.*{ *;  }
-dontwarn okhttp3.**

#Map
-keepclassmembers class * implements android.os.Parcelable {
    static *** CREATOR;
}

# The Maps API uses serialization.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class com.wang.avi.* { *; }
-keep class com.wang.avi.indicators.* { *; }
-keep class com.android.vending.billing.*
-keep class org.xmlpull.v1.* { *; }

##retrofit
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.* { *; }

-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform.IOS.MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform.Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-dontwarn okhttp3.internal.platform.*

#SimpleXML
-dontwarn org.simpleframework.xml.**
-keep class org.simpleframework.xml.** { *; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe.* { *; }
-keep class com.google.gson.stream.* { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.onlineradiofm.trancemusicradio.model.* { *; }
-keep class com.onlineradiofm.trancemusicradio.ypylibs.model.* { *; }
-keep class com.onlineradiofm.trancemusicradio.itunes.model.* { *; }
-keep class com.onlineradiofm.trancemusicradio.itunes.model.rss.* { *; }
-keep class com.onlineradiofm.trancemusicradio.ypylibs.imageloader.* { *; }
