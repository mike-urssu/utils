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


    { // Excel Util
        implementation("org.apache.commons:commons-collections4:4.4")
        implementation("org.apache.poi:poi:5.2.3")
        implementation("org.apache.poi:poi-ooxml:5.2.3")
        implementation("org.apache.poi:poi-ooxml-lite:5.2.3")
        implementation("org.apache.xmlbeans:xmlbeans:5.1.1")
        implementation("org.apache.logging.log4j:log4j-api:2.19.0")
        implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
