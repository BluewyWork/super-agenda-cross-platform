package di

import data.AuthenticationRepository
import data.network.ApiClient
import domain.AuthenticationUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import presentation.screens.example.ExampleDi
import presentation.screens.example.ExampleViewModel
import presentation.screens.login.LoginViewModel

actual val platformModule: Module = module {
   singleOf(::ExampleDi)
   viewModelOf(::ExampleViewModel)

   // this instead of a dedicated function cause
   // i can't get my head around it
   single {
      HttpClient(
         OkHttp.create()
      ) {
         install(Logging) {
            level = LogLevel.ALL
         }
         install(ContentNegotiation) {
            json(
               json = Json {
                  ignoreUnknownKeys = true
               }
            )
         }
      }
   }
   singleOf(::ApiClient)

   singleOf(::AuthenticationRepository)
   singleOf(::AuthenticationUseCase)
   viewModelOf(::LoginViewModel)
}