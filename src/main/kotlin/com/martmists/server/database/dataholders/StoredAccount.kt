package com.martmists.server.database.dataholders

import kotlinx.serialization.Serializable

@Serializable
data class StoredAccount(
        val name: String,
        val homeIP: String
)
