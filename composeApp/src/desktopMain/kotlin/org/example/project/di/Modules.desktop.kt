package org.example.project.di

import org.example.project.presentation.screens.example.ExampleDi
import org.example.project.presentation.screens.example.ExampleViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule: Module = module {
   singleOf(::ExampleDi)
   viewModelOf(::ExampleViewModel)
}