package dao

import dao.DatabaseFactory.dbQuery
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToUserData(row: ResultRow) = EncodedUserData(
        uid = row[UserDataTable.uid],
        listenerData = row[UserDataTable.listenerData],
        streamerData = row[UserDataTable.streamerData]
    )
    
    override suspend fun allUsers(): List<EncodedUserData> = dbQuery {
        UserDataTable.selectAll().map(::resultRowToUserData)
    }
    
    override suspend fun user(uid: Int): EncodedUserData? = dbQuery {
        UserDataTable.select { UserDataTable.uid eq uid }
            .map(::resultRowToUserData)
            .singleOrNull()
    }
    
    override suspend fun existUser(uid: Int): UserData {
        val query=user(uid)
        return if(query != null)
            query.decode()
        else {
            addNewUser(uid,StreamerData(),ListenerData())
            UserData(uid)
        }
    }
    
    override suspend fun addNewUser(uid:Int, streamerData: StreamerData, listenerData: ListenerData): EncodedUserData? = dbQuery {
        val insertStatement = UserDataTable.insert {
            it[UserDataTable.uid] = uid
            it[UserDataTable.streamerData] = Json.encodeToString(StreamerData.serializer(),streamerData)
            it[UserDataTable.listenerData] = Json.encodeToString(ListenerData.serializer(), listenerData)
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUserData)
    }
    
    override suspend fun editUser(uid: Int, streamerData: StreamerData, listenerData: ListenerData): Boolean = dbQuery {
        UserDataTable.update ({UserDataTable.uid eq uid}) {
            it[UserDataTable.uid]=uid
            it[UserDataTable.streamerData]=Json.encodeToString(StreamerData.serializer(),streamerData)
            it[UserDataTable.listenerData] = Json.encodeToString(ListenerData.serializer(), listenerData)
        } > 0
    }
    
    override suspend fun deleteUser(uid: Int): Boolean = dbQuery {
        UserDataTable.deleteWhere { UserDataTable.uid eq uid } > 0
    }
}

val dao: DAOFacade = DAOFacadeImpl().apply {
    runBlocking {
        if(allUsers().isEmpty())
            addNewUser(1024,
                StreamerData(mutableMapOf("테스트" to "안농!"), false),
                ListenerData(mutableMapOf(1024 to 0), mutableMapOf(0 to Roulette(0, 0)))
                )
    }
}