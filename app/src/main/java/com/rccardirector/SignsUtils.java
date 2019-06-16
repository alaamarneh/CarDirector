package com.rccardirector;

import android.support.annotation.Nullable;

public class SignsUtils {
    enum Sign {
        STOP(965, 322), BUMP(1264, 1592), NO_MOVING(1600, 322), NO_RIGH(11600, 424);

        Sign(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x, y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    @Nullable
    public static Sign findNearbySign(int x, int y, App.Direction direction) {
        if (Math.abs(x - Sign.STOP.getX()) <= 150 && Math.abs(y - Sign.STOP.getY()) <= 150 && direction == App.Direction.NORTH) {
            return Sign.STOP;
        }

        if (Math.abs(x - Sign.BUMP.getX()) <= 150 && Math.abs(y - Sign.BUMP.getY()) <= 150) {
            return Sign.BUMP;
        }

        if (Math.abs(x - Sign.NO_MOVING.getX()) <= 150 && Math.abs(y - Sign.NO_MOVING.getY()) <= 150) {
            return Sign.NO_MOVING;
        }

        if (Math.abs(x - Sign.NO_RIGH.getX()) <= 150 && Math.abs(y - Sign.NO_RIGH.getY()) <= 150 && direction == App.Direction.NORTH) {
            return Sign.NO_RIGH;
        }

        return null;
    }
}
