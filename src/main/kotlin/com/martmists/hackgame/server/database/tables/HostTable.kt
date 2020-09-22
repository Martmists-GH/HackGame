package com.martmists.hackgame.server.database.tables

import org.jetbrains.exposed.sql.Table

object HostTable : Table() {
    val address = varchar("address", 23) // max xxx.xxx.xxx.xxx.xxx.xxx
    val device = binary("host")

    override val primaryKey = PrimaryKey(address)
}