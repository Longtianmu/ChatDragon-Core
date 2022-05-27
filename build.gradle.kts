import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "net.ltm"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
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
                implementation("org.kodein.db:kodein-db:0.9.0-beta")
                implementation("org.kodein.db:kodein-db-serializer-kotlinx:0.9.0-beta")
                implementation("com.github.app-outlet:karavel:1.0.0")
                api("net.mamoe:mirai-core:2.11.1")
                api("net.mamoe:mirai-logging-log4j2:2.11.1")
            }
        }
        val jvmTest by getting
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
