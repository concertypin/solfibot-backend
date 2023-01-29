import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.philippheuer.events4j.reactor.ReactorEventHandler
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import modules.sbPluginsIndex
import plugins.etcIndex
import java.nio.file.Files
import java.nio.file.Paths

val auth=AuthToken(
    if(System.getenv("DOCKER") != "1")
        System.getenv("TWITCH_CLIENT_ID")
    else
        Files.readString(Paths.get("/run/secrets/TWITCH_CLIENT_ID")),
    
    if (System.getenv("DOCKER")!="1")
        System.getenv("TWITCH_TOKEN")
    else
        Files.readString(Paths.get("/run/secrets/TWITCH_TOKEN")),
    "", ""
)

fun main() {
    val joinUsername = setOf("solfibot", "orrrchan")
    val chatbot = Chatbot("sudo ", auth)
    
    chatbot.attachCommands(etcIndex)
    chatbot.attachPlugins(sbPluginsIndex)
    chatbot.run(joinUsername)
}


class Chatbot(private val prefix: String, credential:AuthToken) {
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
    private val processorMap = mutableListOf<Processor>()
    fun attachCommands(vararg indexes: List<Command>) {
        for (i in indexes)
            i.forEach { commandsMap[it.name] = it }
    }
    
    fun attachPlugins(vararg indexes: List<Processor>) {
        for (i in indexes)
            i.forEach { processorMap.add(it) }
    }
    
    private fun parseCommand(event: ChannelMessageEvent): String? {
        val rawCommand = event.message
        if (!rawCommand.startsWith(prefix))
            return null
        val command = rawCommand.slice(prefix.length until rawCommand.length).split(" ")
        
        val cmdObj = commandsMap[command[0]] ?: return null
        
        if (cmdObj.requiredParams > command.size - 1)
            return null
        
        return cmdObj.function.invoke(twitchClient,event, command.subList(1, command.size))
    }
    
    fun run(username:Set<String>)
    {
        for(i in username)
            twitchClient.chat.joinChannel(i)
        
        twitchClient.eventManager.onEvent(ChannelMessageEvent::class.java) { event ->
            for (i in processorMap)
                if (i.function.invoke(twitchClient, event))
                    return@onEvent
    
            val response = parseCommand(event)
            if (response != null)
                twitchClient.chat.sendMessage(event.channel.name, response)
        }
        
        
    }
}