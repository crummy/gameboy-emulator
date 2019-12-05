import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.openjfx:javafx-plugin:0.0.7")
    }
}

plugins {
    java
    application
    kotlin("jvm") version "1.3.30"
    id("kotlinx-serialization").version("1.3.30")
    id("org.openjfx.javafxplugin") version "0.0.7"
}

repositories {
    jcenter()
}

dependencies {
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.13")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0")
    implementation("io.github.microutils:kotlin-logging:1.6.26")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.github.almasb:fxgl:11.2-beta")
    implementation(kotlin("stdlib-jdk8"))
}

javafx {
    modules("javafx.controls", "javafx.fxml")
}

application {
    // Define the main class for the application
    mainClassName = "com.malcolmcrum.gameboy.App"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}