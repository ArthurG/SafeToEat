package com.arthur_guo.SafeToEat.InfoAdapter.RestaurantComparator;

import com.arthur_guo.SafeToEat.InfoAdapter.Restaurant;

import java.util.Comparator;

public class HighestInfractionScoreComparator implements Comparator<Restaurant> {

    @Override
    public int compare(Restaurant lhs, Restaurant rhs) {
        return  (lhs.getInfractionScore() >rhs.getInfractionScore())?-1:1;
    }
}
