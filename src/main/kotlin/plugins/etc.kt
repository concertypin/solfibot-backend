package plugins

import Command
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import auth
import com.github.twitch4j.helix.domain.BanUserInput
import com.github.twitch4j.helix.domain.BanUsersList
import com.netflix.hystrix.HystrixCommand
import java.util.StringJoiner

val etcIndex=listOf(
    Command("룰렛", ::roulette,0),
    Command("리더보드",::leaderboard, 0)
)

fun ban(client: TwitchClient, event: ChannelMessageEvent,userID:String,duration:Int,reason:String=""): HystrixCommand<BanUsersList>
    =client.helix.banUser(auth.token, event.channel.id,auth.userID, BanUserInput(userID,reason,duration))


fun roulette(client: TwitchClient, event:ChannelMessageEvent, args:List<String>):String
{
    if((1..6).random()==6) {
        //당첨
        //{"data": {"user_id":"9876","duration":300,"reason":"no reason"}}
        ban(client,event,event.user.id,10,"러시안 룰렛당해버린")
        return "${event.user.name} -> 탕! ${TODO()}번 살아남으셨습니다!"
    }
    else {
        return args.toString()// todo
    }
}

fun leaderboard(client: TwitchClient,event: ChannelMessageEvent,dummy:List<String>):String
{
    return "asdf" //todo
}