package ktsSupport

class EnvConfig(
    val variantName: String,
    val pathToKeystorePropsFile: String,
    val apiBaseUrl: String,
    val googleApiKey: String,
) {

    companion object {

        private const val DEBUG_VARIANT = "debug"
        private const val PREPROD_VARIANT = "preProd"
        private const val RELEASE_VARIANT = "release"

        private val DEBUG = EnvConfig(
            variantName = DEBUG_VARIANT,
            pathToKeystorePropsFile = "keystore/debug/debug-signing.properties",
            apiBaseUrl = "http://your.apiurl.here/",
            googleApiKey = "AIzaXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
        )

        private val PREPROD = EnvConfig(
            variantName = PREPROD_VARIANT,
            pathToKeystorePropsFile = "keystore/debug/debug-signing.properties",
            apiBaseUrl = "http://your.apiurl.here/",
            googleApiKey = "AIzaXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
        )

        private val RELEASE = EnvConfig(
            variantName                    = RELEASE_VARIANT,
            pathToKeystorePropsFile        = "keystore/release/release-signing.properties",
            apiBaseUrl = "http://your.apiurl.here/",
            googleApiKey = "AIzaXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
        )

        fun buildForDebugEnv(): EnvConfig {
            return DEBUG
        }

        fun buildForPreProdEnv(): EnvConfig {
            return PREPROD
        }

        fun buildForReleaseEnv(): EnvConfig {
            return RELEASE
        }
    }
}