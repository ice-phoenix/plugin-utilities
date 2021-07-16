package org.jetbrains.research.ml.pluginUtilities

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import java.io.File

fun assertRepositoryOpens(repositoryRoot: File, acceptedBuildSystems: List<BuildSystem>) {
    val projectRoots = repositoryRoot.collectBuildSystemRoots(acceptedBuildSystems)
    for (projectRoot in projectRoots) {
        assertProjectOpens(projectRoot)
    }
}

fun assertProjectOpens(projectRoot: File) {
    val project = try {
        openSingleProject(projectRoot)
    } catch (e: Exception) {
        throw AssertionError(e)
    }

    if (!project.hasResolvedDependencies) {
        throw AssertionError("Project $projectRoot has no resolved dependencies")
    }
}

val Project.hasResolvedDependencies: Boolean
    get() = countDependencies(this) > 0

private fun countDependencies(project: Project): Int = project.modules.sumOf { countLibraries(it) }

private val Project.modules: Array<Module>
    get() = ModuleManager.getInstance(this).modules

private fun countLibraries(module: Module): Int {
    var nLibraries = 0
    ModuleRootManager.getInstance(module).orderEntries().forEachLibrary {
        nLibraries++
        true
    }
    return nLibraries
}
