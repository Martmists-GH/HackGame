package com.martmists.client.ui

import com.martmists.client.ui.events.KeyEvent
import com.martmists.client.ui.events.ResizeEvent
import com.martmists.client.ui.utils.Size
import io.github.humbleui.skija.*
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glGetInteger
import org.lwjgl.system.MemoryUtil.NULL
import java.util.*


class GameWindow {
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

    init {
        glfwInit()
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        window = glfwCreateWindow(initialSize.width.toInt(), initialSize.height.toInt(), "HackGame", NULL, NULL)
        glfwMakeContextCurrent(window)
        glfwSwapInterval(1)
        glfwShowWindow(window)

        // set up callbacks
        glfwSetWindowSizeCallback(window) { w, width, height -> onResize(width, height) }
        glfwSetKeyCallback(window) { w, key, scancode, action, mods -> onKey(key, scancode, action, mods) }
        glfwSetMouseButtonCallback(window) { w, button, action, mods -> onClick(button, action, mods) }
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
        ResizeEvent.EVENT.invoker().invoke(initialSize)

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

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null)?.free();
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

        val size = Size(width.toFloat(), height.toFloat())
        println("onResize $size")
        ResizeEvent.EVENT.invoker().invoke(size)
    }

    private fun onKey(key: Int, scancode: Int, action: Int, mods: Int) {
        println("onKey $key $scancode $action $mods")
        KeyEvent.EVENT.invoker().invoke(key, scancode, action, mods)
    }

    private fun onClick(button: Int, action: Int, mods: Int) {
        println("onClick $button $action $mods")
        // TODO: Event
    }
}
