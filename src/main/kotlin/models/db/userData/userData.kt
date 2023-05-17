package models.db.userData

import kotlinx.serialization.Serializable
import settings.internalHiddenValue
import settings.maxChance

@Serializable
data class UserData(val userid: String, val streamerData: StreamerData, val listenerData: ListenerData)

@Serializable
data class StreamerData(
    val command: MutableMap<String, List<String>> =
        mutableMapOf("?ping" to listOf("pong?"), internalHiddenValue to listOf(internalHiddenValue)),
    var isSafeBrowsingEnabled: Boolean = false
)

@Serializable
data class Roulette(var chances: Int = maxChance, var combo: Int = 0, var lastEditedTime: Int = 0)

@Serializable
data class ListenerData(
    val score: MutableMap<String, Int> = mutableMapOf("0" to 0),
    val roulette: MutableMap<String, Roulette> = mutableMapOf("0" to Roulette())
)