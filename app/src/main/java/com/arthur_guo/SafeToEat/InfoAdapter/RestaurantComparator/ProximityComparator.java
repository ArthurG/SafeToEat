package com.arthur_guo.SafeToEat.InfoAdapter.RestaurantComparator;

import com.arthur_guo.SafeToEat.InfoAdapter.Restaurant;

import java.util.Comparator;

public class ProximityComparator implements Comparator<Restaurant> {
    double lat;
    double lng;
    public ProximityComparator(double lat,double lng){
        this.lat = lat;
        this.lng = lng;
    }
    @Override
    public int compare(Restaurant lhs, Restaurant rhs) {
        return lhs.distance(this.lat,this.lng) > rhs.distance(this.lat,this.lng)?1:-1;
    }
}
