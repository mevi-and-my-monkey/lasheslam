plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.example.lasheslam"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lasheslam"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Dagger Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    //Material design
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material3:material3-window-size-class:1.3.0")
    // Fragment
    implementation(libs.androidx.fragment.ktx)
    // Activity
    implementation(libs.androidx.activity.ktx)
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)
    //Corrutinas
    implementation(libs.kotlinx.coroutines.android)
    //recyclerView
    implementation(libs.androidx.recyclerview)
    // For control over item selection of both touch and mouse driven selection
    implementation(libs.androidx.recyclerview.selection)
    //convertir URL a imagen
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    //firebase
    implementation(platform(libs.firebase.bom))

    // Firebase libraries gestionadas por el BOM
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)

    // Google Play Services (Autenticación con Google)
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Firebase UI Auth (Autenticación con Firebase UI)
    implementation("com.firebaseui:firebase-ui-auth:8.0.0")
    implementation(libs.play.services.base)
    // dataStore
    implementation(libs.androidx.datastore.preferences)
    // Glide
    implementation(libs.glide.v4151)
    annotationProcessor(libs.compiler.v4151)


}
kapt {
    correctErrorTypes = true
}