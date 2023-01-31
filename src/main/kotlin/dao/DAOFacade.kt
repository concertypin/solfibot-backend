package dao

import models.EncodedUserData
import models.ListenerData
import models.StreamerData
import models.UserData

interface DAOFacade {
    suspend fun allUsers(): List<EncodedUserData>
    suspend fun user(uid: Int): EncodedUserData?
    suspend fun existUser(uid:Int): UserData
    suspend fun addNewUser(uid:Int, streamerData:StreamerData,listenerData:ListenerData): EncodedUserData?
    suspend fun editUser(uid: Int, streamerData: StreamerData,listenerData: ListenerData): Boolean
    suspend fun deleteUser(uid: Int): Boolean
}