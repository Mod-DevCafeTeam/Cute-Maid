import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec
import java.io.File
import kotlin.concurrent.thread

inline val `java-library-str`: String
    get() = "org.gradle.java-library"

fun `kotlin-str`(module: String): String = "org.jetbrains.kotlin.$module"

fun String.execute(wd: String? = null, ignoreExitCode: Boolean = false): String =
        split(" ").execute(wd, ignoreExitCode)

fun List<String>.execute(wd: String? = null, ignoreExitCode: Boolean = false): String {
    val process = ProcessBuilder(this)
            .also { pb -> wd?.let { pb.directory(File(it)) } }
            .start()
    var result = ""
    val errReader = thread { process.errorStream.bufferedReader().forEachLine { /*logger.error(it)*/ } }
    val outReader = thread {
        process.inputStream.bufferedReader().forEachLine { line ->
            //            logger.debug(line)
            result += line
        }
    }
    process.waitFor()
    outReader.join()
    errReader.join()
    if (process.exitValue() != 0 && !ignoreExitCode) error("Non-zero exit status for `$this`")
    return result
}

fun Project.hasProp(name: String): Boolean = extra.has(name)

fun Project.prop(name: String): String? = extra.properties[name] as? String

fun Project.coroutine(module: String): Any =
        "org.jetbrains.kotlinx:kotlinx-coroutines-$module:${prop("coroutines_version")}"
