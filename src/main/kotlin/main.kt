import commands.cmdIndex
import commands.etcIndex
import commands.safeBrowsingIndex
import commands.scoreIndex
import plugins.cmdPluginIndex
import plugins.safeBrowsingPluginIndex
import settings.auth
import settings.joinUsername
import settings.prefix


fun main() {
    val chatbot = Chatbot(prefix, auth)
    
    dao.DatabaseFactory.init()
    chatbot.attachCommands(etcIndex, cmdIndex, scoreIndex, safeBrowsingIndex)
    chatbot.attachPlugins(cmdPluginIndex, safeBrowsingPluginIndex)
    chatbot.run(joinUsername)
}