package ktsSupport

import com.android.build.api.dsl.ApkSigningConfig
import java.io.File
import java.io.FileInputStream
import java.util.*

//fun setupApkSigningConfigg(apkSigningConfig: ApkSigningConfig, file: File) {
//    val keystoreProperties = loadProperties(file)
//    println("keystoreProperties -> $keystoreProperties from $file")
//
//    apkSigningConfig.apply {
//        keyAlias = keystoreProperties.getProperty("keyAlias")
//        keyPassword = keystoreProperties.getProperty("keyPassword")
//        storeFile = File(keystoreProperties.getProperty("storeFile"))
//        storePassword = keystoreProperties.getProperty("storePassword")
//    }
//}

fun loadProperties(file: File): Properties {
    println("loadProperties: $file")
    return Properties().apply {
        load(FileInputStream(file))
    }
}