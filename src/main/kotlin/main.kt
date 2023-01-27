import com.github.philippheuer.events4j.reactor.ReactorEventHandler
import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import plugins.etcIndex

fun main()
{
    val joinUsername=setOf("solfibot", "orrrchan")

    val chatbot=Chatbot("sudo ")

    chatbot.attachPlugin(etcIndex)
    chatbot.parseCommand("sudo 룰렛 하잉 와 샌즈")
    /*
    eventManager.onEvent(ChannelMessageEvent::class.java) { event ->
        println("[${event.channel.name}] ${event.user.name}: ${event.message}")
    }
     */
}


class Chatbot(private val prefix: String) {
    private val twitchClient: TwitchClient = TwitchClientBuilder.builder()
        .withDefaultEventHandler(ReactorEventHandler::class.java)
        .withEnableHelix(true)
        .build()
    
    private val commandsMap= mutableMapOf<String, Command>()

    fun attachPlugin(vararg indexes:List<Command>){
        for(i in indexes)
        {
            for(j in i)
            {
                commandsMap[j.name]=j
            }
        }
    }

    fun parseCommand(rawCommand:String)
    {
        if(!rawCommand.startsWith(prefix))
            return
        val command=rawCommand.slice(prefix.length until rawCommand.length).split(" ")
        val response=commandsMap[command[0]]?.function!!.invoke(command.subList(1,command.size))
        println(response) //todo

    }

    fun run(username:Set<String>)
    {
        //todo
        for(i in username)
            twitchClient.chat.joinChannel(i)
        
        twitchClient.eventManager.onEvent(ChannelMessageEvent::class.java) { event ->
            println("[${event.channel.name}] ${event.user.name}: ${event.message}")
        }
    }
}