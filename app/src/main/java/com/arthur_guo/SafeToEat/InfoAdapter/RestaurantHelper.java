package com.arthur_guo.SafeToEat.InfoAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantComparator.HighestInfractionScoreComparator;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Arthur on 7/19/2015.
 */
public class RestaurantHelper {


    public static ArrayList<Restaurant> allRestaurants;
    private static boolean initialized = false;
    public static ArrayList<String> bookmarkList;



    public static void initialize(ArrayList<Restaurant> a, Activity activity) {

            initialized = true;
            RestaurantHelper.allRestaurants = a;
            setPercentiles(activity);

    }

    //Add a restaurant to bookmarks
    public static void addBookmark(Activity activity, Restaurant restaurant){
        getBookmarkStrings(activity);
        bookmarkList.add(restaurant.getId());
        saveBookmarks(activity);
    }


    //Removes a restaurant from bookmarks
    public static void removeBookmark(Activity activity, Restaurant restaurant){
        getBookmarkStrings(activity);
        bookmarkList.remove(restaurant.getId());
        saveBookmarks(activity);

    }

    //Returns all the bookmarked restaurants
    public static ArrayList<Restaurant> getBookmarks(Activity activity){
        getBookmarkStrings(activity);
        ArrayList<Restaurant> bookmarkRestaurants = new ArrayList<Restaurant>();
        for (Restaurant restaurant:allRestaurants){
            if(bookmarkList.contains(restaurant.getId())){
                bookmarkRestaurants.add(restaurant);
            }
        }
        return bookmarkRestaurants;
    }


    //Returns true if restaurant is bookmarked
    public static boolean isBookmarked(Activity activity, Restaurant restaurant){
        getBookmarkStrings(activity);
        return bookmarkList.contains(restaurant.getId());
    }

    //Updates the database by saving bookmarkList
    private static void saveBookmarks(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < bookmarkList.size(); i++) {
            a.put(bookmarkList.get(i));
        }
        if (!bookmarkList.isEmpty()) {
            editor.putString("bookmarkList", a.toString());
        } else {
            editor.putString("bookmarkList", null);
        }
        editor.commit();
    }

    //Queries the database for bookmarked restaurants
    private static void getBookmarkStrings(Context context) {
        if (bookmarkList != null){
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString("bookmarkList", null);
        bookmarkList = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    bookmarkList.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //takes restaurants and only returns the ones that contain text
    public static ArrayList<Restaurant> filterByString(ArrayList<Restaurant> restaurants, String text) {
        ArrayList<Restaurant> ret = new ArrayList<Restaurant>();
        for (Restaurant rest : restaurants) {
            if (rest.getName().toLowerCase().replace("'","").contains(text.toLowerCase()) || rest.getAddress().toLowerCase().contains(text.toLowerCase())) {
                ret.add(rest);
            }
        }
        return ret;

    }

    public static ArrayList<Restaurant> sortRestaurants(ArrayList<Restaurant> in, Comparator comparator) {
        //TODO FIISH FUNCTION
        Restaurant[] restaurants = sortRestaurants(in.toArray(new Restaurant[in.size()]), comparator);
        ArrayList<Restaurant> out = new ArrayList<Restaurant>();
        Collections.addAll(out, restaurants);
        return out;
    }

    public static ArrayList<Restaurant> sortRestaurants(Comparator comparator) {
        //TODO FIISH FUNCTION
        Restaurant[] restaurants = sortRestaurants(allRestaurants.toArray(new Restaurant[allRestaurants.size()]), comparator);
        ArrayList<Restaurant> sortedRestaurants = new ArrayList<Restaurant>();
        Collections.addAll(sortedRestaurants, restaurants);
        return sortedRestaurants;
    }

    private static Restaurant[] sortRestaurants(Restaurant[] in, Comparator comparator) {
        //Base case
        if (in.length <= 1) {
            return in;
        } else {
            Restaurant[] result = new Restaurant[in.length];
            //Split it in half
            int middle = in.length / 2;
            int firstLength = middle;
            int secondLength = in.length - middle;
            Restaurant[] leftHalf = new Restaurant[firstLength];
            Restaurant[] rightHalf = new Restaurant[secondLength];
            System.arraycopy(in, 0, leftHalf, 0, firstLength);
            System.arraycopy(in, middle, rightHalf, 0, secondLength);
            //Sort both halfs
            Restaurant[] sortedLeft = sortRestaurants(leftHalf, comparator);
            Restaurant[] sortedRight = sortRestaurants(rightHalf, comparator);
            //Merge both halfs
            int leftPointer = 0;
            int rightPointer = 0;
            int resultPointer = 0;
            while (leftPointer < firstLength && rightPointer < secondLength) {
                Restaurant res;
                if (comparator.compare(sortedLeft[leftPointer], sortedRight[rightPointer]) < 1) {
                    res = sortedLeft[leftPointer];
                    leftPointer++;
                } else {
                    res = sortedRight[rightPointer];
                    rightPointer++;
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

    public static ArrayList<Restaurant> getNearby(double lat, double lng, double metres) {
        ArrayList<Restaurant> ret = new ArrayList<Restaurant>();
        for (Restaurant rest : RestaurantHelper.allRestaurants) {
            LatLng latlng = rest.getLatLng();
            //Log.d("Coordinates",lat+" " + lng + " " + latlng.latitude + " " +latlng.longitude);
            if (rest.isNearby(lat, lng, metres)) {
                ret.add(rest);
            }
        }
        return ret;
    }

    public static void setPercentiles(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        int critWeight = Integer.parseInt(prefs.getString("crit_weight", "-11111"));
        int marginError = Integer.parseInt(prefs.getString("margin_error", "-11111").replaceAll("\\D", ""));
        for (int i = 0; i < allRestaurants.size(); i++) {
            allRestaurants.get(i).calculateInfractionScore(critWeight, marginError);
        }
        ArrayList<Restaurant> sortedRestaurant = RestaurantHelper.sortRestaurants(allRestaurants, new HighestInfractionScoreComparator());
        double previousInfraction = 9999999;
        int previousPercentile = 0;
        double totalRes = (double) sortedRestaurant.size();
        for (int i = 0, n = sortedRestaurant.size() - 1; i < n; i++) {
            Restaurant res = sortedRestaurant.get(i);
            if (res.getInfractionScore() == previousInfraction) {
                res.setPercentile(previousPercentile);
            } else {
                previousInfraction = res.getInfractionScore();
                previousPercentile = (int) ((int) (i) * 100 / (totalRes));
                res.setPercentile(previousPercentile);
            }
        }

        double adjust = 10 / (double) previousPercentile;
        for (int i = 0, n = sortedRestaurant.size(); i < n; i++) {
            sortedRestaurant.get(i).setPercentile((int) (sortedRestaurant.get(i).getPercentile() * adjust));
        }

    }

    public static boolean needInit(){
        return !RestaurantHelper.initialized;
    }

}
