import models.AuthToken
import plugins.etcIndex
import java.nio.file.Files
import java.nio.file.Paths

val auth= AuthToken(secret("TWITCH_CLIENT_ID"), secret("TWITCH_TOKEN"), "", "")

fun secret(name:String):String=
    if(System.getenv("DOCKER") != "1")
        System.getenv(name)
    else
        Files.readString(Paths.get("/run/secrets/$name"))

fun main() {
    val joinUsername = setOf("solfibot", "orrrchan")
    val chatbot = Chatbot("sudo ", auth)
    
    dao.DatabaseFactory.init()
    chatbot.attachCommands(etcIndex)
    chatbot.run(joinUsername)
}