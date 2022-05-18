// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        val libraryVers = ktsSupport.LibraryVers

        classpath("com.android.tools.build:gradle:${libraryVers.buildGradleToolsVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
//        classpath("com.google.gms:google-services:4.3.10")

        // NOTE: Do not place your application dependencies here; they belong in the individual module build.gradle files
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${libraryVers.navigationVersion}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${libraryVers.hiltVersion}")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}