package ru.bartwell.kick.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class KickExtensionTest {

    private fun createProject(configure: (Project) -> Unit = {}): Project {
        return ProjectBuilder.builder().build().also { configure(it) }
    }

    /** Simulates -Pkick.enabled / gradle.properties for tests (ProjectBuilder doesn't load gradle.properties). */
    private fun createExtensionWithCliOverride(project: Project, cliValue: String): KickExtension {
        val ext = createExtension(project)
        ext.testCliOverrideProvider = project.providers.provider { cliValue }
        return ext
    }

    private fun createExtension(project: Project): KickExtension {
        return project.extensions.create("kick", KickExtension::class.java, project)
    }

    @Test
    fun `effectiveEnabled - extension only - Auto`() {
        val p = createProject()
        val ext = createExtension(p)
        ext.enabled.set(KickEnabled.Auto)
        ext.modules(KickModule.FileExplorer)
        assertEquals(KickEnabled.Auto, ext.effectiveEnabled())
    }

    @Test
    fun `effectiveEnabled - extension only - Enabled`() {
        val p = createProject()
        val ext = createExtension(p)
        ext.enabled.set(KickEnabled.Enabled)
        ext.modules(KickModule.FileExplorer)
        assertEquals(KickEnabled.Enabled, ext.effectiveEnabled())
    }

    @Test
    fun `effectiveEnabled - extension only - Disabled`() {
        val p = createProject()
        val ext = createExtension(p)
        ext.enabled.set(KickEnabled.Disabled)
        ext.modules(KickModule.FileExplorer)
        assertEquals(KickEnabled.Disabled, ext.effectiveEnabled())
    }

    @Test
    fun `effectiveEnabled - enableKick via extraProperties true wins over extension`() {
        val p = createProject()
        p.extensions.extraProperties.set(KickExtension.KICK_OVERRIDE_KEY, true)
        val ext = createExtension(p)
        ext.enabled.set(KickEnabled.Disabled)
        ext.modules(KickModule.FileExplorer)
        assertEquals(KickEnabled.Enabled, ext.effectiveEnabled())
    }

    @Test
    fun `effectiveEnabled - enableKick via extraProperties false wins over extension`() {
        val p = createProject()
        p.extensions.extraProperties.set(KickExtension.KICK_OVERRIDE_KEY, false)
        val ext = createExtension(p)
        ext.enabled.set(KickEnabled.Enabled)
        ext.modules(KickModule.FileExplorer)
        assertEquals(KickEnabled.Disabled, ext.effectiveEnabled())
    }

    @Test
    fun `effectiveEnabled - CLI true wins over enableKick and extension`() {
        val p = createProject()
        val ext = createExtensionWithCliOverride(p, "true")
        ext.enabled.set(KickEnabled.Disabled)
        ext.modules(KickModule.FileExplorer)
        assertEquals(KickEnabled.Enabled, ext.effectiveEnabled())
    }

    @Test
    fun `effectiveEnabled - CLI false wins over enableKick and extension`() {
        val p = createProject()
        val ext = createExtensionWithCliOverride(p, "false")
        ext.enabled.set(KickEnabled.Enabled)
        ext.modules(KickModule.FileExplorer)
        assertEquals(KickEnabled.Disabled, ext.effectiveEnabled())
    }

    @Test
    fun `effectiveEnabled - CLI wins over enableKick when both set`() {
        val p = createProject()
        p.extensions.extraProperties.set(KickExtension.KICK_OVERRIDE_KEY, true)
        val ext = createExtensionWithCliOverride(p, "false")
        ext.modules(KickModule.FileExplorer)
        assertEquals(KickEnabled.Disabled, ext.effectiveEnabled())
    }

    @Test
    fun `parseCliOverride - invalid value throws`() {
        val p = createProject()
        val ext = createExtensionWithCliOverride(p, "yes")
        ext.modules(KickModule.FileExplorer)
        val ex = assertThrows(GradleException::class.java) { ext.effectiveEnabled() }
        assertEquals("Kick: invalid -Pkick.enabled value. Use true or false.", ex.message)
    }

    @Test
    fun `parseCliOverride - true and false case insensitive`() {
        val pTrue = createProject()
        val extTrue = createExtensionWithCliOverride(pTrue, "TRUE")
        extTrue.modules(KickModule.FileExplorer)
        assertEquals(KickEnabled.Enabled, extTrue.effectiveEnabled())

        val pFalse = createProject()
        val extFalse = createExtensionWithCliOverride(pFalse, "FALSE")
        extFalse.modules(KickModule.FileExplorer)
        assertEquals(KickEnabled.Disabled, extFalse.effectiveEnabled())
    }

    @Test
    fun `validate - empty modules throws`() {
        val p = createProject()
        val ext = createExtension(p)
        val ex = assertThrows(GradleException::class.java) { ext.validate() }
        assertEquals(
            "Kick: modules(...) is required. Example: kick { modules(KickModule.FileExplorer) }",
            ex.message
        )
    }

    @Test
    fun `validate - at least one module passes`() {
        val p = createProject()
        val ext = createExtension(p)
        ext.modules(KickModule.FileExplorer)
        assertDoesNotThrow { ext.validate() }
    }

    @Test
    fun `effectiveEnabled - enableKick via extraProperties before extension creation`() {
        val p = createProject()
        p.extensions.extraProperties.set(KickExtension.KICK_OVERRIDE_KEY, true)
        val ext = createExtension(p)
        ext.enabled.set(KickEnabled.Disabled)
        ext.modules(KickModule.FileExplorer)
        assertEquals(KickEnabled.Enabled, ext.effectiveEnabled())
    }

    @Test
    fun `isRelease - Auto with release-like task names returns true`() {
        val p = createProject()
        p.gradle.startParameter.setTaskNames(listOf("assembleRelease"))
        val ext = createExtension(p)
        ext.enabled.set(KickEnabled.Auto)
        ext.modules(KickModule.FileExplorer)
        assertEquals(true, ext.isRelease())
    }

    @Test
    fun `isRelease - Auto with linkReleaseFramework returns true`() {
        val p = createProject()
        p.gradle.startParameter.setTaskNames(listOf("linkReleaseFrameworkIosArm64"))
        val ext = createExtension(p)
        ext.enabled.set(KickEnabled.Auto)
        ext.modules(KickModule.FileExplorer)
        assertEquals(true, ext.isRelease())
    }

    @Test
    fun `isRelease - Auto with bundleProduction returns true`() {
        val p = createProject()
        p.gradle.startParameter.setTaskNames(listOf("bundleProductionFramework"))
        val ext = createExtension(p)
        ext.enabled.set(KickEnabled.Auto)
        ext.modules(KickModule.FileExplorer)
        assertEquals(true, ext.isRelease())
    }

    @Test
    fun `isRelease - Auto with debug task names returns false`() {
        val p = createProject()
        p.gradle.startParameter.setTaskNames(listOf("assembleDebug"))
        val ext = createExtension(p)
        ext.enabled.set(KickEnabled.Auto)
        ext.modules(KickModule.FileExplorer)
        assertEquals(false, ext.isRelease())
    }
}
