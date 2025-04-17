plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "2.1.0"
}

android {
    namespace = "com.example.messageapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.messageapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // todo ksp 

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    // Для поддержки RxJava или Coroutine
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.recyclerview)

    implementation(libs.androidx.navigation.fragment.ktx)  // или ваша версия
    implementation(libs.androidx.navigation.ui.ktx)

    // Retrofit
    implementation (libs.retrofit)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.converter.gson)

    // Основная зависимость для Room
    implementation (libs.androidx.room.runtime.v252)
    // Для корутин и LiveData
    implementation (libs.androidx.room.ktx.v252)
    // Компилятор для обработки аннотаций (используйте kapt для Kotlin)
    kapt("androidx.room:room-compiler:2.6.1")

    implementation(libs.converter.scalars)

    implementation (libs.okhttp)
    implementation (libs.java.websocket)


}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}
