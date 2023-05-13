package plugins

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dao.dao
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import models.db.userData.decode
import models.http.safeBrowsing.*
import models.twitch.Plugin
import java.net.URI

val safeBrowsingPluginIndex = listOf(Plugin(SafeBrowsing::checkFromChat))
val URLRegex = "([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?".toRegex()

object SafeBrowsing {
    private val endpoint =
        "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=${settings.safeBrowsingAPIkey}"
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }
    private val isURLSafe = utils.Cache.cached {
        runBlocking {
            val rawResponse = client.post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(
                    SafeBrowsingLookupRequest(
                        ClientInfo("solfibot", "0.0.0.0"),
                        ThreatInfo(
                            listOf("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE"),
                            listOf("ANY_PLATFORM"),
                            listOf("URL", "EXECUTABLE"),
                            listOf(
                                ThreatEntry(it)
                            )
                        )
                    )
                )
            }
            val response: SafeBrowsingLookupResponse = rawResponse.body()
            println("lookup")
            if (response.matches?.isEmpty() != false)
                return@runBlocking null
            
            return@runBlocking response.matches.first().threatType
        }
    }
    
    private fun isURL(message: String): Sequence<String> = URLRegex.findAll(message).map { it.value }
    
    private fun parseURL(url: String): String {
        val uri = URI(url)
        return "http://${uri.host ?: ""}${uri.path}"
    }
    
    fun checkFromChat(client: TwitchClient, event: ChannelMessageEvent): Boolean {
        val isEnabled =
            runBlocking { dao.user(event.channel.id)?.decode()?.streamerData?.isSafeBrowsingEnabled ?: false }
        if (!isEnabled)
            return true
        for (url in isURL(event.message)) {
            val response = isURLSafe(parseURL(url))
            if (response != null) {
                print(response)
                client.chat.sendMessage(event.channel.name, "위험한 사이트 같아요! 조심하세요! 사이트 분류는 ${response}라고 해요!")
                return false
            }
        }
        return true
    }
}