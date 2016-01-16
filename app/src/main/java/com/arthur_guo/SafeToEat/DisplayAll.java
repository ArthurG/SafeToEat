package com.arthur_guo.SafeToEat;

import android.location.LocationListener;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.arthur_guo.SafeToEat.InfoAdapter.Restaurant;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantComparator.HighestInfractionScoreComparator;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantComparator.LexicographicComparator;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantComparator.LowestInfractionScoreComparator;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantComparator.ProximityComparator;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantHelper;
import com.arthur_guo.SafeToEat.R;

import java.util.ArrayList;
import java.util.Comparator;

public class DisplayAll extends Fragment {

    private ArrayList<Restaurant> displayedRestaurant;
    private ArrayAdapter<Restaurant> listRestaurantAdapter;
    private EditText searchBar;
    private Spinner sort;
    private double lat;
    private double lng;
    private LocationManager lm;
    private LocationListener locListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_all, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Create a location manager for getting location
        lm = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);


        //Populate listview with the right restaurants to start
        displayedRestaurant = RestaurantHelper.filterByString(RestaurantHelper.allRestaurants, "");
        //Add a ListView for possible restaurants
        LinearLayout outer = (LinearLayout) getActivity().findViewById(R.id.display_all_outerlayout);
        ListView listview = new ListView(getActivity());
        LinearLayout.LayoutParams lprams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        listview.setLayoutParams(lprams);
        outer.addView(listview);
        listRestaurantAdapter = new RestaurantListAdapter(getActivity(),R.layout.restaurant_list_item,displayedRestaurant);
        listview.setAdapter(listRestaurantAdapter);

        //Handle ListView clicks
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FacultyDisplay.class);
                intent.putExtra("Faculty", listRestaurantAdapter.getItem(position));
                startActivity(intent);
            }
        });

        //Searchbar input handler
        searchBar = (EditText) getActivity().findViewById(R.id.display_all_search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Update listview with the correct faculties (depending on string)
                updateDisplayAll();
            }


        });

        //Sort based on dropdown
        sort = (Spinner) getActivity().findViewById(R.id.display_all_search_filter);
        String[] sortOptions = new String[]{"Closest Distance","Best Rating","Worst Rating","Alphabetical"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,sortOptions);
        sort.setAdapter(sortAdapter);
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDisplayAll();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        newLocListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,200, locListener);

        Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null){
            loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (loc != null){
            lat = loc.getLatitude();
            lng = loc.getLongitude();
        }
    }

    private void newLocListener() {
        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    private void updateDisplayAll() {
        int position = sort.getSelectedItemPosition();
        Comparator comp = null;
        switch (position) {
            case 0:
                if (lat != -1 && lng != -1){
                    comp = new ProximityComparator(lat,lng);
                }else{
                    Toast.makeText(getActivity(), "Location can not be found, cannot sort by distance", Toast.LENGTH_LONG).show();
                }
                break;
            case 1:
                comp = new LowestInfractionScoreComparator();
                break;
            case 2:
                comp = new HighestInfractionScoreComparator();
                break;
            case 3:
                comp = new LexicographicComparator();
                break;
            default:
                break;
        }
        if (!(comp == null)){
            //Sort the restaurants
            ArrayList<Restaurant> sortRestaurants = RestaurantHelper.sortRestaurants(comp);
            //Filter the restaurants
            ArrayList<Restaurant> filtered = RestaurantHelper.filterByString(RestaurantHelper.sortRestaurants(sortRestaurants, comp),searchBar.getText().toString());
            //Display restaurants
            displayedRestaurant.clear();
            displayedRestaurant.addAll(filtered);
            listRestaurantAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        lm.removeUpdates(locListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,200, locListener);
    }
}
