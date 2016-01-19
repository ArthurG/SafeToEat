package com.arthur_guo.SafeToEat.InfoAdapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

public class RestaurantDBAdapter{

    private SQLiteDatabase db;
    RestaurantDbHelper helper;

    public RestaurantDBAdapter(Context context){
        helper = new RestaurantDbHelper(context);
    }

    public ArrayList<Infraction> getInfractions(String inspectionId){
        ArrayList<Infraction> infractions = new ArrayList<Infraction>();
        //SELECT INFRACTIONTYPE, CATEGORYCODE, DESCRIPTION FROM infractions WHERE INSPECTIONID = ?;
        String[] columns = {helper.INFRACTIONTYPE, helper.CATEGORYCODE, helper.INFRAC_DESCRIPTION };
        String[] selectionArgs = {inspectionId};
        Cursor cursor = db.query(helper.TABLE_INFRACTIONS, columns, helper.INSPECTIONID+" = ?", selectionArgs, null, null, null, null);
        while (cursor.moveToNext()){
            String s1 = cursor.getString(cursor.getColumnIndex(RestaurantDbHelper.INFRACTIONTYPE));
            String s2 = cursor.getString(cursor.getColumnIndex(RestaurantDbHelper.CATEGORYCODE));
            String s3 = cursor.getString(cursor.getColumnIndex(RestaurantDbHelper.INFRAC_DESCRIPTION));
            infractions.add(new Infraction(s1,s2,s3));
        }
        cursor.close();
        return infractions;
    }

    public ArrayList<Restaurant> getRestaurants(){
        // public Restaurant(String facId, String name, String telephone, String address, String city, int critical, int nonCritical, double lat, double lng) {

        ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
        //SELECT INFRACTIONTYPE, CATEGORYCODE, DESCRIPTION FROM infractions WHERE INSPECTIONID = ?;
        String[] columns = {helper._FACULTIES_ID, helper.BUSINESS_NAME, helper.TELEPHONE,helper.ADDR,helper.CITY,helper.FAC_DESCRIPTION,helper.FAC_CRITICAL,helper.FAC_NONCRITICAL,helper.LAT,helper.LNG };
        Cursor cursor = db.query(helper.TABLE_FACULTIES, columns, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            String type = cursor.getString(cursor.getColumnIndex(helper.FAC_DESCRIPTION));
            if (!(type.equals("Food, General - Food Take Out") || type.equals("Food, General - Restaurant" ))){
                continue;
            }
            String s1 = cursor.getString(cursor.getColumnIndex(helper._FACULTIES_ID));
            String s2 = cursor.getString(cursor.getColumnIndex(helper.BUSINESS_NAME));
            String s3 = cursor.getString(cursor.getColumnIndex(helper.TELEPHONE));
            String s4 = cursor.getString(cursor.getColumnIndex(helper.ADDR));
            String s5 = cursor.getString(cursor.getColumnIndex(helper.CITY));
            int s6 = cursor.getInt(cursor.getColumnIndex(helper.FAC_CRITICAL));
            int s7 = cursor.getInt(cursor.getColumnIndex(helper.FAC_NONCRITICAL));
            double s8 = cursor.getDouble(cursor.getColumnIndex(helper.LAT));
            double s9 = cursor.getDouble(cursor.getColumnIndex(helper.LNG));
            restaurants.add(new Restaurant(s1,s2,s3,s4,s5,s6,s7,s8,s9));
        }
        cursor.close();
        return restaurants;
    }

    public ArrayList<Inspection> getInspections(String restaurantId){
        ArrayList<Inspection> inspections = new ArrayList<Inspection>();
        db = helper.getWritableDatabase();
        String[] columns = {helper.RELATEDINSPECTIONID, helper.INSPECTION_DATE, helper.REQUIRE_REINSPECTION,helper.INSPEC_CRITICAL,helper.INSPEC_NONCRITICAL,helper.CERTIFIED_FOOD_HANDLER,helper.INSPECTION_TYPE};
        String[] selectionArgs = {restaurantId};
        Cursor cursor = db.query(helper.TABLE_INSPECTIONS, columns, helper.FACULTIES_ID + "=?", selectionArgs, null, null, null, null);
        while (cursor.moveToNext()){
            String s1 = cursor.getString(cursor.getColumnIndex(helper.INSPECTIONID));
            String s2 = cursor.getString(cursor.getColumnIndex(helper.INSPECTION_DATE));
            String s3 = cursor.getString(cursor.getColumnIndex(helper.REQUIRE_REINSPECTION));
            int s4 = cursor.getInt(cursor.getColumnIndex(helper.INSPEC_CRITICAL));
            int s5 = cursor.getInt(cursor.getColumnIndex(helper.INSPEC_NONCRITICAL));
            String s6 = cursor.getString(cursor.getColumnIndex(helper.CERTIFIED_FOOD_HANDLER));
            String s7 = cursor.getString(cursor.getColumnIndex(helper.INSPECTION_TYPE));
            inspections.add(new Inspection(s1,s2,s3,s4,s5,s6,s7));
        }
        cursor.close();
        return inspections;
    }

