# Отладочные стек-трейсы в release
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Gson: DTO сериализуются рефлексией ---
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.example.messageapp.data.network.model.** { *; }
-dontwarn com.google.gson.**

# --- java-websocket (org.java_websocket) ---
-dontwarn org.java_websocket.**
-keep class org.java_websocket.** { *; }

# --- OkHttp / Okio / Retrofit ---
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# sl4j-facade тянется транзитивно (java-websocket), на Android биндинг не нужен
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.spi.SLF4JServiceProvider
