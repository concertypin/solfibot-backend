package plugins

import auth
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import com.github.twitch4j.helix.domain.BanUserInput
import com.github.twitch4j.helix.domain.BanUsersList
import com.netflix.hystrix.HystrixCommand
import dao.dao
import kotlinx.coroutines.runBlocking
import models.*

val etcIndex=listOf(
    Command("룰렛", ::roulette,0),
    Command("리더보드",::leaderboard, 0)
)

fun ban(client: TwitchClient, event: ChannelMessageEvent,userID:String,duration:Int,reason:String=""): HystrixCommand<BanUsersList>
    =client.helix.banUser(auth.token, event.channel.id,auth.userID, BanUserInput(userID,reason,duration))


fun roulette(client: TwitchClient, event:ChannelMessageEvent, args:List<String>):String
{
    return if ((1..6).random() == 6) {
        val score: Int
        runBlocking {
            val user = dao.user(event.user.id.toInt())
            score = if (user == null)
                0
            else {
                user.decode().listenerData.roulette[event.channel.id.toInt()]?.combo ?: 0
            }
        
            dao.editUser(
                event.user.id.toInt(),
                user?.decode()?.streamerData ?: StreamerData(mutableMapOf(), false),
                user?.decode()?.listenerData?.apply { this.roulette[event.channel.id.toInt()] = Roulette(3, 0) }
                    ?: ListenerData(mutableMapOf(), mutableMapOf(event.channel.id.toInt() to Roulette(3, 0))),
            )
            ban(client, event, event.user.id, 10, "러시안 룰렛당해버린")
        }
        "${event.user.name} -> 탕! ${score}번 살아남으셨습니다!"
    } else {
        args.toString()// todo
    }
}

fun leaderboard(client: TwitchClient,event: ChannelMessageEvent,dummy:List<String>):String
{
    return "asdf" //todo
}
