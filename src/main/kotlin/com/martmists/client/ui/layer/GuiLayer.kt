package com.martmists.client.ui.layer

import com.martmists.client.Client
import com.martmists.client.ui.element.Element
import com.martmists.client.ui.element.InputElement
import com.martmists.client.ui.element.TextElement
import com.martmists.client.ui.element.Window
import com.martmists.common.utilities.TextColor
import org.lwjgl.util.yoga.Yoga.*

class GuiLayer : ElementLayer() {
    private val mappedElements = mutableMapOf<String, Element>()

    val log = Window(TextElement(
        "${TextColor.ANSI.CYAN}Game loaded.${TextColor.RESET}",
        true
    )).also { el ->
        el.title = "Log"
    }
    private val input = Window(InputElement {
        log.addLine("> $it")
        Client.command(it)
    }).also { el ->
        el.title = "Input"
    }

    override var selected: Element = input

    init {
        setupElements()
        setupLayout()
    }

    private fun addElement(name: String, element: Element) {
        mappedElements[name] = element
        addElement(element)
    }

    private fun setupElements() {
        addElement("log", log)
        addElement("input", input)
    }

    private fun setupLayout() {
        // Node
        YGNodeStyleSetFlex(log.node, 1f)

        // Input
        YGNodeStyleSetHeight(input.node, 2 * TextElement.padding + 3 * TextElement.fontSize)
    }
}
