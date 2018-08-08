package com.patientz.databases;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.patientz.VO.AddressVO;
import com.patientz.VO.CountryVO;
import com.patientz.VO.HospitalVO;
import com.patientz.VO.OrgBranchVO;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static java.lang.Double.parseDouble;

/**
 * Created by sunil on 14/12/15.
 */
public class DatabaseHandlerAssetHelper {
    private static final String TAG = "DatabaseHandlerAssetHelper";
    private Context context;
    private static SQLiteDatabase assetHelperDb;
    private MySqliteAssetHelper sqlHelper;
    public static int assetHelperOpenConnections = 0;

    public DatabaseHandlerAssetHelper(Context context) {
        this.context = context;
    }

    private static DatabaseHandlerAssetHelper dbHandler;

    public static DatabaseHandlerAssetHelper dbInit(Context context) {
        if (dbHandler == null) {
            // Log.d("DB tag...", "New connection");
            dbHandler = new DatabaseHandlerAssetHelper(context);
            return dbHandler;
        } else {
            return dbHandler;
        }
    }

    public DatabaseHandlerAssetHelper openDatabase() throws SQLException {
        assetHelperOpenConnections++;
        Log.d(TAG, "openDatabase>>" + assetHelperOpenConnections);
        if (sqlHelper == null) {
            Log.d(TAG, "INITIALISING SQLITE HELPER");
            sqlHelper = new MySqliteAssetHelper(context);
        }
        if (assetHelperDb == null || (!assetHelperDb.isOpen())) {
            Log.d(TAG, "assetHelperDb IS NULL / NOT OPEN");
            assetHelperDb = sqlHelper.getReadableDatabase();
            Log.d("assethelper", "assetHelperDb object created");
        }
        return this;
    }

    public void closeDatabase() {
        assetHelperOpenConnections--;
        Log.d(TAG, "closeDatabase()>>" + assetHelperOpenConnections);
        if (assetHelperOpenConnections == 0) {
            sqlHelper.close();
        }
    }

    public DatabaseHandlerAssetHelper openDatabaseAndBeginTransaction()
            throws SQLException {
        Log.d(TAG, "openDatabaseAndBeginTransaction");

        assetHelperOpenConnections++;
        if (sqlHelper == null) {
            Log.d(TAG, "INITIATING SQL HELPER");
            sqlHelper = new MySqliteAssetHelper(context);
        }
        if (assetHelperDb == null || (!assetHelperDb.isOpen() || assetHelperDb.isReadOnly())) {
            Log.d(TAG, "GETTING DB TYPE WRITEABLE");
            assetHelperDb = sqlHelper.getWritableDatabase();
        }
        assetHelperDb.beginTransaction();
        Log.d(TAG, "openDatabaseAndBeginTransaction>>" + assetHelperOpenConnections);
        return this;
    }

    public void setTransactionSeccessful() {
        assetHelperDb.setTransactionSuccessful();
    }

    public void EndTransaction() {
        assetHelperDb.endTransaction();
    }


