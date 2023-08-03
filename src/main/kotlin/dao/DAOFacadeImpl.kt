package dao

import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.flow.firstOrNull
import models.db.userData.ListenerData
import models.db.userData.StreamerData
import models.db.userData.UserData

class DAOFacadeImpl : DAOFacade {
    override suspend fun user(uid: String): UserData? {
        val col = database.getCollection<UserData>(uid)
        return col.find().firstOrNull()
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
        val col = database.getCollection<UserData>(uid)
        val doc = UserData(uid, streamerData, listenerData)
        
        return if (col.insertOne(doc).wasAcknowledged())
            doc
        else
            null
    }
    
    override suspend fun editUser(uid: String, streamerData: StreamerData, listenerData: ListenerData): Boolean {
        val col = database.getCollection<UserData>(uid)
        return col.replaceOne(
            eq(UserData::userid.name,uid),
            UserData(uid, streamerData, listenerData)
        ).wasAcknowledged()
    }
    
    override suspend fun deleteUser(uid: String): Boolean {
        val col = database.getCollection<UserData>(uid)
        col.drop()
        return true
    }
}

val dao: DAOFacade = DAOFacadeImpl()