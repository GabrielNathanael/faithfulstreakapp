plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("kapt")
}

android {
    namespace = "com.faithfulstreak.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.faithfulstreak.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        vectorDrawables { useSupportLibrary = true }
    }

    buildFeatures { compose = true }

    composeCompiler {
        enableStrongSkippingMode.set(true)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources.excludes += setOf(
            "META-INF/DEPENDENCIES",
            "META-INF/NOTICE",
            "META-INF/LICENSE*"
        )
    }

    // ==============================================
    // 🔒 SIGNING & BUILD TYPE UNTUK RELEASE
    // ==============================================
    signingConfigs {
        create("release") {
            storeFile = file("faithful-release-key.jks")
            storePassword = "Bakwanmalang08"   // ganti sesuai password kamu
            keyAlias = "faithful"
            keyPassword = "Bakwanmalang08"     // ganti sesuai password kamu
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = false
        }
        getByName("debug") {
            isDebuggable = true
        }
    }

    // ==============================================
    // 💡 AUTO NAMA FILE APK
    // ==============================================
    applicationVariants.all {
        outputs.all {
            val appName = "FaithfulStreak"
            val buildTypeName = buildType.name
            val output = this as com.android.build.gradle.internal.api.ApkVariantOutputImpl
            output.outputFileName = "${appName}-${buildTypeName}.apk"
        }
    }
}

dependencies {
    implementation(libs.androidx.ui.geometry)
    implementation(libs.androidx.foundation)
    val composeBom = platform("androidx.compose:compose-bom:2024.10.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    implementation("androidx.navigation:navigation-compose:2.8.3")

    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("com.airbnb.android:lottie-compose:6.4.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.android.material:material:1.12.0")
}
