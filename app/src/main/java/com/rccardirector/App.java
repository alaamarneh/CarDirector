package com.rccardirector;

import android.app.Application;
import android.graphics.PointF;

public class App extends Application {
    /*
     330,248 ==> 965,248 ==> 1600,248
     -
     -
     330,883 ==> 965,883 ==> 1600,883
     -
     -
     330,1518 ==> 965,1518 ==> 1600,1518
     origin (330,248) path length 1270 , half 635

     street width = 404 - 254 = 150
    */
    enum Direction {
        NORTH, SOUTH, WEST, EAST
    }

    public static final PointF mapOrigin = new PointF(330, 322);
    public volatile static PointF currLocation = new PointF(mapOrigin.x, mapOrigin.y);
    public final static int mapPathLength = 1270;
    public final static int mapPathMid = 635;
    public final static int streetWidth = 150;
    public final static int errorValue = streetWidth / 2;
    public volatile static PointF destLocation = new PointF();
    public static Direction carDirection = Direction.WEST;

    public static PointF qualifyDestPoint(float x, float y) {
        PointF res = new PointF(x, y);

        if (Math.abs(x - mapOrigin.x) < errorValue)
            res.x = mapOrigin.x;
        else if (Math.abs(x - mapOrigin.x - mapPathMid) < errorValue)
            res.x = mapOrigin.x + mapPathMid;
        else if (Math.abs(x - mapOrigin.x - mapPathLength) < errorValue)
            res.x = mapOrigin.x + mapPathLength;


        if (Math.abs(y - mapOrigin.y) < errorValue)
            res.y = mapOrigin.y;
        else if (Math.abs(y - mapOrigin.y - mapPathMid) < errorValue)
            res.y = mapOrigin.y + mapPathMid;
        else if (Math.abs(y - mapOrigin.y - mapPathLength) < errorValue)
            res.y = mapOrigin.y + mapPathLength;

        return res;
    }
}
