package dev.slne.surf.parkour.util

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import kotlin.math.ceil
import kotlin.math.min

@DslMarker
annotation class PageableMessageBuilderDsl

@PageableMessageBuilderDsl
class PageableMessageBuilder(private val linesPerPage: Int = 10) {

    private val lines = mutableObjectListOf<Component>()
    var pageCommand = "An error occurred while trying to display the page."
    private var title: Component = Component.empty()

    companion object {
        operator fun invoke(block: PageableMessageBuilder.() -> Unit): PageableMessageBuilder {
            return PageableMessageBuilder().apply(block)
        }
    }

    fun line(block: SurfComponentBuilder.() -> Unit) {
        lines.add(SurfComponentBuilder(block))
    }

    fun title(block: SurfComponentBuilder.() -> Unit) {
        title = SurfComponentBuilder(block)
    }

    fun send(sender: Audience, page: Int) {
        val totalPages = ceil(lines.size.toDouble() / linesPerPage).toInt()
        val start = (page - 1) * linesPerPage
        val end = min(start + linesPerPage, lines.size)

        if (page < 1 || page > totalPages) {
            sender.sendText {
                error("Seite ")
                variableValue(page.toString())
                error(" existiert nicht.")
            }
            return
        }

        sender.sendText {
            appendNewline()
            append {
                title
            }
            appendNewline()

            for (i in start..<end) {
                append {
                    append(lines[i])
                    appendNewline()
                    decoration(TextDecoration.BOLD, false).build()
                }
            }

            append(getComponent(page, totalPages))
        }
    }

    private fun getComponent(page: Int, totalPages: Int): Component {
        return buildText {
            append {
                if (page > 1) {
                    success("[<<] ")
                    clickRunsCommand(pageCommand.replace("%page%", "1"))
                } else {
                    error("[<<] ")
                }

                if (page > 1) {
                    success("[<] ")
                    clickRunsCommand(pageCommand.replace("%page%", (page - 1).toString()))
                } else {
                    error("[<] ")
                }

                darkSpacer("Seite $page von $totalPages")

                if (page < totalPages) {
                    success(" [>] ")
                    clickRunsCommand(pageCommand.replace("%page%", (page + 1).toString()))
                } else {
                    error(" [>] ")
                }

                if (page < totalPages) {
                    success(" [>>]")
                    clickRunsCommand(pageCommand.replace("%page%", totalPages.toString()))
                } else {
                    error(" [>>]")
                }.build()
            }
        }
    }
}
