package plugins

import Command
import kotlin.reflect.KFunction1


val etcIndex=listOf<Command>(
    Command("룰렛", ::roulette,0)
)
fun roulette(args:List<String>):String
{
    return args.toString()// todo
}