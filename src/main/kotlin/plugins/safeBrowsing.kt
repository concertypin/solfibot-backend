package plugins

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dao.dao
import kg.net.bazi.gsb4j.Gsb4j
import kg.net.bazi.gsb4j.api.SafeBrowsingApi
import kotlinx.coroutines.runBlocking
import models.Plugin
import models.decode

val safeBrowsingPluginIndex = listOf(Plugin(SafeBrowsing::checkFromChat))
val URLRegex = "([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?".toRegex()

object SafeBrowsing {
    private val gsb4j: Gsb4j = Gsb4j.bootstrap()
    private val api = gsb4j.getApiClient(SafeBrowsingApi.Type.UPDATE_API)
    
    private fun isURLSafe(url: String): String? {
        return api.check(url)?.threatType?.name
    }
    
    private fun isURL(message: String): Sequence<MatchResult> = URLRegex.findAll(message)
    
    fun checkFromChat(client: TwitchClient, event: ChannelMessageEvent): Boolean {
        val isEnabled =
            runBlocking { dao.user(event.channel.id)?.decode()?.streamerData?.isSafeBrowsingEnabled ?: false }
        if (!isEnabled)
            return true
        for (url in isURL(event.message)) {
            val response = isURLSafe(url.value)
            if (response != null) {
                print(response)
                client.chat.sendMessage(event.channel.name, "위험한 사이트 같아요! 조심하세요! 사이트 분류는 ${response}라고 해요!")
                return false
            }
        }
        return true
    }
}