package com.example.nomansanaullah.assignment3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class GPS implements LocationListener {
    private Context context;
    private MainActivity mainActivity;
    private SecondaryActivity secondaryActivity;
    boolean isGPSEnabled = false;
    boolean canGetLocation = false;
    Location location;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
    private static final long MIN_TIME_BW_UPDATES = 5000;
    Activity activity;
    protected LocationManager locationManager;
    public static double lat = 0.0;
    private List<Location> list;

    // result variables
    private double distanceTravelled;
    private double latitude;
    private double longitude;
    private double altitude;
    private double minAltitude;
    private double maxAltitude;
    private double gainAltitude;
    private double lossAltitude;
    private String getAltitude;
    private double maxSpeed;
    private double minSpeed;
    private double averageSpeed;
    private String getSpeed;
    private String getDistance;



    public GPS(){}

    public GPS(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        this.locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
        this.list = new ArrayList<Location>();
    }
    public GPS(List<Location> list, double distanceTravelled, double maxSpeed, double minSpeed, double averageSpeed ,double minAltitude, double maxAltitude, double gainAltitude, double lossAltitude ){
        this.list = list;
        this.distanceTravelled = distanceTravelled;
        this.maxSpeed = maxSpeed;
        this.minSpeed = minSpeed;
        this.averageSpeed = averageSpeed;
        this.minAltitude = minAltitude;
        this.maxAltitude = maxAltitude;
        this.gainAltitude = gainAltitude;
        this.lossAltitude = lossAltitude;

    }

    /**
     * enable GPS tracker
     *
     */
    public void enableGPSTrack(){
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            // No network provider is enabled

        } else {
            this.canGetLocation = true;
        }
        if(isGPSEnabled){
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mainActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
        }


    }

    /**
     * disable GPS tracker and compute all the result
     */
    public void disableGPSTrack(){
        if(locationManager !=null)
            locationManager.removeUpdates(this);
        //analyze data here, create method to return all values
        computeAltitude();
        computeSpeed();
        computeDistance();


    }

    /**
     *
     * @return
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Shows settings alert dialog if GPS is disabled and prompt the user to enable GPS from the settings menu
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("GPS settings");

        alertDialog.setMessage("GPS is not enabled. This app requires GPS permissions to function. \nDo you want to go to settings menu and enable it?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    /**
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(mainActivity,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//
//        }
//        else{
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                altitude = location.getAltitude();

                list.add(location);

                String info = "Location Changed: " + latitude + "\n"+ longitude + "\n" + altitude + "\n" + location.getSpeed() + "\n" + location.getTime()/1000;


            }

        //}

    }

    /**
     *
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     *
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     *
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {
        showSettingsAlert();

    }

    /**
     * adds location object to the list which we will then use in all our methods to retrieve location information
     * @param location
     */
    public void addLocation(Location location){
        list.add(location);
    }

    /**
     * Return location list which contains the Location object
     * @return
     */
    public List<Location> getLocationList() {
        return list;
    }

    /**
     * Calculate distance between two coodinates
     * @param l1
     * @param l2
     * @return
     */
    private double distanceBetweenPoints(Location l1, Location l2){
        return l1.distanceTo(l2);
    }

    /**
     * Calculate total distance travelled
     */
    public void computeDistance(){
        if(list.size()>1){
            for(int i = 0; i < list.size()-1; i++)
                distanceTravelled += distanceBetweenPoints(list.get(i), list.get(i+1));
        }
    }

    /**
     * Returns total distance and prettifies it
     * @return
     */
    public String getAllDistanceVal(){
        getDistance = String.format("%.2f",getDistance()) +" m";
        return getDistance;
    }

    /**
     * distance getter
     * @return
     */
    public double getDistance() {
        return distanceTravelled;
    }

    /**
     * altitude getter
     * @return
     */
    public double altitudeMin() {
        return minAltitude;
    }

    /**
     * altitude getter
     * @return
     */
    public double altitudeMax() {
        return maxAltitude;
    }

    /**
     * altitude getter
     * @return
     */
    public double altitudeGain() {
        return gainAltitude;
    }

    /**
     * altitude getter
     * @return
     */
    public double altitudeLoss() {
        return lossAltitude;
    }

    /**
     * Return Altitude values and prettify them
     * @return
     */
    public String getAllAltitudeVal(){
        getAltitude = "Max: " + String.format("%.2f",altitudeMax()) + " m" +
                "\nMin: " + String.format("%.2f",altitudeMin()) + " m" +
                "\nGain: " + String.format("%.2f",altitudeGain()) + " m" +
                "\nLoss: " + String.format("%.2f",altitudeLoss()) + " m";
        return getAltitude;
    }

    /**
     * Calculates the altitude
     */
    private void computeAltitude() {
        if (list.size() > 1) {
            double temp;
            double tempDifference;
            minAltitude = list.get(0).getAltitude();
            maxAltitude = list.get(0).getAltitude();

            for (int i = 1; i < (list.size()); i++) {
                temp = list.get(i).getAltitude();
                tempDifference = getDifferenceBetweenAltitude(list.get(i-1), list.get(i));

                if (temp < minAltitude) {
                    minAltitude = temp;
                }
                if (temp > maxAltitude) {
                    maxAltitude = temp;
                }

                if(tempDifference > 0){
                    gainAltitude = gainAltitude + tempDifference;
                } else if(tempDifference < 0 ){
                    lossAltitude = lossAltitude + tempDifference;
                }
            }
        }
    }

    /**
     * Get altitude difference between two coordinates
     * @param location1
     * @param location2
     * @return
     */
    private double getDifferenceBetweenAltitude(Location location1, Location location2){
        return location2.getAltitude() - location1.getAltitude();
    }


    /**
     * speed getter
     * @return
     */
    public double getAverageSpeed() {
        return averageSpeed;
    }

    /**
     * speed getter
     * @return
     */
    public double getMinSpeed() {
        return minSpeed;
    }

    /**
     * speed getter
     * @return
     */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Return speed values and prettify them
     * @return
     */
    public String getAllSpeedVal(){
        getSpeed = "Max: " + String.format("%.3f",getMaxSpeed()) + " m/s" +
                "\nMin: " + String.format("%.3f",getMinSpeed()) + " m/s" +
                "\nAvg: " + String.format("%.3f",getMaxSpeed()) + " m/s";
        return getSpeed;
    }

    /**
     * Calculate speed between two coordinates.
     * Checks if the hasSpeed method contains the speed componenet, if it does then that is saved else,
     * speed is calculated between two coordiantes
     * @param location1
     * @param location2
     * @return
     */
    private double getSpeedBetweenLocation(Location location1, Location location2){
        if(location1.hasSpeed() && location2.hasSpeed()){
            if(location1.getSpeed() > location2.getSpeed()){
                return location1.getSpeed();
            } else if(location1.getSpeed() < location2.getSpeed()){
                return location2.getSpeed();
            } else {
                return location1.getSpeed();
            }
        } else{
            double distanceBetweenLocation = distanceBetweenPoints(location1, location2);
            long timeBetweenLocations = location2.getTime() - location1.getTime();

            return calculateSpeed(distanceBetweenLocation, timeBetweenLocations);
        }
    }

    /**
     * Speed formula
     * @param distance
     * @param time
     * @return
     */
    private double calculateSpeed(double distance, long time){
        return distance / time;
    }

    /**
     * Calculates the total speed
     */
    public void computeSpeed(){
        if(list.size() > 1){
            double temp;
            double totalSpeed = 0;
            this.maxSpeed = 0;
            this.minSpeed = getSpeedBetweenLocation(list.get(0), list.get(1));

            for(int i=0 ; i<(list.size() - 1) ; i++){
                temp = getSpeedBetweenLocation(list.get(i), list.get(i+1));
                totalSpeed = totalSpeed + temp;

                if(temp < minSpeed){
                    minSpeed = temp;
                }
                if(temp > maxSpeed){
                    maxSpeed = temp;
                }
            }

            this.averageSpeed = totalSpeed / list.size();
        }
    }


    /**
     * Returns array with altitude values that will be used to draw graph
     * @return
     */
    public double[] graphPoints() {

        double [] gp = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {

            gp[i]= list.get(i).getAltitude();

        }

        return gp;

    }



}
