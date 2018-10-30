import org.gradle.kotlin.dsl.execution.ProgramText.Companion.from
import org.jetbrains.kotlin.config.TargetPlatformVersion.NoVersion.description
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    id("org.jetbrains.kotlin.frontend")
    id("kotlin2js")
}

dependencies {
    compile(kotlin("stdlib-js"))
}

/*val compileKotlin2Js: Kotlin2JsCompile by tasks

compileKotlin2Js.apply {
    kotlinOptions.moduleKind = "commonjs"
}*/

tasks {
    val mainSourceSet = sourceSets["main"]
    "compileKotlin2Js"(Kotlin2JsCompile::class) {
        kotlinOptions {
            sourceMap = true
            moduleKind = "commonjs"
        }
    }
    val unpackKotlinJsStdlib by registering {
        group = "build"
        description = "Unpack the Kotlin JavaScript standard library"
        val outputDir = file("$buildDir/$name")
        val compileClasspath = configurations["compileClasspath"]
        inputs.property("compileClasspath", compileClasspath)
        outputs.dir(outputDir)
        doLast {
            val kotlinStdLibJar = compileClasspath.single {
                it.name.matches(Regex("kotlin-stdlib-js-.+\\.jar"))
            }
            copy {
                includeEmptyDirs = false
                from(zipTree(kotlinStdLibJar))
                into(outputDir)
                include("**/*.js")
                exclude("META-INF/**")
            }
        }
    }
    val assembleWeb by registering(Copy::class) {
        group = "build"
        description = "Assemble the web application"
        includeEmptyDirs = false
        from(unpackKotlinJsStdlib)
        from(mainSourceSet.output) {
            exclude("**/*.kjsm")
        }
        into("$buildDir/web")
    }
    "assemble" {
        dependsOn(assembleWeb)
    }
}