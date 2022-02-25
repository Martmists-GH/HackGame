package com.martmists.client

data class HostConfig(
    val host: String,
    val port: Int
)

data class ThemeConfig(
    val name: String,
    val colors: Map<String, Int>
)

data class UIConfig(
    val vsync: Boolean,
)

data class ClientConfig(
    val server: HostConfig,
    val theme: ThemeConfig,
    val ui: UIConfig
)
