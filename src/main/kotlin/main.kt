import commands.cmdIndex
import commands.etcIndex
import plugins.cmdPluginIndex
import settings.*


fun main() {
    val chatbot = Chatbot("sudo ", auth)
    
    dao.DatabaseFactory.init()
    chatbot.attachCommands(etcIndex, cmdIndex)
    chatbot.attachPlugins(cmdPluginIndex)
    chatbot.run(joinUsername)
}