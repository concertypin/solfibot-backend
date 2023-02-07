package dao

import kotlinx.coroutines.Dispatchers
import models.UserDataTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val database =
            Database.connect(settings.jdbcURL, driverClassName, user = settings.username, password = settings.password)
    
        transaction(database) {
            SchemaUtils.create(UserDataTable)
        }
    }
    
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

