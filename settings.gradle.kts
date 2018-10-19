include(
        "common",
        "database",
        "discord",
        "website"
)

enableFeaturePreview("STABLE_PUBLISHING")

val kotlin_version: String by settings

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            println("Requested: " + requested.id.namespace)
            when (requested.id.namespace) {
                "org.jetbrains.kotlin" -> useVersion(kotlin_version)
                else -> when (requested.id.id) {

                }
            }
        }
    }
}