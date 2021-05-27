package com.martmists.hackgame.client.ui

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.SimpleTheme
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import com.martmists.hackgame.client.Client
import com.martmists.hackgame.common.packets.CommandPacket
import com.martmists.hackgame.common.packets.LoginPacket
import com.martmists.hackgame.common.registry.BuiltinPackets
import java.lang.NullPointerException
import java.util.regex.Pattern
import kotlin.math.floor
import kotlin.system.exitProcess

object Screen {
    val terminal: Terminal
    val screen: TerminalScreen

    val FOREGROUND = TextColor.RGB(255, 255, 255)
    val BACKGROUND = TextColor.RGB(0, 0, 0)

    fun makeTheme(foreground: TextColor = FOREGROUND, background: TextColor = BACKGROUND) = SimpleTheme.makeTheme(
            false,
            foreground,
            background,
            foreground,
            background,
            foreground,
            background,
            background
    )

    val mainPanel: Panel
    val logPanel: Panel
    val chatPanel: Panel
    val infoPanel: Panel
    val inputPanel: Panel
    val logText: ReadOnlyTextBox
    val chatText: ReadOnlyTextBox
    val infoText: ReadOnlyTextBox
    val inputBox: ActionTextBox
    val gui: MultiWindowTextGUI
    val mainWindow: BasicWindow

    init {
        val factory = DefaultTerminalFactory()
        factory.setForceTextTerminal(Client.INSTANCE.terminal)
        terminal = factory.createTerminal()
        screen = TerminalScreen(terminal)
        screen.startScreen()
        gui = MultiWindowTextGUI(screen, DefaultWindowManager(), null)
        gui.theme = makeTheme()

        mainWindow = BasicWindow()
        val width = screen.terminalSize.columns
        val height = screen.terminalSize.rows
        mainWindow.setHints(listOf(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS, Window.Hint.FIT_TERMINAL_WINDOW))

        mainPanel = Panel(GridLayout(2)).also { main ->
            main.layoutData = LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)

            logPanel = Panel().also { log ->
                log.layoutData = GridLayout.createLayoutData(
                        GridLayout.Alignment.FILL,
                        GridLayout.Alignment.FILL,
                        true,
                        true,
                        1,
                        2
                )
                log.layoutManager = GridLayout(1)
                log.preferredSize = TerminalSize(floor(width / 10.0 * 7).toInt(), height - 6)
                logText = ReadOnlyTextBox(log.preferredSize).also {
                    it.layoutData = GridLayout.createLayoutData(
                            GridLayout.Alignment.FILL,
                            GridLayout.Alignment.FILL,
                            true,
                            true,
                            1,
                            1
                    )
                    log.addComponent(it)
                }
            }

            chatPanel = Panel().also { chat ->
                chat.layoutData = GridLayout.createLayoutData(
                        GridLayout.Alignment.FILL,
                        GridLayout.Alignment.FILL,
                        true,
                        true,
                        1,
                        1
                )
                chat.layoutManager = GridLayout(1)
                chat.preferredSize = TerminalSize(floor(width / 10.0 * 3).toInt(), floor((height - 6.0) / 3 * 2).toInt())
                chatText = ReadOnlyTextBox(chat.preferredSize).also {
                    it.layoutData = GridLayout.createLayoutData(
                            GridLayout.Alignment.FILL,
                            GridLayout.Alignment.FILL,
                            true,
                            true,
                            1,
                            1
                    )
                    chat.addComponent(it)
                }
            }

            infoPanel = Panel().also { info ->
                info.layoutData = GridLayout.createLayoutData(
                        GridLayout.Alignment.FILL,
                        GridLayout.Alignment.FILL,
                        true,
                        true,
                        1,
                        1
                )
                info.layoutManager = GridLayout(1)
                info.preferredSize = TerminalSize(floor(width / 10.0 * 3).toInt(), floor((height - 6.0) / 3).toInt())
                infoText = ReadOnlyTextBox(info.preferredSize).also {
                    it.addText("Not Connected.")
                    it.layoutData = GridLayout.createLayoutData(
                            GridLayout.Alignment.FILL,
                            GridLayout.Alignment.FILL,
                            true,
                            true,
                            1,
                            1
                    )
                    info.addComponent(it)
                }
            }

            inputPanel = Panel().also { input ->
                input.layoutData = GridLayout.createLayoutData(
                        GridLayout.Alignment.FILL,
                        GridLayout.Alignment.FILL,
                        true,
                        false,
                        2,
                        1
                )
                input.preferredSize = TerminalSize(width, 1)
                input.layoutManager = GridLayout(3).also { it.horizontalSpacing = 0 }
                val inputBoxPrefix = ReadOnlyTextBox(TerminalSize(3, 1), " > ").also {
                    it.layoutData = GridLayout.createLayoutData(
                            GridLayout.Alignment.FILL,
                            GridLayout.Alignment.FILL,
                            false,
                            false,
                            1,
                            1
                    )
                    input.addComponent(it)
                }
                inputBox = ActionTextBox(TerminalSize(3, 1)).also {
                    it.layoutData = GridLayout.createLayoutData(
                            GridLayout.Alignment.FILL,
                            GridLayout.Alignment.FILL,
                            true,
                            false,
                            1,
                            1
                    )
                    it.callback = { cmd ->
                        logText.addText("> $cmd")
                        BuiltinPackets.COMMAND_C2S.send(CommandPacket(cmd), Client.INSTANCE.connection)
                    }
                    input.addComponent(it)
                }
            }

            main.addComponent(logPanel.withBorder(Borders.singleLine("LOG")))
            main.addComponent(chatPanel.withBorder(Borders.singleLine("CHAT")))
            main.addComponent(infoPanel.withBorder(Borders.singleLine("STATUS")))
            main.addComponent(inputPanel.withBorder(Borders.singleLine("INPUT")))
        }

        mainWindow.component = mainPanel
        mainWindow.focusedInteractable = inputBox
    }

    fun createLoginWindow() {
        val old = mainWindow.component

        val loginPanel = Panel().also { root ->
            root.layoutData = LinearLayout.createLayoutData(LinearLayout.Alignment.Center)

            root.layoutManager = GridLayout(2)
            val usernameLabel = Label("Username").also {
                root.addComponent(it)
            }
            val usernameInput = TextBox().also { input ->
                root.addComponent(input)
            }

            val passwordLabel = Label("Password").also {
                root.addComponent(it)
            }
            val passwordInput = TextBox().also { input ->
                input.mask = '*'
                root.addComponent(input)
            }

            fun handle(mode: String) {
                val p = LoginPacket(usernameInput.text, passwordInput.text, mode == "signup")
                BuiltinPackets.LOGIN_C2S.send(p, Client.INSTANCE.connection)
                mainWindow.component = old
                mainWindow.focusedInteractable = inputBox
                mainWindow.setHints(listOf(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS, Window.Hint.FIT_TERMINAL_WINDOW))
            }

            val loginButton = Button("Log In") {
                handle("login")
            }.addTo(root)

            val signUpButton = Button("Sign Up") {
                handle("signup")
            }.addTo(root)
        }

        mainWindow.component = loginPanel.withBorder(Borders.singleLine())
        mainWindow.setHints(listOf(Window.Hint.CENTERED, Window.Hint.NO_DECORATIONS, Window.Hint.FIT_TERMINAL_WINDOW))
    }

    fun initialize() {

    }

    fun start() {
        try {
            gui.addWindowAndWait(mainWindow)
        } catch (e: NullPointerException) {
            exitProcess(0)
        }
    }
}