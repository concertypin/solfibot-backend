package modules

import Processor
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import kg.net.bazi.gsb4j.Gsb4j
import kg.net.bazi.gsb4j.api.SafeBrowsingApi

val sbPluginsIndex = listOf(Processor(SafeBrowsing::isURLSafe))
val URLRegex = "([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?".toRegex()

object SafeBrowsing {
    
    private val client: Gsb4j = Gsb4j.bootstrap()
    private val api: SafeBrowsingApi = client.getApiClient(SafeBrowsingApi.Type.UPDATE_API)
    
    private fun isURL(message: String): Sequence<MatchResult> = URLRegex.findAll(message)
    
    fun isURLSafe(client: TwitchClient, event: ChannelMessageEvent): Boolean {
        for (url in isURL(event.message)) {
            val response = api.check(url.value)
            if (response != null) {
                client.chat.sendMessage(event.channel.name, "악성 사이트 같아요! 조심하세요!")
                return false //block executing more commands
            }
        }
        return true //keep executing another ones
    }
    //{ client:TwitchClient, event:ChannelMessageEvent -> return@Processor false }
}