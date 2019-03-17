package com.example.nomansanaullah.assignment3;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {
    //todo permissions
    static final int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private GPS gps2;
    private MainActivity thisActivity;
    public static long startTime, runTime = 0;
    private FloatingActionButton start, stop;
    private GPXWriter gpxWriter;
    private Context context;
    private TextView p_1,p_2,p_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


         start = (FloatingActionButton) findViewById(R.id.start);
         stop = (FloatingActionButton) findViewById(R.id.stop);
        thisActivity = this;
        gps2 = new GPS(this, thisActivity);
        gpxWriter = new GPXWriter();

        p_1 = (TextView)findViewById(R.id.p_1);
        p_2 = (TextView)findViewById(R.id.p_2);
        p_3 = (TextView)findViewById(R.id.p_3);

        p_1.setText(R.string.instruction_1);
        p_2.setText(R.string.instruction_2);
        p_3.setText(R.string.instruction_3);

//        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        if(!checkPermissions(this, PERMISSIONS)){
           // ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            checkPermissionMethod();

        }



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop.show();
                start.hide();
                startTime = System.currentTimeMillis();
                gps2.enableGPSTrack();
                Toast.makeText(thisActivity,"Tracking Started",Toast.LENGTH_SHORT).show();


            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps2.disableGPSTrack();
                runTime = System.currentTimeMillis() - startTime;
                gpxWriter.writePath(gps2,context,thisActivity);
                Intent intent = new Intent(MainActivity.this,SecondaryActivity.class);
                startActivity(intent);



            }
        });

    }

    /**
     * Show rationale for permissions, and if permissions are not granted then grant them
     */
    public void checkPermissionMethod(){
        if(ContextCompat.checkSelfPermission(thisActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(
                thisActivity,Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(
                thisActivity,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions are not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    thisActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    thisActivity,Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    thisActivity,Manifest.permission.ACCESS_FINE_LOCATION)){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setMessage("Permissions are required for the app to function. Please grant these permissions");
                builder.setTitle("Permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                thisActivity,
                                PERMISSIONS,
                                PERMISSION_ALL
                        );
                    }
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        thisActivity,
                        PERMISSIONS,
                        PERMISSION_ALL
                );
            }
        }else {
            // Do something, when permissions are already granted
            Toast.makeText(context,"Permissions already granted",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check if Permissions are granted
     * @param context
     * @param permissions
     * @return
     */
    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
