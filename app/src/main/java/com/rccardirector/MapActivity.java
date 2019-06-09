package com.rccardirector;

import android.annotation.SuppressLint;
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
        carDirection = App.Direction.SOUTH;
        String path = imgMap.getSelectedPath();
        Log.d(TAG, "path: " + path);
    }
}
