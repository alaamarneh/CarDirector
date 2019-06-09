package com.rccardirector;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;

import static com.rccardirector.App.carDirection;
import static com.rccardirector.App.currLocation;
import static com.rccardirector.App.destLocation;
import static com.rccardirector.App.mapOrigin;
import static com.rccardirector.App.qualifyDestPoint;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    MapImageView imgMap;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        imgMap = findViewById(R.id.imgMapDest);

        currLocation = new PointF(mapOrigin.x, mapOrigin.y);
        carDirection = App.Direction.EAST;

        imgMap.setImage(ImageSource.resource(R.drawable.map2));
        imgMap.setPin(currLocation);

        imgMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                imgMap.setPin(currLocation);
                imgMap.setDrawPath(true);
                PointF tmp = imgMap.viewToSourceCoord(new PointF(event.getX(), event.getY()));
                //System.out.println(tmp.x+"  "+tmp.y);
                tmp = qualifyDestPoint(tmp.x, tmp.y);
                imgMap.setDestination(tmp);
                destLocation.x = tmp.x;
                destLocation.y = tmp.y;
                setResult(1);
                System.out.println(tmp.x + "  " + tmp.y);

                return true;
            }
        });
    }

    public void click(View view) {
//        carDirection = App.Direction.SOUTH;
        int x1 = 0;
        int y1 = 0;
        int x2 = 0;
        int y2 = 0;

        for (int i = 0; i < imgMap.getSelectedPathPoints().size(); i++) {
            Point point = imgMap.getSelectedPathPoints().get(i);
            if (i == 0) {
                x1 = point.x;
                y1 = point.y;
                continue;
            }
            if ((point.x != x1 && point.y != y1 || i == imgMap.getSelectedPathPoints().size() - 1)) {
                Log.d(TAG, "new Line" + x1 + " " + y1 + " " + x2 + " " + y2);

                x1 = x2;
                y1 = y2;
            } else {
                x2 = point.x;
                y2 = point.y;
            }
        }
    }

    class Line {
        private float x1, x2, y1, y2;

        public Line(float x1, float x2, float y1, float y2) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
        }

        public float getX1() {
            return x1;
        }

        public void setX1(float x1) {
            this.x1 = x1;
        }

        public float getX2() {
            return x2;
        }

        public void setX2(float x2) {
            this.x2 = x2;
        }

        public float getY1() {
            return y1;
        }

        public void setY1(float y1) {
            this.y1 = y1;
        }

        public float getY2() {
            return y2;
        }

        public void setY2(float y2) {
            this.y2 = y2;
        }
    }
}
