package dev.slne.surf.parkour.service

class ParkourService {

    companion object {
        val INSTANCE = ParkourService()
    }
}

val parkourService get() = ParkourService.INSTANCE