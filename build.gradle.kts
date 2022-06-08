import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.6.10"
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

val exposedVersion: String = "0.38.2"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.apache.logging.log4j:log4j-api:2.17.2")
                implementation("org.apache.logging.log4j:log4j-core:2.17.2")
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("io.ktor:ktor-client-cio:1.6.7")
                implementation("org.xerial:sqlite-jdbc:3.30.1")
                implementation("com.github.ltttttttttttt:load-the-image:1.0.1")
                implementation("com.github.app-outlet:karavel:1.0.0")
                api("net.mamoe:mirai-core:2.11.1")
                api("net.mamoe:mirai-logging-log4j2:2.11.1")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "net.ltm.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("java.sql", "java.management")
            packageName = "Chat-Dragon"
            packageVersion = "1.0.0"
            vendor = "Longtianmu"
            licenseFile.set(project.file("LICENSE"))
            windows {
                console = true
            }
        }
    }
}

tasks.withType<org.gradle.jvm.tasks.Jar>() {
    exclude("META-INF/*.RSA", "META-INF/*.DSA", "META-INF/*.SF")
}
