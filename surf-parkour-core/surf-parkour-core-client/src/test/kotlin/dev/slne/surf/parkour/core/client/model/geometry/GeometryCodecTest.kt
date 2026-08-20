package dev.slne.surf.parkour.core.client.model.geometry

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class GeometryCodecTest {

    @Test
    fun `writes a region as its ordered corners`() {
        val region = ParkourRegion.of(ParkourVector(9, 315, 189), ParkourVector(328, 215, 391))

        assertEquals("9.0,215.0,189.0;328.0,315.0,391.0", serializeRegion(region))
    }

    @Test
    fun `reads back the region it wrote`() {
        val region = ParkourRegion.of(ParkourVector(-4, 2, 7), ParkourVector(11.5, -3.25, 0.0))

        assertEquals(region, deserializeRegion(serializeRegion(region)))
    }

    @Test
    fun `orders the corners of a region whichever way round they come`() {
        val region = ParkourRegion.of(ParkourVector(5, 6, 7), ParkourVector(1, 2, 3))

        assertEquals(ParkourVector(1.0, 2.0, 3.0), region.min)
        assertEquals(ParkourVector(5.0, 6.0, 7.0), region.max)
    }

    @Test
    fun `rejects a region that does not hold two corners`() {
        assertThrows(IllegalStateException::class.java) { deserializeRegion("1,2,3") }
        assertThrows(IllegalStateException::class.java) { deserializeRegion("1,2;3,4") }
    }

    @Test
    fun `reads back the location it wrote`() {
        val location = ParkourLocation("world", 111.5, 149.0, 315.5, 90f, 0f)

        assertEquals("world,111.5,149.0,315.5,90.0,0.0", serializeLocation(location))
        assertEquals(location, deserializeLocation(serializeLocation(location)))
    }

    @Test
    fun `rejects a location that does not hold a world and five coordinates`() {
        assertThrows(IllegalStateException::class.java) {
            deserializeLocation("world,1.0,2.0,3.0,4.0")
        }
    }

    @Test
    fun `pulls a point outside a region onto its closest edge`() {
        val region = ParkourRegion.of(ParkourVector(0, 0, 0), ParkourVector(10, 10, 10))

        assertEquals(
            ParkourVector(0.0, 10.0, 4.0),
            region.clamp(ParkourVector(-5.0, 25.0, 4.0))
        )
    }
}
