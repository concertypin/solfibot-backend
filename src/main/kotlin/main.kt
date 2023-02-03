import commands.cmdIndex
import commands.etcIndex
import commands.scoreIndex
import plugins.cmdPluginIndex
import settings.auth
import settings.joinUsername
import settings.prefix


fun main() {
    val chatbot = Chatbot(prefix, auth)
    
    dao.DatabaseFactory.init()
    chatbot.attachCommands(etcIndex, cmdIndex, scoreIndex)
    chatbot.attachPlugins(cmdPluginIndex)
    chatbot.run(joinUsername)
}