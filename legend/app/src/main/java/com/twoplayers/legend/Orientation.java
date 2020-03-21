package com.twoplayers.legend;

public enum Orientation {
    INIT, NONE,
    UP, DOWN, LEFT, RIGHT,
    UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT;

    public static Orientation reverseOrientation(Orientation orientation) {
        switch (orientation) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
        }
        return INIT;
    }
}
