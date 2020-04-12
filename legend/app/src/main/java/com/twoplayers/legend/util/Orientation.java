package com.twoplayers.legend.util;

public enum Orientation {
    INIT(0),
    ANY(0),
    DEGREES_20(Math.PI / 8),
    DEGREES_45(2 * Math.PI / 8),
    DEGREES_70(3 * Math.PI / 8),
    UP(Math.PI / 2),
    DEGREES_110(5 * Math.PI / 8),
    DEGREES_135(6 * Math.PI / 8),
    DEGREES_160(7 * Math.PI / 8),
    LEFT(Math.PI),
    DEGREES_200(9 * Math.PI / 8),
    DEGREES_225(10 * Math.PI / 8),
    DEGREES_250(11 * Math.PI / 8),
    DOWN(3 * Math.PI / 2),
    DEGREES_290(13 * Math.PI / 8),
    DEGREES_315(14 * Math.PI / 8),
    DEGREES_340(15 * Math.PI / 8),
    RIGHT(0);

    public float angle;

    Orientation(double angle) {
        this.angle = (float) angle;
    }

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
            case ANY:
                return true;
        }
        return false;
    }
}
