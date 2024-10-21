buildscript {

    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        //mío
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.6")

    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
    id ("com.google.dagger.hilt.android") version "2.48" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}