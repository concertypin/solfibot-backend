import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.philippheuer.events4j.reactor.ReactorEventHandler
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import models.AuthToken
import models.Command
import models.Plugins
import settings.auth

const val COMMAND_INDICATOR='`'

class Chatbot(private val prefix: String, credential: AuthToken) {
    
    private val twitchClient: TwitchClient = TwitchClientBuilder.builder()
        .withDefaultEventHandler(ReactorEventHandler::class.java)
        .withEnableHelix(true)
        .withEnableChat(true)
        .withDefaultAuthToken(OAuth2Credential(credential.clientID,credential.token))
        .withClientId(credential.clientID)
        .withChatAccount(OAuth2Credential(credential.clientID, credential.token))
        .build()
    
    init {
        val me = twitchClient.helix.getUsers(null, null, null).execute().users[0]
        auth.username = me.login
        auth.userID = me.id
    }
    
    private val commandsMap = mutableMapOf<String, Command>()
    private val pluginsMap = mutableListOf<Plugins>()
    
    fun attachCommands(vararg indexes: List<Command>) {
        for (i in indexes)
            i.forEach { commandsMap[it.name] = it }
    }
    
    fun attachPlugins(vararg indexes: List<Plugins>) {
        for (i in indexes)
            i.forEach { pluginsMap.add(it) }
    }
    
    private fun parseCommand(event: ChannelMessageEvent): String? {
        val rawCommand = event.message
        if (!rawCommand.startsWith(prefix))
            return null
        
        fun String.parseViaIndicator():List<String>
        {
            val result = mutableListOf<String>()
            var temp = ""
            var isIndicator = false
            for (i in this)
            {
                if (i == COMMAND_INDICATOR)
                {
                    isIndicator = !isIndicator
                    continue
                }
                if (i == ' ' && !isIndicator)
                {
                    result.add(temp)
                    temp = ""
                    continue
                }
                temp += i
            }
            result.add(temp)
            return result
        }
        
        val command = rawCommand.slice(prefix.length until rawCommand.length).parseViaIndicator()
        
        val cmdObj = commandsMap[command[0]] ?: return null
        
        if (cmdObj.requiredParams > command.size - 1)
            return null
    
        return try { cmdObj.function.invoke(twitchClient, event, command.subList(1, command.size)) }
            catch (e: Exception) {
                e.printStackTrace();null
            }
    }
    
    fun run(username:Set<String>)
    {
        for(i in username)
            twitchClient.chat.joinChannel(i)
        
        twitchClient.eventManager.onEvent(ChannelMessageEvent::class.java) { event ->
            for (i in pluginsMap)
                try {
                    if (!i.function.invoke(twitchClient, event))
                            return@onEvent
                } finally { }
            
            val response = parseCommand(event)
            if (response != null)
                twitchClient.chat.sendMessage(event.channel.name, response)
        }
    }
}