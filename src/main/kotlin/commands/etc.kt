package commands

import com.apollographql.apollo.api.toInput
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import com.github.twitch4j.helix.domain.BanUserInput
import dao.dao
import kotlinx.coroutines.runBlocking
import models.*
import settings.*

val etcIndex=listOf(
    Command("룰렛", ::roulette,0),
    Command("리더보드",::leaderboard, 0)
)

fun ban(client: TwitchClient, event: ChannelMessageEvent,userID:String,duration:Int,reason:String="") {
    client.helix.banUser(auth.token, event.channel.id, auth.userID, BanUserInput(userID, reason, duration))
}

fun roulette(client: TwitchClient, event:ChannelMessageEvent, args:List<String>):String
{
    //todo: block roulette by chances
    val channelId=event.channel.id.toInt()
    return if ((1..6).random() == 6) {
        val score: Int
        runBlocking {
            val user= dao.existUser(event.user.id.toInt())
            score = user.listenerData.roulette[channelId]?.combo ?: 0 // 0 if record not exist
            
            //writing db
            dao.editUser(
                event.user.id.toInt(),
                user.streamerData, //pass it without modifying
                user.listenerData.editRoulette(channelId, (-1).offset, 0.data)
            )
        }
            ban(client, event, event.user.id, 10, "러시안 룰렛당해버린") //timeout 10s
        "${event.user.name} -> 탕! ${score}번 살아남으셨습니다!"
    }
    else{
        val score:Int
        runBlocking {
            val user= dao.existUser(event.user.id.toInt())
            score=(user.listenerData.roulette[channelId]?.combo ?: 0) + 1
            dao.editUser(event.user.id.toInt(),
                user.streamerData,
                user.listenerData.editRoulette(channelId,0.offset, 1.offset)) // if record exist, combo++
        }
        "${event.user.name} -> 찰랔! ${score}번 살아남으셨습니다!"
    }
}

fun leaderboard(client: TwitchClient,event: ChannelMessageEvent,dummy:List<String>):String
{
    return "asdf" //todo
}
