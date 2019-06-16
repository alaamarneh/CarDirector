package com.rccardirector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.*;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

import static com.rccardirector.App.carDirection;
import static com.rccardirector.App.mapOrigin;
import static com.rccardirector.App.mapPathLength;
import static com.rccardirector.App.mapPathMid;


public class MapImageView extends SubsamplingScaleImageView {

    private final Paint paint = new Paint(), linePaint = new Paint();
    private final PointF vPin = new PointF(), gPin = new PointF();
    private PointF sPin, dPin;
    private Bitmap pin, car;
    boolean drawPath = false;

    public String getSelectedPath() {
        return selectedPath;
    }

    private String selectedPath;

    public List<Point> getSelectedPathPoints() {
        return selectedPathPoints;
    }

    private List<Point> selectedPathPoints;

    boolean[][] mapMatrix = new boolean[mapPathLength + 1][mapPathLength + 1];

    public MapImageView(Context context) {
        this(context, null);
    }

    public MapImageView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
        initialiseData();

    }

    public void setPin(PointF sPin) {
        this.sPin = sPin;
        //initialise();
        postInvalidate();
    }

    public void initialiseData() {
        for (int i = 0; i < mapPathLength; i++)
            for (int j = 0; j < mapPathLength; j++)
                if (i == 0 || j == 0 || i == mapPathMid || j == mapPathMid || i == mapPathLength || j == mapPathLength)
                    mapMatrix[i][j] = false;
                else
                    mapMatrix[i][j] = true;
        drawPath = false;

    }

    private void initialise() {
        float density = getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pushpin_blue);

        float w = (density / 420f) * pin.getWidth() / 2f;
        float h = (density / 420f) * pin.getHeight() / 2f;
        pin = Bitmap.createScaledBitmap(pin, (int) w, (int) h, true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        paint.setAntiAlias(true);

        Matrix matrix = new Matrix();

        car = BitmapFactory.decodeResource(this.getResources(), R.drawable.car);
        float density = getResources().getDisplayMetrics().densityDpi;
        float w = (density / 420f) * car.getWidth() / 6f;
        float h = (density / 420f) * car.getHeight() / 6f;
        car = Bitmap.createScaledBitmap(car, (int) w, (int) h, true);

        if (carDirection == App.Direction.NORTH)
            matrix.postRotate(90);
        else if (carDirection == App.Direction.EAST)
            matrix.postRotate(180);
        else if (carDirection == App.Direction.SOUTH)
            matrix.postRotate(270);

        car = Bitmap.createBitmap(car, 0, 0, car.getWidth(),
                car.getHeight(), matrix, true);

        if (sPin != null && pin != null) {
            sourceToViewCoord(sPin, vPin);
            float vX = vPin.x - car.getWidth() / 2;
            float vY = vPin.y - (car.getHeight() / 2);
            canvas.drawBitmap(car, vX, vY, paint);


            try {
                Paint linePaint = new Paint();
                sourceToViewCoord(dPin, gPin);
                PointF tmp = new PointF();
                Path path = new Path();
                //Find shortest path
                final Maze maze = new Maze(mapMatrix);
                final Point source = new Point((int) (sPin.x - mapOrigin.x), (int) (sPin.y - mapOrigin.y)); // Same as new Point(0, 0):
                final Point target = new Point((int) (dPin.x - mapOrigin.x), (int) (dPin.y - mapOrigin.y));

                final List<Point> pathPoints = new MazePathFinder().findPath(maze,
                        source,
                        target);
                selectedPathPoints = new ArrayList<Point>(pathPoints);

                selectedPath = maze.withPath(pathPoints);
                //System.out.println(selectedPath);
                //sourceToViewCoord(sPin.x,sPin.x,tmp);
                path.moveTo(vPin.x, vPin.y);
                for (Point point : pathPoints) {
                    sourceToViewCoord(point.x + mapOrigin.x, point.y + mapOrigin.y, tmp);

                    path.lineTo(tmp.x, tmp.y);
                }


                linePaint.setStrokeWidth(8);
                linePaint.setColor(Color.RED);
                linePaint.setStyle(Paint.Style.STROKE);
                vX = gPin.x - (pin.getWidth() / 2);
                vY = gPin.y - pin.getHeight();
                canvas.drawBitmap(pin, vX, vY, paint);

                canvas.drawPath(path, linePaint);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                setDrawPath(false);
            }
        }
    }

    void setDestination(PointF dest) {
        dPin = dest;
        invalidate();

    }

    void setDrawPath(boolean what) {
        drawPath = what;
    }

}