    public void insertOrgBranchesList(final ArrayList<OrgBranchVO> orgBranchList) {
        SimpleDateFormat sdf = new SimpleDateFormat(context.getResources().getString(R.string.ins_date_format));
        Log.e("orgBranchList size", "" + orgBranchList.size());
        try {
            openDatabaseAndBeginTransaction();
            for (OrgBranchVO orgBranchVO : orgBranchList) {
                Log.e("org name", "" + orgBranchVO.getOrgName());
                ContentValues mContentValues = new ContentValues();
                mContentValues.put("org_id", orgBranchVO.getOrgId());
                mContentValues.put("org_branch_id", orgBranchVO.getOrgBranchId());
                mContentValues.put("org_name", orgBranchVO.getOrgName());
                mContentValues.put("org_type", orgBranchVO.getOrgType());
                mContentValues.put("branch_name", orgBranchVO.getBranchName());
                mContentValues.put("display_name", orgBranchVO.getDisplayName());
                mContentValues.put("email", orgBranchVO.getEmail());
                mContentValues.put("telephone", orgBranchVO.getTelephone());
                mContentValues.put("file_id", orgBranchVO.getOrgLogoId());

                mContentValues.put("specialities", AppUtil.convertToJsonString(orgBranchVO.getSpecialities()));
                mContentValues.put("facilities", AppUtil.convertToJsonString(orgBranchVO.getFacilities()));

                mContentValues.put("is_active", orgBranchVO.isActive());
                mContentValues.put("emergency_phone_number", orgBranchVO.getEmergencyPhoneNumber());
                mContentValues.put("is_network_branch", orgBranchVO.isNetworkBranch());
                if (orgBranchVO.getAddress() != null) {
                    AddressVO orgBranchAddress = orgBranchVO.getAddress();
                    mContentValues.put("address_id", orgBranchAddress.getAddressId());
                    mContentValues.put("street_address", orgBranchAddress.getStreetAddress());
                    mContentValues.put("additional_address", orgBranchAddress.getAdditionalAddress());
                    mContentValues.put("city", orgBranchAddress.getCity());
                    mContentValues.put("country", (orgBranchAddress.getCountry() != null) ? orgBranchAddress.getCountry().getDisplayName() : "");
                    mContentValues.put("postal_code", orgBranchAddress.getPostalCode());
                    mContentValues.put("zone", orgBranchAddress.getZone());
                    mContentValues.put("district", orgBranchAddress.getDistrict());
                    mContentValues.put("longitude", orgBranchAddress.getLongitude());
                    mContentValues.put("latitude", orgBranchAddress.getLatitude());
                    mContentValues.put("landmarks", orgBranchAddress.getLandmarks());
                    mContentValues.put("map_url", orgBranchAddress.getMapURL());
                }
                if (orgBranchVO.getDateCreated() != null && !orgBranchVO.getDateCreated().equals("")) {
                    mContentValues.put("date_created", sdf.format(orgBranchVO.getDateCreated()));
                }
                if (orgBranchVO.getLastUpdated() != null && !orgBranchVO.getLastUpdated().equals("")) {
                    Log.d(TAG, "LAST_UPDATE=" + sdf.format(orgBranchVO.getLastUpdated()));
                    mContentValues.put("last_updated", sdf.format(orgBranchVO.getLastUpdated()));
                }
                assetHelperDb.insertWithOnConflict("org_branch_tbl", null, mContentValues, SQLiteDatabase.CONFLICT_REPLACE);

            }
            setTransactionSeccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
    }

    public long getPreferredOrgId(long preferredOrgBranchID) {
        Cursor cursor = null;
        String selectQuery = "SELECT org_id FROM org_branch_tbl where org_branch_id=" + preferredOrgBranchID;
        Log.d(TAG, "getPreferredOrgId() -->" + selectQuery);
        long preferredOrgId = 0;
        try {
            openDatabase();
            cursor = assetHelperDb.rawQuery(selectQuery, null);
            if (cursor.moveToNext()) {
                do {
                    preferredOrgId = cursor.getLong(cursor
                            .getColumnIndex("org_id"));
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return preferredOrgId;
    }

    public OrgBranchVO getPreferredOrgBranch(long orgBranchId) {
        OrgBranchVO orgBranchVO = null;
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Cursor cursor = null;
        String selectQuery = "SELECT * FROM org_branch_tbl where org_branch_id=" + orgBranchId;
        Log.d(TAG, "getPreferredOrgBranch()=" + selectQuery);
        try {
            openDatabase();
            cursor = assetHelperDb.rawQuery(selectQuery, null);
            Log.d(TAG, "1111 cursor count()=" + cursor.getCount());
            Log.d(TAG, "1111 cursor column count()=" + cursor.getColumnCount());
            if (cursor.moveToNext()) {
                do {
                    Log.d(TAG, "2222 do start");
                    orgBranchVO = new OrgBranchVO();
                    AddressVO mAddressVO = new AddressVO();
                    orgBranchVO.setOrgId(cursor.getLong(cursor
                            .getColumnIndex("org_id")));
                    orgBranchVO.setOrgBranchId(cursor.getLong(cursor
                            .getColumnIndex("org_branch_id")));
                    orgBranchVO.setOrgName(cursor.getString(cursor
                            .getColumnIndex("org_name")));
                    orgBranchVO.setOrgType(cursor.getString(cursor
                            .getColumnIndex("org_type")));
                    orgBranchVO.setBranchName(cursor.getString(cursor
                            .getColumnIndex("branch_name")));
                    orgBranchVO.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("display_name")));
                    orgBranchVO.setOrgLogoId(cursor.getLong(cursor
                            .getColumnIndex("file_id")));
                    orgBranchVO.setEmail(cursor.getString(cursor
                            .getColumnIndex("email")));
                    orgBranchVO.setTelephone(cursor.getString(cursor
                            .getColumnIndex("telephone")));
                    Log.e("dddddd", "--->" + cursor.getString(cursor.getColumnIndex("specialities")));
                    Log.e("dddddd", "--->" + cursor.getString(cursor.getColumnIndex("facilities")));
                    orgBranchVO.setSpecialities(AppUtil.getOrgBranchSpecialities(context, cursor.getString(cursor
                            .getColumnIndex("specialities"))));
                    orgBranchVO.setFacilities(AppUtil.getOrgBranchFacilities(context, cursor.getString(cursor
                            .getColumnIndex("facilities"))));

                    orgBranchVO.setEmergencyPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("emergency_phone_number")));
                    orgBranchVO.setNetworkBranch(cursor.getInt(cursor
                            .getColumnIndex("is_network_branch")) == 1 ? true : false);
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("last_updated")))) {
                        orgBranchVO.setLastUpdated(mSimpleDateFormat.parse(cursor.getString(cursor
                                .getColumnIndex("last_updated"))));
                    }
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("date_created")))) {
                        orgBranchVO.setDateCreated(mSimpleDateFormat.parse(cursor.getString(cursor
                                .getColumnIndex("date_created"))));
                    }
                    orgBranchVO.setActive(cursor.getInt(cursor
                            .getColumnIndex("is_active")) == 1 ? true : false);
                    mAddressVO.setAddressId(cursor.getLong(cursor
                            .getColumnIndex("address_id")));
                    mAddressVO.setStreetAddress(cursor.getString(cursor
                            .getColumnIndex("street_address")));
                    mAddressVO.setAdditionalAddress(cursor.getString(cursor
                            .getColumnIndex("additional_address")));
                    mAddressVO.setCity(cursor.getString(cursor
                            .getColumnIndex("city")));
                    mAddressVO.setState(cursor.getString(cursor
                            .getColumnIndex("state")));
                    mAddressVO.setPostalCode(cursor.getString(cursor
                            .getColumnIndex("postal_code")));
                    CountryVO mCountryVO = new CountryVO();
                    mCountryVO.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("country")));
                    mAddressVO.setCountry(mCountryVO);
                    mAddressVO.setZone(cursor.getString(cursor
                            .getColumnIndex("zone")));
                    mAddressVO.setDistrict(cursor.getString(cursor
                            .getColumnIndex("district")));
                    mAddressVO.setLongitude(cursor.getString(cursor
                            .getColumnIndex("longitude")));
                    mAddressVO.setLatitude(cursor.getString(cursor
                            .getColumnIndex("latitude")));
                    mAddressVO.setLandmarks(cursor.getString(cursor
                            .getColumnIndex("landmarks")));
                    mAddressVO.setMapURL(cursor.getString(cursor
                            .getColumnIndex("map_url")));

                    orgBranchVO.setAddress(mAddressVO);
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return orgBranchVO == null ? new OrgBranchVO() : orgBranchVO;
    }

    public ArrayList<OrgBranchVO> getPreferredOrgBranchesList(Context applicationContext, Double currentLocationLat, Double currentLocationLon, long orgId, String orgType) {
        ArrayList<OrgBranchVO> mOrgBranchVOs = new ArrayList<OrgBranchVO>();
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Cursor cursor = null;
        String selectQuery = "SELECT * FROM org_branch_tbl where org_id=" + orgId + " AND org_type=" + "'" + orgType + "'";
        Log.d(TAG, "getPreferredOrgBranchesList()=" + selectQuery);
        try {
            openDatabase();
            cursor = assetHelperDb.rawQuery(selectQuery, null);
            if (cursor.moveToNext()) {
                do {
                    OrgBranchVO orgBranchVO = new OrgBranchVO();
                    AddressVO mAddressVO = new AddressVO();
                    orgBranchVO.setOrgId(cursor.getLong(cursor
                            .getColumnIndex("org_id")));
                    orgBranchVO.setOrgBranchId(cursor.getLong(cursor
                            .getColumnIndex("org_branch_id")));
                    orgBranchVO.setOrgName(cursor.getString(cursor
                            .getColumnIndex("org_name")));
                    orgBranchVO.setOrgType(cursor.getString(cursor
                            .getColumnIndex("org_type")));
                    orgBranchVO.setBranchName(cursor.getString(cursor
                            .getColumnIndex("branch_name")));
                    orgBranchVO.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("display_name")));

                    Log.e("aaaaaa", "--->" + cursor.getString(cursor.getColumnIndex("specialities")));
                    Log.e("aaaaaa", "--->" + cursor.getString(cursor.getColumnIndex("facilities")));
//
                    orgBranchVO.setSpecialities(AppUtil.getOrgBranchSpecialities(context, cursor.getString(cursor
                            .getColumnIndex("specialities"))));

                    orgBranchVO.setFacilities(AppUtil.getOrgBranchFacilities(context, cursor.getString(cursor
                            .getColumnIndex("facilities"))));


                    orgBranchVO.setOrgLogoId(cursor.getLong(cursor
                            .getColumnIndex("file_id")));
                    orgBranchVO.setEmail(cursor.getString(cursor
                            .getColumnIndex("email")));
                    orgBranchVO.setTelephone(cursor.getString(cursor
                            .getColumnIndex("telephone")));

                    orgBranchVO.setEmergencyPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("emergency_phone_number")));
                    orgBranchVO.setNetworkBranch(cursor.getInt(cursor
                            .getColumnIndex("is_network_branch")) == 1 ? true : false);
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("last_updated")))) {
                        orgBranchVO.setLastUpdated(mSimpleDateFormat.parse(cursor.getString(cursor
                                .getColumnIndex("last_updated"))));
                    }
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("date_created")))) {
                        orgBranchVO.setDateCreated(mSimpleDateFormat.parse(cursor.getString(cursor
                                .getColumnIndex("date_created"))));
                    }
                    orgBranchVO.setActive(cursor.getInt(cursor
                            .getColumnIndex("is_active")) == 1 ? true : false);
                    mAddressVO.setAddressId(cursor.getLong(cursor
                            .getColumnIndex("address_id")));
                    mAddressVO.setStreetAddress(cursor.getString(cursor
                            .getColumnIndex("street_address")));
                    mAddressVO.setAdditionalAddress(cursor.getString(cursor
                            .getColumnIndex("additional_address")));
                    mAddressVO.setCity(cursor.getString(cursor
                            .getColumnIndex("city")));
                    mAddressVO.setState(cursor.getString(cursor
                            .getColumnIndex("state")));
                    mAddressVO.setPostalCode(cursor.getString(cursor
                            .getColumnIndex("postal_code")));
                    CountryVO mCountryVO = new CountryVO();
                    mCountryVO.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("country")));
                    mAddressVO.setCountry(mCountryVO);
                    mAddressVO.setZone(cursor.getString(cursor
                            .getColumnIndex("zone")));
                    mAddressVO.setDistrict(cursor.getString(cursor
                            .getColumnIndex("district")));
                    mAddressVO.setLongitude(cursor.getString(cursor
                            .getColumnIndex("longitude")));
                    mAddressVO.setLatitude(cursor.getString(cursor
                            .getColumnIndex("latitude")));
                    mAddressVO.setLandmarks(cursor.getString(cursor
                            .getColumnIndex("landmarks")));
                    mAddressVO.setMapURL(cursor.getString(cursor
                            .getColumnIndex("map_url")));
                    orgBranchVO.setAddress(mAddressVO);
                    double distance = AppUtil.getDistanceToInKms(applicationContext, currentLocationLat, currentLocationLon, parseDouble(orgBranchVO.getAddress().getLatitude()), parseDouble(orgBranchVO.getAddress().getLongitude()));
                    orgBranchVO.setDistanceInKms(distance);
                    mOrgBranchVOs.add(orgBranchVO);
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return mOrgBranchVOs;
    }


    public Date getLastUpdatedRecordTimeStamp() throws Exception {
        Cursor cursor = null;
        Date lastUpdatedRecordTimeStamp = null;

        // String selectQuery = "SELECT last_updated from r_hospital_tbl where last_updated=MAX(last_updated)";
        String selectQuery = "SELECT MAX (last_updated) AS Max_Date FROM org_branch_tbl where is_active=1";
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(context.getString(R.string.ins_date_format), Locale.ENGLISH);
        try {
            openDatabase();
            cursor = assetHelperDb.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                Log.d(TAG, "LAST UPDATE DATE=" + cursor.getString(cursor.getColumnIndex("Max_Date")));
                if (cursor.getString(cursor.getColumnIndex("Max_Date")) != null) {
                    lastUpdatedRecordTimeStamp = mSimpleDateFormat.parse(cursor.getString(cursor.getColumnIndex("Max_Date")));
                    Log.d(TAG, "lastUpdatedRecordTimeStamp=" + lastUpdatedRecordTimeStamp);
                }
            }
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return lastUpdatedRecordTimeStamp;
    }

    public ArrayList<HospitalVO> getHospitalsList() throws Exception {
        ArrayList<HospitalVO> hospitalsList = new ArrayList<HospitalVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT name,additional_address,city from r_hospital_tbl where is_active=1";
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            openDatabase();
            cursor = assetHelperDb.rawQuery(selectQuery, null);
            if (cursor.moveToNext()) {
                do {
                    HospitalVO hospitalVO = new HospitalVO();
                    hospitalVO.setHospitalId(cursor.getLong(cursor
                            .getColumnIndex("hospital_id")));
                   /* hospitalVO.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("display_name")));*/
                    hospitalVO.setName(cursor.getString(cursor
                            .getColumnIndex("name")));
                  /*  hospitalVO.setStreetAddress(cursor.getString(cursor
                            .getColumnIndex("street_address")));*/
                    hospitalVO.setAdditionalAddress(cursor.getString(cursor
                            .getColumnIndex("additional_address")));
                    hospitalVO.setCity(cursor.getString(cursor
                            .getColumnIndex("city")));
                    /*hospitalVO.setState(cursor.getString(cursor
                            .getColumnIndex("state")));
                    hospitalVO.setPostalCode(cursor.getString(cursor
                            .getColumnIndex("postal_code")));*/
                   /* hospitalVO.isActive(cursor.getString(cursor
                            .getColumnIndex("is_active")));*/
                   /* if(!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("date_created"))))
                    {
                        hospitalVO.setDateCreated(mSimpleDateFormat.parse(cursor.getString(cursor
                                .getColumnIndex("date_created"))));
                    }*/
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("last_updated")))) {
                        hospitalVO.setLastUpdated(mSimpleDateFormat.parse(cursor.getString(cursor
                                .getColumnIndex("last_updated"))));
                    }
                    hospitalsList.add(hospitalVO);
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            Log.d(TAG, "EXCEPTION=" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION=" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return hospitalsList;
    }

    public void deleteDatabaseAndClearStaticVariables(Context context) {
        Log.e("delete data->", "start");
        if (assetHelperDb != null) {
            context.deleteDatabase("org_branches.db");
            Log.e("delete data->", "end");
            if (assetHelperDb.isOpen()) {
                Log.d(TAG, "CLOSING DATABASE");
                assetHelperDb.close();
            }
            Log.d(TAG, "deleteDatabaseAndClearStaticVariables>>>DATABASE IS NOT NULL");
            assetHelperDb = null;
            assetHelperOpenConnections = 0;
            dbHandler = null;
        }

    }

    public Cursor getMatchingStates(Activity mActivity, String constraint, String orgType) throws SQLException {

        // String queryString = "SELECT hospital_id as _id,name,additional_address,city from r_hospital_tbl";
        String queryString = "SELECT org_branch_id as _id,org_id,telephone,branch_name,display_name,emergency_phone_number,additional_address,city from org_branch_tbl";

        Cursor cursor;
        openDatabase();

        if (constraint != null) {
            // Query for any rows where the state name begins with the
            // string specified in constraint.
            //
            // NOTE:
            // If wildcards are to be used in a rawQuery, they must appear
            // in the query parameters, and not in the query string proper.
            // See http://code.google.com/p/android/issues/detail?id=3153
            constraint = constraint.trim() + "%";
            queryString += " WHERE is_active=1 AND is_network_branch=1 AND org_type=" + "'" + orgType + "'" + " AND display_name LIKE ?";
        }
        String params[] = {constraint};

        if (constraint == null) {
            // If no parameters are used in the query,
            // the params arg must be null.
            params = null;
        }
        try {
            cursor = assetHelperDb.rawQuery(queryString, params);
            if (cursor != null) {
                cursor.moveToFirst();
                return cursor;
            }
        } catch (SQLException e) {
            Log.d(TAG, "EXCEPTION=" + e.getMessage());
            throw e;
        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION=" + e.getMessage());
            throw e;
        } finally {
            closeDatabase();
        }

        return cursor;
    }

    public ArrayList<OrgBranchVO> getAllActiveNetworkOrgBranchesList(Context applicationContext, long preferredOrgId, String orgType, double currentLocationLat, double currentLocationLon) {
        int distanceInKms = 5;
        if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_HOSPITAL)) {
            distanceInKms = 50;
        }
        if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_BLOOD_BANK)) {
            distanceInKms = 50;
        }
        Log.d(TAG, "ORG_TYPE=" + orgType);
        Log.d(TAG, "DISTANCE=" + distanceInKms);

        SharedPreferences mSharedPreferences = applicationContext
                .getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
       /* Double currentLocationLat = Double.valueOf(mSharedPreferences.getString("lat", "0"));
        Double currentLocationLon = Double.valueOf(mSharedPreferences.getString("lon", "0"));*/
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        double earthRadius = 6371;  // earth's mean radius, km

        // first-cut bounding box (in degrees)
        double maxLat = currentLocationLat + Math.toDegrees(distanceInKms / earthRadius);
        double minLat = currentLocationLat - Math.toDegrees(distanceInKms / earthRadius);
        double maxLon = currentLocationLon + Math.toDegrees(Math.asin(distanceInKms / earthRadius) / Math.cos(Math.toRadians(currentLocationLat)));
        double minLon = currentLocationLon - Math.toDegrees(Math.asin(distanceInKms / earthRadius) / Math.cos(Math.toRadians(currentLocationLat)));

        ArrayList<OrgBranchVO> networkOrgBranchesList = new ArrayList<OrgBranchVO>();

        Cursor cursor = null;
        String selectQuery = "SELECT * FROM org_branch_tbl WHERE" + " (latitude >= " + minLat + " AND latitude <= " + maxLat + ")" + " AND (longitude >= " + minLon + " AND longitude <=" + maxLon + ")" + " AND is_network_branch=1 AND org_id!=" + preferredOrgId + " AND is_active=1 " + " AND org_type=" + "'" + orgType + "'";
        Log.d(TAG, "getAllActiveNetworkOrgBranchesList()=" + selectQuery);
        try {
            openDatabase();
            cursor = assetHelperDb.rawQuery(selectQuery, null);
            if (cursor.moveToNext()) {
                do {
                    OrgBranchVO orgBranchVO = new OrgBranchVO();
                    AddressVO mAddressVO = new AddressVO();
                    orgBranchVO.setOrgId(cursor.getLong(cursor
                            .getColumnIndex("org_id")));
                    orgBranchVO.setOrgBranchId(cursor.getLong(cursor
                            .getColumnIndex("org_branch_id")));
                    orgBranchVO.setOrgName(cursor.getString(cursor
                            .getColumnIndex("org_name")));
                    orgBranchVO.setOrgType(cursor.getString(cursor
                            .getColumnIndex("org_type")));
                    orgBranchVO.setBranchName(cursor.getString(cursor
                            .getColumnIndex("branch_name")));
                    orgBranchVO.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("display_name")));
                    orgBranchVO.setOrgLogoId(cursor.getLong(cursor
                            .getColumnIndex("file_id")));
                    orgBranchVO.setEmail(cursor.getString(cursor
                            .getColumnIndex("email")));
                    orgBranchVO.setTelephone(cursor.getString(cursor
                            .getColumnIndex("telephone")));
                    orgBranchVO.setSpecialities(AppUtil.getOrgBranchSpecialities(context, cursor.getString(cursor
                            .getColumnIndex("specialities"))));

                    orgBranchVO.setFacilities(AppUtil.getOrgBranchFacilities(context, cursor.getString(cursor
                            .getColumnIndex("facilities"))));

                    Log.e("bbbbbb", "--->" + cursor.getString(cursor.getColumnIndex("specialities")));
                    Log.e("bbbbbb", "--->" + cursor.getString(cursor.getColumnIndex("facilities")));


                    orgBranchVO.setEmergencyPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("emergency_phone_number")));
                    orgBranchVO.setActive(cursor.getInt(cursor
                            .getColumnIndex("is_active")) == 1 ? true : false);
                    orgBranchVO.setNetworkBranch(cursor.getInt(cursor
                            .getColumnIndex("is_network_branch")) == 1 ? true : false);
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("last_updated")))) {
                        orgBranchVO.setLastUpdated(mSimpleDateFormat.parse(cursor.getString(cursor
                                .getColumnIndex("last_updated"))));
                    }
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("date_created")))) {
                        orgBranchVO.setDateCreated(mSimpleDateFormat.parse(cursor.getString(cursor
                                .getColumnIndex("date_created"))));
                    }


                    mAddressVO.setAddressId(cursor.getLong(cursor
                            .getColumnIndex("address_id")));
                    mAddressVO.setStreetAddress(cursor.getString(cursor
                            .getColumnIndex("street_address")));
                    mAddressVO.setAdditionalAddress(cursor.getString(cursor
                            .getColumnIndex("additional_address")));
                    mAddressVO.setCity(cursor.getString(cursor
                            .getColumnIndex("city")));
                    mAddressVO.setState(cursor.getString(cursor
                            .getColumnIndex("state")));
                    mAddressVO.setPostalCode(cursor.getString(cursor
                            .getColumnIndex("postal_code")));
                    mAddressVO.setZone(cursor.getString(cursor
                            .getColumnIndex("zone")));
                    mAddressVO.setDistrict(cursor.getString(cursor
                            .getColumnIndex("district")));
                    mAddressVO.setLongitude(cursor.getString(cursor
                            .getColumnIndex("longitude")));
                    mAddressVO.setLatitude(cursor.getString(cursor
                            .getColumnIndex("latitude")));
                    mAddressVO.setMapURL(cursor.getString(cursor
                            .getColumnIndex("map_url")));

                    orgBranchVO.setAddress(mAddressVO);
                    double distance = AppUtil.getDistanceToInKms(applicationContext, currentLocationLat, currentLocationLon, Double.parseDouble(orgBranchVO.getAddress().getLatitude()), Double.parseDouble(orgBranchVO.getAddress().getLongitude()));
                    orgBranchVO.setDistanceInKms(distance);
                    Log.d(TAG, "getAllActiveNetworkOrgBranchesList()=" + selectQuery);
                    Log.d(TAG, "dISTANCE=" + distance);

                    if (distance <= distanceInKms)
                        networkOrgBranchesList.add(orgBranchVO);
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return networkOrgBranchesList;
    }

    public ArrayList<OrgBranchVO> getAllActiveNonNetworkOrgBranchesList(Context applicationContext, long preferredOrgId, String orgType, double currentLocationLat, double currentLocationLon) {
        int distanceInKms = 5;
        if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_HOSPITAL)) {
            distanceInKms = 50;
        }
        if (orgType.equalsIgnoreCase(Constant.ORG_TYPE_URL_BLOOD_BANK)) {
            distanceInKms = 50;
        }
        Log.d(TAG, "distance=" + distanceInKms);
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        double earthRadius = 6371;  // earth's mean radius, km

        // first-cut bounding box (in degrees)
        double maxLat = currentLocationLat + Math.toDegrees(distanceInKms / earthRadius);
        double minLat = currentLocationLat - Math.toDegrees(distanceInKms / earthRadius);
        double maxLon = currentLocationLon + Math.toDegrees(Math.asin(distanceInKms / earthRadius) / Math.cos(Math.toRadians(currentLocationLat)));
        double minLon = currentLocationLon - Math.toDegrees(Math.asin(distanceInKms / earthRadius) / Math.cos(Math.toRadians(currentLocationLat)));

        ArrayList<OrgBranchVO> nonNetworkOrgBranchesList = new ArrayList<OrgBranchVO>();

        Cursor cursor = null;
        String selectQuery = "SELECT * FROM org_branch_tbl WHERE" + " (latitude >= " + minLat + " AND latitude <= " + maxLat + ")" + " AND (longitude >= " + minLon + " AND longitude <=" + maxLon + ")" + " AND is_network_branch=0 AND is_active=1 AND org_id!=" + preferredOrgId + " AND org_type=" + "'" + orgType + "'";
        Log.d(TAG, "getAllActiveNonNetworkOrgBranchesList()=" + selectQuery);
        try {
            openDatabase();
            cursor = assetHelperDb.rawQuery(selectQuery, null);
            if (cursor.moveToNext()) {
                do {
                    OrgBranchVO orgBranchVO = new OrgBranchVO();
                    AddressVO mAddressVO = new AddressVO();
                    orgBranchVO.setOrgId(cursor.getLong(cursor
                            .getColumnIndex("org_id")));
                    orgBranchVO.setOrgBranchId(cursor.getLong(cursor
                            .getColumnIndex("org_branch_id")));
                    orgBranchVO.setOrgName(cursor.getString(cursor
                            .getColumnIndex("org_name")));
                    orgBranchVO.setOrgType(cursor.getString(cursor
                            .getColumnIndex("org_type")));
                    orgBranchVO.setBranchName(cursor.getString(cursor
                            .getColumnIndex("branch_name")));
                    orgBranchVO.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("display_name")));
                    orgBranchVO.setOrgLogoId(cursor.getLong(cursor
                            .getColumnIndex("file_id")));
                    orgBranchVO.setEmail(cursor.getString(cursor
                            .getColumnIndex("email")));
                    Log.e("cccccc", "--->" + cursor.getString(cursor.getColumnIndex("specialities")));
                    Log.e("cccccc", "--->" + cursor.getString(cursor.getColumnIndex("facilities")));
                    orgBranchVO.setSpecialities(AppUtil.getOrgBranchSpecialities(context, cursor.getString(cursor
                            .getColumnIndex("specialities"))));

                    orgBranchVO.setFacilities(AppUtil.getOrgBranchFacilities(context, cursor.getString(cursor
                            .getColumnIndex("facilities"))));

                    orgBranchVO.setTelephone(cursor.getString(cursor
                            .getColumnIndex("telephone")));
                    orgBranchVO.setEmergencyPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("emergency_phone_number")));
                    orgBranchVO.setActive(cursor.getInt(cursor
                            .getColumnIndex("is_active")) == 1 ? true : false);
                    orgBranchVO.setNetworkBranch(cursor.getInt(cursor
                            .getColumnIndex("is_network_branch")) == 1 ? true : false);
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("last_updated")))) {
                        orgBranchVO.setLastUpdated(mSimpleDateFormat.parse(cursor.getString(cursor
                                .getColumnIndex("last_updated"))));
                    }
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("date_created")))) {
                        orgBranchVO.setDateCreated(mSimpleDateFormat.parse(cursor.getString(cursor
                                .getColumnIndex("date_created"))));
                    }


                    mAddressVO.setAddressId(cursor.getLong(cursor
                            .getColumnIndex("address_id")));
                    mAddressVO.setStreetAddress(cursor.getString(cursor
                            .getColumnIndex("street_address")));
                    mAddressVO.setAdditionalAddress(cursor.getString(cursor
                            .getColumnIndex("additional_address")));
                    mAddressVO.setCity(cursor.getString(cursor
                            .getColumnIndex("city")));
                    mAddressVO.setState(cursor.getString(cursor
                            .getColumnIndex("state")));
                    mAddressVO.setPostalCode(cursor.getString(cursor
                            .getColumnIndex("postal_code")));
                    mAddressVO.setZone(cursor.getString(cursor
                            .getColumnIndex("zone")));
                    mAddressVO.setDistrict(cursor.getString(cursor
                            .getColumnIndex("district")));
                    mAddressVO.setLongitude(cursor.getString(cursor
                            .getColumnIndex("longitude")));
                    mAddressVO.setLatitude(cursor.getString(cursor
                            .getColumnIndex("latitude")));
                    mAddressVO.setMapURL(cursor.getString(cursor
                            .getColumnIndex("map_url")));

                    orgBranchVO.setAddress(mAddressVO);
                    double distance = AppUtil.getDistanceToInKms(applicationContext, currentLocationLat, currentLocationLon, parseDouble(orgBranchVO.getAddress().getLatitude()), parseDouble(orgBranchVO.getAddress().getLongitude()));
                    Log.d(TAG, "getAllActiveNonNetworkOrgBranchesList()=" + selectQuery);
                    Log.d(TAG, "dISTANCE=" + distance);
                    if (distance <= distanceInKms)
                        nonNetworkOrgBranchesList.add(orgBranchVO);
                    orgBranchVO.setDistanceInKms(distance);

                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return nonNetworkOrgBranchesList;
    }

    public ArrayList<OrgBranchVO> getOrgBranches(long orgId) {
        ArrayList<OrgBranchVO> orgBranchVOs = new ArrayList<>();
        Cursor cursor = null;
        String selectQuery = "SELECT * from org_branch_tbl WHERE org_id=" + orgId;

        try {
            openDatabase();
            cursor = assetHelperDb.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    OrgBranchVO orgBranchVO = new OrgBranchVO();
                    AddressVO mAddressVO = new AddressVO();
                    orgBranchVO.setOrgId(cursor.getLong(cursor
                            .getColumnIndex("org_id")));
                    orgBranchVO.setOrgBranchId(cursor.getLong(cursor
                            .getColumnIndex("org_branch_id")));
                    orgBranchVO.setOrgName(cursor.getString(cursor
                            .getColumnIndex("org_name")));
                    orgBranchVO.setOrgType(cursor.getString(cursor
                            .getColumnIndex("org_type")));
                    orgBranchVO.setBranchName(cursor.getString(cursor
                            .getColumnIndex("branch_name")));
                    orgBranchVO.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("display_name")));
                    orgBranchVO.setOrgLogoId(cursor.getLong(cursor
                            .getColumnIndex("file_id")));
                    orgBranchVO.setEmail(cursor.getString(cursor
                            .getColumnIndex("email")));

                    orgBranchVO.setTelephone(cursor.getString(cursor
                            .getColumnIndex("telephone")));
                    orgBranchVO.setEmergencyPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("emergency_phone_number")));
                    orgBranchVO.setActive(cursor.getInt(cursor
                            .getColumnIndex("is_active")) == 1 ? true : false);
                    orgBranchVO.setNetworkBranch(cursor.getInt(cursor
                            .getColumnIndex("is_network_branch")) == 1 ? true : false);

                    mAddressVO.setAddressId(cursor.getLong(cursor
                            .getColumnIndex("address_id")));
                    mAddressVO.setStreetAddress(cursor.getString(cursor
                            .getColumnIndex("street_address")));
                    mAddressVO.setAdditionalAddress(cursor.getString(cursor
                            .getColumnIndex("additional_address")));
                    mAddressVO.setCity(cursor.getString(cursor
                            .getColumnIndex("city")));
                    mAddressVO.setState(cursor.getString(cursor
                            .getColumnIndex("state")));
                    mAddressVO.setPostalCode(cursor.getString(cursor
                            .getColumnIndex("postal_code")));
                    mAddressVO.setZone(cursor.getString(cursor
                            .getColumnIndex("zone")));
                    mAddressVO.setDistrict(cursor.getString(cursor
                            .getColumnIndex("district")));
                    mAddressVO.setLongitude(cursor.getString(cursor
                            .getColumnIndex("longitude")));
                    mAddressVO.setLatitude(cursor.getString(cursor
                            .getColumnIndex("latitude")));
                    mAddressVO.setMapURL(cursor.getString(cursor
                            .getColumnIndex("map_url")));

                    orgBranchVO.setAddress(mAddressVO);
                    orgBranchVOs.add(orgBranchVO);
                } while (cursor.moveToNext());
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return orgBranchVOs;
    }

}
