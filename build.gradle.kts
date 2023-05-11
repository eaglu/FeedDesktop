import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.kapt") version "1.7.10"
    id("org.jetbrains.compose")
}


group = "com.faye"
version = "1.0-SNAPSHOT"



repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}


val exposedVersion: String by project

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
                implementation("org.jsoup:jsoup:1.15.4")
                implementation("org.xerial:sqlite-jdbc:3.34.0")
                implementation("com.rometools:rome:1.12.0")
                implementation("net.java.dev.jna:jna:5.13.0")
                implementation("net.java.dev.jna:jna-platform:5.13.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
                implementation("com.squareup.okhttp3:okhttp:4.10.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")
                implementation("org.jetbrains.exposed:exposed-core:0.40.1")
                implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.40.1")
                implementation("org.jetbrains.exposed:exposed-java-time:0.40.1")
                implementation("com.zaxxer:HikariCP:3.3.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("com.alialbaali.kamel:kamel-image:0.4.0")
                implementation("io.ktor:ktor-client-apache:2.2.4")
                implementation("io.github.leobert-lan:class-diagram-reporter:1.0.0")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
                implementation("net.java.dev.jna:jna:5.13.0")
                implementation("net.java.dev.jna:jna-platform:5.13.0")
                implementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")
                implementation("com.squareup.okhttp3:okhttp:4.10.0")
                implementation("org.jsoup:jsoup:1.15.4")
                implementation("org.xerial:sqlite-jdbc:3.34.0")
                implementation("com.rometools:rome:1.12.0")
                implementation("net.java.dev.jna:jna:5.13.0")
                implementation("net.java.dev.jna:jna-platform:5.13.0")
                implementation("com.squareup.okhttp3:okhttp:4.10.0")
                implementation("org.jetbrains.exposed:exposed-core:0.40.1")
                implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.40.1")
            }
        }
    }
    tasks.named<Test>("jvmTest") {
        useJUnitPlatform()
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            modules("java.sql")
            includeAllModules = true
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "FeedDesktop"
            packageVersion = "1.0.0"
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
        }
    }
}
