package dao

import models.EncodedUserData
import models.ListenerData
import models.StreamerData
import models.UserData
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

interface DAOFacade {
    suspend fun allUsers(): List<EncodedUserData>
    
    suspend fun user(uid: Int): EncodedUserData?
    suspend fun user(uid:String): EncodedUserData?
    
    suspend fun existUser(uid:Int): UserData
    suspend fun existUser(uid:String): UserData
    
    suspend fun addNewUser(uid:Int, streamerData:StreamerData,listenerData:ListenerData): EncodedUserData?
    suspend fun addNewUser(uid:String,streamerData: StreamerData,listenerData: ListenerData):EncodedUserData?
    
    suspend fun editUser(uid: Int, streamerData: StreamerData,listenerData: ListenerData): Boolean
    suspend fun editUser(uid:String,streamerData: StreamerData,listenerData: ListenerData):Boolean
    
    suspend fun selectUsersVia(query:SqlExpressionBuilder.()->Op<Boolean>): List<UserData>
    
    suspend fun deleteUser(uid: Int): Boolean
    suspend fun deleteUser(uid:String):Boolean
}