package settings

import models.twitch.AuthToken
import java.nio.file.Files
import java.nio.file.Paths

fun secret(name: String): String =
        System.getenv(name) ?: Files.readAllLines(Paths.get("/run/secrets/$name"))[0]


val auth = AuthToken(secret("TWITCH_CLIENT_ID"), secret("TWITCH_TOKEN"), "", "")
val joinUsername = System.getenv("TARGET").split(",").toSet()
val maxChance = System.getenv("MAX_CHANCE")?.toIntOrNull() ?: 9999
val prefix: String = System.getenv("PREFIX")
val trustableUser = System.getenv("TRUSTABLE_USER")?.split(",")?.toSet() ?: setOf()

