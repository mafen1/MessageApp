plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "2.1.0"
    id("com.google.devtools.ksp")
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
    packaging {
        resources {
            excludes.add("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
        }
    }
}

dependencies {

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
    ksp(libs.androidx.room.compiler)

    // Для поддержки RxJava или Coroutine
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation (libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.recyclerview)

    implementation(libs.androidx.navigation.fragment.ktx)  // или ваша версия
    implementation(libs.androidx.navigation.ui.ktx)

    // Retrofit
    implementation (libs.retrofit)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.converter.gson)

    implementation (libs.androidx.room.runtime.v252)
    implementation (libs.androidx.room.ktx.v252)
    ksp(libs.androidx.room.compiler.v261)

    implementation(libs.converter.scalars)

    implementation (libs.okhttp)
    implementation (libs.java.websocket)
    implementation("com.google.code.gson:gson:2.13.2")

    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    implementation(libs.mockito.core)
    testImplementation(libs.mockwebserver)

    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson.v300)
    implementation(libs.circleimageview)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.material.v1110)


}

