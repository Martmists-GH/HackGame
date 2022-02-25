package com.martmists.client.ui.layers

import com.martmists.client.ui.RenderContext
import com.martmists.client.ui.RenderLayer
import com.martmists.client.ui.components.SGRTextBox
import com.martmists.common.api.TextColor

class GuiLayer : RenderLayer {
    val log = SGRTextBox(0f, 0f, 1f, 1f)

    init {
        log.addLine("${TextColor.RESET}Hello ${TextColor.ANSI.RED}World ${TextColor.ANSI.BRIGHT_BLUE}RGB")
        log.addLine("${TextColor.RESET}Line ${TextColor.fromRGB(0, 255, 0)}2!")
    }

    override fun render(context: RenderContext) {
        log.render(context)
    }
}
