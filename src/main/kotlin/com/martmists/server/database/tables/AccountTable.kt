package com.martmists.server.database.tables

import org.jetbrains.exposed.sql.Table

object AccountTable : Table() {
    val username = varchar("username", 32).uniqueIndex()
    val password = varchar("password", 256)
    val homeAddress = varchar("home_address", 23).references(HostTable.address)

    override val primaryKey = PrimaryKey(username)
}
