package com.arthur_guo.SafeToEat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arthur_guo.SafeToEat.InfoAdapter.Restaurant;
import com.arthur_guo.SafeToEat.R;

import java.util.ArrayList;

/**
 * Created by Arthur on 7/17/2015.
 */
public class RestaurantListAdapter extends ArrayAdapter<Restaurant>{

    private  int layoutResourceId;
    private ArrayList<Restaurant> restaurants;
    private Context context;

    public RestaurantListAdapter(Context context, int layoutid, ArrayList<Restaurant> restaurants) {
        super(context, layoutid, restaurants);
        this.layoutResourceId = layoutid;
        this.restaurants = restaurants;
        this.context = context;
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(layoutResourceId, null);
        }

        Restaurant restaurant = getItem(position);

        if (restaurant != null) {
            TextView name = (TextView) v.findViewById(R.id.restaurant_name);
            TextView address = (TextView) v.findViewById(R.id.restaurant_address);
            TextView rating = (TextView) v.findViewById(R.id.faculty_display_rating);

            if (name != null) {
                name.setText(restaurant.getName());
            }

            if (address != null) {
                address.setText(restaurant.getAddress());
            }

            if (rating != null) {
                int percentile = restaurant.getPercentile();
                rating.setText(Integer.toString(percentile));
            }
        }

        return v;
    }
}

