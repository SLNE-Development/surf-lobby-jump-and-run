package dev.slne.surf.parkour.util

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.CommandSender
import kotlin.math.ceil
import kotlin.math.min

class PageableMessageBuilder {
    private val lines: ObjectList<Component> = ObjectArrayList()
    private var pageCommand = "An error occurred while trying to display the page."
    private var title: Component = Component.empty()

    fun addLine(line: Component): PageableMessageBuilder {
        lines.add(line)
        return this
    }

    fun setTitle(title: Component): PageableMessageBuilder {
        this.title = title
        return this
    }

    fun setPageCommand(command: String): PageableMessageBuilder {
        this.pageCommand = command
        return this
    }

    fun send(sender: CommandSender, page: Int) {
        val linesPerPage = 10
        val totalPages = ceil(lines.size.toDouble() / linesPerPage).toInt()
        val start = (page - 1) * linesPerPage
        val end: Int = min(start + linesPerPage, lines.size)

        if (page < 1 || page > totalPages) {
            sender.sendMessage(Component.text("Seite $page existiert nicht.", NamedTextColor.RED))
            return
        }

        var message: Component = title.append(MessageBuilder().darkSpacer(" (Seite $page von $totalPages)").build().decorate(TextDecoration.ITALIC)).append(Component.newline())
        val navigation = this.getComponent(page, totalPages)

        for (i in start..<end) {
            message = message.append(lines[i]).append(Component.newline()).decoration(TextDecoration.BOLD, false)
        }

        if (navigation != Component.empty()) {
            message = message.append(navigation)
        }

        sender.sendMessage(Component.newline().append(message))
    }

    private fun getComponent(page: Int, totalPages: Int): Component {
        var navigation: Component = Component.empty()

        if (page > 1) {
            navigation = navigation.append(Component.text("[<< Zurück] ", Colors.SUCCESS).clickEvent(ClickEvent.runCommand(pageCommand.replace("%page%", (page - 1).toString()))))
        }

        if (page < totalPages) {
            navigation = navigation.append(Component.text("[Weiter >>]", Colors.SUCCESS).clickEvent(ClickEvent.runCommand(pageCommand.replace("%page%", (page + 1).toString()))))
        }

        return navigation
    }
}
