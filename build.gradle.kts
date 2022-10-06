import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.7.20-RC"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    // logging for java
    implementation("org.slf4j:slf4j-api:2.0.1")
    implementation("ch.qos.logback:logback-classic:1.4.1") {
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "ch.qos.logback", module = "logback-core")
    }
    implementation("ch.qos.logback:logback-core:1.4.1")

    // logging for kotlin
    // https://github.com/MicroUtils/kotlin-logging
    // Important note: kotlin-logging depends on slf4j-api (in the JVM artifact).
    //                 In runtime, it is also required to depend on a logging implementation
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
