import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import pl.deniotokiari.capital.gain.calculator.buildlogic.implementation

plugins {
    alias(libs.plugins.common.android.aplication.convention)
}

android {
    defaultConfig {
        versionCode = 14
        versionName = "1.0.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "GITHUB_TOKEN", "\"${gradleLocalProperties(rootDir, providers).getProperty("github.token")}\"")
        buildConfigField("String", "GITHUB_URL", "\"${gradleLocalProperties(rootDir, providers).getProperty("github.url")}\"")
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

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.kotlin.test.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":core:misc"))
}

apollo {
    service("github") {
        packageName.set("pl.deniotokiari.githubcontributioncalendar.service.github")
    }
}
