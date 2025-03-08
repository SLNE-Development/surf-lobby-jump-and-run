package dev.slne.surf.parkour.serialization

import dev.slne.surf.parkour.serialization.serializer.VectorSerializer
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.contextual

object ParkourSerializationModule {
    fun register(builder: SerializersModuleBuilder) = with(builder) {
        contextual(VectorSerializer)
    }
}