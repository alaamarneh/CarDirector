package com.rccardirector;

import android.content.Intent;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;

import static com.rccardirector.App.carDirection;
import static com.rccardirector.App.currLocation;
import static com.rccardirector.App.destLocation;
import static com.rccardirector.App.mapOrigin;

public class MainActivity extends AppCompatActivity {
    TextView xText, yText;
    MapImageView imgMap;
    int[][] mapMatrix;
    int[][] pathMatrix;
    Button destButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xText = (TextView) findViewById(R.id.xText);
        yText = (TextView) findViewById(R.id.yText);
        imgMap = (MapImageView) findViewById(R.id.imgMap);
        imgMap.setImage(ImageSource.resource(R.drawable.map2));
        //imgMap.setDestination(new PointF(965,883));
        destButton = (Button) findViewById(R.id.destButton);
        destButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, DestActivity.class), 10);
            }
        });
        //imgMap.initialiseData();
        //new getLocation().execute();
        new refreshLocation().execute();


        //imgMap.setPin(currLocation);
        imgMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*PointF point = imgMap.viewToSourceCoord(event.getX(), event.getY());


                imgMap.setDrawPath(false);
                point = qualifyDestPoint(point.x, point.y);
                xText.setText(point.x + "");
                yText.setText(point.y + "");
                imgMap.setDrawPath(true);
                imgMap.setDestination(point);*/
                return true;
            }
        });


    }

    class refreshLocation extends AsyncTask<Void, PointF, Void> {
        refreshLocation() {
            currLocation = new PointF(mapOrigin.x, mapOrigin.y);
            carDirection = App.Direction.EAST;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                synchronized (currLocation) {
                    PointF tmpLocation = currLocation;
                    /*if (tmpLocation.y == mapOrigin.y && tmpLocation.x < mapOrigin.x + mapPathLength) {
                        tmpLocation.x += 10;
                        carDirection = App.Direction.EAST;
                    }
                    else if (tmpLocation.x == mapOrigin.x + mapPathLength &&
                            tmpLocation.y < mapOrigin.y + mapPathLength) {
                        tmpLocation.y += 10;
                        carDirection = App.Direction.SOUTH;
                    }
                    else if (tmpLocation.y == mapOrigin.y + mapPathLength && tmpLocation.x > mapOrigin.x) {
                        tmpLocation.x -= 10;
                        carDirection = App.Direction.WEST;
                    }
                    else if (tmpLocation.x == mapOrigin.x && tmpLocation.y > mapOrigin.y) {
                        tmpLocation.y -= 10;
                        carDirection = App.Direction.NORTH;
                    }
                    currLocation = tmpLocation;*/
                    publishProgress(tmpLocation);
                }

                try {
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                }
            }


        }

        @Override
        protected void onProgressUpdate(PointF... values) {
            super.onProgressUpdate(values);
            imgMap.setPin(values[0]);
            xText.setText(values[0].x + "");
            yText.setText(values[0].y + "");
        }


    }

    class getLocation extends AsyncTask<Void, PointF, Void> {
        getLocation() {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                synchronized (currLocation) {

                    publishProgress();
                }
            }

        }

        @Override
        protected void onProgressUpdate(PointF... values) {
            super.onProgressUpdate(values);
            Toast.makeText(MainActivity.this, "ttttttttttttttttttttttttt", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 1)
            imgMap.setDestination(destLocation);
    }
}
