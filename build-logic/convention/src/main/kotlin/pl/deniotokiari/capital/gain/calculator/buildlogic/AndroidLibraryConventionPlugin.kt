package pl.deniotokiari.capital.gain.calculator.buildlogic

import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
        }

        extensions.configure<LibraryExtension> {
            val moduleName = path
                .removePrefix(":")
                .split(":")
                .joinToString(".")
                .replace("-", "")

            namespace = "$BASE_PACKAGE_NAME.$moduleName"

            compileSdk = libs.findVersion("compile-sdk").get().requiredVersion.toInt()

            defaultConfig {
                minSdk = libs.findVersion("min-sdk").get().requiredVersion.toInt()

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
            }

            buildTypes {
                release {
                    isMinifyEnabled = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            buildFeatures {
                compose = false
                buildConfig = true
            }
        }

        dependencies {
            listOf(
                "core-ktx",
                "androidx-appcompat",
                "material",
            )
                .asSequence()
                .map { libs.findLibrary(it).get().get() }
                .forEach(::implementation)
        }
    }
}

/*

/*dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}*/
 */
