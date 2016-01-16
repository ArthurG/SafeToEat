package com.arthur_guo.SafeToEat.InfoAdapter.RestaurantComparator;

import com.arthur_guo.SafeToEat.InfoAdapter.Restaurant;

import java.util.Comparator;

/**
 * Created by Arthur on 7/18/2015.
 */
public class LexicographicComparator implements Comparator<Restaurant> {

    @Override
    public int compare(Restaurant lhs, Restaurant rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }
}
