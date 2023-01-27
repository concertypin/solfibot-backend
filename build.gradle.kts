plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    implementation("com.google.guava:guava:31.1-jre")

    implementation("com.github.twitch4j:twitch4j:1.13.0")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.3.5")
    implementation("com.github.philippheuer.events4j:events4j-handler-reactor:0.11.0")
}

application {
    mainClass.set("MainKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.jar {
    manifest.attributes["Main-Class"] = "MainKt"
}
tasks.jar {
    manifest.attributes["Main-Class"] = "com.example.MyMainClass"
    manifest.attributes["Class-Path"] = configurations
        .runtimeClasspath
        .get()
        .joinToString(separator = " ") { file ->
            "libs/${file.name}"
        }
}