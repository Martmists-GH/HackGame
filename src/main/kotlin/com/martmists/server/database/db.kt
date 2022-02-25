package com.martmists.server.database

import com.martmists.server.Server
import com.martmists.server.database.tables.AccountTable
import com.martmists.server.database.tables.HostTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import java.sql.Connection

private val database by lazy {
    val cfg = Server.config.database
    val db = when (cfg.driver) {
        "sqlite" -> {
            val db = Database.connect("jdbc:sqlite:./${cfg.database}", driver = "org.sqlite.JDBC")
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            db
        }
        "postgres" -> {
            Database.connect(
                "jdbc:postgresql://${cfg.host}:${cfg.port}/${cfg.database}",
                driver = "org.postgresql.Driver",
                user = cfg.username,
                password = cfg.password
            )
        }
        else -> throw IllegalArgumentException("Unknown database driver: ${cfg.driver}")
    }

    org.jetbrains.exposed.sql.transactions.transaction(db) {
        SchemaUtils.createMissingTablesAndColumns(
            HostTable,
            AccountTable,
        )
    }

    db
}

suspend fun <T> transaction(block: suspend () -> T) : T {
    return suspendedTransactionAsync(Dispatchers.IO, database) {
        block()
    }.await()
}
