package com.arthur_guo.SafeToEat;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arthur_guo.SafeToEat.InfoAdapter.Infraction;
import com.arthur_guo.SafeToEat.InfoAdapter.Inspection;
import com.arthur_guo.SafeToEat.InfoAdapter.Restaurant;
import com.arthur_guo.SafeToEat.R;


public class InspectionDisplay extends ActionBarActivity {

    Restaurant restaurant;
    Inspection inspection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_display);
        //Set Fields with intent
        Intent intent = getIntent();
        restaurant = (Restaurant) intent.getSerializableExtra("restaurant");
        inspection = (Inspection) intent.getSerializableExtra("inspection");
        inspection.setInfractions(this);

        //Set faculty details
        ((TextView) findViewById(R.id.restaurant_header_name)).setText(restaurant.getName());
        ((TextView) findViewById(R.id.restaurant_header_address)).setText(restaurant.getAddress());
        ((TextView) findViewById(R.id.restaurant_header_rating)).setText(Integer.toString(restaurant.getPercentile()));
        //Set inspection details
        ((TextView) findViewById(R.id.inspection_display_foodhandler)).setText(inspection.certifiedFoodHandler);
        ((TextView) findViewById(R.id.inspection_display_type)).setText(inspection.inspectionType);
        ((TextView) findViewById(R.id.inspection_display_date)).setText(inspection.inspectionDateString);
        //Set infraction details
        LinearLayout infractions = (LinearLayout) findViewById(R.id.inspection_display);
        for (int i = 0;i<inspection.criticalList.size();i++){
            Infraction infraction = inspection.criticalList.get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.infraction_card,null,false);
            ((TextView) view.findViewById(R.id.infraction_card_type)).setText("Critical infraction");
            ((TextView) view.findViewById(R.id.infraction_card_descrip)).setText(infraction.getDescription());
            ((TextView) view.findViewById(R.id.infraction_card_descrip_specifics)).setText(infraction.getCategory_code());
            infractions.addView(view);
        }
        for (int i = 0;i<inspection.nonCriticalList.size();i++){
            Infraction infraction = inspection.nonCriticalList.get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.infraction_card,null,false);
            ((TextView) view.findViewById(R.id.infraction_card_type)).setText("Non-critical infraction");
            ((TextView) view.findViewById(R.id.infraction_card_descrip)).setText(infraction.getDescription());
            ((TextView) view.findViewById(R.id.infraction_card_descrip_specifics)).setText(infraction.getCategory_code());
            infractions.addView(view);
        }
        /*
        LinearLayout critical = (LinearLayout) findViewById(R.id.inspection_display_critical);
        for (int i = 0;i<inspection.criticalList.size();i++){
            String inspectionInfo = "-"+ inspection.criticalList.get(i);
            TextView tv = new TextView(this);
            tv.setText(inspectionInfo);
            tv.setTextAppearance(this,android.R.style.TextAppearance_Medium);
            critical.addView(tv);
        }

        LinearLayout noncritical = (LinearLayout) findViewById(R.id.inspection_display_non_critical);
        for (int i = 0;i<inspection.nonCriticalList.size();i++){
            String inspectionInfo = "-"+inspection.nonCriticalList.get(i);
            TextView tv = new TextView(this);
            tv.setTextAppearance(this,android.R.style.TextAppearance_Medium);
            tv.setText(inspectionInfo);
            noncritical.addView(tv);
        }
        */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inspection_display, menu);
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
}
