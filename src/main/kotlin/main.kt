import commands.etcIndex
import settings.*


fun main() {
    val chatbot = Chatbot("sudo ", auth)
    
    dao.DatabaseFactory.init()
    chatbot.attachCommands(etcIndex)
    chatbot.run(joinUsername)
}