package dev.slne.surf.parkour.`object`.jump

enum class JumpType(val jump: Jump) {
    //    ONE_FORWARD_TWO_LEFT(Jump(1, 2, 0)),
//    ONE_FORWARD_TWO_RIGHT(Jump(1, -2, 0)),
//    ONE_FORWARD_THREE_LEFT(Jump(1, 3, 0)),
//    ONE_FORWARD_THREE_RIGHT(Jump(1, -3, 0)),

    //TWO_FORWARD(Jump(2, 0, 0)),
//    TWO_FORWARD_ONE_LEFT(Jump(2, 1, 0)),
//    TWO_FORWARD_ONE_RIGHT(Jump(2, -1, 0)),
//    TWO_FORWARD_TWO_LEFT(Jump(2, 2, 0)),
//    TWO_FORWARD_TWO_RIGHT(Jump(2, -2, 0)),

    //    THREE_FORWARD(Jump(3, 0, 0)),
//    THREE_FORWARD_ONE_LEFT(Jump(3, 1, 0)),
//    THREE_FORWARD_ONE_RIGHT(Jump(3, -1, 0)),
    THREE_FORWARD_TWO_LEFT(Jump(3, 2, 0)),
    THREE_FORWARD_TWO_RIGHT(Jump(3, -2, 0)),
//    THREE_FORWARD_THREE_LEFT(Jump(3, 3, 0)),
//    THREE_FORWARD_THREE_RIGHT(Jump(3, -3, 0)),

    //    FOUR_FORWARD(Jump(4, 0, 0)),
//    FOUR_FORWARD_ONE_LEFT(Jump(4, 1, 0)),
//    FOUR_FORWARD_ONE_RIGHT(Jump(4, -1, 0)),
//    FOUR_FORWARD_TWO_LEFT(Jump(4, 2, 0)),
//    FOUR_FORWARD_TWO_RIGHT(Jump(4, -2, 0)),
//    FOUR_FORWARD_THREE_LEFT(Jump(4, 3, 0)),
//    FOUR_FORWARD_THREE_RIGHT(Jump(4, -3, 0)),
    TWO_FORWARD_TWO_LEFT_UP(Jump(2, 2, 1)),
    TWO_FORWARD_TWO_RIGHT_UP(Jump(2, -2, 1)),
    TWO_FORWARD_TWO_LEFT_DOWN(Jump(2, 2, -1)),
    TWO_FORWARD_TWO_RIGHT_DOWN(Jump(2, -2, -1)),
    THREE_FORWARD_TWO_LEFT_UP(Jump(3, 2, 1)),
    THREE_FORWARD_TWO_RIGHT_UP(Jump(3, -2, 1)),
    THREE_FORWARD_TWO_LEFT_DOWN(Jump(3, 2, -1)),
    THREE_FORWARD_TWO_RIGHT_DOWN(Jump(3, -2, -1))
}