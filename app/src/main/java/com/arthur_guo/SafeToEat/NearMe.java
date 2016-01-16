package com.arthur_guo.SafeToEat;

import android.content.SharedPreferences;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.arthur_guo.SafeToEat.InfoAdapter.Restaurant;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantComparator.LowestInfractionScoreComparator;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantHelper;
import com.arthur_guo.SafeToEat.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NearMe extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap googleMap;
    private boolean mapLoaded = false;
    private Marker currMarker;
    private Spinner distanceSelector;
    //private EditText search;
    private ArrayList<Restaurant> restaurants;
    private RestaurantListAdapter restaurantAdapter;
    private ListView nearbyListView;
    private LatLng myLatLng;
    private Marker[] markers;
    private LatLngBounds.Builder builder;
    private HashMap<Marker, Restaurant> markerCorresponder;
    private LocationManager lm;
    private LocationListener locListener;

    public NearMe() {
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
        return inflater.inflate(R.layout.fragment_near_me, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Create the map
        MapFragment mf = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.search_nearby_map);
        mf.getMapAsync(this);

        //Create a location manager
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        //Populate list with restaurants
        nearbyListView = (ListView) getActivity().findViewById(R.id.search_nearby_listview);
        restaurants = new ArrayList<Restaurant>();
        restaurantAdapter = new RestaurantListAdapter(getActivity(), R.layout.restaurant_list_item, restaurants);
        nearbyListView.setAdapter(restaurantAdapter);
        nearbyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FacultyDisplay.class);
                intent.putExtra("Faculty", restaurantAdapter.getItem(position));
                startActivity(intent);
            }
        });

        //Initialize spinner
        distanceSelector = (Spinner) getActivity().findViewById(R.id.search_nearby_filter);
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

        //Set the spinner default position from sharedprefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String distance = prefs.getString("default_distance", "null");
        distanceSelector.setSelection(distanceAdapter.getPosition(distance));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        //Request location updates
        newLocListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 200, locListener);
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
        updateRestaurants();

        /*
        Location currLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currLocation == null) {
            currLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (currLocation != null) {
            myLatLng = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
            currMarker = googleMap.addMarker(new MarkerOptions().position(myLatLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mapLoaded = true;
            CameraUpdate cf = CameraUpdateFactory.newLatLngZoom(myLatLng, 16);
            googleMap.moveCamera(cf);
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

            updateRestaurants();
        } else {
            Toast.makeText(getActivity(), "Please enable location service to use this feature!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        */
    }

    private void newLocListener() {
        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (currMarker != null) {
                    currMarker.remove();
                }
                currMarker = googleMap.addMarker(new MarkerOptions().position(myLatLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mapLoaded = true;
                updateRestaurants();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                if(provider.equals(LocationManager.GPS_PROVIDER)){
                    Toast.makeText(getActivity(), "Please enable location service to use this feature!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }
        };
    }

    //Repopulates restaurants arrayList for when ordering settings is changed
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

    //Create markers for restaurants
    private void setMarkers() {
        if (markers != null) {
            for (Marker m : markers) {
                m.remove();
            }
        }

        markers = new Marker[restaurants.size()];
        markerCorresponder = new HashMap<Marker, Restaurant>();

        ArrayList<Double> lat = new ArrayList<>();
        ArrayList<Double> lng = new ArrayList<>();
        builder = new LatLngBounds.Builder();
        if (myLatLng != null) {
            builder.include(myLatLng);
        }
        //Go through each of the restaurants and generate a marker
        for (int i = 0; i < markers.length; i++) {
            Restaurant restaurant = restaurants.get(i);
            LatLng latLng = generateLatLng(restaurant.getLatLng(), lat, lng);
            builder.include(latLng);
            markers[i] = googleMap.addMarker(new MarkerOptions().position(latLng).title(restaurant.getName()));
            markerCorresponder.put(markers[i], restaurant);
        }
        //Animation to move the screen
        if (markers.length > 0) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }
    }

    //"Hack" to offset lat and lng location of restaurants to minimze collisions of pins on map
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
                .findFragmentById(R.id.search_nearby_map);
        if (f != null) {
            getActivity().getFragmentManager().beginTransaction().remove(f).commit();
        }
    }

    public void onResume() {
        super.onResume();
        newLocListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 200, locListener);


    }

    public void onPause() {
        super.onPause();
        lm.removeUpdates(locListener);
    }


}


