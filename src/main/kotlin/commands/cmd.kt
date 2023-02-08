package commands

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dao.dao
import kotlinx.coroutines.runBlocking
import models.Command
import models.decode

val cmdIndex= listOf(
    Command("등록", ::registerCommand, 2, true),
    Command("삭제", ::removeCommand, 1, true),
    Command("목록", ::listCommand, 0, false)
)

fun registerCommand(client: TwitchClient, event: ChannelMessageEvent, args: List<String>): String {
    val commandName = args[0]
    val commandText = args[1]
    runBlocking {
        val user = dao.existUser(event.channel.id)
        user.streamerData.command[commandName] = commandText
        dao.editUser(event.channel.id, user.streamerData, user.listenerData)
    }
    return "명령어가 등록되었습니다."
}

fun removeCommand(client: TwitchClient, event: ChannelMessageEvent, args: List<String>): String {
    val commandName = args[0]
    return runBlocking {
        val user = dao.user(event.channel.id)?.decode() ?: return@runBlocking "존재하지 않는 명령어입니다."
        
        if (user.streamerData.command.remove(commandName) == null)
            return@runBlocking "존재하지 않는 명령어입니다."
        
        dao.editUser(
            event.channel.id,
            user.streamerData,
            user.listenerData
        )
        return@runBlocking "삭제되었습니다."
    }
}

fun listCommand(client: TwitchClient,event: ChannelMessageEvent,args: List<String>):String
{
    val queryName=args.firstOrNull()
    
    return runBlocking {
        val user = dao.user(event.channel.id)?.decode() ?: return@runBlocking ""
    
        if (queryName == null)
            return@runBlocking user.streamerData.command.keys.joinToString(" | ")
        else
            return@runBlocking user.streamerData.command.keys.filter { it.contains(queryName) }.joinToString(" | ")
    }
}