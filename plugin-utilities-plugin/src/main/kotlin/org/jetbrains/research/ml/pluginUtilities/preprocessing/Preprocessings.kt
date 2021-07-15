package org.jetbrains.research.ml.pluginUtilities.preprocessing

import org.jetbrains.research.ml.pluginUtilities.collectJavaProjectRoots
import java.io.File

interface Preprocessing {
    val name: String
    fun preprocess(repoDirectory: File)
}

fun List<Preprocessing>.preprocess(repoDirectory: File) {
    for (preprocessing in this) {
        preprocessing.preprocess(repoDirectory)
    }
}

class AndroidSdkPreprocessing(private val androidSdkAbsolutePath: String) : Preprocessing {
    override val name: String = "Add Android SDK with local.properties"
    override fun preprocess(repoDirectory: File) {
        for (projectRoot in repoDirectory.collectJavaProjectRoots()) {
            println("Running $name in ${projectRoot.path}")
            createLocalPropertiesFile(projectRoot)
        }
    }

    private fun createLocalPropertiesFile(projectRoot: File) {
        val localPropertiesFile = projectRoot.resolve("local.properties")
        localPropertiesFile.createNewFile()
        localPropertiesFile.writeText(
            """
            sdk.dir=$androidSdkAbsolutePath
            """.trimIndent()
        )
    }
}
