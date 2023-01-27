import kotlin.reflect.KFunction1

data class Command(val name:String, val function: KFunction1<List<String>, String?>,val requiredParams:Int)