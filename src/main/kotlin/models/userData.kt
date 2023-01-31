package models

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*

@Serializable
data class StreamerData(val command: MutableMap<String, String>, val isSafeBrowsingEnabled:Boolean)

@Serializable
data class Roulette(val chances:Int, val combo:Int)

@Serializable
data class ListenerData(val score:MutableMap<Int,Int>,val roulette:MutableMap<Int,Roulette>)

data class EncodedUserData(val uid:Int, val streamerData:String, val listenerData:String)
data class UserData(val uid:Int, val streamerData: StreamerData, val listenerData: ListenerData)

fun UserData.encode():EncodedUserData =
    EncodedUserData(uid,Json.encodeToString(streamerData),Json.encodeToString(listenerData))
fun EncodedUserData.decode():UserData = UserData(uid,Json.decodeFromString(streamerData),Json.decodeFromString(listenerData))

object UserDataTable: Table(){
    val uid=integer("id")
    val streamerData=varchar("command",1024)
    val listenerData=varchar("listenerData",1024)
    
    override val primaryKey=PrimaryKey(uid)
}