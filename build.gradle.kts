import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.squareup.sqldelight") version("1.5.3")
}

group = "net.ltm"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven( "https://jitpack.io")
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.squareup.sqldelight:gradle-plugin:1.5.3")
    }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation ("com.squareup.sqldelight:sqlite-driver:1.5.3")
                implementation("com.github.app-outlet:karavel:1.0.0")
                api("net.mamoe:mirai-core:2.11.0")
                api("net.mamoe:mirai-logging-log4j2:2.11.0")
            }
        }
        val jvmTest by getting
    }
}

sqldelight {
    database("ChatHistory") {
        packageName = "net.ltm.db"
        sourceFolders = listOf("sqldelight")
        schemaOutputDirectory = file("build/dbs")
        dialect = "sqlite:3.24"
        verifyMigrations = true
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Chat-Dragon"
            packageVersion = "1.0.0"
        }
    }
}
