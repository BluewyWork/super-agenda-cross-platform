package di

import presentation.screens.example.ExampleDi
import presentation.screens.example.ExampleViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule: Module = module {
   singleOf(::ExampleDi)
   viewModelOf(::ExampleViewModel)
}