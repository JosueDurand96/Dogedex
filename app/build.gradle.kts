plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("com.google.devtools.ksp") version "1.9.0-1.0.12"
}

android {
    namespace = "com.durand.dogedex"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.durand.dogedex"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
    }
    allprojects {
        tasks.withType<JavaCompile> {
            options.compilerArgs.addAll(
                listOf(
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
                )
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
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    //splash
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("com.airbnb.android:lottie:5.2.0")
    //maps AIzaSyARn49PzNgHMtJDnAw_VOMoe0-0UCjF3QU
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    //viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("androidx.activity:activity-ktx:1.6.0")
    //retrofit y moshi
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.squareup.retrofit2:converter-gson:2.5.0")
    //coil
    implementation("io.coil-kt:coil:1.4.0")
    //navigation fragment
    implementation("androidx.navigation:navigation-fragment:2.5.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.2")
    //CameraX
    implementation("androidx.camera:camera-camera2:1.1.0-beta02")
    implementation("androidx.camera:camera-lifecycle:1.1.0-beta02")
    implementation("androidx.camera:camera-view:1.1.0-beta02")

    //tensorflow
    implementation("org.tensorflow:tensorflow-lite:0.0.0-nightly-SNAPSHOT")
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")

    //image
    //user permission
    implementation("com.karumi:dexter:6.2.2")

    //coil
    implementation("io.coil-kt:coil:1.1.1")
    //chucker
    implementation("com.github.chuckerteam.chucker:library:4.0.0")
}