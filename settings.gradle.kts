include(
        "common",
        "database",
        "discord",
        "website"/*,
        "website:frontend"*/
)

enableFeaturePreview("STABLE_PUBLISHING")

val kotlin_version: String by settings
val kotlin_frontend_version: String by settings

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "Kotlin EAP (for kotlin-frontend-plugin)"
            url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
        }
    }
    resolutionStrategy {
        eachPlugin {
            println("Requested: " + requested.id.id)
            when (requested.id.id) {
                "org.jetbrains.kotlin.frontend" -> useModule("org.jetbrains.kotlin:kotlin-frontend-plugin:$kotlin_frontend_version")
                "kotlin2js" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
                else -> when (requested.id.namespace) {
                    "org.jetbrains.kotlin" -> useVersion(kotlin_version)
                }
            }
            if (requested.id.id == "kotlin2js") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}