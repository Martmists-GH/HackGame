package com.martmists.server.game.software

import com.martmists.commandparser.dispatch.Dispatcher
import com.martmists.commandparser.dsl.BuildCommandContext
import com.martmists.commandparser.dsl.build
import com.martmists.server.commands.ServerCommandContext
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
abstract class Software {
    abstract val filename: String
    abstract val name: String
    abstract val tier: Int
    abstract val type: Type

    internal fun Dispatcher<ServerCommandContext>.build() {
        build(this) {
            command(name) {
                check {
                    session.device!!.software.contains(this@Software)
                }

                buildCommand()
            }
        }
    }

    abstract fun BuildCommandContext<ServerCommandContext>.buildCommand()

    enum class Type {
        LOCK,
        EXECUTABLE,
        MALWARE,
        TROPHY
    }
}
