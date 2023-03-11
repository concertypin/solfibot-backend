package commands

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import com.github.twitch4j.helix.domain.BanUserInput
import dao.dao
import kotlinx.coroutines.runBlocking
import models.*
import settings.auth
import settings.maxChance
import utils.usernameToUID

val etcIndex=listOf(
    Command("룰렛", 0, false, ::roulette),
    Command("머리수복", 2, true, ::modify)
)

fun ban(client: TwitchClient, event: ChannelMessageEvent,userID:String,duration:Int,reason:String="") {
    client.helix.banUser(auth.token, event.channel.id, auth.userID, BanUserInput(userID, reason, duration)).execute()
}

fun roulette(client: TwitchClient, event: ChannelMessageEvent, ignoredArgs: List<String>): String {
    if (
        runBlocking {
            val user = dao.existUser(event.user.id)
            val roulette = user.let { it.listenerData.roulette[event.channel.id.toInt()] ?: Roulette() }
            if (roulette.lastEditedTime < System.currentTimeMillis() - (86_400_000L)) /*lastEditedTime < now -1day*/ {
                dao.editUser(
                    event.user.id,
                    user.streamerData,
                    user.listenerData.editRoulette(event.channel.id, chances = maxChance.data) //recover day by day
                )
                return@runBlocking false
            }
        return@runBlocking roulette.chances<=0
    }) return "오늘 룰렛을 돌리기에는 머리가 너무 많이 깨졌습니다."
    
    return if ((5..6).random() == 6) {
        val score: Int
        runBlocking {
            val user = dao.existUser(event.user.id)
            score = user.listenerData.roulette[event.channel.id.toInt()]?.combo ?: 0 // 0 if record not exist
            
            //writing db
            dao.editUser(
                event.user.id,
                user.streamerData, //pass it without modifying
                user.listenerData.editRoulette(event.channel.id, (-1).offset, 0.data)
            )
        }
            ban(client, event, event.user.id, 10, "러시안 룰렛당해버린") //timeout 10s
        "${event.user.name} -> 탕! ${score}번 살아남으셨습니다!"
    }
    else{
        val score:Int
        runBlocking {
            val user= dao.existUser(event.user.id)
            score = (user.listenerData.roulette[event.channel.id.toInt()]?.combo ?: 0) + 1
            dao.editUser(
                event.user.id,
                user.streamerData,
                user.listenerData.editRoulette(event.channel.id, 0.offset, 1.offset)
            ) // if record exist, combo++
        }
        "${event.user.name} -> 찰캌! ${score}번 살아남으셨습니다!"
    }
}

fun modify(client: TwitchClient, event: ChannelMessageEvent, args: List<String>): String {
    return runBlocking {
        val uid = args[0].usernameToUID(client) ?: return@runBlocking "적절하지 않은 아이디에요!"
        val user = dao.existUser(uid)
        
        val listenerData = user.listenerData.editRoulette(event.channel.id, 1.offset)
        //writing db
        dao.editUser(
            uid,
            user.streamerData, //pass it without modifying
            listenerData
        )
        return@runBlocking "${args[0]} 유저의 룰렛 기회는 이제 ${listenerData.roulette[event.channel.id.toInt()]?.chances}입니다!"
    }
}
