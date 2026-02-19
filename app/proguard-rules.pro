# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve NetworkResult and all its inner classes (sealed class pattern)
-keep class com.example.manospy.util.NetworkResult { *; }
-keep class com.example.manospy.util.NetworkResult$* { *; }
-keepclassmembers class com.example.manospy.util.NetworkResult$* {
    public static final com.example.manospy.util.NetworkResult$* INSTANCE;
    public final com.example.manospy.util.NetworkResult getInstance();
}

# Keep all ViewModel classes
-keep class com.example.manospy.ui.viewmodel.** { *; }
-keep class com.example.manospy.admin.ui.viewmodel.** { *; }

# Keep all data models
-keep class com.example.manospy.data.model.** { *; }
-keep class com.example.manospy.admin.data.model.** { *; }

# Keep Retrofit and API interfaces
-keep interface com.example.manospy.data.api.** { *; }

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

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