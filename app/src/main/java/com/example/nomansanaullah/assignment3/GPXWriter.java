package com.example.nomansanaullah.assignment3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.nomansanaullah.assignment3.GPS;
import com.example.nomansanaullah.assignment3.MainActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GPXWriter {
    private final String TAG = GPXWriter.class.getName();
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private  SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss") ;
    private Calendar calendar = Calendar.getInstance();
    private String date = dateFormat.format(calendar.getTime());
    private final String GPX_FILE_NAME = date+".gpx";
    private MainActivity mainActivity;
    private Context context;

    private File pathtoSD;
    private File directory;
    private File gpxFile;

    public GPXWriter(){
        this.pathtoSD = Environment.getExternalStorageDirectory();
        this.directory = new File(pathtoSD + "/GPStracks");
        directory.mkdirs();
        this.gpxFile = new File(directory, GPX_FILE_NAME );
    }

    /**
     * Write all the recorded information to a GPX file
     * @param gps
     * @param context
     * @param main
     */
    public void writePath(GPS gps, Context context, MainActivity main) {

        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";


        String distance = "<distance>" + gps.getDistance() + "</distance>\n";
        String time = "<time>" + /*gps.getTotalTime()*/ MainActivity.runTime + "</time>\n";
//
        String minSpeed = "<minspeed>" + gps.getMinSpeed() + "</minspeed>\n";
        String maxSpeed = "<maxspeed>" + gps.getMaxSpeed() + "</maxspeed>\n";
        String averageSpeed = "<averagespeed>" + gps.getAverageSpeed() + "</averagespeed>\n";
//
        String minAltitude = "<minaltitude>" + gps.altitudeMin() + "</minaltitude>\n";
        String maxAltitude = "<maxaltitude>" + gps.altitudeMax() + "</maxaltitude>\n";
        String gainAltitude = "<gainaltitude>" + gps.altitudeGain() + "</gainaltitude>\n";
        String lossAltitude = "<lossaltitude>" + gps.altitudeLoss() + "</lossaltitude>\n";

        String trackingPoints = "<trkseg>\n";

        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        for (Location location : gps.getLocationList()) {
            trackingPoints += "<trkpt lat=\"" + location.getLatitude() + "\" lon=\"" + location.getLongitude() + "\"><ele>" + location.getAltitude() + "</ele><time>" + simpleDateFormat.format(new Date(location.getTime())) + "</time></trkpt>\n";
        }

        String footer = "</trkseg>\n</trk>\n</gpx>\n";

        try {
            if(!MainActivity.checkPermissions(context, PERMISSIONS)){
                ActivityCompat.requestPermissions(mainActivity, PERMISSIONS, 1);
            } else {
                File file = new File(directory , GPX_FILE_NAME);
                FileWriter writer = new FileWriter(file, false);
                writer.append(header);
                writer.append(distance);
                writer.append(time);
                writer.append(minSpeed);
                writer.append(maxSpeed);
                writer.append(averageSpeed);
                writer.append(minAltitude);
                writer.append(maxAltitude);
                writer.append(gainAltitude);
                writer.append(lossAltitude);
                writer.append(trackingPoints);
                writer.append(footer);
                writer.flush();
                writer.close();

                Toast.makeText(main, "GPX recorded", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * GPX Reader
     * @return
     */
    public GPS readerGPX(){
        GPS gps;
        List<Location> list = new ArrayList<Location>();
        double distance = 0;
        double maxSpeed = 0;
        double minSpeed = 0;
        double averageSpeed = 0;
        double minAltitude = 0;
        double maxAltitude = 0;
        double gainAltitude = 0;
        double lossAltitude = 0;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(lastFileModified(Environment.getExternalStorageDirectory().toString() + "/GPSTracks"));
            Document document = documentBuilder.parse(fileInputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");
            NodeList nodelist_ele = elementRoot.getElementsByTagName("ele");

            distance = Float.parseFloat(elementRoot.getElementsByTagName("distance").item(0).getTextContent());
            minSpeed = Float.parseFloat(elementRoot.getElementsByTagName("minspeed").item(0).getTextContent());
            maxSpeed = Float.parseFloat(elementRoot.getElementsByTagName("maxspeed").item(0).getTextContent());
            averageSpeed = Float.parseFloat(elementRoot.getElementsByTagName("averagespeed").item(0).getTextContent());
            minAltitude = Double.parseDouble(elementRoot.getElementsByTagName("minaltitude").item(0).getTextContent());
            maxAltitude = Double.parseDouble(elementRoot.getElementsByTagName("maxaltitude").item(0).getTextContent());
            gainAltitude = Double.parseDouble(elementRoot.getElementsByTagName("gainaltitude").item(0).getTextContent());
            lossAltitude = Double.parseDouble(elementRoot.getElementsByTagName("lossaltitude").item(0).getTextContent());

            for(int i = 0; i < nodelist_trkpt.getLength(); i++){
                Node node = nodelist_trkpt.item(i);
                NamedNodeMap attributes = node.getAttributes();

                double latitude = Double.parseDouble(attributes.getNamedItem("lat").getTextContent());
                double longitude = Double.parseDouble(attributes.getNamedItem("lon").getTextContent());
                double altitude = Double.parseDouble(nodelist_ele.item(i).getTextContent());

                Location location = new Location("GPX " + i);
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                location.setAltitude(altitude);

                list.add(location);
            }

            fileInputStream.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        gps = new GPS(list, distance,maxSpeed, minSpeed, averageSpeed, minAltitude, maxAltitude, gainAltitude, lossAltitude);
        return gps;
    }

    public static File lastFileModified(String dir) {
        File fl = new File(dir);
        File choice = null;
        if (fl.listFiles().length>0) {
            File[] files = fl.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return file.isFile();
                }
            });
            long lastMod = Long.MIN_VALUE;

            for (File file : files) {
                if (file.lastModified() > lastMod) {
                    choice = file;
                    lastMod = file.lastModified();
                }
            }
        }
        return choice;
    }

}
