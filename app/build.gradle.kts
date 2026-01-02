plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maps.secrets)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.durand.dogedex"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.durand.dogedex"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.legacy.support.v4)
    //splash
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.airbnb.lottie)
    //maps AIzaSyARn49PzNgHMtJDnAw_VOMoe0-0UCjF3QU
    implementation(libs.google.play.services.maps)
    implementation(libs.google.play.services.location)
    //viewmodel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.ktx)
    //retrofit y moshi
    implementation(libs.squareup.retrofit2.retrofit)
    implementation(libs.squareup.retrofit2.converter.moshi)
    implementation(libs.squareup.okhttp3.logging.interceptor)
    implementation(libs.squareup.retrofit2.converter.gson)
    //coil
    implementation(libs.coil)
    //navigation fragment
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui.ktx)
    //CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    //tensorflow
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)

    //image
    //user permission
    implementation(libs.karumi.dexter)

    //coil
    implementation(libs.coil.legacy)
    //chucker
    implementation(libs.chucker)
    
    // Firebase Cloud Messaging
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
}