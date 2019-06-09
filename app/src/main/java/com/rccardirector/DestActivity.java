package com.rccardirector;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import static com.rccardirector.App.currLocation;
import static com.rccardirector.App.destLocation;
import static com.rccardirector.App.qualifyDestPoint;

;

public class DestActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    MapImageView imgMap;
    Button submit;
    String serverIP = "192.168.1.18";
    int serverPort = 8885;
    Socket clientSocket = new Socket();
    OutputStream outputStream;
    InputStream inputStream;
    BufferedOutputStream bufferedOutputStream;
    DataOutputStream dataOutputStream;
    BufferedInputStream bufferedInputStream;
    DataInputStream dataInputStream;
    AlertDialog progDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dest);
        imgMap = (MapImageView) findViewById(R.id.imgMapDest);
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

        submit = (Button) findViewById(R.id.submitDestButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("send pending");
                                Log.i("dd", "ss");

//                                while (!clientSocket.isConnected());
                                System.out.println("send pending2");


                                //PrintWriter printer = new PrintWriter(outputStream,true);

                                //printer.println(imgMap.getSelectedPath());
//                                try {

                                    /*dataOutputStream.writeBytes("$path$");
                                    dataOutputStream.flush();*/
                                    //dataOutputStream.writeBytes(Arrays.toString(imgMap.getSelectedPathPoints().toArray()));
                                    for(Point p:imgMap.getSelectedPathPoints()){
                                        Log.d(TAG, "run: " + p.x+","+p.y+"$");
//                                        dataOutputStream.writeBytes(p.x+","+p.y+"$") ;
//                                        dataOutputStream.flush();
                                    }
                                    boolean flag=true;


                                    //System.out.println(imgMap.getSelectedPath() + "$$");
                                    //Thread.sleep(10000);
                                    Log.d(TAG, "run: path#$");
//                                    dataOutputStream.writeBytes("path#$");
//                                    dataOutputStream.flush();
                                    //outputStream.close();
                                    System.out.println("Path sent");
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                    Log.i("Error", e.getMessage(), e);
//                                }
                            }
                        }).start();
            }
        });
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while(true) {
                            while(clientSocket.isConnected());
                            try {
                                clientSocket = new Socket(InetAddress.getByName(serverIP), serverPort);
                                bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
                                dataOutputStream = new DataOutputStream(bufferedOutputStream);
                                bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
                                dataInputStream = new DataInputStream(bufferedInputStream);

                                //inputStream = clientSocket.getInputStream();
                            } catch (IOException e) {
                                Log.d("SocketError", e.getMessage());
                                System.out.println(e.getMessage());
                                Toast.makeText(DestActivity.this,"Can't find the car",Toast.LENGTH_LONG).show();
                            }
                            finally {
                                continue;
                            }
                        }
                    }
                }).start();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                while (!clientSocket.isConnected()) ;
                                String line = "";
                                System.out.println("reciving");
                                while (true) {
                                    try {
                                        //System.out.println("NULL !");
                                        if(dataInputStream != null)
                                            if ((line = dataInputStream.readLine()) == null) continue;
                                        //System.out.println("RECIEVED A MESSAGE !");
                                        System.out.println(line);
                                    } catch (SocketException e) {
                                        //System.out.println("reciving");
                                        e.printStackTrace();
                                        continue;
                                    } catch (IOException e) {
                                        if(e.getClass() == EOFException.class)
                                            continue;
                                        //System.out.println("reciving");
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        //System.out.println("reciving");
                                        e.printStackTrace();
                                        continue;
                                    }

                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setMinimumWidth(100);
        progressBar.setMinimumHeight(100);
        builder.setView(progressBar);
        progDialog = builder.create();
    }

    @Override
    protected void onDestroy() {
        if (clientSocket != null && clientSocket.isConnected()) {
            try {
                dataOutputStream.close();
                dataInputStream.close();
                //bufferedOutputStream.close();
                //bufferedInputStream.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    class sendPath extends AsyncTask<Void, Void, Void> {

        ContentLoadingProgressBar progressBar;
        sendPath() {
            progressBar = new ContentLoadingProgressBar(DestActivity.this);
            progressBar.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println("send pending");
            Log.i("dd", "ss");
            while (clientSocket == null || !clientSocket.isConnected()) ;
            System.out.println("send pending2");
            Toast.makeText(DestActivity.this, "socket connected", Toast.LENGTH_LONG).show();
            send();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        void send() {

            //PrintWriter printer = new PrintWriter(outputStream,true);

            //printer.println(imgMap.getSelectedPath());
            try {
                dataOutputStream.writeBytes("Hello$$");
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Error", e.getMessage(), e);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.hide();
            super.onPostExecute(aVoid);
        }
    }

}
