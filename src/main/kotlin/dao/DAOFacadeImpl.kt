package dao

import models.db.userData.*
import realm

class DAOFacadeImpl : DAOFacade {
    
    override suspend fun allUsers(): List<UserData> {
    
    }
    
    
    override suspend fun user(uid: String): UserData? {
        realm.query()
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
    
    
    /*
    override suspend fun addNewUser(uid:Int, streamerData: StreamerData, listenerData: ListenerData): UserData?
    {
        realm.write {
        
        }
    }
    */
    override suspend fun addNewUser(uid: String, streamerData: StreamerData, listenerData: ListenerData) =
        addNewUser(uid.toInt(), streamerData, listenerData)
    /*
    override suspend fun editUser(uid: Int, streamerData: StreamerData, listenerData: ListenerData): Boolean = dbQuery {
        UserDataTable.update ({UserDataTable.uid eq uid}) {
            it[UserDataTable.uid]=uid
            it[UserDataTable.streamerData]=Json.encodeToString(StreamerData.serializer(),streamerData)
            it[UserDataTable.listenerData] = Json.encodeToString(ListenerData.serializer(), listenerData)
        } > 0
    }
    
    override suspend fun editUser(uid: String, streamerData: StreamerData, listenerData: ListenerData)=editUser(uid.toInt(),streamerData,listenerData)
    
    override suspend fun selectUsersVia(query: SqlExpressionBuilder.() -> Op<Boolean>): List<UserData> = dbQuery {
        UserDataTable.select(query)
            .map(::resultRowToUserData)
            .map(EncodedUserData::decode)
    }
    
    override suspend fun deleteUser(uid: Int): Boolean = dbQuery {
        UserDataTable.deleteWhere { UserDataTable.uid eq uid } > 0
    }
    
    override suspend fun deleteUser(uid: String) = deleteUser(uid.toInt())
    
    override suspend fun queryRawStringInStreamerData(query: String): List<StreamerData> = dbQuery {
        UserDataTable.select { UserDataTable.streamerData like query }
            .map(::resultRowToUserData)
            .map(EncodedUserData::decode)
            .map(UserData::streamerData)
    }
    
    override suspend fun queryRawStringInListenerData(query: String): List<ListenerData> = dbQuery {
        UserDataTable.select { UserDataTable.listenerData like query }
            .map(::resultRowToUserData)
            .map(EncodedUserData::decode)
            .map(UserData::listenerData)
     }
     */
}

val dao: DAOFacade = DAOFacadeImpl()