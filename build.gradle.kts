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

    apply(plugin = `java-library-str`)
    apply(plugin = `kotlin-str`("jvm"))

    repositories {
        jcenter()
    }

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
        runtimeOnly("org.slf4j:slf4j-simple:$slf4j_version")
    }

    val sourceJar by tasks.registering(Jar::class) {
        from(sourceSets["main"].allSource)
        from(sourceSets["test"].allSource)
        classifier = "sources"
    }
}
kotlin.experimental.coroutines = Coroutines.ENABLE