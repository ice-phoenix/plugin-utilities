package org.jetbrains.research.pluginUtilities.openRepository

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.intellij.openapi.application.ApplicationStarter
import org.jetbrains.research.pluginUtilities.util.subdirectories
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File
import kotlin.system.exitProcess

class OpenRepositoriesAndSaveResultsCommand : CliktCommand() {
    private val inputFolder by option("--input").file(mustExist = true, mustBeReadable = true, canBeFile = false)
        .required()
    private val outputFile by option("--output").file(canBeDir = false).required()

    private val table by lazy { ResultsTable(getKotlinJavaRepositoryOpener(), outputFile) }

    override fun run() {
        outputFile.createNewFile()

        for (repositoryRoot in inputFolder.subdirectories) {
            table.tryOpenRepository(repositoryRoot)
        }
        exitProcess(0)
    }
}

class ResultsTable(private val repositoryOpener: RepositoryOpener, outputFile: File) : Closeable {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val writer = outputFile.printWriter()

    init {
        writer.println("repository, opened_successfully")
    }

    fun tryOpenRepository(repositoryRoot: File) {
        val wasSuccessfullyOpened = isRepositoryOpenable(repositoryRoot)
        writer.println("${repositoryRoot.name}, $wasSuccessfullyOpened")
        writer.flush()
    }

    private fun isRepositoryOpenable(repositoryRoot: File): Boolean {
        logger.info("Opening repository ${repositoryRoot.name}")

        var successful = true
        try {
            repositoryOpener.assertRepositoryOpens(repositoryRoot)
        } catch (e: Throwable) {
            logger.error("Failed to open repository ${repositoryRoot.name}", e)
            successful = false
        }
        return successful
    }

    override fun close() {
        writer.close()
    }
}

object OpenRepositoriesStatsStarter : ApplicationStarter {
    override fun getCommandName(): String = "openReposAndSaveResults"

    override fun main(args: MutableList<String>) = OpenRepositoriesAndSaveResultsCommand().main(args.drop(1))
}
