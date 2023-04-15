package models.twitch

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import kotlin.reflect.KSuspendFunction3

data class Command(
    val name: String,
    val requiredParams: Int,
    val isAdminCommand: Boolean,
    val function: KFunction3<TwitchClient, ChannelMessageEvent, List<String>, String?>? = null,
    val suspendFunction: KSuspendFunction3<TwitchClient, ChannelMessageEvent, List<String>, String?>? = null
)

data class Plugin(val function: KFunction2<TwitchClient, ChannelMessageEvent, Boolean>)
data class AuthToken(val clientID: String, val token: String, var username: String, var userID: String)

