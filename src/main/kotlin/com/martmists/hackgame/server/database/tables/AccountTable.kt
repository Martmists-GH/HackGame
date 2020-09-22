package com.martmists.hackgame.server.database.tables

import org.jetbrains.exposed.sql.Table

object AccountTable : Table() {
    val username = text("username").uniqueIndex()
    val passwordHash = binary("password_hash")
    val homeAddress = varchar("home_address", 23).references(HostTable.address)

    override val primaryKey = PrimaryKey(username)
}
