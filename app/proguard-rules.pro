# Hilt/Dagger
-dontwarn dagger.hilt.**
-dontwarn javax.inject.**

# Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keep @kotlinx.serialization.Serializable class ** { *; }
-keep class kotlinx.serialization.internal.** { *; }

# Retrofit + OkHttp (generally safe defaults)
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Room (annotations used for reflection)
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Proto (lite)
-keep class com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**

# Keep model classes used in (de)serialization
-keepclassmembers class net.readian.parcel.data.model.** { *; }

