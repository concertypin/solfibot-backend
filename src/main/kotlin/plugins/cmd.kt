package plugins

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dao.dao
import kotlinx.coroutines.runBlocking
import models.Plugin

val cmdPluginIndex=listOf(Plugin(::pop))

fun pop(client: TwitchClient, event: ChannelMessageEvent):Boolean
{
    return runBlocking {
        val user= dao.existUser(event.channel.id)
        val response=user.streamerData.command[event.message]
        if (response != null) {
            client.chat.sendMessage(event.channel.name, response)
            return@runBlocking false
        }
        return@runBlocking true
    }
}