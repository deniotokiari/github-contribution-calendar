plugins {
    `kotlin-dsl`
}

group = "pl.deniotokiari.capital.gain.calculator.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplicationConventionPlugin") {
            id = "gradlePlugins.android.application.convention"
            implementationClass = "pl.deniotokiari.capital.gain.calculator.buildlogic.AndroidApplicationConventionPlugin"
        }

        register("androidLibraryConventionPlugin") {
            id = "gradlePlugins.android.library.convention"
            implementationClass = "pl.deniotokiari.capital.gain.calculator.buildlogic.AndroidLibraryConventionPlugin"
        }
    }
}
