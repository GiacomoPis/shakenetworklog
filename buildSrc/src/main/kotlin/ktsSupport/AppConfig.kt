package ktsSupport

import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.LibraryDefaultConfig

object AppConfig {

    private const val applicationId = "it.icemangp.shakenetworklog"
    private const val minSdkVersion = 21
    private const val targetSdkVersion = 31

    const val compileSdkVersion = 31

    private const val versionMajor = 1
    private const val versionMinor = 0
    private const val versionPatch = 0
    private const val releaseName = ""

    fun setupAppDefaultConfig(appDefaultConfig: ApplicationDefaultConfig) {
        with(appDefaultConfig) {
            applicationId = AppConfig.applicationId
            minSdk = minSdkVersion
            targetSdk = targetSdkVersion
            versionCode = versionMajor * 100000 + versionMinor * 1000 + versionPatch
            versionName = "${versionMajor}.${versionMinor}.${versionPatch}${releaseName}"

            multiDexEnabled = true

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }
    }

    fun setupLibraryDefaultConfig(libDefaultConfig: LibraryDefaultConfig) {
        with(libDefaultConfig) {
            minSdk = minSdkVersion
            targetSdk = targetSdkVersion

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
}

fun ApplicationBuildType.buildConfigString(name: String, value: String) {
    this.buildConfigField("String", name, "\"${value}\"")
}

fun ApplicationBuildType.resValueString(name: String, value: String) {
    this.resValue("string", name, value)
}

fun ApplicationBuildType.setManifestPlaceholder(name: String, value: String) {
    manifestPlaceholders[name] = value
}

fun setupApplicationBuildType(appBuildType: ApplicationBuildType, envConfig: EnvConfig) {
    appBuildType.apply {
        // GOOGLE API KEY
        setManifestPlaceholder("google_api_key", envConfig.googleApiKey)
        buildConfigString("GOOGLE_API_KEY", envConfig.googleApiKey)

        // API
        buildConfigString("API_BASE_URL", envConfig.apiBaseUrl)
    }
}

