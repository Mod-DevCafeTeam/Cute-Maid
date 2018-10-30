import org.jetbrains.kotlin.incremental.isKotlinFile

/*plugins {
    id("com.moowork.node") version "1.2.0"
}*/

repositories {
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx/") }
}

dependencies {
    val ktor_version by extra.properties
    listOf(
            "server-netty",
            "auth",
            "freemarker",
            "jackson",
            "locations",
            "client-apache",
            "html-builder",
            "client-json",
            "client-jackson"
    ).forEach {
        implementation("io.ktor:ktor-$it:$ktor_version") {
            //            exclude(module = "kotlin-stdlib-jdk8")
        }
    }

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${prop("jackson_version")}")
    implementation(project(":database"))
}

/*val copyFrontend by tasks.registering(Copy::class) {
    into ("$buildDir/resources/main")
    subprojects {
        from (tasks.getByName("jar").inputs.files)
    }
}

val assemble by tasks
assemble.dependsOn(copyFrontend)*/
