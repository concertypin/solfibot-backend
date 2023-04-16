package utils

import com.yundom.kache.Builder
import com.yundom.kache.Kache
import com.yundom.kache.config.LRU


object Cache {
    private val lruCache:Kache<Int, String> = Builder.build{
        policy = LRU
        capacity = 1024
    }
    private const val cachedAsNull="NULL_CACHED"
    fun cached(func: (String) -> String?): (String) -> String? {
        fun responseFunction(input:String):String?
        {
            val hashed=input.hashCode()
            val cacheResponse=lruCache.get(hashed) //try getting cached response
            if(cacheResponse != null) // hit
                return if(cacheResponse==cachedAsNull) null else cacheResponse
            val response=func(input)
            lruCache.put(hashed, response?: cachedAsNull)
            return if(response== cachedAsNull) null else response
        }
        return ::responseFunction
    }
}