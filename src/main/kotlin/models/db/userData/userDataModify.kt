package models.db.userData

class ModifyValueConflict : Exception("offset or data must be null.")

data class Modify(val offset: Int? = null, val data: Int? = null)

fun ListenerData.editRoulette(uid: String, chances: Modify = 0.offset, combo: Modify = 0.offset): ListenerData {
    val rouletteData = roulette[uid] ?: Roulette(lastEditedTime = 0)
    
    if ((chances.data ?: rouletteData.chances) < rouletteData.chances || (chances.offset
            ?: 1) < 0
    ) //modified chance < original chance
        rouletteData.lastEditedTime = (System.currentTimeMillis() / 1000).toInt()
    if (chances.offset == null && chances.data != null)
        rouletteData.chances = chances.data
    else if (chances.offset != null && chances.data == null)
        rouletteData.chances += chances.offset
    else
        throw ModifyValueConflict()
    
    if (combo.offset == null && combo.data != null)
        rouletteData.combo = combo.data
    else if (combo.offset != null && combo.data == null)
        rouletteData.combo += combo.offset
    else
        throw ModifyValueConflict()
    roulette[uid] = rouletteData
    return this
}

val Int.offset: Modify
    get() = Modify(this)
val Int.data: Modify
    get() = Modify(data = this)