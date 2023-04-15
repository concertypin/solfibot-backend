package dao

import kotlinx.coroutines.Dispatchers
import models.userData.UserDataTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.sqlite.JDBC"
        val database =
            Database.connect("jdbc:sqlite:${settings.jdbcURL}", driverClassName)
    
        transaction(database) {
            SchemaUtils.create(UserDataTable)
        }
    }
    
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

