plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    application
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
