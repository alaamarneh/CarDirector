package com.rccardirector;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rccardirector.App.carDirection;
import static com.rccardirector.App.currLocation;
import static com.rccardirector.App.destLocation;
import static com.rccardirector.App.mapOrigin;
import static com.rccardirector.App.qualifyDestPoint;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    MapImageView imgMap;

    private boolean notificationShown = false;

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
//                currLocation = tmp;
                //System.out.println(tmp.x+"  "+tmp.y);
                tmp = qualifyDestPoint(tmp.x, tmp.y);
                imgMap.setDestination(tmp);
                System.out.println(tmp.x + "  " + tmp.y);

                return true;
            }
        });

        FirebaseDatabase.getInstance().getReference().child("current_location")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int x = 0;
                        int y = 0;
                        int d = 1;
                        if (dataSnapshot.child("x").getValue() != null)
                            x = dataSnapshot.child("x").getValue(Integer.class);
                        if (dataSnapshot.child("y").getValue() != null)
                            y = dataSnapshot.child("y").getValue(Integer.class);
                        if (dataSnapshot.child("d").getValue() != null)
                            d = dataSnapshot.child("d").getValue(Integer.class);
                        currLocation = qualifyDestPoint(mapOrigin.x + y, mapOrigin.y + x);

                        if (d == 1) {
                            carDirection = App.Direction.EAST;
                        } else if (d == 2) {
                            carDirection = App.Direction.WEST;
                        } else if (d == 3) {
                            carDirection = App.Direction.NORTH;
                        } else if (d == 4) {
                            carDirection = App.Direction.SOUTH;
                        }

                        imgMap.setPin(currLocation);
                        imgMap.invalidate();

                        Log.d(TAG, "onDataChange: " + dataSnapshot.child("x").getValue());
                        Log.d(TAG, "onDataChange: " + dataSnapshot.child("y").getValue());
                        Log.d(TAG, "onDataChange: " + dataSnapshot.child("d").getValue());

                        checkSigns((int) (mapOrigin.x + y), (int) (mapOrigin.y + x));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void checkSigns(int x, int y) {
        SignsUtils.Sign sign = SignsUtils.findNearbySign(x, y, carDirection);

        if (sign != null) {
            switch (sign) {
                case STOP:
                    if (!notificationShown)
                        NotificationUtils.notifyStop(this);
                    break;
                case BUMP:
                    if (!notificationShown)
                        NotificationUtils.notifyBump(this);
                    break;
                case NO_MOVING:
                    if (!notificationShown)
                        NotificationUtils.notifyNoMoving(this);
                    break;
                case NO_RIGH:
                    if (!notificationShown)
                        NotificationUtils.notifyNoRight(this);
                    break;
            }
            notificationShown = true;
        } else {
            notificationShown = false;
        }
    }

    public void click(View view) {

        int x1 = 0;
        int y1 = 0;
        int x2 = 0;
        int y2 = 0;

        List<Line> lines = new ArrayList<>();

        for (int i = 0; i < imgMap.getSelectedPathPoints().size(); i++) {
            Point point = imgMap.getSelectedPathPoints().get(i);
            if (i == 0) {
                x1 = point.x;
                y1 = point.y;
                continue;
            }
            if ((point.x != x1 && point.y != y1 || i == imgMap.getSelectedPathPoints().size() - 1)) {
                Log.d(TAG, "new Line" + x1 + " " + y1 + " " + x2 + " " + y2);
                lines.add(new Line(y1, y2, x1, x2));
                x1 = x2;
                y1 = y2;
            } else {
                x2 = point.x;
                y2 = point.y;
            }
        }
        Map<String, Object> map = new HashMap<>();

        for (int i = 0; i < lines.size(); i++) {
            map.put(i + "", lines.get(i));
        }

        FirebaseDatabase.getInstance().getReference().child("car")
                .setValue(map);

        Toast.makeText(this, "Sent successfully", Toast.LENGTH_SHORT).show();

    }

    public void reset(View view) {
        FirebaseDatabase.getInstance().getReference().child("car")
                .removeValue();
        Map<String, Object> map = new HashMap<>();
        map.put("x", 0);
        map.put("y", 0);
        map.put("d", 1);
        FirebaseDatabase.getInstance().getReference().child("current_location")
                .setValue(map);
    }

    class Line {
        private int x1, x2, y1, y2;

        public Line(int x1, int x2, int y1, int y2) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
        }

        public int getX1() {
            return x1;
        }

        public void setX1(int x1) {
            this.x1 = x1;
        }

        public int getX2() {
            return x2;
        }

        public void setX2(int x2) {
            this.x2 = x2;
        }

        public int getY1() {
            return y1;
        }

        public void setY1(int y1) {
            this.y1 = y1;
        }

        public int getY2() {
            return y2;
        }

        public void setY2(int y2) {
            this.y2 = y2;
        }
    }
}
