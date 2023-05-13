package dao

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import models.db.userData.ListenerData
import models.db.userData.StreamerData
import models.db.userData.UserData
import org.litote.kmongo.eq

class DAOFacadeImpl : DAOFacade {
    override suspend fun user(uid: String): UserData? {
        val col = database.getCollection(uid, UserData::class.java)
        return col.find().awaitFirstOrNull()
    }
    
    override suspend fun existUser(uid: String): UserData {
        val query = user(uid)
        return if (query != null)
            query
        else {
            addNewUser(uid, StreamerData(), ListenerData())
            UserData(uid, StreamerData(), ListenerData())
        }
    }
    
    override suspend fun addNewUser(uid: String, streamerData: StreamerData, listenerData: ListenerData): UserData? {
        val col = database.getCollection(uid, UserData::class.java)
        val doc = UserData(uid, streamerData, listenerData)
        
        return if (col.insertOne(doc).awaitFirstOrNull() == null)
            doc
        else
            null
    }
    
    override suspend fun editUser(uid: String, streamerData: StreamerData, listenerData: ListenerData): Boolean {
        val col = database.getCollection(uid, UserData::class.java)
        return col.replaceOne(UserData::userid eq uid, UserData(uid, streamerData, listenerData))
            .awaitFirstOrNull() == null
    }
    
    override suspend fun deleteUser(uid: String): Boolean {
        val col = database.getCollection(uid, UserData::class.java)
        col.drop().awaitFirst()
        return true
    }
}

val dao: DAOFacade = DAOFacadeImpl()