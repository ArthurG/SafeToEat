package com.arthur_guo.SafeToEat;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arthur_guo.SafeToEat.InfoAdapter.Inspection;
import com.arthur_guo.SafeToEat.InfoAdapter.Restaurant;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantDBAdapter;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantHelper;
import com.arthur_guo.SafeToEat.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.util.ArrayList;


public class FacultyDisplay extends ActionBarActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap googleMap;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_display);

        //Get restaurant object
        Intent intent = getIntent();
        restaurant = (Restaurant) intent.getSerializableExtra("Faculty");

        //Set the details of the faculty for top bar
        ((TextView) findViewById(R.id.restaurant_header_name)).setText(restaurant.getName());
        ((TextView) findViewById(R.id.restaurant_header_address)).setText(restaurant.getAddress());
        ((TextView) findViewById(R.id.restaurant_header_rating)).setText(Integer.toString(restaurant.getPercentile()));

        //Create map at bottom of restaurant screen
        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mf.getMapAsync(this);

        //Get the inspectison from the database
        ArrayList<Inspection> inspections = new ArrayList<Inspection>();
        RestaurantDBAdapter dbAdapter = new RestaurantDBAdapter(getApplicationContext());
        try {
            dbAdapter.createDatabase();
            dbAdapter.open();
            inspections = dbAdapter.getInspections(restaurant.getId());
            dbAdapter.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Sort inspections
        restaurant.setInspections(this);

        LinearLayout infractionTable = (LinearLayout) findViewById(R.id.faculty_infractions_list);
        //Display each of the inspections
        for (int i = 0; i < restaurant.inspections.size(); i++) {

            final Inspection inspection = restaurant.inspections.get(i);
            String[] inspectionColumns = new String[]{inspection.inspectionDateString, Integer.toString(inspection.critical), Integer.toString(inspection.noncritical)};
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflated = inflater.inflate(R.layout.inspection_card, null, false);
            TextView dateText= (TextView) inflated.findViewById(R.id.faculty_inspection_date);
            dateText.setText(inspection.inspectionDateString);
            if (i == 0){
                dateText.setTextSize(24);
            }
            TextView critText= (TextView) inflated.findViewById(R.id.faculty_inspection_crit);
            critText.setText(Integer.toString(inspection.critical));
            TextView noncritText= (TextView) inflated.findViewById(R.id.faculty_inspection_noncrit);
            noncritText.setText(Integer.toString(inspection.noncritical));
            inflated.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), InspectionDisplay.class);
                    intent.putExtra("restaurant", restaurant);
                    intent.putExtra("inspection", inspection);
                    startActivity(intent);
                }
            });
            infractionTable.addView(inflated);

        }
        //Display the bookmark button
        displayRightBookmarkImage();

        //Set the imagebuttons for calling, navigating, searching, and bookmarking
        ImageButton callButton = (ImageButton) findViewById(R.id.faculty_call);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),restaurant.getTelephone(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + restaurant.getTelephone()));
                startActivity(intent);
            }
        });
        ImageButton navigateButton = (ImageButton) findViewById(R.id.faculty_navigate);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=" + restaurant.getAddress() + "+" + restaurant.getCity() + " Ontario (+"+restaurant.getName().replace(" ", "+")+"+)"));
                startActivity(intent);
            }
        });
        ImageButton searchButton = (ImageButton) findViewById(R.id.faculty_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, restaurant.getName() + " " + restaurant.getAddress() + " " + restaurant.getCity());
                startActivity(intent);

            }
        });

        ImageButton bookmarkButton = (ImageButton) findViewById(R.id.faculty_bookmark);
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RestaurantHelper.isBookmarked(FacultyDisplay.this,restaurant)){
                    RestaurantHelper.removeBookmark(FacultyDisplay.this, restaurant);
                    Toast.makeText(getApplicationContext(),"Bookmark Deleted!",Toast.LENGTH_SHORT).show();
                    displayRightBookmarkImage();
                }else{
                    RestaurantHelper.addBookmark(FacultyDisplay.this,restaurant);
                    Toast.makeText(getApplicationContext(),"Bookmark successful!",Toast.LENGTH_SHORT).show();
                    displayRightBookmarkImage();
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_faculty_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onMapLoaded() {
        Geocoder coder = new Geocoder(this);
        LatLng restaurantLatLng = null;
        LatLng myLatLng = null;
            /*
            ArrayList<Address> addresses = (ArrayList<Address>) coder.getFromLocationName(restaurant.getAddress() + " " + restaurant.getCity(),2);
            restaurantLatLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());*/
        restaurantLatLng = restaurant.getLatLng();
        MarkerOptions mrk = new MarkerOptions()
                .position(restaurantLatLng)
                .title(restaurant.getName());
        googleMap.addMarker(mrk);
        CameraUpdate cf = CameraUpdateFactory.newLatLngZoom(restaurantLatLng, 16);
        googleMap.moveCamera(cf);
        //Find out user's current location and display on map
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        //Drop a pin at user's current location and move camera so both faculty and currLocation are visible
        if (loc != null) {
            myLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(myLatLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            if (restaurantLatLng != null) {
                CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().include(restaurantLatLng).include(myLatLng).build(), 100);
                googleMap.animateCamera(camUpdate);
            }
        }

        //Handling markers, don't camera marker if marker represents the user's location
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle().equals("Current Location")) {
                    marker.showInfoWindow();
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapLoadedCallback(this);
    }

    private void displayRightBookmarkImage(){
        ImageButton bookmarkButton2 = (ImageButton) findViewById(R.id.faculty_bookmark);
        if (!RestaurantHelper.isBookmarked(FacultyDisplay.this,restaurant)){
            bookmarkButton2.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
        }else{
            bookmarkButton2.setImageResource(R.drawable.ic_bookmark_black_36dp);
        }
    }


}
