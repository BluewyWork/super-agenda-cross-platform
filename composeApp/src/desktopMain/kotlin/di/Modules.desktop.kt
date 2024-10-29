package di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import data.AuthenticationRepository
import data.database.AppDatabase
import data.database.TokenDao
import data.network.Api
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
import presentation.screens.login.LoginViewModel
import java.io.File

actual val platformModule: Module = module {
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
   singleOf(::Api)

   singleOf(::AuthenticationRepository)
   singleOf(::AuthenticationUseCase)
   viewModelOf(::LoginViewModel)

   single {
      val dbFile = File(System.getProperty("java.io.tmpdir"), "database.db")

      Room.databaseBuilder<AppDatabase>(
         name = dbFile.absolutePath
      )
         .setDriver(BundledSQLiteDriver())
         .build()
   }

   single<TokenDao> {
      get<AppDatabase>().tokenDao()
   }
}