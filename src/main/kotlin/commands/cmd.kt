package commands

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dao.dao
import kotlinx.coroutines.runBlocking
import models.Command

val cmdIndex= listOf(
    Command("등록",::registerCommand, 2)
)

fun registerCommand(client:TwitchClient, event:ChannelMessageEvent, args:List<String>):String
{
    val channelId=event.channel.id.toInt()
    val commandName=args[0]
    val commandText=args[1]
    runBlocking {
        val user=dao.existUser(event.channel.id)
        user.streamerData.command[commandName]=commandText
        dao.editUser(event.channel.id, user.streamerData,user.listenerData)
    }
    return "명령어가 등록되었습니다."
}
