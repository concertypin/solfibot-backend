package models.userData

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ModifyValueConflict : Exception("offset or data must be null.")

data class Modify(val offset:Int?=null, val data:Int?=null)

fun ListenerData.editRoulette(uid:String,chances: Modify=0.offset,combo: Modify=0.offset):ListenerData=
    this.editRoulette(uid.toInt(),chances,combo)

fun ListenerData.editRoulette(uid: Int, chances: Modify=0.offset, combo: Modify=0.offset):ListenerData
{
    val rouletteData=roulette[uid] ?: Roulette(lastEditedTime = 0)
    
    if((chances.data ?: rouletteData.chances) < rouletteData.chances || (chances.offset ?: 1) < 0) //modified chance < original chance
        rouletteData.lastEditedTime=System.currentTimeMillis()
    if(chances.offset == null && chances.data != null)
        rouletteData.chances=chances.data
    else if(chances.offset != null && chances.data == null)
        rouletteData.chances += chances.offset
    else
        throw ModifyValueConflict()
    
    if(combo.offset == null && combo.data != null)
        rouletteData.combo=combo.data
    else if(combo.offset != null && combo.data == null)
        rouletteData.combo += combo.offset
    else
        throw ModifyValueConflict()
    roulette[uid] = rouletteData
    return this
}

val Int.offset:Modify
    get()= Modify(this)
val Int.data:Modify
    get()= Modify(data=this)

fun EncodedUserData.decode():UserData = UserData(uid, Json.decodeFromString(streamerData), Json.decodeFromString(listenerData))