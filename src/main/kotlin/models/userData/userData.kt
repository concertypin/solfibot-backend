package models.userData

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Table
import settings.maxChance


@Serializable
data class StreamerData(
    val command: MutableMap<String, List<String>> = mutableMapOf(),
    var isSafeBrowsingEnabled: Boolean = false
)

@Serializable
data class Roulette(var chances:Int = maxChance, var combo:Int = 0, var lastEditedTime:Long=0)

@Serializable
data class ListenerData(val score:MutableMap<Int,Int> = mutableMapOf(),val roulette:MutableMap<Int,Roulette> = mutableMapOf())

data class EncodedUserData(val uid:Int, val streamerData:String=Json.encodeToString(StreamerData()), val listenerData:String=Json.encodeToString(ListenerData()))
data class UserData(val uid:Int, val streamerData: StreamerData= StreamerData(), val listenerData: ListenerData= ListenerData())

object UserDataTable: Table(){
    val uid=integer("id")
    val streamerData=varchar("streamerData",1024)
    val listenerData=varchar("listenerData",1024)
    
    override val primaryKey=PrimaryKey(uid)
}