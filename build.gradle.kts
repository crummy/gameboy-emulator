import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.3.30"
    id("kotlinx-serialization").version("1.3.30")

}

repositories {
    jcenter()
}

dependencies {
    testCompile("com.willowtreeapps.assertk:assertk-jvm:0.13")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testCompile("org.junit.jupiter:junit-jupiter-params:5.4.2")
    testCompile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0")
    implementation(kotlin("stdlib-jdk8"))
}

application {
    // Define the main class for the application
    mainClassName = "App"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}