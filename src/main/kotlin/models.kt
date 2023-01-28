import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import kotlin.reflect.*

data class Command(val name:String, val function: KFunction3<TwitchClient,ChannelMessageEvent,List<String>, String?>,val requiredParams:Int)
data class AuthToken(val clientID:String, val token:String,var username:String, var userID:String)