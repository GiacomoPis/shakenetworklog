import com.android.build.api.dsl.ApkSigningConfig
import java.io.File
import ktsSupport.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
//    id("com.google.gms.google-services")
    kotlin("kapt")
}

android {
    compileSdk = AppConfig.compileSdkVersion

    defaultConfig {
        AppConfig.setupAppDefaultConfig(this)
    }

    val debugEnv = EnvConfig.buildForDebugEnv()
    val preProdEnv = EnvConfig.buildForPreProdEnv()
    val releaseEnv = EnvConfig.buildForReleaseEnv()

    val javaVersion = JavaVersion.VERSION_1_8

    signingConfigs {
//        getByName(debugEnv.variantName) {
//            setupApkSigningConfig(this, File(rootProject.rootDir, debugEnv.pathToKeystorePropsFile))
//        }
//
//        create(preProdEnv.variantName) {
//            setupApkSigningConfig(this, File(rootProject.rootDir, preProdEnv.pathToKeystorePropsFile))
//        }
//
//        create(releaseEnv.variantName) {
//            setupApkSigningConfig(this, File(rootDir, releaseEnv.pathToKeystorePropsFile))
//        }
    }

    buildTypes {

        debug {
            isDebuggable = true
            isMinifyEnabled = false
            setupApplicationBuildType(this, debugEnv)
//            signingConfig = signingConfigs.getByName(debugEnv.variantName)
        }

        create(preProdEnv.variantName) {
            isDebuggable = true
            isMinifyEnabled = false
            setupApplicationBuildType(this, preProdEnv)
//            signingConfig = signingConfigs.getByName(debugEnv.variantName)
            matchingFallbacks += listOf(debugEnv.variantName)
        }

        release {
            isDebuggable = false
            isMinifyEnabled = false
            setupApplicationBuildType(this, releaseEnv)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//            signingConfig = signingConfigs.getByName(EnvConfig.RELEASE_VARIANT))
        }
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {

//    implementation(project(path = ":data"))
//    implementation(project(path = ":core"))
//    implementation(project(path = ":utils"))
//    implementation(project(path = ":androidUtils"))
//    implementation(project(path = ":domain"))

    implementation(project(path = ":shakenetworklog"))

    // Test
    testImplementation("junit:junit:${LibraryVers.junitVersion}")

    // Android test
    androidTestImplementation("androidx.test.ext:junit:${LibraryVers.androidXTestExtJunitVersion}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${LibraryVers.androidXTestEspressoCoreVersion}")
    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:${LibraryVers.navigationVersion}")

    // Core
    implementation("androidx.core:core-ktx:${LibraryVers.ktxVersion}")
    implementation("androidx.appcompat:appcompat:${LibraryVers.appCompatVersion}")
    implementation("androidx.multidex:multidex:${LibraryVers.multidexVersion}")

    // Layout
    implementation("com.google.android.material:material:${LibraryVers.materialVersion}")
    implementation("androidx.constraintlayout:constraintlayout:${LibraryVers.constraintVersion}")

    // Lifecycle (Kotlin)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${LibraryVers.lifecycleVersion}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${LibraryVers.lifecycleVersion}")

    // Nav component (Kotlin)
    implementation("androidx.navigation:navigation-fragment-ktx:${LibraryVers.navigationVersion}")
    implementation("androidx.navigation:navigation-ui-ktx:${LibraryVers.navigationVersion}")
    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:${LibraryVers.navigationVersion}")

    // Dependency injection
    implementation("com.google.dagger:hilt-android:${LibraryVers.hiltVersion}")
    kapt("com.google.dagger:hilt-android-compiler:${LibraryVers.hiltVersion}")

    // Retrofit and Moshi
    implementation("com.squareup.retrofit2:retrofit:${LibraryVers.retrofitVersion}")
    implementation("com.squareup.retrofit2:converter-moshi:${LibraryVers.retrofitVersion}")
    implementation("com.squareup.okhttp3:logging-interceptor:${LibraryVers.httpLoggingInterceptorVersion}")
//    implementation("com.squareup.moshi:moshi-kotlin:${LibraryVers.moshiVersion}")

    // Log
    implementation("com.jakewharton.timber:timber:${LibraryVers.timberVersion}")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

// =====================

fun setupApkSigningConfig(apkSigningConfig: ApkSigningConfig, file: File) {
    val keystoreProperties = loadProperties(file)
    println("keystoreProperties -> $keystoreProperties from $file")

    apkSigningConfig.apply {
        keyAlias = keystoreProperties.getProperty("keyAlias")
        keyPassword = keystoreProperties.getProperty("keyPassword")
        storeFile = file(keystoreProperties.getProperty("storeFile"))
        storePassword = keystoreProperties.getProperty("storePassword")
    }
}