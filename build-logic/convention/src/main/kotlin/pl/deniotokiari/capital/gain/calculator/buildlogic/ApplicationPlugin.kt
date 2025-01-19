package pl.deniotokiari.capital.gain.calculator.buildlogic

import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

private const val PACKAGE_NAME = "pl.deniotokiari.githubcontributioncalendar"

class LibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            listOf(
                "android-library",
                "org-jetbrains-kotlin-android",
            ).forEach { alias ->
                apply(libs.findPlugin(alias).get().get().pluginId)
            }
        }

        extensions.configure<LibraryExtension> {
            val moduleName = path
                .removePrefix(":")
                .split(":")
                .joinToString(".")
                .replace("-", "")

            namespace = "$PACKAGE_NAME.$moduleName"
            compileSdk = libs.findVersion("compile-sdk").get().requiredVersion.toInt()

            defaultConfig {
                minSdk = libs.findVersion("min-sdk").get().requiredVersion.toInt()

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
        }

        dependencies {

        }
    }
}