    public RestaurantDBAdapter createDatabase() throws SQLException
    {
        try
        {
            helper.createDataBase();
        }
        catch (IOException mIOException)
        {
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public RestaurantDBAdapter open() throws SQLException
    {
        try
        {
            helper.openDataBase();
            helper.close();
            db = helper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        helper.close();
    }

    static class RestaurantDbHelper extends SQLiteOpenHelper{

        private static String DB_PATH = "/data/data/com.arthur_guo.restaurantsafety/databases/";
        private SQLiteDatabase db;
        private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
        private static final String DATABASE_NAME = "RestaurantDB.sqlite";
        private static final int DATABASEVERSION = 12;
        private static Context context;

        private static final String TABLE_INFRACTIONS = "infractions";
        private static final String _INFRACTION_ID = "infraction_id";
        private static final String RELATEDINSPECTIONID = "inspection_id";
        private static final String INFRACTIONTYPE = "infraction_type";
        private static final String CATEGORYCODE = "category_code";
        private static final String LETTERCODE = "letter_code";
        private static final String INFRAC_DESCRIPTION = "Description";
        private static final String INSPECTIONDATE = "InspectionDate";
        private static final String CHARGEDETAILS = "ChargeDetails";

        private static final String TABLE_FACULTIES = "faculties";
        private static final String _FACULTIES_ID = "_faculty_id";
        private static final String BUSINESS_NAME = "business_name";
        private static final String TELEPHONE = "telephone";
        private static final String ADDR = "addr";
        private static final String CITY = "city";
        private static final String EATSMART = "eatsmart";
        private static final String OPENDATE = "open_date";
        private static final String FAC_DESCRIPTION = "description";
        private static final String MOST_RECENT_INSPECTION = "most_recent_inspection";
        private static final String FAC_CRITICAL = "critical";
        private static final String FAC_NONCRITICAL = "noncritical";
        private static final String LAT = "lat";
        private static final String LNG = "lng";

        private static final String TABLE_INSPECTIONS = "inspections";
        private static final String INSPECTIONID = "inspection_id";
        private static final String FACULTIES_ID = "faculties_id";
        private static final String INSPECTION_DATE = "inspection_date";
        private static final String REQUIRE_REINSPECTION = "requires_reinspection";
        private static final String CERTIFIED_FOOD_HANDLER = "certified_food_handler";
        private static final String INSPECTION_TYPE = "inspection_type";
        private static final String CHARGE_REVOCKED = "charge_revoked";
        private static final String Actions = "actions";
        private static final String CHARGE_DATE = "charge_date";
        private static final String INSPEC_CRITICAL = "critical";
        private static final String INSPEC_NONCRITICAL = "non_critical";

        RestaurantDbHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASEVERSION);
            this.context = context;
            if(android.os.Build.VERSION.SDK_INT >= 17){
                DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
            }
            else
            {
                DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
            }
        }
        public void createDataBase() throws IOException
        {
            //If database not exists copy it from the assets

            boolean mDataBaseExist = checkDataBase();
            if(!mDataBaseExist)
            {
                this.getReadableDatabase();
                this.close();
                try
                {
                    //Copy the database from assests
                    copyDataBase();
                    //Log.e(TAG, "createDatabase database created");
                }
                catch (IOException mIOException)
                {
                    throw new Error("ErrorCopyingDataBase");
                }
            }
        }
        //Check that the database exists here: /data/data/your package/databases/Da Name
        private boolean checkDataBase()
        {
            File dbFile = new File(DB_PATH + DATABASE_NAME);
            return dbFile.exists();
        }
        //Copy the database from assets
        private void copyDataBase() throws IOException
        {
            InputStream mInput = context.getAssets().open(DATABASE_NAME);
            String outFileName = DB_PATH + DATABASE_NAME;
            OutputStream mOutput = new FileOutputStream(outFileName);
            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = mInput.read(mBuffer))>0)
            {
                mOutput.write(mBuffer, 0, mLength);
            }
            mOutput.flush();
            mOutput.close();
            mInput.close();
        }
        //Open the database, so we can query it
        public boolean openDataBase() throws SQLException
        {
            String mPath = DB_PATH + DATABASE_NAME;
            db = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            return db != null;
        }

        @Override
        public synchronized void close()
        {
            if(db != null)
                db.close();
            super.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
