package org.jetbrains.research.ml.pluginUtilities

import org.jetbrains.research.ml.pluginUtilities.util.subdirectories
import java.io.File

enum class BuildSystem(val buildFile: String) {
    Maven("pom.xml"),
    Gradle("build.gradle"),
}

val javaBuildSystems = listOf(BuildSystem.Maven, BuildSystem.Gradle)

fun File.detectBuildSystem(): BuildSystem? =
    BuildSystem.values().find { buildSystem -> resolve(buildSystem.buildFile).exists() }

fun File.isJavaProjectRoot(): Boolean = detectBuildSystem() in javaBuildSystems

fun File.collectBuildSystemRoots(acceptedBuildSystems: List<BuildSystem> = BuildSystem.values().asList()): List<File> =
    sequence {
        if (this@collectBuildSystemRoots.detectBuildSystem() in acceptedBuildSystems) {
            yield(this@collectBuildSystemRoots)
        } else {
            for (subdirectory in this@collectBuildSystemRoots.subdirectories) {
                yieldAll(subdirectory.collectBuildSystemRoots(acceptedBuildSystems))
            }
        }
    }.toList()

fun File.collectJavaProjectRoots(): List<File> = collectBuildSystemRoots(javaBuildSystems)
