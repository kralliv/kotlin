/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.fir.utils.libraries.source

import com.intellij.mock.MockApplication
import com.intellij.mock.MockProject
import com.intellij.openapi.Disposable
import com.intellij.openapi.vfs.impl.jar.CoreJarFileSystem
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.analysis.api.fir.FirFrontendApiTestConfiguratorService
import org.jetbrains.kotlin.analysis.api.impl.barebone.test.FrontendApiTestConfiguratorService
import org.jetbrains.kotlin.analysis.api.impl.base.test.utils.libraries.CompiledLibraryProvider
import org.jetbrains.kotlin.analysis.api.impl.base.test.utils.libraries.LibraryEnvironmentConfigurator
import org.jetbrains.kotlin.analysis.low.level.api.fir.compiler.based.registerTestServices
import org.jetbrains.kotlin.analysis.project.structure.ProjectStructureProvider
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.load.kotlin.PackagePartProvider
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.services.ServiceRegistrationData

object LibrarySourceFrontendApiTestConfiguratorService : FrontendApiTestConfiguratorService {
    override val allowDependedAnalysisSession: Boolean get() = false

    override fun TestConfigurationBuilder.configureTest(disposable: Disposable) {
        useConfigurators(::LibraryEnvironmentConfigurator)
        usePreAnalysisHandlers(::LibrarySourceModuleRegistrarPreAnalysisHandler)
        useAdditionalServices(ServiceRegistrationData(CompiledLibraryProvider::class, ::CompiledLibraryProvider))
    }

    override fun registerProjectServices(
        project: MockProject,
        compilerConfig: CompilerConfiguration,
        files: List<KtFile>,
        packagePartProvider: (GlobalSearchScope) -> PackagePartProvider,
        projectStructureProvider: ProjectStructureProvider,
        jarFileSystem: CoreJarFileSystem,
    ) {
        project.registerTestServices(files, packagePartProvider, projectStructureProvider)
    }

    override fun registerApplicationServices(application: MockApplication) {
        FirFrontendApiTestConfiguratorService.registerApplicationServices(application)
    }

    override fun doOutOfBlockModification(file: KtFile) {
        FirFrontendApiTestConfiguratorService.doOutOfBlockModification(file)
    }
}
