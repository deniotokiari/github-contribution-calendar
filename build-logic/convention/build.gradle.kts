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

    }
}
