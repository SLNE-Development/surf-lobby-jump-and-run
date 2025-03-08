package dev.slne.surf.parkour.serialization.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.util.Vector

object VectorSerializer : KSerializer<Vector> {
    private val delegate = DoubleArraySerializer()
    override val descriptor =
        SerialDescriptor("dev.slne.surf.parkour.VectorSerializer", delegate.descriptor)


    override fun serialize(
        encoder: Encoder,
        value: Vector
    ) {
        delegate.serialize(encoder, doubleArrayOf(value.x, value.y, value.z))
    }

    override fun deserialize(decoder: Decoder): Vector {
        val (x, y, z) = delegate.deserialize(decoder)
        return Vector(x, y, z)
    }
}