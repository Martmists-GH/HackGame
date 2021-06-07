package com.martmists.hackgame.server.content

val HELP_TEXT = """\
    |Welcome to HackGame!
    |
    |You can type 'commands' to view a list of all commands, and 'help <command>' to learn more about what it does.
    |
    |To get started, connect to 1.2.3.4 with password 'h4ckpr00f' and get the installed software.
    |You can also browse the filesystem a bit, there may be some interesting files.
""".trimMargin()

val HELP_COMMANDS = mapOf(
    "money" to """\
        |Shows how much money is stored on this system.
    """.trimMargin()
)
