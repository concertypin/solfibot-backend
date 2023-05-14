package plugins

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dao.dao
import isSudoers
import kotlinx.coroutines.runBlocking
import models.twitch.Plugin
import settings.internalHiddenValue

val cmdPluginIndex=listOf(Plugin(::pop))

fun pop(client: TwitchClient, event: ChannelMessageEvent):Boolean
{
    return runBlocking {
        val user = dao.existUser(event.channel.id)
        val response = user.streamerData.command[event.message]?.toMutableList()
        if (response != null) {
            response.remove(internalHiddenValue)
            if (response.isNotEmpty()) {
                response.random().let { sampledResponse ->
                    val userSpecifiedResponse=sampledResponse.split(settings.commandPermissionIndicator).let {
                        println("data: $it")
                        if (event.user.name in settings.trustableUser)
                            it.last()
                        else if (isSudoers(event))
                            it[if (it.size == 3) 1 else 0]
                        else
                            it.first()
                    }
                    client.chat.sendMessage(event.channel.name, userSpecifiedResponse)
                    return@runBlocking false
                }
            }
        }
        return@runBlocking true
    }
}