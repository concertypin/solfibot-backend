package commands

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dao.dao
import kotlinx.coroutines.runBlocking
import models.Command
import settings.trustableUser

val safeBrowsingIndex = listOf(
    Command("링크검열", ::toggleSafeBrowsing, 0, true)
)

fun toggleSafeBrowsing(client: TwitchClient, event: ChannelMessageEvent, args: List<String>): String? {
    if (!(event.channel.id == event.user.id || event.user.name in trustableUser))
        return null
    return runBlocking {
        val user = dao.existUser(event.channel.id)
        user.streamerData.isSafeBrowsingEnabled = !user.streamerData.isSafeBrowsingEnabled
        dao.editUser(
            event.channel.id,
            user.streamerData,
            user.listenerData
        )
        if (user.streamerData.isSafeBrowsingEnabled)
            "이제부터 위험한 링크를 경고할게요!"
        else
            "더 이상 위험한 링크를 경고하지 않을게요!"
    }
}