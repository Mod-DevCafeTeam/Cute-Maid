import groovy.lang.GroovyObject
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.concurrent.thread

plugins {
    `java-library`
    kotlin("jvm")
}

val branch = prop("branch") ?: "git rev-parse --abbrev-ref HEAD".execute(rootDir.absolutePath).lines().last()
logger.info("On branch $branch")

group = "be.bluexin.cutemaid"
version = prop("major") + "." + prop("minor") + "." + prop("patch") + "-$branch".takeUnless { branch == "master" }.orEmpty()
description = "Cute Maid helping around at the Mod Dev Cafe"

allprojects {
    group = "be.bluexin.cutemaid"
    version = prop("major") + "." + prop("minor") + "." + prop("patch") + "-$branch".takeUnless { branch == "master" }.orEmpty()

    repositories {
        jcenter()
        maven {
            name = "KotlinX repo (Serialization transitive dep)"
            url = uri("https://kotlin.bintray.com/kotlinx/")
        }
        maven {
            name = "Ktor bintray"
            url = uri("https://kotlin.bintray.com/ktor")
        }
    }

    if (this.name != "frontend") {
        apply(plugin = `java-library-str`)
        apply(plugin = `kotlin-str`("jvm"))

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }

        dependencies {
            api(kotlin("stdlib-jdk8"))
            api(coroutine("core"))
            api(coroutine("jdk8"))

            val slf4j_version by extra.properties
            implementation("org.slf4j:slf4j-api:$slf4j_version")
            implementation("io.github.microutils:kotlin-logging:1.6.10")
            runtimeOnly("ch.qos.logback:logback-classic:${prop("logback_version")}")

            if (this@allprojects.name != "common") implementation(project(":common"))
            else {
                implementation(kotlin("reflect")) // Explicit otherwise jackson pulls in wrong one
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${prop("jackson_version")}")
            }
        }

        val sourceJar by tasks.registering(Jar::class) {
            from(sourceSets["main"].allSource)
            from(sourceSets["test"].allSource)
            classifier = "sources"
        }

    }
}

dependencies {
    implementation(project(":database"))
    implementation(project(":website"))
}