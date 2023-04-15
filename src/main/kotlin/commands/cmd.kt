package commands

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dao.dao
import kotlinx.coroutines.runBlocking
import models.twitch.Command
import models.userData.decode

val cmdIndex= listOf(
    Command("등록", 2, true, ::registerCommand),
    Command("삭제", 1, true, ::removeCommand),
    Command("목록", 0, false, ::listCommand)
)

fun registerCommand(ignoredClient: TwitchClient, event: ChannelMessageEvent, args: List<String>): String {
    val commandName = args[0]
    val commandText = args.slice(1 until args.size)
    runBlocking {
        val user = dao.existUser(event.channel.id)
        user.streamerData.command[commandName] = commandText
        dao.editUser(event.channel.id, user.streamerData, user.listenerData)
    }
    return "명령어가 등록되었습니다."
}

fun removeCommand(ignoredClient: TwitchClient, event: ChannelMessageEvent, args: List<String>): String {
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

fun listCommand(ignoredClient: TwitchClient, event: ChannelMessageEvent, args: List<String>): String {
    val queryName = args.firstOrNull()
    
    return runBlocking {
        val user = dao.user(event.channel.id)?.decode() ?: return@runBlocking ""
        
        if (queryName == null)
            return@runBlocking user.streamerData.command.keys.joinToString(" | ")
        else
            return@runBlocking user.streamerData.command.keys.filter { it.contains(queryName) }.joinToString(" | ")
    }
}
