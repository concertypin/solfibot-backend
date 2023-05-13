package dao

import models.db.userData.ListenerData
import models.db.userData.StreamerData
import models.db.userData.UserData

interface DAOFacade {
    suspend fun allUsers(): List<UserData>
    
    suspend fun user(uid: String): UserData?
    
    suspend fun existUser(uid: String): UserData
    
    suspend fun addNewUser(uid: String, streamerData: StreamerData, listenerData: ListenerData): UserData?
    
    suspend fun editUser(uid: String, streamerData: StreamerData, listenerData: ListenerData): Boolean
    
    suspend fun deleteUser(uid: String): Boolean
}