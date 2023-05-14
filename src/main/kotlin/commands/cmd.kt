package commands

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dao.dao
import models.twitch.Command
import settings.internalHiddenValue

val cmdIndex= listOf(
    Command("등록", 2, true, null, ::registerCommand),
    Command("삭제", 1, true, null, ::removeCommand),
    Command("목록", 0, false, null, ::listCommand)
)

suspend fun registerCommand(ignoredClient: TwitchClient, event: ChannelMessageEvent, args: List<String>): String {
    val commandName = args[0]
    
    if (commandName == internalHiddenValue) return "내부 값이므로 변경할 수 없습니다."
    
    val commandText = args.slice(1 until args.size)
    val user = dao.existUser(event.channel.id)
    
    user.streamerData.command[commandName] = commandText
    dao.editUser(event.channel.id, user.streamerData, user.listenerData)
    
    return "명령어가 등록되었습니다."
}

suspend fun removeCommand(ignoredClient: TwitchClient, event: ChannelMessageEvent, args: List<String>): String {
    val commandName = args[0]
    
    if (commandName == internalHiddenValue) return "존재하지 않는 명령어입니다."
    val user = dao.user(event.channel.id) ?: return "존재하지 않는 명령어입니다."
    
    if (user.streamerData.command.remove(commandName) == null)
        return "존재하지 않는 명령어입니다."
    
    dao.editUser(
        event.channel.id,
        user.streamerData,
        user.listenerData
    )
    return "삭제되었습니다."
}

suspend fun listCommand(ignoredClient: TwitchClient, event: ChannelMessageEvent, args: List<String>): String {
    val queryName = args.firstOrNull()
    
    val user = dao.user(event.channel.id) ?: return ""
    user.streamerData.command.remove(internalHiddenValue)
    
    return if (queryName == null)
        user.streamerData.command.keys.joinToString(" | ")
    else
        user.streamerData.command.keys.filter { it.contains(queryName) }.joinToString(" | ")
}