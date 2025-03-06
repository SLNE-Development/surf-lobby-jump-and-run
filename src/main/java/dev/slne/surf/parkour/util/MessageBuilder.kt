package dev.slne.surf.parkour.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.MiniMessage

class MessageBuilder {
    private var message: Component

    constructor() {
        this.message = Component.empty()
    }
    constructor(message: String) {
        this.message = Component.text(message, Colors.PRIMARY)
    }

    fun append(other: MessageBuilder): MessageBuilder {
        message = message.append(other.build())
        return this
    }

    fun withPrefix(): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(dev.slne.surf.parkour.util.Colors.PREFIX)
        return this
    }

    fun primary(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.text(text, dev.slne.surf.parkour.util.Colors.PRIMARY))
        return this
    }

    fun secondary(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.text(text, dev.slne.surf.parkour.util.Colors.SECONDARY))
        return this
    }

    fun info(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.text(text, dev.slne.surf.parkour.util.Colors.INFO))
        return this
    }

    fun success(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.text(text, dev.slne.surf.parkour.util.Colors.SUCCESS))
        return this
    }

    fun warning(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.text(text, dev.slne.surf.parkour.util.Colors.WARNING))
        return this
    }

    fun error(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.text(text, dev.slne.surf.parkour.util.Colors.ERROR))
        return this
    }

    fun variableKey(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.text(text,
            dev.slne.surf.parkour.util.Colors.VARIABLE_KEY
        ))
        return this
    }

    fun variableValue(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.text(text,
            dev.slne.surf.parkour.util.Colors.VARIABLE_VALUE
        ))
        return this
    }

    fun prefixColor(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.text(text,
            dev.slne.surf.parkour.util.Colors.PREFIX_COLOR
        ))
        return this
    }

    fun darkSpacer(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.text(text, dev.slne.surf.parkour.util.Colors.DARK_SPACER))
        return this
    }

    fun miniMessage(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(MiniMessage.miniMessage().deserialize(text))
        return this
    }

    fun component(component: Component): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(component)
        return this
    }

    fun command(text: dev.slne.surf.parkour.util.MessageBuilder, hover: dev.slne.surf.parkour.util.MessageBuilder, command: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(text.build().clickEvent(ClickEvent.runCommand(command)).hoverEvent(HoverEvent.showText(hover.build())))
        return this
    }

    fun suggest(text: dev.slne.surf.parkour.util.MessageBuilder, hover: dev.slne.surf.parkour.util.MessageBuilder, command: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(text.build().clickEvent(ClickEvent.suggestCommand(command)).hoverEvent(HoverEvent.showText(hover.build())))
        return this
    }

    fun white(text: String): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.text(text, dev.slne.surf.parkour.util.Colors.WHITE))
        return this
    }

    fun newLine(): dev.slne.surf.parkour.util.MessageBuilder {
        message = message.append(Component.newline())
        return this
    }

    fun build(): Component {
        return message
    }
}