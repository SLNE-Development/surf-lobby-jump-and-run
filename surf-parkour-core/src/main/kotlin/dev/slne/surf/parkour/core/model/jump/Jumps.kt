package dev.slne.surf.parkour.core.model.jump

import dev.slne.surf.parkour.api.model.Jump

enum class Jumps(val jump: Jump) {

    ONE_FORWARD_TWO_LEFT(object : DirectionalJump(1, 2) {}),
    ONE_FORWARD_TWO_RIGHT(object : DirectionalJump(1, -2) {}),
    ONE_FORWARD_THREE_LEFT(object : DirectionalJump(1, 3) {}),
    ONE_FORWARD_THREE_RIGHT(object : DirectionalJump(1, -3) {}),

    TWO_FORWARD(object : DirectionalJump(2, 0) {}),
    TWO_FORWARD_ONE_LEFT(object : DirectionalJump(2, 1) {}),
    TWO_FORWARD_ONE_RIGHT(object : DirectionalJump(2, -1) {}),
    TWO_FORWARD_TWO_LEFT(object : DirectionalJump(2, 2) {}),
    TWO_FORWARD_TWO_RIGHT(object : DirectionalJump(2, -2) {}),

    THREE_FORWARD(object : DirectionalJump(3, 0) {}),
    THREE_FORWARD_ONE_LEFT(object : DirectionalJump(3, 1) {}),
    THREE_FORWARD_ONE_RIGHT(object : DirectionalJump(3, -1) {}),
    THREE_FORWARD_TWO_LEFT(object : DirectionalJump(3, 2) {}),
    THREE_FORWARD_TWO_RIGHT(object : DirectionalJump(3, -2) {}),
    THREE_FORWARD_THREE_LEFT(object : DirectionalJump(3, 3) {}),
    THREE_FORWARD_THREE_RIGHT(object : DirectionalJump(3, -3) {}),

//    FOUR_FORWARD(object : DirectionalJump(4, 0) {}),
//    FOUR_FORWARD_ONE_LEFT(object : DirectionalJump(4, 1) {}),
//    FOUR_FORWARD_ONE_RIGHT(object : DirectionalJump(4, -1) {}),
//    FOUR_FORWARD_TWO_LEFT(object : DirectionalJump(4, 2) {}),
//    FOUR_FORWARD_TWO_RIGHT(object : DirectionalJump(4, -2) {}),
//    FOUR_FORWARD_THREE_LEFT(object : DirectionalJump(4, 3) {}),
//    FOUR_FORWARD_THREE_RIGHT(object : DirectionalJump(4, -3) {});
}