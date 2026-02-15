package ru.bartwell.kick.gradle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class KickGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val kickExt = project.extensions.create("kick", KickExtension::class.java, project)
        // version default is set in extension from plugin's Implementation-Version (version.properties at build time)
        // Do not touch other projects or trigger their configuration here (no rootProject/subprojects/project(":…")).
        // Defer all logic that might affect configuration order to projectsEvaluated.
        project.gradle.projectsEvaluated {
            if (!project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
                throw GradleException(
                    "Kick: Kotlin Multiplatform plugin is required (id(\"org.jetbrains.kotlin.multiplatform\"))."
                )
            }
            configureKick(project, kickExt)
        }
    }

    /**
     * Called from projectsEvaluated; all projects are already configured, so we don't use afterEvaluate
     * and don't trigger configuration of other projects.
     */
    private fun configureKick(project: Project, kickExt: KickExtension) {
        kickExt.validate()
        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val version = kickExt.kickVersion()
        val isRelease = kickExt.isRelease()
        val mods = kickExt.modules.getOrElse(emptySet())
        val runtimeArtifact =
            if (isRelease) ModuleArtifacts.mainRuntimeStub(version) else ModuleArtifacts.mainRuntime(version)
        val moduleArtifacts = mods.flatMap { module ->
            if (isRelease) {
                ModuleArtifacts.stubArtifacts(module, version)
            } else {
                ModuleArtifacts.runtimeArtifacts(module, version)
            }
        }

        val commonMain = kotlin.sourceSets.findByName("commonMain")
            ?: throw GradleException(
                "Kick: commonMain source set not found. " +
                    "Ensure Kotlin Multiplatform targets are configured."
            )
        commonMain.dependencies {
            api(ModuleArtifacts.mainCore(version))
            api(runtimeArtifact)
            moduleArtifacts.forEach { api(it) }
        }

        val exportDeps = (listOf(ModuleArtifacts.mainCore(version), runtimeArtifact) + moduleArtifacts)
            .distinct()
            .map(project.dependencies::create)
        kotlin.targets.withType(KotlinNativeTarget::class.java).configureEach { target ->
            target.binaries.withType(Framework::class.java).configureEach { framework ->
                exportDeps.forEach(framework::export)
            }
        }
    }
}
