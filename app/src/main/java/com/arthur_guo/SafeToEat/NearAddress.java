package com.arthur_guo.SafeToEat;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.arthur_guo.SafeToEat.InfoAdapter.Restaurant;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantComparator.LowestInfractionScoreComparator;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantHelper;
import com.arthur_guo.SafeToEat.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NearAddress extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap googleMap;
    private boolean mapLoaded = false;
    private Marker currMarker;
    private Spinner distanceSelector;
    private EditText search;
    private ArrayList<Restaurant> restaurants;
    private RestaurantListAdapter restaurantAdapter;
    private ListView nearby;
    private LatLng myLatLng;
    private Marker[] markers;
    private LatLngBounds.Builder builder;
    private HashMap<Marker, Restaurant> markerCorresponder;

    long lastTextChange;


    public NearAddress() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near_address, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Ready to start the map
        MapFragment mf = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.near_address_map);
        mf.getMapAsync(this);


        //Nearby listview
        nearby = (ListView) getActivity().findViewById(R.id.search_address_listview);
        restaurants = new ArrayList<Restaurant>();
        restaurantAdapter = new RestaurantListAdapter(getActivity(), R.layout.restaurant_list_item, restaurants);
        nearby.setAdapter(restaurantAdapter);
        nearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FacultyDisplay.class);
                intent.putExtra("Faculty", restaurantAdapter.getItem(position));
                startActivity(intent);
            }
        });
        //Create a spinner selecting distance
        distanceSelector = (Spinner) getActivity().findViewById(R.id.search_address_filter);
        ArrayAdapter<String> distanceAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.nearmedistances));
        distanceSelector.setAdapter(distanceAdapter);
        distanceSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRestaurants();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Get the distance from settings and select dropdown
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String distance = prefs.getString("default_distance","null");
        distanceSelector.setSelection(distanceAdapter.getPosition(distance));

        //Enter an address to find restaurants
        search = (EditText) getActivity().findViewById(R.id.search_address_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                latLngUpdate(s.toString());
                updateRestaurants();
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapLoadedCallback(this);

    }

    @Override
    public void onMapLoaded() {
        //Handle marker clicks
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Restaurant restaurant = markerCorresponder.get(marker);
                if (restaurant != null) {
                    Intent intent = new Intent(getActivity(), FacultyDisplay.class);
                    intent.putExtra("Faculty", restaurant);
                    startActivity(intent);
                }
            }
        });
        latLngUpdate("");
    }
    //Takes the text entered by the user and gets the restaurants and displays the restaurant near address
    private void latLngUpdate(String strAddress) {
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
            //Place your latitude and longitude
            List<Address> addresses = geocoder.getFromLocationName(strAddress + " Waterloo, Ontario Canada", 5);

            if (addresses != null && addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                myLatLng = new LatLng(fetchedAddress.getLatitude(), fetchedAddress.getLongitude());
                updateRestaurants();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Could not get address..!", Toast.LENGTH_LONG).show();
        }
    }
    //Updates the restaurant list and map
    private void updateRestaurants() {
        restaurants.clear();
        String distanceText = distanceSelector.getSelectedItem().toString();
        int distance = 0;
        if (distanceText.contains("KM")) {
            distance = Integer.parseInt(distanceText.replace("KM", "")) * 1000;
        } else if (distanceText.contains("M")) {
            distance = Integer.parseInt(distanceText.replace("M", ""));
        } else {
            Log.d("UPDATING RESTAURNT", distanceText);
        }
        //String filter = search.getText().toString();
        if (myLatLng != null) {
            restaurants.addAll(RestaurantHelper.sortRestaurants(RestaurantHelper.getNearby(myLatLng.latitude, myLatLng.longitude, distance), new LowestInfractionScoreComparator()));
        }
        restaurantAdapter.notifyDataSetChanged();
        setMarkers();
    }
    //Create markers for the map
    private void setMarkers() {
        if (markers != null) {
            for (Marker m : markers) {
                m.remove();
            }
        }
        if (currMarker != null) {
            currMarker.remove();
        }
        if (myLatLng != null) {
            currMarker = googleMap.addMarker(new MarkerOptions().position(myLatLng).title("Current Location"));
            CameraUpdate cf = CameraUpdateFactory.newLatLngZoom(myLatLng, 16);
            googleMap.moveCamera(cf);
        }

        markers = new Marker[restaurants.size()];
        markerCorresponder = new HashMap<Marker, Restaurant>();

        ArrayList<Double> lat = new ArrayList<>();
        ArrayList<Double> lng = new ArrayList<>();
        builder = new LatLngBounds.Builder();
        if (myLatLng != null) {
            builder.include(myLatLng);
        }
        for (int i = 0; i < markers.length; i++) {
            Restaurant restaurant = restaurants.get(i);
            LatLng latLng = generateLatLng(restaurant.getLatLng(), lat, lng);
            builder.include(latLng);
            markers[i] = googleMap.addMarker(new MarkerOptions().position(latLng).title(restaurant.getName()));
            markerCorresponder.put(markers[i], restaurant);
        }
        if (markers.length > 0) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }
    }
    //Ensures no 2 pins on the map are on top of each other
    private LatLng generateLatLng(LatLng latLng, ArrayList<Double> lat, ArrayList<Double> lng) {
        if (lat.contains(latLng.latitude) && lng.contains(latLng.longitude)) {
            Random random = new Random();
            double latTemp = latLng.latitude;
            double lngTemp = latLng.longitude;
            double change;
            change = (random.nextBoolean()) ? 0.0002 : -0.0002;
            if (random.nextBoolean()) {
                latTemp += change;
            } else {
                lngTemp += change;
            }
            return generateLatLng(new LatLng(latTemp, lngTemp), lat, lng);

        } else {
            lat.add(latLng.latitude);
            lng.add(latLng.longitude);
            return latLng;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapFragment f = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.near_address_map);
        if (f != null){
            getActivity().getFragmentManager().beginTransaction().remove(f).commit();
        }
    }


}


