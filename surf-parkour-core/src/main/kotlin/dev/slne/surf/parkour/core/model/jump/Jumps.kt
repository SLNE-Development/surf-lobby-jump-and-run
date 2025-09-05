package dev.slne.surf.parkour.core.model.jump

import dev.slne.surf.parkour.api.model.Jump

enum class Jumps(val jump: Jump) {

    //    ONE_FORWARD_TWO_LEFT(object : DirectionalJump(1, 2, 0) {}),
//    ONE_FORWARD_TWO_RIGHT(object : DirectionalJump(1, -2, 0) {}),
    ONE_FORWARD_THREE_LEFT(object : DirectionalJump(1, 3, 0) {}),
    ONE_FORWARD_THREE_RIGHT(object : DirectionalJump(1, -3, 0) {}),

    //TWO_FORWARD(object : DirectionalJump(2, 0, 0) {}),
//    TWO_FORWARD_ONE_LEFT(object : DirectionalJump(2, 1, 0) {}),
//    TWO_FORWARD_ONE_RIGHT(object : DirectionalJump(2, -1, 0) {}),
//    TWO_FORWARD_TWO_LEFT(object : DirectionalJump(2, 2, 0) {}),
//    TWO_FORWARD_TWO_RIGHT(object : DirectionalJump(2, -2, 0) {}),

    //    THREE_FORWARD(object : DirectionalJump(3, 0, 0) {}),
    THREE_FORWARD_ONE_LEFT(object : DirectionalJump(3, 1, 0) {}),
    THREE_FORWARD_ONE_RIGHT(object : DirectionalJump(3, -1, 0) {}),
    THREE_FORWARD_TWO_LEFT(object : DirectionalJump(3, 2, 0) {}),
    THREE_FORWARD_TWO_RIGHT(object : DirectionalJump(3, -2, 0) {}),
    THREE_FORWARD_THREE_LEFT(object : DirectionalJump(3, 3, 0) {}),
    THREE_FORWARD_THREE_RIGHT(object : DirectionalJump(3, -3, 0) {}),

    //    FOUR_FORWARD(object : DirectionalJump(4, 0, 0) {}),
//    FOUR_FORWARD_ONE_LEFT(object : DirectionalJump(4, 1, 0) {}),
//    FOUR_FORWARD_ONE_RIGHT(object : DirectionalJump(4, -1, 0) {}),
//    FOUR_FORWARD_TWO_LEFT(object : DirectionalJump(4, 2, 0) {}),
//    FOUR_FORWARD_TWO_RIGHT(object : DirectionalJump(4, -2, 0) {}),
//    FOUR_FORWARD_THREE_LEFT(object : DirectionalJump(4, 3, 0) {}),
//    FOUR_FORWARD_THREE_RIGHT(object : DirectionalJump(4, -3, 0) {}),
    TWO_FORWARD_TWO_LEFT_UP(object : DirectionalJump(2, 2, 1) {}),
    TWO_FORWARD_TWO_RIGHT_UP(object : DirectionalJump(2, -2, 1) {}),
    TWO_FORWARD_TWO_LEFT_DOWN(object : DirectionalJump(2, 2, -1) {}),
    TWO_FORWARD_TWO_RIGHT_DOWN(object : DirectionalJump(2, -2, -1) {}),
    THREE_FORWARD_TWO_LEFT_UP(object : DirectionalJump(3, 2, 1) {}),
    THREE_FORWARD_TWO_RIGHT_UP(object : DirectionalJump(3, -2, 1) {}),
    THREE_FORWARD_TWO_LEFT_DOWN(object : DirectionalJump(3, 2, -1) {}),
    THREE_FORWARD_TWO_RIGHT_DOWN(object : DirectionalJump(3, -2, -1) {})
}
