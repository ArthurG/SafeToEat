package com.arthur_guo.SafeToEat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.arthur_guo.SafeToEat.InfoAdapter.Restaurant;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantHelper;
import com.arthur_guo.SafeToEat.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavouriteRestaurants.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavouriteRestaurants#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouriteRestaurants extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Restaurant> favRestaurantList;
    private ArrayAdapter<Restaurant> listRestaurantAdapter;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavouriteRestaurants.
     */
    // TODO: Rename and change types and number of parameters
    public static FavouriteRestaurants newInstance(String param1, String param2) {
        FavouriteRestaurants fragment = new FavouriteRestaurants();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FavouriteRestaurants() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Populate listview with the restaurants to start
        favRestaurantList = RestaurantHelper.getBookmarks(getActivity());

        //Add a ListView for possible restaurants
        LinearLayout outer = (LinearLayout) getActivity().findViewById(R.id.fav_restaurants_outerlayout);
        ListView listview = new ListView(getActivity());
        LinearLayout.LayoutParams lprams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        listview.setLayoutParams(lprams);
        listRestaurantAdapter = new RestaurantListAdapter(getActivity(), R.layout.restaurant_list_item,favRestaurantList);
        listview.setAdapter(listRestaurantAdapter);
        outer.addView(listview);

        //Handle ListView clicks
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FacultyDisplay.class);
                intent.putExtra("Faculty", listRestaurantAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite_restaurants, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        favRestaurantList.clear();
        favRestaurantList.addAll(RestaurantHelper.getBookmarks(getActivity()));
        listRestaurantAdapter.notifyDataSetChanged();
    }
}
