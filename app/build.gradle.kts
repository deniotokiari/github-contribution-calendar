import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.apollographql.apollo3)
    alias(libs.plugins.com.google.gms.google.services)
    alias(libs.plugins.com.google.firebase.crashlytics)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

val appId = "pl.deniotokiari.githubcontributioncalendar"

android {
    namespace = appId
    compileSdk = 35

    defaultConfig {
        applicationId = appId
        minSdk = 26
        targetSdk = 35
        versionCode = 12
        versionName = "1.0.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "GITHUB_TOKEN", "\"${gradleLocalProperties(rootDir, providers).getProperty("github.token")}\"")
        buildConfigField("String", "GITHUB_URL", "\"${gradleLocalProperties(rootDir, providers).getProperty("github.url")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.activity.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.apollo.runtime)
    implementation(libs.bundles.glance)
    implementation(libs.bundles.koin)
    implementation(libs.core.ktx)
    implementation(libs.data.store)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.config)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.kotlin.serialization)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.material3)
    implementation(libs.navigation.compose)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.work)
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.firebase.bom))

    debugImplementation(libs.ui.test.manifest)
    debugImplementation(libs.ui.tooling)
}

apollo {
    service("github") {
        packageName.set("$appId.service.github")
    }
}
