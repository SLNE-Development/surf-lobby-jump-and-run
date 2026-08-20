package dev.slne.surf.parkour.paper.util

import com.destroystokyo.paper.profile.ProfileProperty
import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.parkour.core.client.service.ParkourTexturesService
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ResolvableProfile
import io.papermc.paper.datacomponent.item.TooltipDisplay
import org.bukkit.Material
import java.util.*

fun UUID.playerHead() = playerHeadOf(ParkourTexturesService.getTexture(this).texture)

@Suppress("UnstableApiUsage")
fun playerHeadOf(textures: String) = buildItem(Material.PLAYER_HEAD) {
    setData(
        DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile().addProperty(
            ProfileProperty("textures", textures)
        ).build()
    )
    setData(
        DataComponentTypes.TOOLTIP_DISPLAY,
        TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.PROFILE).build()
    )
}
