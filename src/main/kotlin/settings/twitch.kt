package settings

import models.AuthToken
import java.nio.file.Files
import java.nio.file.Paths

fun secret(name: String): String =
    if (System.getenv("DOCKER") != "1")
        System.getenv(name)
    else
        Files.readString(Paths.get("/run/secrets/$name"))


val auth = AuthToken(secret("TWITCH_CLIENT_ID"), secret("TWITCH_TOKEN"), "", "")
val joinUsername = System.getenv("TARGET").split(",").toSet()
val maxChance = System.getenv("MAX_CHANCE")?.toInt() ?: 9999
val prefix: String = System.getenv("PREFIX")
val trustableUser = System.getenv("TRUSTABLE_USER")?.split(",")?.toSet() ?: setOf()

