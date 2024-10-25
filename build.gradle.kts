plugins {
   // this is necessary to avoid the plugins to be loaded multiple times
   // in each subproject's classloader
   alias(libs.plugins.jetbrainsCompose) apply false
   alias(libs.plugins.compose.compiler) apply false
   alias(libs.plugins.kotlinMultiplatform) apply false

   // hmmm, can't figure how to use version catalogs with this one
   // is it even possible?
   kotlin("plugin.serialization") version "2.0.20"
}