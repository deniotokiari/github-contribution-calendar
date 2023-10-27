import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.apollographql.apollo3)
    alias(libs.plugins.com.google.gms.google.services)
    alias(libs.plugins.com.google.firebase.crashlytics)
}

android {
    namespace = "pl.deniotokiari.githubcontributioncalendar"
    compileSdk = 34

    defaultConfig {
        applicationId = "pl.deniotokiari.githubcontributioncalendar"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "GITHUB_TOKEN", "\"${gradleLocalProperties(rootDir).getProperty("github.token")}\"")
        buildConfigField("String", "GITHUB_URL", "\"${gradleLocalProperties(rootDir).getProperty("github.url")}\"")
        buildConfigField("String", "HOME_SCREEN_AD_ID", "\"${gradleLocalProperties(rootDir).getProperty("homeScreenAdId")}\"")
        buildConfigField("String", "USER_SCREEN_AD_ID", "\"${gradleLocalProperties(rootDir).getProperty("userScreenAdId")}\"")
        buildConfigField("String", "ABOUT_SCREEN_AD_ID", "\"${gradleLocalProperties(rootDir).getProperty("aboutScreenAdId")}\"")
        buildConfigField("String", "SUPPORT_AD_ID", "\"${gradleLocalProperties(rootDir).getProperty("supportAdId")}\"")
        buildConfigField("String", "CONFIGURE_WIDGET_AD_ID", "\"${gradleLocalProperties(rootDir).getProperty("configureWidgetAdId")}\"")

        manifestPlaceholders["admobAppId"] = gradleLocalProperties(rootDir).getProperty("admobAppId")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    flavorDimensions += "blocksGeneration"
    productFlavors {
        create("random") {
            dimension = "blocksGeneration"
        }
        create("github") {
            dimension = "blocksGeneration"
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.apollo.runtime)
    implementation(libs.bundles.glance)
    implementation(libs.data.store)
    implementation(libs.work)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.bundles.koin)
    implementation(libs.navigation.compose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.play.services.ads)
    implementation(libs.firebase.config)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)

    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}

apollo {
    service("github") {
        packageName.set("pl.deniotokiari.githubcontributioncalendar.service.github")
    }
}