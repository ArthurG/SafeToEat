package com.arthur_guo.SafeToEat.InfoAdapter;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Arthur on 7/17/2015.
 */
public class Restaurant implements Serializable {


    private String facId;
    private String telephone;

    private String name;
    private String address;
    private String city;
    private Date openDate;
    private int critical;
    private int noncritical;
    public ArrayList<Inspection> inspections;
    private double infractionScore;

    private double lat;
    private double lng;
    private int percentile;
    private String percentileString;

    public Restaurant(String facId, String name, String telephone, String address, String city, int critical, int nonCritical, double lat, double lng) {

        this.facId = facId;
        this.name = name;
        this.telephone = telephone;
        this.address = address;
        this.city = city;
        this.critical = critical;
        this.noncritical = nonCritical;
        this.lat = lat;
        this.lng = lng;

    }

    //Get inspections from Database, by most recent first
    public void setInspections(Context context) {
        ArrayList<Inspection> in = new ArrayList<Inspection>();
        RestaurantDBAdapter dbAdapter = new RestaurantDBAdapter(context);
        try {
            dbAdapter.createDatabase();
            in = dbAdapter.getInspections(this.facId);
            dbAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Inspection[] restaurants = sortInspections(in.toArray(new Inspection[in.size()]));
        ArrayList<Inspection> ret = new ArrayList<Inspection>();
        Collections.addAll(ret, restaurants);
        this.inspections = new ArrayList<Inspection>(ret);
    }

    //Sort inspections by most recent first
    private static Inspection[] sortInspections(Inspection[] in) {
        //Base case
        if (in.length <= 1) {
            return in;
        } else {
            Inspection[] result = new Inspection[in.length];
            //Split it in half
            int middle = in.length / 2;
            int firstLength = middle;
            int secondLength = in.length - middle;
            Inspection[] leftHalf = new Inspection[firstLength];
            Inspection[] rightHalf = new Inspection[secondLength];
            System.arraycopy(in, 0, leftHalf, 0, firstLength);
            System.arraycopy(in, middle, rightHalf, 0, secondLength);
            //Sort both halfs
            Inspection[] sortedLeft = sortInspections(leftHalf);
            Inspection[] sortedRight = sortInspections(rightHalf);
            //Merge both halfs
            int leftPointer = 0;
            int rightPointer = 0;
            int resultPointer = 0;
            while (leftPointer < firstLength && rightPointer < secondLength) {
                Inspection res;
                if (sortedLeft[leftPointer].isBefore(sortedRight[rightPointer])) {
                    res = sortedRight[rightPointer];
                    rightPointer++;
                } else {
                    res = sortedLeft[leftPointer];
                    leftPointer++;
                }
                result[resultPointer] = res;
                resultPointer++;
            }
            while (leftPointer < firstLength) {
                result[resultPointer] = sortedLeft[leftPointer];
                resultPointer++;
                leftPointer++;
            }
            while (rightPointer < secondLength) {
                result[resultPointer] = sortedRight[rightPointer];
                resultPointer++;
                rightPointer++;
            }
            return result;
        }
    }

    public boolean isNearby(double userLat, double userLng, double maxDistance) {
        //Check whether faculty is within the maxDistance specified by the user
        double distance = distance(userLat, userLng);
        return distance <= maxDistance;
    }

    public double distance(double lat1, double lng1) {
        //Distance between faculty and specified point in metres
        double earthRadius = 6371000;
        double dLat = Math.toRadians(this.lat - lat1);
        double dLng = Math.toRadians(this.lng - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(this.lat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (earthRadius * c);
    }

    public String getCity() {
        return city;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return facId;
    }

    public void calculateInfractionScore(int critValue, int marginError) {
        infractionScore = Math.max(critValue * critical + noncritical, 0);
    }

    public double getInfractionScore() {
        return infractionScore;
    }

    public void setPercentile(int r) {
        this.percentile = r;
        this.percentileString = Integer.toString(r);
    }

    public int getPercentile() {
        return this.percentile;
    }

    public String getTelephone() {
        return telephone;
    }


}
