package utils

import com.github.twitch4j.TwitchClient
import settings.auth

fun String.usernameToUID(client: TwitchClient): String? {
    val response = client.helix.getUsers(auth.token, null, listOf(this)).execute()
    return response.users.firstOrNull()?.id
}