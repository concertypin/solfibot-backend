val ktorVersion: String by project

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("plugin.serialization") version "1.7.0"
    id("io.realm.kotlin") version "1.8.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4-native-mt")
    implementation("com.google.guava:guava:31.1-jre")
    
    //twitch4j
    implementation("com.github.twitch4j:twitch4j:1.13.0")
    implementation("com.github.philippheuer.events4j:events4j-handler-reactor:0.11.0")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.3.5")
    
    //ktor client
    implementation("io.ktor:ktor-client-core:2.2.2")
    implementation("io.ktor:ktor-client-cio:2.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC")
    implementation("io.ktor:ktor-client-content-negotiation:2.2.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.2")
    
    //ktor server
    implementation("io.ktor:ktor-server-core:2.3.0")
    implementation("io.ktor:ktor-server-netty:2.3.0")
    implementation("io.ktor:ktor-server-content-negotiation:2.2.2")
    implementation("io.ktor:ktor-server-auth-jvm:2.3.0")
    implementation("io.ktor:ktor-server-core-jvm:2.3.0")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.0")
    
    implementation("com.github.yundom:kache:1.0.5")
    
    //MongoDB
    implementation("io.realm.kotlin:library-base:1.8.0")
    implementation("io.realm.kotlin:library-sync:1.8.0")
    
    //test
    testImplementation("io.ktor:ktor-server-test-host:2.2.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.10")
    
}
application {
    mainClass.set("MainKt")
}

tasks.jar {
    manifest.attributes["Main-Class"] = "MainKt"
}