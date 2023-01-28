package plugins

import Command
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import auth
val etcIndex=listOf(
    Command("룰렛", ::roulette,0),
    Command("리더보드",::leaderboard, 0)
)

fun roulette(client: TwitchClient, event:ChannelMessageEvent, args:List<String>):String
{
    if((1..6).random()==6)
    {
        //당첨
        client.helix.banUser(auth.token, //todo)
    }
    return args.toString()// todo
}

fun leaderboard(client: TwitchClient,event: ChannelMessageEvent,dummy:List<String>):String
{
    return "asdf" //todo
}