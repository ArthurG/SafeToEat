package com.arthur_guo.SafeToEat.InfoAdapter;

import android.content.Context;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Inspection implements Serializable{
    //String[] allDetails;
    public  String inspectionId;
    //String FACILITYID;
    public Date inspectionDate;
    public String inspectionDateString;
    public String needReinspection;
    public String certifiedFoodHandler;
    public String inspectionType;
    String CHARGE_REVOCKED;
    String Actions;
    //String CHARGE_DATE;
    public  String details;
    public int critical;
    public int noncritical;
    public ArrayList<Infraction> infractions = null;
    public ArrayList<Infraction> criticalList = null;
    public ArrayList<Infraction> nonCriticalList = null;

    public Inspection(String inspectionId,String inspectionDateString,String needReinspection, int critical, int noncritical, String certifiedFoodHandler, String inspectionType){
        this.inspectionId = inspectionId;
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        this.inspectionDateString = inspectionDateString;
        this.needReinspection = needReinspection;
        try {
            inspectionDate = df.parse(inspectionDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.critical = critical;
        this.noncritical = noncritical;
        this.certifiedFoodHandler = certifiedFoodHandler;
        this.inspectionType = inspectionType;

    }

    public void setInfractions(Context context){
        this.nonCriticalList = new ArrayList<Infraction>();
        this.criticalList = new ArrayList<Infraction>();

        RestaurantDBAdapter dbAdapter = new RestaurantDBAdapter(context);
        try {
            dbAdapter.createDatabase();
            dbAdapter.open();
            this.infractions = dbAdapter.getInfractions(this.inspectionId);
            dbAdapter.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0,n = infractions.size();i<n;i++){
            Infraction infraction = infractions.get(i);
            if (infraction.getInfractionType().equals("CRITICAL")){
                criticalList.add(infraction);
            }else{
                nonCriticalList.add(infraction);
            }
        }
    }

    public boolean isBefore(Inspection inspection){
        return this.inspectionDate.before(inspection.inspectionDate);
    }




}
