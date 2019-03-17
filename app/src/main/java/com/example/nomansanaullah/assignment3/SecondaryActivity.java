package com.example.nomansanaullah.assignment3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class SecondaryActivity extends Activity {

    private FloatingActionButton reset;
    private GPS gps2;
    private SecondaryActivity secondActivity;
    private MainActivity mainActivity;
    private GPXWriter gpx;
    private LocationManager lm;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };

    //Textviews
    TextView altitude, timetaken, speed, totaldistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call the super class method and set the content for this activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        secondActivity = this;
        this.gpx = new GPXWriter();
        gps2 = new GPS(this, this);
        altitude = (TextView)findViewById(R.id.altitude);
        timetaken = (TextView)findViewById(R.id.timetaken);
        speed = (TextView)findViewById(R.id.speed);
        totaldistance = (TextView)findViewById(R.id.totaldistance);
        reset = (FloatingActionButton)findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondaryActivity.this,MainActivity.class);
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                finish();

            }
        });
        if(!MainActivity.checkPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }else{
            addLocationListener();

        }


    }

    /**
     * Read GPX file, set all the values and draw graph
     */
    private void addLocationListener(){

                GPS gps =  gpx.readerGPX();
                altitude.setText(gps.getAllAltitudeVal());
                timetaken.setText(convert(MainActivity.runTime));
                speed.setText(gps.getAllSpeedVal());
                totaldistance.setText(gps.getAllDistanceVal());


            GraphView graphView = (GraphView)findViewById(R.id.graph);
            double graphArray[] = gps.graphPoints();
        for (int i = 0; i <graphArray.length ; i++) {

            graphArray[i]=graphArray[i]/10;

        }
            graphView.setGraphArray(graphArray);





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * function to convert milliseconds into HH:MM:SS
     * @param miliSeconds
     * @return
     */
    public String convert(long miliSeconds)
    {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }

}
