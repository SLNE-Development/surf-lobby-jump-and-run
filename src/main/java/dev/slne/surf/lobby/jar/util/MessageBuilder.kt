package dev.slne.surf.lobby.jar.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.MiniMessage

class MessageBuilder {
    private var message: Component = Component.empty()

    fun withPrefix(): MessageBuilder {
        message = message.append(Colors.PREFIX)
        return this
    }

    fun primary(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.PRIMARY))
        return this
    }

    fun secondary(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.SECONDARY))
        return this
    }

    fun info(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.INFO))
        return this
    }

    fun success(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.SUCCESS))
        return this
    }

    fun warning(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.WARNING))
        return this
    }

    fun error(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.ERROR))
        return this
    }

    fun variableKey(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.VARIABLE_KEY))
        return this
    }

    fun variableValue(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.VARIABLE_VALUE))
        return this
    }

    fun prefixColor(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.PREFIX_COLOR))
        return this
    }

    fun darkSpacer(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.DARK_SPACER))
        return this
    }

    fun miniMessage(text: String): MessageBuilder {
        message = message.append(MiniMessage.miniMessage().deserialize(text))
        return this
    }

    fun component(component: Component): MessageBuilder {
        message = message.append(component)
        return this
    }

    fun command(text: MessageBuilder, hover: MessageBuilder, command: String): MessageBuilder {
        message = message.append(text.build().clickEvent(ClickEvent.runCommand(command)).hoverEvent(HoverEvent.showText(hover.build())))
        return this
    }

    fun suggest(text: MessageBuilder, hover: MessageBuilder, command: String): MessageBuilder {
        message = message.append(text.build().clickEvent(ClickEvent.suggestCommand(command)).hoverEvent(HoverEvent.showText(hover.build())))
        return this
    }

    fun white(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.WHITE))
        return this
    }

    fun newLine(): MessageBuilder {
        message = message.append(Component.newline())
        return this
    }

    fun build(): Component {
        return message
    }
}