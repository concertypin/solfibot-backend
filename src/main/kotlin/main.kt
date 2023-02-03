import commands.cmdIndex
import commands.etcIndex
import commands.scoreIndex
import plugins.cmdPluginIndex
import settings.*


fun main() {
    val chatbot = Chatbot("sudo ", auth)
    
    dao.DatabaseFactory.init()
    chatbot.attachCommands(etcIndex, cmdIndex, scoreIndex)
    chatbot.attachPlugins(cmdPluginIndex)
    chatbot.run(joinUsername)
}