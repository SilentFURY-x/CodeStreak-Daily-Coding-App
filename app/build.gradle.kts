// build.gradle.kts (Module :app)
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // For Room and Hilt
    id("com.google.dagger.hilt.android") // Dependency Injection
    id("com.google.gms.google-services") // Firebase
}

android {
    namespace = "com.fury.codestreak"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fury.codestreak"
        minSdk = 26
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
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    // Allow Hilt to generate code
    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    // 0. Confetti Animation
    implementation("nl.dionsegijn:konfetti-compose:2.0.4")

    // 1. Core Android & Compose (The UI)
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.0")

    // Extended Icons (For those nice filled/outlined icons in your UI)
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    // 2. Navigation (Moving between screens)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // 3. Hilt (Dependency Injection - The Architecture)
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // 4. Firebase (Auth & Database)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // 5. Room (Offline Database)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // 6. Retrofit & GSON (For Codeforces API)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // To debug API calls

    // 7. Data Visualization (For the charts in the stats screen)
    // We will use Vico or MPAndroidChart later, but let's stick to core for now.

    // 8. System UI Controller (To make the status bar transparent/dark)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    // 9. WorkManager (Background Tasks & Notifications)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}