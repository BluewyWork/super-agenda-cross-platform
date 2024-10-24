package di

import org.koin.core.module.Module
import org.koin.dsl.module

// see https://www.youtube.com/watch?v=TAKZy3uQTdE
expect val platformModule: Module
val sharedModule = module {}
