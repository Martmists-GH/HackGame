package com.martmists.client.ui

import com.martmists.client.Client
import com.martmists.client.ui.layer.ElementLayer
import com.martmists.client.ui.layer.RenderLayer
import com.martmists.client.ui.shape.Size
import com.martmists.common.utilities.Loggable
import io.github.humbleui.skija.*
import io.github.humbleui.types.Rect
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glGetInteger
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.util.yoga.YGNode
import org.lwjgl.util.yoga.Yoga.*
import java.util.*
import kotlin.math.roundToInt


class GameWindow : Loggable {
    // Skija values
    private lateinit var context: DirectContext
    private lateinit var renderTarget: BackendRenderTarget
    private lateinit var surface: Surface

    // Contains utilities and canvas
    private lateinit var ctx: RenderContext

    private val window: Long

    // initial values
    private val initialSize = Size(640f, 480f)

    // Seems to be needed?
    private var fbId = 0

    // Low numbers are rendered first
    private val layers = PriorityQueue<Pair<Int, RenderLayer>>(compareBy { it.first })

    private var cursorPosX = 0.0
    private var cursorPosY = 0.0

    init {
        glfwInit()
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        window = glfwCreateWindow(initialSize.width.toInt(), initialSize.height.toInt(), "HackGame", NULL, NULL)
        glfwMakeContextCurrent(window)
        glfwSwapInterval(1)

        // set up callbacks
        glfwSetWindowSizeCallback(window) { w, width, height -> onResize(width, height) }
        glfwSetCharCallback(window) { w, codepoint -> onChar(codepoint) }
        glfwSetKeyCallback(window) { w, key, scancode, action, mods -> onKey(key, scancode, action, mods) }
        glfwSetMouseButtonCallback(window) { w, button, action, mods -> onClick(button, action, mods) }
        glfwSetScrollCallback(window) { w, xOffset, yOffset -> onScroll(xOffset, yOffset) }
        glfwSetCursorPosCallback(window) { w, x, y -> onMove(x, y) }
        glfwSetWindowMaximizeCallback(window) { w, maximized ->
            val ws = memAllocInt(1)
            val hs = memAllocInt(1)
            glfwGetWindowSize(w, ws, hs)
            onResize(ws.get(0), hs.get(0))
            memFree(ws)
            memFree(hs)
        }
    }

    fun start() {
        GL.createCapabilities()
        context = DirectContext.makeGL()
        fbId = glGetInteger(0x8CA6) // GL_FRAMEBUFFER_BINDING

        renderTarget = BackendRenderTarget.makeGL(
            initialSize.width.toInt(),
            initialSize.height.toInt(),
            0,
            8,
            fbId,
            FramebufferFormat.GR_GL_RGBA8
        )

        surface = Surface.makeFromBackendRenderTarget(
            context,
            renderTarget,
            SurfaceOrigin.BOTTOM_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.getSRGB()
        )

        ctx = RenderContext(surface.canvas)

        // Initial event
        onResize(initialSize.width.toInt(), initialSize.height.toInt())
        glfwSwapInterval(if (Client.config.ui.vsync) 1 else 0)
        glfwShowWindow(window)
        // Render loop
        while (!glfwWindowShouldClose(window)) {
            surface.canvas.clear(0xFF000000.toInt() + ctx.theme.BACKGROUND)

            layers.forEach {
                it.second.render(ctx)
            }

            context.flush()
            glfwSwapBuffers(window)
            glfwPollEvents()
        }

        surface.close()
        renderTarget.close()
        context.close()

        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)

        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    fun addLayer(priority: Int, layer: RenderLayer) {
        layers.add(Pair(priority, layer))
    }

    private fun onResize(width: Int, height: Int) {
        // Create new backend
        val newBackend = BackendRenderTarget.makeGL(
            width,
            height,
            0,
            8,
            fbId,
            FramebufferFormat.GR_GL_RGBA8
        )
        val newSurface = Surface.makeFromBackendRenderTarget(
            context,
            renderTarget,
            SurfaceOrigin.BOTTOM_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.getSRGB()
        )

        val _renderTarget = renderTarget
        val _surface = surface
        val _ctx = ctx
        renderTarget = newBackend
        surface = newSurface
        ctx = RenderContext(surface.canvas)

        _renderTarget.close()
        _surface.close()
        _ctx.textRenderer.close()

        layers.forEach {
            it.second.onResize(width.toInt(), height.toInt())
        }
    }

    private fun onKey(key: Int, scancode: Int, action: Int, mods: Int) {
        if (action == GLFW_PRESS || action == GLFW_REPEAT) {
            if (key == GLFW_KEY_D && mods == GLFW_MOD_CONTROL or GLFW_MOD_SHIFT) {
                glfwSetWindowShouldClose(window, true)
            }

            if (mods == 0) {
                layers.forEach {
                    it.second.onKeyPressed(key)
                }
            }
        }
    }

    private fun onChar(codepoint: Int) {
        layers.forEach {
            it.second.onCharPressed(codepoint.toChar())
        }
    }

    private fun onClick(button: Int, action: Int, mods: Int) {
        if (action == GLFW_PRESS && button == GLFW_MOUSE_BUTTON_LEFT && mods == 0) {
            layers.forEach {
                it.second.onClicked(cursorPosX.roundToInt(), cursorPosY.roundToInt())
            }
        }
    }

    private fun onScroll(xOffset: Double, yOffset: Double) {
        layers.forEach {
            it.second.onScrolled(xOffset.roundToInt(), yOffset.roundToInt())
        }
    }

    private fun onMove(x: Double, y: Double) {
        cursorPosX = x
        cursorPosY = y
    }
}
