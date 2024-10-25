import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
   alias(libs.plugins.kotlinMultiplatform)
   alias(libs.plugins.jetbrainsCompose)
   alias(libs.plugins.compose.compiler)

   kotlin("plugin.serialization") version "2.0.20"
}

kotlin {
   jvm("desktop")

   sourceSets {
      val desktopMain by getting

      commonMain.dependencies {
         implementation(compose.runtime)
         implementation(compose.foundation)
         implementation(compose.material)
         implementation(compose.ui)
         implementation(compose.components.resources)
         implementation(compose.components.uiToolingPreview)
         implementation(libs.androidx.lifecycle.viewmodel)
         implementation(libs.androidx.lifecycle.runtime.compose)

         implementation(libs.androidx.navigation.compose)
         api(libs.koin.core)
         implementation(libs.koin.compose)
         implementation(libs.koin.compose.viewmodel)
         implementation(libs.ktor.client.core)
         implementation(libs.ktor.client.logging)
         implementation(libs.ktor.client.content.negotiation)
         implementation(libs.ktor.serialization.kotlinx.json)
         implementation(libs.ktor.client.okhttp)
      }
      desktopMain.dependencies {
         implementation(compose.desktop.currentOs)
         implementation(libs.kotlinx.coroutines.swing)
      }
   }
}

compose.desktop {
   application {
      mainClass = "MainKt"

      nativeDistributions {
         targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
         packageName = "org.example.project"
         packageVersion = "1.0.0"
      }
   }
}
