package plugins

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dao.dao
import isSudoers
import kotlinx.coroutines.runBlocking
import models.Plugin

val cmdPluginIndex=listOf(Plugin(::pop))

fun pop(client: TwitchClient, event: ChannelMessageEvent):Boolean
{
    return runBlocking {
        val user= dao.existUser(event.channel.id)
        val response=user.streamerData.command[event.message]
        if (response != null) {
            if (response.isNotEmpty()) {
                response.random().let { sampledResponse ->
                    val userSpecifiedResponse=sampledResponse.split(settings.commandPermissionIndicator).let {
                        println("data: $it")
                        if(event.user.name in settings.trustableUser)
                            it.last()
                        else if(isSudoers(client,event))
                            it[if(it.size==3) 1 else 0]
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