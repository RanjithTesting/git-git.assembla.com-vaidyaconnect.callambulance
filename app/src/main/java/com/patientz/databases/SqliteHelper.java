package com.patientz.databases;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import com.patientz.VO.PatientUserVO;
import com.patientz.activity.R;
import com.patientz.services.CallAmbulanceSyncService;
import com.patientz.utils.Log;
import com.patientz.utils.SyncUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SqliteHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "callambulance.sqlite";
    private static String OLD_DB_NAME = "patientz.db";
    private static int DB_VERSION = 27;
    private final Context myContext;
    private SQLiteDatabase mydatabase;
    private final String TAG = "SqliteHelper";
    DatabaseHandlerAssetHelper mDatabaseHandlerAssetHelper1;

    public SqliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        Log.i(TAG, "SqliteHelper() class called.............");
    }


    public void createDatabase(SQLiteDatabase db) {
        Log.i(TAG, "createDatabase().............");
        InputStream input = null;
        InputStreamReader reader = null;
        BufferedReader buffer = null;
        StringBuffer strBuffer = new StringBuffer(2000);
        String query = null;
        int intCh;
        char ch;
        try {
            input = myContext.getAssets().open("callambulance.sqlite");
            reader = new InputStreamReader(input);
            buffer = new BufferedReader(reader);
            while ((intCh = buffer.read()) > -1) {
                ch = (char) intCh;
                strBuffer.append(ch);
                if (';' == ch) {
                    query = strBuffer.toString();
                    Log.i(TAG, "................" + strBuffer.length());
                    executeQuery(db, query);
                    Log.i("", query);
                    strBuffer = new StringBuffer(2000);
                }
            }
            reader.close();
            input.close();
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void executeQuery(SQLiteDatabase db, String query) {
        db.execSQL(query);

    }

    public synchronized void close() {
        if (mydatabase != null)
            mydatabase.close();
        super.close();
    }

    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate().............");
        createDatabase(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // use while loop or for loop for all db updates
        Log.i(TAG, "onUpgrade()............." + oldVersion);
        mDatabaseHandlerAssetHelper1 = DatabaseHandlerAssetHelper.dbInit(myContext);
        switch (oldVersion) {
            case 1:
                Log.i("DatabaseHelper", "City of Practice Upgrade od ver="
                        + oldVersion + " new version" + newVersion);
                executeQuery(db,
                        "CREATE TABLE IF NOT EXISTS device_tbl (_id  integer PRIMARY KEY autoincrement,device_id varchar(255) NOT NULL unique,mobile_no varchar(255),sim_serial_no varchar(255));");
                Log.i("DatabaseHelper", "Successful upgrade");
                break;
            case 2:
                db.execSQL("ALTER TABLE user_tbl ADD COLUMN emergency_token TEXT");
                break;
            case 3:
                executeQuery(db, "CREATE TABLE IF NOT EXISTS emergency_help_provider_tbl(_id  integer PRIMARY KEY autoincrement,name TEXT,phone INTEGER,lat REAL,lon REAL,logo_url TEXT);");
                break;
            case 4:
                executeQuery(db, "CREATE TABLE IF NOT EXISTS preferred_ambulance_providers_tbl(_id  integer PRIMARY KEY autoincrement,emergency_org_setting_id REAL NOT NULL unique,org_id INTEGER,name TEXT,phone INTEGER,lat TEXT,lon TEXT);");
                db.execSQL("ALTER TABLE emergency_help_provider_tbl ADD COLUMN location TEXT");
                db.execSQL("ALTER TABLE user_tbl ADD COLUMN preffered_ambulanc_provider_pno TEXT");
                break;
            case 5:
                db.execSQL("ALTER TABLE preferred_ambulance_providers_tbl ADD COLUMN location_name TEXT DEFAULT NULL");
                break;
            case 6:
                db.execSQL("ALTER TABLE preferred_ambulance_providers_tbl ADD COLUMN org_logo_id INTEGER");
                db.execSQL("DROP TABLE IF EXISTS emergency_help_provider_tbl");
                break;
            case 7:
                executeQuery(db, "CREATE TABLE IF NOT EXISTS health_record_tbl(_id  integer PRIMARY KEY autoincrement,patient_id REAL NOT NULL unique,attended_by_id REAL,doctor_id REAL,org_id REAL,org_name TEXT,attended_by_display_name TEXT,role TEXT,role_description TEXT,record_type TEXT,start_date datetime,end_date datetime,visit_record_id REAL,last_updated_user_role_id REAL,last_updated_user_display_name TEXT,un_reg_doctor_name TEXT,un_reg_org_name TEXT,health_record_map_key TEXT,health_record_map_value TEXT,record_id REAL,attachment_id REAL);");
                executeQuery(db, "CREATE TABLE IF NOT EXISTS user_complete_details_tbl(_id  integer PRIMARY KEY autoincrement,patient_id REAL NOT NULL unique,user_profile_id REAL,first_name TEXT,last_name TEXT,date_of_birth datetime,gender INTEGER);");
                db.execSQL("ALTER TABLE emergency_tbl ADD has_got_emergency_location bit(1) DEFAULT false,has_notified_emri bit(1) DEFAULT false,has_notified_contacts bit(1) DEFAULT false,has_called_ambulance bit(1) DEFAULT false);");
                executeQuery(db, "CREATE TABLE IF NOT EXISTS insurance_info_tbl(id  integer PRIMARY KEY autoincrement,insurance_id bigint(20) NOT NULL unique,ins_policy_company TEXT,ins_policy_coverage TEXT,ins_policy_name TEXT,ins_policy_number TEXT,patient_id REAL,ins_policy_start_date REAL,ins_policy_end_date REAL,ins_policy_claim_number TEXT,date_created REAL,date_updated REAL,created_by REAL,updated_by REAL);");
                executeQuery(db, "CREATE TABLE IF NOT EXISTS user_info_tbl (_id  integer PRIMARY KEY autoincrement,patient_id bigint(20) NOT NULL unique,first_name TEXT,last_name TEXT,role TEXT,relationship TEXT,address TEXT,company_name TEXT,city TEXT,state TEXT,country TEXT,pin_code TEXT,phone_number TEXT,phone_number_isd_code TEXT,alt_phone_number TEXT,alt_phoneNumber_isd_code TEXT,gender REAL,age REAL,email_id TEXT,pic_file_id TEXT,pic_file_name TEXT,emergency_level REAL,financial_status TEXT,family_history TEXT,habits TEXT,remarks TEXT,blood_donation TEXT,organ_donation TEXT,marital_status TEXT,food_habits TEXT,date_of_birth REAL,created_by REAL,updated_by REAL,created_date REAL,updated_date REAL);");
                executeQuery(db, "CREATE TABLE IF NOT EXISTS user_org_table (\n" +
                        " \t_id  integer PRIMARY KEY autoincrement,\n" +
                        " \torg_id REAL NOT NULL unique,\n" +
                        " \tpatient_id REAL,\n" +
                        " \temail TEXT,\n" +
                        "    org_logo_id REAL,\n" +
                        "    org_name TEXT,\n" +
                        "    org_type TEXT,\n" +
                        "    phone TEXT,\n" +
                        "    status TEXT\n" +
                        ");");
                deleteOldAccount(myContext);
                deleteOldFilesAndDatabase(myContext);
                onCreate(db);
                break;
            case 8:
                db.execSQL("ALTER TABLE user_info_tbl ADD COLUMN preffered_ambulanc_provider_pno TEXT DEFAULT NULL");
                db.execSQL("ALTER TABLE contact_tbl ADD COLUMN patient_access_id REAL");
                db.execSQL("ALTER TABLE contact_tbl ADD COLUMN pic_id TEXT DEFAULT NULL");
                Intent iService = new Intent(myContext, CallAmbulanceSyncService.class);
                myContext.startService(iService);
                break;
            case 9:
                DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(myContext);
                databaseHandlerAssetHelper.deleteDatabaseAndClearStaticVariables(myContext);
                break;
            case 10:
                executeQuery(db, "CREATE TABLE IF NOT EXISTS blood_friends_tbl (_id  integer PRIMARY KEY autoincrement,  contact_id bigint(20) NOT NULL, lookup_id TEXT, phone_number TEXT NOT NULL,  blood_group TEXT,  server_name TEXT, phone_name TEXT,  contact_type integer,  update_status integer,  update_time datetime,  download_time datetime, unique(contact_id,phone_number));");
                Log.i("DatabaseHelper", "Successful upgrade");
                db.execSQL("ALTER TABLE health_record_tbl ADD COLUMN last_updated_date TEXT DEFAULT NULL");
                db.execSQL("ALTER TABLE user_info_tbl ADD COLUMN blood_group TEXT DEFAULT NULL");
                myContext.startService(new Intent(myContext, CallAmbulanceSyncService.class));
                //break;
            case 11:
                db.execSQL("ALTER TABLE preferred_ambulance_providers_tbl ADD COLUMN street_address TEXT DEFAULT NULL");
                db.execSQL("ALTER TABLE preferred_ambulance_providers_tbl ADD COLUMN branch_name TEXT DEFAULT NULL");
                //  myContext.startService(new Intent(myContext, CallAmbulanceSyncService.class));
                //break;
            case 12:
                db.execSQL("CREATE TABLE IF NOT EXISTS emergency_numbers_tbl (_id  integer PRIMARY KEY autoincrement,sno REAL NOT NULL unique,country TEXT,country_short_name TEXT,state_short_name TEXT,police_no TEXT,ambulance_no TEXT,fire_no TEXT);");
                db.execSQL("ALTER TABLE user_info_tbl ADD COLUMN preferred_org_branch_id REAL");
                db.execSQL("DROP TABLE IF EXISTS preferred_ambulance_providers_tbl");
                myContext.deleteDatabase("hospital.db");
//                myContext.startService(new Intent(myContext, CallAmbulanceSyncService.class));
                //db.execSQL("CREATE TABLE IF NOT EXISTS org_branch_tbl (_id  integer PRIMARY KEY autoincrement,org_branch_id REAL unique,org_id REAL,org_name TEXT,org_type TEXT,branch_name TEXT,address_id REAL,street_address TEXT,additional_address TEXT,city TEXT,state TEXT,postal_code TEXT,zone TEXT,district TEXT,country TEXT,longitude TEXT,latitude TEXT,map_url TEXT,display_name TEXT,emergency_phone_number TEXT,is_network_branch NUMERIC);");
            case 13:
                myContext.deleteDatabase("org_branches.db");
//                myContext.startService(new Intent(myContext, CallAmbulanceSyncService.class));
                // break;
            case 14:
                db.execSQL("ALTER TABLE user_info_tbl ADD COLUMN patient_handle TEXT");
            case 15:
                db.execSQL("ALTER TABLE user_info_tbl ADD COLUMN last_blood_donation_date INTEGER");
                db.execSQL("ALTER TABLE user_info_tbl ADD COLUMN preferred_blood_bank_id INTEGER");
                db.execSQL("ALTER TABLE user_info_tbl ADD COLUMN notify_blood_donation_request bit(1)");
            case 16:
                db.execSQL("ALTER TABLE user_tbl ADD COLUMN share_points INTEGER");
            case 17:
                db.execSQL("ALTER TABLE contact_tbl ADD COLUMN profile_pic_id INTEGER DEFAULT NULL");
                db.execSQL("ALTER TABLE contact_tbl ADD COLUMN specialities TEXT");
                // myContext.startService(new Intent(myContext, CallAmbulanceSyncService.class));
            case 18:
                myContext.deleteDatabase("org_branches.db");
                Intent syncService = new Intent(myContext, CallAmbulanceSyncService.class);
                myContext.startService(syncService);
                //break;
            case 19:
                Log.e("19 data->", "start");
                db.execSQL("ALTER TABLE blood_friends_tbl ADD COLUMN user_invited integer");
                Log.e("19 data->", "end");
            case 20:
                myContext.deleteDatabase("org_branches.db");
                Intent syncServicee = new Intent(myContext, CallAmbulanceSyncService.class);
                myContext.startService(syncServicee);
            case 21:
                myContext.deleteDatabase("org_branches.db");
                Intent sservice = new Intent(myContext, CallAmbulanceSyncService.class);
                myContext.startService(sservice);
            case 22:
                db.execSQL("ALTER TABLE emergency_tbl ADD COLUMN emergency_status TEXT");
            case 23:
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            readEmergencyNumbersFromCSVFile(myContext);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            case 24:
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN is_email_verified INTEGER");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN is_aadhar_verified INTEGER");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN is_mobile_number_verified INTEGER");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN policy_doc TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN sent_date datetime");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN status INTEGER");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN customer_id TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN pincode TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN city TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN address2 TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN address1 TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN mobile_number TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN email TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN aadhar_no TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN date_of_birth TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN nominee_name TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN nominee_relationship TEXT");

                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN gender INTEGER");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN district TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN last_name TEXT");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN insurance_upload_id REAL");

                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN first_name INTEGER");
                db.execSQL("ALTER TABLE insurance_info_tbl ADD COLUMN paytm_ref_id TEXT");
            case 25:
                executeQuery(db, "CREATE TABLE IF NOT EXISTS ambulance_details_tbl (\n" +
                        "     \t_id  integer PRIMARY KEY autoincrement,\n" +
                        "     \tambulance_id INTEGER NOT NULL unique,\n" +
                        "     \tlicense_plate_no TEXT,\n" +
                        "        min_fare REAL,\n" +
                        "        price_rate REAL,\n" +
                        "        ride_time_rate REAL,\n" +
                        "        org_name TEXT,\n" +
                        "        category TEXT,\n" +
                        "        amb_manager_phone_no TEXT,\n" +
                        "        oxygen INTEGER,\n" +
                        "        wheel_chair INTEGER,\n" +
                        "        stretcher INTEGER,\n" +
                        "        freezer_box INTEGER,\n" +
                        "        ventilator INTEGER,\n" +
                        "        air_conditioner INTEGER,\n" +
                        "        emt_availability INTEGER,\n" +
                        "        AED INTEGER,\n" +
                        "        seating_capacity_number INTEGER,\n" +
                        "        make_and_model TEXT,\n" +
                        "        year_of_manufacture TEXT,\n" +
                        "        status INTEGER,\n" +
                        "        ambulance_status INTEGER,\n" +
                        "        distance REAL,\n" +
                        "        latitude TEXT,\n" +
                        "        longitude TEXT\n" +
                        "        );");
            case 26:
                executeQuery(db, "CREATE TABLE IF NOT EXISTS specialities_tbl  (\n" +
                        "        \t_id  integer PRIMARY KEY autoincrement,\n" +
                        "        \tspeciality_id INTEGER,\n" +
                        "           code TEXT,\n" +
                        "           display_name TEXT,\n" +
                        "           notes TEXT,\n" +
                        "           date_created datetime,\n" +
                        "           last_updated datetime,\n" +
                        "           unique(code,speciality_id)\n" +
                        "       );");

                executeQuery(db, "CREATE TABLE IF NOT EXISTS facilities_tbl  (\n" +
                        "        \t_id  integer PRIMARY KEY autoincrement,\n" +
                        "        \tfacility_id INTEGER,\n" +
                        "           code TEXT,\n" +
                        "           display_name TEXT,\n" +
                        "           notes TEXT,\n" +
                        "           date_created datetime,\n" +
                        "           last_updated datetime,\n" +
                        "           unique(code,facility_id)\n" +
                        "       );");

                executeQuery(db, "CREATE TABLE IF NOT EXISTS availability_capability_tbl  (\n" +
                        "         \t_id  integer PRIMARY KEY autoincrement,\n" +
                        "         \torg_id INTEGER,\n" +
                        "         \torg_type TEXT,\n" +
                        "         \torg_branch_id INTEGER,\n" +
                        "         \tflag INTEGER,\n" +
                        "         \tspeciality_id INTEGER,\n" +
                        "         \tfacility_id INTEGER,\n" +
                        "         \tprofile_id INTEGER,\n" +
                        "         \tday_of_week TEXT,\n" +
                        "         \tis_uploaded INTEGER DEFAULT 0,\n" +
                        "         \tstart_time TEXT,\n" +
                        "         \tend_time TEXT,\n" +
                        "         \tstart_time_local INTEGER,\n" +
                        "         \tend_time_local INTEGER,\n" +
                        "         \tcertification_id INTEGER,\n" +
                        "         \tinsurance_company_id INTEGER,\n" +
                        "         \tavailability_status INTEGER,\n" +
                        "         \tdiagnostic_tests_id INTEGER,\n" +
                        "         \tlast_updated datetime\n" +
                        "        );");
                Intent s = new Intent(myContext, CallAmbulanceSyncService.class);
                myContext.startService(s);
                break;
            default:
                break;
        }

    }

    private void readEmergencyNumbersFromCSVFile(Context myContext) throws IOException {
        InputStream inputStream = myContext.getAssets().open("Emergency_numbers.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(myContext);
        try {
            mDatabaseHandler.insertEmergencyNumbers(reader);
        } catch (Exception ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }

    }

    private void deleteOldFilesAndDatabase(Context context) {
        DatabaseHandler dh = DatabaseHandler.dbInit(context);
        try {
            ArrayList<PatientUserVO> list = dh.getAllUser();
            Log.d(TAG, "GOT ALL PATIENTS");
            for (PatientUserVO patientUserVO : list) {
                SyncUtil.removeFile(context, patientUserVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        myContext.deleteDatabase(OLD_DB_NAME);
        dh.deleteDatabaseAndClearStaticVariables(context);
        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(context);
        databaseHandlerAssetHelper.deleteDatabaseAndClearStaticVariables(context);
        context.getSharedPreferences("Patientz_pref", Context.MODE_PRIVATE).edit().clear().commit();
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().clear().commit();
    }

    public static void deleteOldAccount(Context context) {
        AccountManager mAccountManager = AccountManager.get(context);
        Handler mHandler = new Handler(Looper.getMainLooper());
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Account[] accounts = mAccountManager.getAccountsByType(context
                .getString(R.string.package_name));
        if (accounts.length != 0)
            mAccountManager.removeAccount(accounts[0], null, mHandler);
    }
}
