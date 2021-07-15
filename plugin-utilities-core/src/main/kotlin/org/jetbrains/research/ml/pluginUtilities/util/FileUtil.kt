package org.jetbrains.research.ml.pluginUtilities.util

import java.io.File
import java.nio.file.Files
import java.util.concurrent.TimeUnit
import kotlin.streams.toList

enum class Extension(val value: String) {
    KT("kt"),
    KTS("kts"),
    PY("py"),
    TXT("txt")
}

val File.subdirectories: List<File>
    get() = Files.walk(this.toPath(), 1).filter { Files.isDirectory(it) && it != this.toPath() }.map { it.toFile() }
        .toList()

fun File.runCommand(vararg arguments: String) {
    ProcessBuilder(*arguments)
        .directory(this)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(60, TimeUnit.MINUTES)
}
