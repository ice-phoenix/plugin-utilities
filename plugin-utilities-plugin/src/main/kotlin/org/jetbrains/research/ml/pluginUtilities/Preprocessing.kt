package org.jetbrains.research.ml.pluginUtilities

import java.io.File

class Preprocessor(private val preprocessings: List<Preprocessing>) {
    fun preprocess(repoDirectory: File, outputDirectory: File) {
        repoDirectory.copyRecursively(outputDirectory)
        for (preprocessing in preprocessings) {
            preprocessing.preprocess(outputDirectory)
        }
    }
}

interface Preprocessing {
    val name: String
    fun preprocess(repoDirectory: File)
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
