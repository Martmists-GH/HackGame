package com.martmists.server

data class HostConfig(
    val port: Int,
)

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val database: String,
    val driver: String,
)

data class ServerConfig(
    val server: HostConfig,
    val database: DatabaseConfig,
)
