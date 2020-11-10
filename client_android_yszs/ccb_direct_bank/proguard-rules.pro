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

-keep class com.tongda.ccb_direct_bank.CCBMainActivity.** { *; }
-keep class com.tongda.ccb_direct_bank.CcbWebViewActivity.** { *; }
-keep class com.tongda.ccb_direct_bank.CCBMainActivityLoader.** { *; }
-keep class com.tongda.ccb_direct_bank.CCBMainGroupLoader.** { *; }
-keep class com.tongda.ccb_direct_bank.UrlProcessor.** { *; }
-keep class com.tongda.ccb_direct_bank.constant.** { *; }
-keep class com.tongda.ccb_direct_bank.controller.** { *; }
-keep class com.tongda.ccb_direct_bank.entity.** { *; }
-keep class com.tongda.ccb_direct_bank.Listener.** { *; }
-keep class com.tongda.ccb_direct_bank.utils.** { *; }
-keep class com.tendyron.**{*;}
-keep class com.sensetime.**{*;}
-keep class com.intsig.**{*;}

-keep class com.tongda.ziubao.WVJBWebViewClient$WVJBMessage
-keepclassmembers public class com.tongda.ziubao.WVJBWebViewClient{
   <fields>;
   <methods>;
   public *;
   private *;
}
-keepclassmembers class com.tongda.ziubao.WVJBWebViewClient$WVJBMessage{
    <fields>;
}