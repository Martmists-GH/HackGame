package com.martmists.server.entities

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import kotlinx.serialization.ExperimentalSerializationApi
import com.mojang.brigadier.builder.ArgumentBuilder as BrigadierArgumentBuilder
import java.util.concurrent.CompletableFuture

@ExperimentalSerializationApi
object CommandBuilder {
    @JvmStatic
    fun builder(dispatcher: CommandDispatcher<ServerCommandSource>, callback: Builder.() -> Unit) {
        val builder = Builder(dispatcher)
        callback(builder)
    }

    @ExperimentalSerializationApi
    class Builder(val dispatcher: CommandDispatcher<ServerCommandSource>) {
        fun command(vararg names: String, builder: CommandBuilder.() -> Unit) {
            val base = names.first()
            val aliases = names.filter { it != base }

            names.forEach {
                val baseCommand = LiteralArgumentBuilder.literal<ServerCommandSource>(it)
                builder(CommandBuilder(baseCommand))
                val baseNode = dispatcher.register(baseCommand)
            }

            /*
            TODO: Use this instead of building for every alias once
                  https://github.com/Mojang/brigadier/issues/46 is fixed
            aliases.forEach {
                val node = CommandManager.literal(it)
                node.fork(baseNode)
                node.executes(baseNode.command)
                dispatcher.register(node)
            }
            */
        }
        
        fun software(name: String, builder: CommandBuilder.() -> Unit) {
            val node = LiteralArgumentBuilder.literal<ServerCommandSource>(name)
            builder(CommandBuilder(node))
            val configured = node.requirement
            node.requires {
                it.currentHost.software.any { it.name == name } && configured.test(it)
            }
            dispatcher.register(node)
        }
    }

    @ExperimentalSerializationApi
    open class CommandBuilder(internal val command: BrigadierArgumentBuilder<ServerCommandSource, *>) {
        fun requires(checkFunction: (ServerCommandSource) -> Boolean) {
            this.command.requires(checkFunction)
        }

        private fun argumentNode(node: BrigadierArgumentBuilder<ServerCommandSource, *>, builder: ArgumentBuilder.() -> Unit) {
            builder(ArgumentBuilder(node))
            command.then(node)
        }

        fun argument(name: String, type: ArgumentType<*>, builder: ArgumentBuilder.() -> Unit) {
            val arg = RequiredArgumentBuilder.argument<ServerCommandSource, Any>(name, type as ArgumentType<Any>)
            argumentNode(arg, builder)
        }

        fun literal(vararg literals: String, builder: ArgumentBuilder.() -> Unit) {
            literals.forEach {
                val arg = LiteralArgumentBuilder.literal<ServerCommandSource>(it)
                argumentNode(arg, builder)
            }
            // TODO: switch to redirects if possible
        }

        fun executes(callback: (CommandContext<ServerCommandSource>) -> Unit) {
            command.executes {
                callback.invoke(it)
                1
            }
        }
    }

    @ExperimentalSerializationApi
    class ArgumentBuilder(argument: BrigadierArgumentBuilder<ServerCommandSource, *>) : CommandBuilder(argument) {
        fun suggests(callback: (CommandContext<ServerCommandSource>, SuggestionsBuilder) -> CompletableFuture<Suggestions>) {
            (this.command as RequiredArgumentBuilder<ServerCommandSource, *>).suggests(callback)
        }
    }
}
