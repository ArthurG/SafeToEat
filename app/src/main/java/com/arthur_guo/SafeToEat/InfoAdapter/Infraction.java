package com.arthur_guo.SafeToEat.InfoAdapter;

/**
 * Created by Arthur on 7/18/2015.
 */
public class Infraction {

    //String ChargeDetails;
    //String InspectionDate;
    //String INFRACTION_ID;
    //String inspectionId;
    private String infractionType;
    private String category_code;
    private String descriptionDetailed;

    public Infraction(String infractionType,String category_code,String descriptionDetailed){
        this.infractionType = infractionType;
        this.category_code = category_code;
        this.descriptionDetailed = descriptionDetailed;
    }

    public String getInfractionType() {
        return infractionType;
    }
    public String getCategory_code() {
        return category_code;
    }
    public String getDescription() {
        return descriptionDetailed;
    }


}
