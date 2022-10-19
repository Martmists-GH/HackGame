package com.martmists.server.database.tables

import com.martmists.server.Server
import org.jetbrains.exposed.sql.Table

object HostTable : Table() {
    val address = varchar("address", 23) // max xxx.xxx.xxx.xxx.xxx.xxx
    val device = if (Server.config.database.driver == "postgres") binary("host") else binary("host", 1000000)

    override val primaryKey = PrimaryKey(address)
}
