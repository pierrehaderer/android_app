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

    public static Orientation getRandomOrientation(double random) {
        if (random < 0.25) {
            return Orientation.UP;
        }
        if (random < 0.5) {
            return Orientation.DOWN;
        }
        if (random < 0.75) {
            return Orientation.LEFT;
        }
        return Orientation.RIGHT;
    }

    public Orientation reverseOrientation() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case DEGREES_45:
                return DEGREES_225;
            case DEGREES_135:
                return DEGREES_315;
            case DEGREES_225:
                return DEGREES_45;
            case DEGREES_315:
                return DEGREES_135;
        }
        return UP;
    }

    public boolean isSameAs(Orientation orientation) {
        switch (this) {
            case UP:
            case DOWN:
                return (orientation == UP || orientation == DOWN || orientation == ANY
                        || orientation == DEGREES_45 || orientation == DEGREES_135 || orientation == DEGREES_225 || orientation == DEGREES_315);
            case LEFT:
            case RIGHT:
                return (orientation == LEFT || orientation == RIGHT || orientation == ANY
                        || orientation == DEGREES_45 || orientation == DEGREES_135 || orientation == DEGREES_225 || orientation == DEGREES_315);
            case DEGREES_45:
            case DEGREES_135:
            case DEGREES_225:
            case DEGREES_315:
                return (orientation == UP || orientation == DOWN || orientation == LEFT || orientation == RIGHT || orientation == ANY);
            case ANY:
                return true;
        }
        return false;
    }

    public Orientation[] getOrientationsBeside() {
        switch (this) {
            case RIGHT:
                return new Orientation[] {DEGREES_20, DEGREES_340};
            case DEGREES_20:
                return new Orientation[] {RIGHT, DEGREES_45};
            case DEGREES_45:
                return new Orientation[] {DEGREES_20, DEGREES_70};
            case DEGREES_70:
                return new Orientation[] {DEGREES_45, UP};
            case UP:
                return new Orientation[] {DEGREES_70, DEGREES_110};
            case DEGREES_110:
                return new Orientation[] {UP, DEGREES_135};
            case DEGREES_135:
                return new Orientation[] {DEGREES_110, DEGREES_160};
            case DEGREES_160:
                return new Orientation[] {DEGREES_135, LEFT};
            case LEFT:
                return new Orientation[] {DEGREES_160, DEGREES_200};
            case DEGREES_200:
                return new Orientation[] {LEFT, DEGREES_225};
            case DEGREES_225:
                return new Orientation[] {DEGREES_200, DEGREES_250};
            case DEGREES_250:
                return new Orientation[] {DEGREES_225, DOWN};
            case DOWN:
                return new Orientation[] {DEGREES_250, DEGREES_290};
            case DEGREES_290:
                return new Orientation[] {DOWN, DEGREES_315};
            case DEGREES_315:
                return new Orientation[] {DEGREES_290, DEGREES_340};
            case DEGREES_340:
                return new Orientation[] {DEGREES_315, RIGHT};
        }
        return new Orientation[] {DEGREES_45, DEGREES_135};
    }

    public Orientation[] getOrientationsBesideAt45Degrees() {
        switch (this) {
            case UP:
                return new Orientation[] {DEGREES_45, DEGREES_135};
            case DOWN:
                return new Orientation[] {DEGREES_225, DEGREES_315};
            case LEFT:
                return new Orientation[] {DEGREES_135, DEGREES_225};
            case RIGHT:
                return new Orientation[] {DEGREES_45, DEGREES_315};
            case DEGREES_45:
                return new Orientation[] {UP, RIGHT};
            case DEGREES_135:
                return new Orientation[] {UP, LEFT};
            case DEGREES_225:
                return new Orientation[] {DOWN, LEFT};
            case DEGREES_315:
                return new Orientation[] {DOWN, RIGHT};
        }
        return new Orientation[] {DEGREES_45, DEGREES_135};
    }
}
