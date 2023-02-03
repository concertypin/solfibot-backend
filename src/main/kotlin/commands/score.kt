package commands

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dao.dao
import kotlinx.coroutines.runBlocking
import models.Command
import models.decode
import settings.auth
import settings.trustableUser

val scoreIndex= listOf(
    Command("학점",::score,0)
)

fun isSudoers(client:TwitchClient,event:ChannelMessageEvent):Boolean
{
    if(event.channel.id==event.user.id)
        return true
    if(event.user.name in trustableUser)
        return true
    val response=client.helix.getModerators(auth.token,event.channel.id, listOf(event.user.id), null,1).execute()
    return response.moderators.isNotEmpty()
}

fun String.usernameToUid(client: TwitchClient):String?
{
    val response=client.helix.getUsers(auth.token,null, listOf(this)).execute()
    return response.users.firstOrNull()?.id
}

fun score(client:TwitchClient,event:ChannelMessageEvent,args:List<String>):String
{
    fun lookup(uid:String):Int
    {
        return runBlocking {
            return@runBlocking dao.user(uid)?.decode()?.listenerData?.score?.get(event.channel.id.toInt()) ?: 0
            //"${event.user.name}님의 학점은 ${score}이에요!"
        }
    }
    
    if(!isSudoers(client,event) || args.isEmpty()) //self-listing by normal user or sudoers(no args)
        return "${event.user.name}님의 학점은 ${lookup(event.user.id)}이에요!"
    
    val targetUid=args[0].usernameToUid(client) ?: return "사용자가 존재하지 않아요!"
    
    if(args.size==1)
        return "${args[0]}님의 학점은 ${lookup(targetUid)}이에요!"
    
    else //edit
    {
        return runBlocking {
            val user = dao.existUser(targetUid)
            val score = user.listenerData.score[event.channel.id.toInt()] ?: 0
            user.listenerData.score[event.channel.id.toInt()] = score + (args[1].toIntOrNull() ?: return@runBlocking "올바르지 않은 숫자에요!")
            dao.editUser(
                targetUid,
                user.streamerData,
                user.listenerData
            )
            return@runBlocking "이제 ${args[0]}님의 학점은 ${score + args[1].toInt()}이에요!"
        }
    }
}