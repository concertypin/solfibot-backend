import api.module
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.philippheuer.events4j.reactor.ReactorEventHandler
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import com.github.twitch4j.events.ChannelGoOfflineEvent
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import models.twitch.AuthToken
import models.twitch.Command
import models.twitch.Plugin
import org.slf4j.LoggerFactory
import settings.auth
import settings.trustableUser
import kotlin.system.exitProcess

const val COMMAND_INDICATOR='`'

object Chatbot {
    private val logger = LoggerFactory.getLogger(Chatbot::class.java)
    lateinit var twitchClient: TwitchClient
    private lateinit var prefix: String
    
    fun setup(commandPrefix: String, credential: AuthToken) {
        prefix = commandPrefix
        twitchClient = TwitchClientBuilder.builder()
            .withDefaultEventHandler(ReactorEventHandler::class.java)
            .withEnableHelix(true)
            .withEnableChat(true)
            .withDefaultAuthToken(OAuth2Credential(credential.clientID, credential.token))
            .withClientId(credential.clientID)
            .withChatAccount(OAuth2Credential(credential.clientID, credential.token))
            .withFeignLogLevel(feign.Logger.Level.HEADERS)
            .build()
        
        val me = twitchClient.helix.getUsers(null, null, null).execute().users[0]
        auth.username = me.login
        auth.userID = me.id
    }
    
    private val commandsMap = mutableMapOf<String, Command>()
    private val pluginMap = mutableListOf<Plugin>()
    
    fun attachCommands(vararg indexes: List<Command>) {
        for (i in indexes)
            i.forEach { commandsMap[it.name] = it }
    }
    
    fun attachPlugins(vararg indexes: List<Plugin>) {
        for (i in indexes)
            i.forEach { pluginMap.add(it) }
    }
    
    private fun parseCommand(event: ChannelMessageEvent): String? {
        val rawCommand = event.message
        if (!rawCommand.startsWith(prefix))
            return null
        
        fun String.parseViaIndicator():List<String> {
            val result = mutableListOf<String>()
            var temp = ""
            var isIndicator = false
            for (i in this) {
                if (i == COMMAND_INDICATOR) {
                    isIndicator = !isIndicator
                    continue
                }
                if (i == ' ' && !isIndicator) {
                    result.add(temp)
                    temp = ""
                    continue
                }
                temp += i
            }
            result.add(temp)
            result.forEach { it.trim() }
            return result
        }
        
        val command = rawCommand.slice(prefix.length until rawCommand.length).parseViaIndicator()
        
        command.forEach {
            if (it.startsWith("/"))
                return "슬래시(/)로 시작하는 명령어는 입력할 수 없습니다."
        }
        
        val cmdObj = commandsMap[command[0]] ?: return null
        
        if (cmdObj.requiredParams > command.size - 1)
            return null
        if (cmdObj.isAdminCommand)
            if (!isSudoers(event))
                return null
        
        if (cmdObj.suspendFunction != null && cmdObj.function != null) // only one func
        {
            logger.error("Command ${cmdObj.name} doesn't have function OR suspendFunction.")
            exitProcess(1)
        }
        if (cmdObj.suspendFunction == null && cmdObj.function == null) {
            logger.error("Command ${cmdObj.name} doesn't have any function.")
        }
        
        return try {
            
            if (cmdObj.function != null)
                cmdObj.function.invoke(twitchClient, event, command.subList(1, command.size))
            else if (cmdObj.suspendFunction != null)
                runBlocking {
                    return@runBlocking cmdObj.suspendFunction.invoke(
                        twitchClient,
                        event,
                        command.subList(1, command.size)
                    )
                }
            else
                throw Exception("이거 왜 호출됨?")
            
        } catch (e: Exception) {
            logger.error(e.stackTraceToString())
            null
        }
    }
    
    fun run(username:Set<String>) {
        
        for(i in username)
            twitchClient.chat.joinChannel(i)
        
        twitchClient.eventManager.onEvent(ChannelGoOfflineEvent::class.java){
            exitProcess(0)
        }
        
        twitchClient.eventManager.onEvent(ChannelMessageEvent::class.java) { event ->
            if (event.user.id == auth.userID)
                return@onEvent
            
            for (i in pluginMap)
                try {
                    val response = i.function.invoke(twitchClient, event)
                    if (!response) { // if return value is false, stop running.
                        logger.warn("Chat ${event.message} process is blocked by ${i.function.name} in ${event.channel.name}")
                        return@onEvent
                    }
                } finally {
                }
            
            val response = parseCommand(event)
            if (response != null) {
                logger.info("${event.message} -> $response on ${event.channel.name}")
                twitchClient.chat.sendMessage(event.channel.name, response)
            }
        }
    }
    
    suspend fun startAPI() {
        embeddedServer(Netty, port = settings.port, host = "0.0.0.0", module = Application::module).start(wait = true)
    }
}

fun isSudoers(event: ChannelMessageEvent): Boolean {
    if (event.channel.id == event.user.id) // is broadcaster
        return true
    
    return event.user.name in trustableUser // is trustable user
}
