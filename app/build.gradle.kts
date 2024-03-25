plugins {
    //alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    // ...
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "fr.isen.mouillot.sportscape"
    compileSdk = 34

    defaultConfig {
        applicationId = "fr.isen.mouillot.sportscape"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies

    // Firebase Analytics
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Firebase Authentication
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-auth-ktx")

    // Cloud Firestore
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Realtime Database
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-database-ktx")

    // Firebase Cloud Messaging (FCM)
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Firebase Storage
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-storage-ktx")

    // Firebase Crashlytics
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Firebase Remote Config
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-config-ktx")

    // (Optional) Add the dependencies for any other Firebase products you want to use
    // Check the Firebase documentation for the latest versions and additional services: https://firebase.google.com/docs/android/setup#available-libraries
}
