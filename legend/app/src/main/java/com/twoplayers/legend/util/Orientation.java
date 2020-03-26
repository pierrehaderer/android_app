package com.twoplayers.legend.util;

public enum Orientation {
    INIT,
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

    public boolean isSameAs(Orientation orientation) {
        switch (this) {
            case UP:
            case DOWN:
                return (orientation == UP || orientation == DOWN);
            case LEFT:
            case RIGHT:
                return (orientation == LEFT || orientation == RIGHT);
        }
        return false;
    }
}
