package com.patientz.databases;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.patientz.VO.AmbulanceDetailsVO;
import com.patientz.VO.AmbulanceProviderVO;
import com.patientz.VO.AvailabilityCapabilityVO;
import com.patientz.VO.BloodyFriendVO;
import com.patientz.VO.EmergencyContactsVO;
import com.patientz.VO.EmergencyStatusVO;
import com.patientz.VO.Facility;
import com.patientz.VO.FileUploadImagesVO;
import com.patientz.VO.HealthRecordVO;
import com.patientz.VO.InsuranceUpload;
import com.patientz.VO.InsuranceVO;
import com.patientz.VO.OrgLocationVO;
import com.patientz.VO.OrganisationVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.PrefferedAmbulanceProviderLocationVO;
import com.patientz.VO.PublicProviderVO;
import com.patientz.VO.Speciality;
import com.patientz.VO.UserInfoVO;
import com.patientz.VO.UserRecordVO;
import com.patientz.VO.UserUploadedMedia;
import com.patientz.VO.UserVO;
import com.patientz.activity.R;
import com.patientz.services.StickyNotificationInsuranceFGService;
import com.patientz.utils.AppCrypto;
import com.patientz.utils.AppUtil;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.SecretKey;


public class DatabaseHandler {

    private static final String TAG = "DatabaseHandler";
    static int noOfInvites = 0;
    SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private Context context;

    private static SQLiteDatabase database;

    private SqliteHelper sqlHelper;
    public static int openConnections = 0;
    public static int openConnectionsbf = 0;


    public DatabaseHandler(Context context) {
        this.context = context;
    }

    private LocalBroadcastManager mLocalBroadcastManager;

    private static DatabaseHandler dbHandler;

    public static DatabaseHandler dbInit(Context context) {
        if (dbHandler == null) {
            // Log.d("DB tag...", "New connection");
            dbHandler = new DatabaseHandler(context);
            return dbHandler;
        } else {
            return dbHandler;
        }
    }

    public SQLiteDatabase getDatabase(Context context) {
        return database;
    }

    public DatabaseHandler openDatabase() throws SQLException {
        openConnections++;
        Log.d(TAG, "openDatabase>>" + openConnections);
        if (sqlHelper == null) {
            Log.d(TAG, "INITIALISING SQLITE HELPER");
            sqlHelper = new SqliteHelper(context);
        }
        if (database == null || (!database.isOpen())) {
            Log.d(TAG, "DATABASE IS NULL / NOT OPEN");
            Log.d(TAG, "SQLITE HELPER=" + sqlHelper);
            database = sqlHelper.getReadableDatabase();
            Log.d("databasehelper", "database object created");

        }
        return this;
    }

    public DatabaseHandler openDatabaseBF() throws SQLException {
        openConnectionsbf++;
        Log.d(TAG, "openConnectionsbf>>" + openConnectionsbf);
        if (sqlHelper == null) {
            Log.d(TAG, "INITIALISING SQLITE HELPER");
            sqlHelper = new SqliteHelper(context);
        }
        if (database == null || (!database.isOpen())) {
            Log.d(TAG, "DATABASE IS NULL / NOT OPEN");
            Log.d(TAG, "SQLITE HELPER=" + sqlHelper);
            database = sqlHelper.getReadableDatabase();
        }
        return this;
    }

    public void closeDatabase() {
        openConnections--;
        Log.d(TAG, "closeDatabase()>>" + openConnections);
        if (openConnections == 0) {
            sqlHelper.close();
        }
    }

    public void closeDatabaseBF() {
        openConnectionsbf--;
        Log.d(TAG, "closeDatabaseBF()>>" + openConnections);
        if (openConnectionsbf == 0) {
            sqlHelper.close();
        }
    }

    public DatabaseHandler openDatabaseAndBeginTransaction()
            throws SQLException {
        Log.d(TAG, "openDatabaseAndBeginTransaction");

        openConnections++;
        if (sqlHelper == null) {
            Log.d(TAG, "INITIATING SQL HELPER");
            sqlHelper = new SqliteHelper(context);
        }
        if (database == null || (!database.isOpen() || database.isReadOnly())) {
            Log.d(TAG, "GETTING DB TYPE WRITEABLE");

            database = sqlHelper.getWritableDatabase();
        }
        database.beginTransaction();
        Log.d(TAG, "openDatabaseAndBeginTransaction>>" + openConnections);
        return this;
    }

    public DatabaseHandler openDatabaseAndBeginTransactionBF()
            throws SQLException {
        Log.d(TAG, "openDatabaseAndBeginTransactionBF");

        openConnectionsbf++;
        if (sqlHelper == null) {
            Log.d(TAG, "INITIATING SQL HELPER");
            sqlHelper = new SqliteHelper(context);
        }
        if (database == null || (!database.isOpen() || database.isReadOnly())) {
            Log.d(TAG, "GETTING DB TYPE WRITEABLE");

            database = sqlHelper.getWritableDatabase();
        }
        database.beginTransaction();
        Log.d(TAG, "openDatabaseAndBeginTransactionBF>>" + openConnectionsbf);
        return this;
    }

    public void setTransactionSuccessful() {
        database.setTransactionSuccessful();
    }

    public void EndTransaction() {
        database.endTransaction();
    }

    public void insertUsers(ArrayList<PatientUserVO> list) {
        if (list != null) {
            for (PatientUserVO patientUserVO : list) {
                try {
                    insertUser(patientUserVO);
                    insertContacts(patientUserVO);
                    if (patientUserVO.getRecordVO() != null && patientUserVO.getRecordVO().getUserInfoVO() != null) {
                        Log.d(TAG, "INSERTING " + patientUserVO.getFirstName() + " USER IN USER_INFO_TBL");
                        insertUserInfo(patientUserVO.getRecordVO(), patientUserVO.getBloodGroup(), patientUserVO.getPatientHandle());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void insertUser(PatientUserVO user) throws Exception {
        Log.d(TAG, "Checking user name =" + user.getFirstName() + user.getLastName());

        if (user != null) {
            try {
                openDatabaseAndBeginTransaction();
                ContentValues mContentValues = new ContentValues();
                mContentValues.put("user_id", user.getUserProfileId());
                mContentValues.put("patient_id", user.getPatientId());
                mContentValues.put("first_name", user.getFirstName());
                mContentValues.put("last_name", user.getLastName());
                mContentValues.put("event_log_id", user.getEventLogId());
                Log.d(TAG, "STATUS=" + user.getStatus());
                Log.d(TAG, "Role=" + user.getRole());
                Log.d(TAG, "Relationship=" + user.getRelationship());
                Log.d(TAG, "Status=" + user.getStatus());

                mContentValues.put("status", user.getStatus());
                mContentValues.put("patient_access_id",
                        user.getPatientAccessId());
                mContentValues.put("phone_number", user.getPhoneNumber());
                mContentValues.put("relationship", user.getRelationship());
                mContentValues.put("pic_id", user.getPicId());
                mContentValues.put("pic_name", user.getPicName());
                mContentValues.put("filename", user.getFileName());
                mContentValues.put("role", user.getRole());
                mContentValues.put("emergency_token", user.getEmergencyToken());
                Log.d(TAG, "SHARE POINTS=" + user.getSharePoints());
                mContentValues.put("share_points", user.getSharePoints());

                if (user.getCurrentSelectedPatient() != null) {
                    Log.d(TAG, "INSERT USER CURRENT SELECTED PROFILE=" + user.getCurrentSelectedPatient());
                    mContentValues.put("current_selected_patient", user.getCurrentSelectedPatient());
                }

                mContentValues.put("under_emergency", user.isUnderEmergency());
                mContentValues.put("emergency_url_token",
                        user.getEmergencyTokenUrl());
                Log.d(TAG + " patient details",
                        "EmergencyAPIKEY: " + user.getEmergencyAPIKEY());
                /* Encoding apikey */
                mContentValues.put("emergency_api_key",
                        user.getEmergencyAPIKEY());
                //openDatabaseAndBeginTransaction();
                if (TextUtils.equals(user.getStatus(), "Deleted")) {
                    // don't insert user if status is "Deleted"
                    database.delete("user_tbl",
                            "patient_id=" + user.getPatientId(), null);
                } else {
                    database.insertWithOnConflict("user_tbl", null,
                            mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                }
                setTransactionSuccessful();
                EndTransaction();
            } catch (SQLException e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } finally {
                closeDatabase();
            }
        }
    }

    //file upload images table

    public void insertFileUploadImage(int page_number, String image_path) throws Exception {

        try {
            openDatabaseAndBeginTransaction();

            ContentValues mContentValues = new ContentValues();
            mContentValues.put("image_path", image_path);
            mContentValues.put("page_number", page_number);

            database.insertWithOnConflict("file_upload_images_tbl", null,
                    mContentValues, SQLiteDatabase.CONFLICT_REPLACE);

            setTransactionSuccessful();
            EndTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }

    }

    public ArrayList<FileUploadImagesVO> getFileUploadImages() {

        ArrayList<FileUploadImagesVO> uploadimages = new ArrayList<FileUploadImagesVO>();
        Cursor cursor = null;
        String selectQuery = "";

        selectQuery = "SELECT * from file_upload_images_tbl where page_number != '-1'";

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    FileUploadImagesVO fileUploadImagesVO = new FileUploadImagesVO();
                    fileUploadImagesVO.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                    fileUploadImagesVO.setPage_number(cursor.getInt(cursor.getColumnIndex("page_number")));
                    fileUploadImagesVO.setImage_path(cursor.getString(cursor.getColumnIndex("image_path")));
                    uploadimages.add(fileUploadImagesVO);
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

        Log.d(TAG, "contacts_list -- " + uploadimages.size());

        FileUploadImagesVO fileUploadImagesVO1 = new FileUploadImagesVO();
        fileUploadImagesVO1.setImage_path("");
        fileUploadImagesVO1.setPage_number(-1);
        uploadimages.add(fileUploadImagesVO1);

        return uploadimages;
    }

    public ArrayList<FileUploadImagesVO> getFileUploadImagesToServer() {

        ArrayList<FileUploadImagesVO> uploadimages = new ArrayList<FileUploadImagesVO>();
        Cursor cursor = null;
        String selectQuery = "";

        selectQuery = "SELECT * from file_upload_images_tbl where page_number != '-1'";

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    FileUploadImagesVO fileUploadImagesVO = new FileUploadImagesVO();
                    fileUploadImagesVO.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                    fileUploadImagesVO.setPage_number(cursor.getInt(cursor.getColumnIndex("page_number")));
                    fileUploadImagesVO.setImage_path(cursor.getString(cursor.getColumnIndex("image_path")));
                    uploadimages.add(fileUploadImagesVO);
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

        Log.d(TAG, "contacts_list ----> " + uploadimages.size());

        return uploadimages;
    }

    public void removeFileUploadImages(int id) {
        try {
            openDatabaseAndBeginTransaction();
            database.delete("file_upload_images_tbl", "_id=" + id, null);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
    }

    public void deleteFileUploadImages() {
        try {
            openDatabaseAndBeginTransaction();
            database.delete("file_upload_images_tbl", null, null);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
    }


    public int updateFileUploadImages(int _id, String image_path) {
        int update = -1;
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("image_path", image_path);
            String[] args = new String[]{String.valueOf(_id)};
            update = database.update("file_upload_images_tbl", mContentValues, "_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
        return update;
    }


    ///blood_friends_tbl

    public void deletePhoneContactsInfo() {
        try {
            openDatabaseAndBeginTransaction();
            database.delete("blood_friends_tbl", null, null);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
    }

    public boolean tableStatus() {
        Cursor cursor = null;
        String selectQuery = "";
        int table_status = 0;
        selectQuery = "SELECT COUNT() FROM sqlite_master WHERE name = 'blood_friends_tbl'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            Log.d(TAG, "getCount -- " + cursor.getCount());
            table_status = cursor.getCount();
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
        if (table_status == 0) {
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<BloodyFriendVO> getPhoneContactsInfoShow(String blood_group) {

        ArrayList<BloodyFriendVO> contactslist = new ArrayList<BloodyFriendVO>();
        Cursor cursor = null;
        String selectQuery = "";

//        if (blood_group.contains("Select")) {
//            selectQuery = "SELECT * from blood_friends_tbl where blood_group IS NOT NULL";
//        } else {
        selectQuery = "SELECT * from blood_friends_tbl where blood_group = '" + blood_group + "'";
        // }
        Log.d(TAG, "getPhoneContactsInfoShow=" + selectQuery);

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {

                do {
                    BloodyFriendVO contact = new BloodyFriendVO();
                    contact.setContactName(cursor.getString(cursor.getColumnIndex("phone_name")));
                    contact.setServerContactName(cursor.getString(cursor.getColumnIndex("server_name")));
                    contact.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                    contact.setUpdate_status(cursor.getInt(cursor.getColumnIndex("update_status")));
                    contact.setBloodGroup(cursor.getString(cursor.getColumnIndex("blood_group")));
                    contact.setContactType(cursor.getInt(cursor.getColumnIndex("contact_type")));
                    contact.setContact(cursor.getString(cursor.getColumnIndex("phone_number")));
                    if (cursor.getInt(cursor.getColumnIndex("user_invited")) == 1) {
                        contact.setUserInvited(true);
                    } else {
                        contact.setUserInvited(false);
                    }
                    contactslist.add(contact);
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

        Log.d(TAG, "contacts_list -- " + contactslist.size());

        return contactslist;
    }

    public boolean checkInvitedStatus(int _id) {
        Cursor cursor = null;
        String selectQuery = "";
        boolean exist_status = false;
        selectQuery = "SELECT * from blood_friends_tbl where _id = '" + _id + "'" + " and blood_group IS NOT NULL";

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                exist_status = true;
            } else {
                exist_status = false;
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

        Log.d(TAG, "exist_status -- " + exist_status);

        return exist_status;
    }

    public ArrayList<BloodyFriendVO> getPhoneContactsInfoDUPLICATE() {

        ArrayList<BloodyFriendVO> contactslist = new ArrayList<BloodyFriendVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM blood_friends_tbl where update_status = '-1' LIMIT 1";
//        String selectQuery = "SELECT * FROM blood_friends_tbl where update_status = '0'";

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    BloodyFriendVO contact = new BloodyFriendVO();
                    contact.setContactId(cursor.getInt(cursor.getColumnIndex("contact_id")));
                    contact.setBloodGroup(cursor.getString(cursor.getColumnIndex("blood_group")));
                    contact.setLookUpId(cursor.getString(cursor.getColumnIndex("lookup_id")));
                    contact.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                    contact.setUpdate_status(cursor.getInt(cursor.getColumnIndex("update_status")));
                    contact.setContactName(cursor.getString(cursor.getColumnIndex("phone_name")));
                    contact.setContactType(cursor.getInt(cursor.getColumnIndex("contact_type")));
                    contact.setContact(cursor.getString(cursor.getColumnIndex("phone_number")));
                    if (cursor.getInt(cursor.getColumnIndex("user_invited")) == 1) {
                        contact.setUserInvited(true);
                    } else {
                        contact.setUserInvited(false);
                    }
                    contactslist.add(contact);
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
        return contactslist;
    }

    public ArrayList<BloodyFriendVO> getPhoneContactsInfo() {

        ArrayList<BloodyFriendVO> contactslist = new ArrayList<BloodyFriendVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM blood_friends_tbl where update_status = '0' LIMIT 1";
//        String selectQuery = "SELECT * FROM blood_friends_tbl where update_status = '0'";

        try {
            openDatabaseBF();
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    BloodyFriendVO contact = new BloodyFriendVO();
                    contact.setContactId(cursor.getInt(cursor.getColumnIndex("contact_id")));
                    contact.setBloodGroup(cursor.getString(cursor.getColumnIndex("blood_group")));
                    contact.setLookUpId(cursor.getString(cursor.getColumnIndex("lookup_id")));
                    contact.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                    contact.setUpdate_status(cursor.getInt(cursor.getColumnIndex("update_status")));
                    contact.setContactName(cursor.getString(cursor.getColumnIndex("phone_name")));
                    contact.setContactType(cursor.getInt(cursor.getColumnIndex("contact_type")));
                    contact.setContact(cursor.getString(cursor.getColumnIndex("phone_number")));
                    if (cursor.getInt(cursor.getColumnIndex("user_invited")) == 1) {
                        contact.setUserInvited(true);
                    } else {
                        contact.setUserInvited(false);
                    }
                    contactslist.add(contact);

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
            closeDatabaseBF();
        }
        return contactslist;
    }

    public ArrayList<BloodyFriendVO> getAllPhoneContacts() {

        ArrayList<BloodyFriendVO> contactslist = new ArrayList<BloodyFriendVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM blood_friends_tbl where contact_type ='" + 1 + "' ORDER BY phone_name ASC";

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    BloodyFriendVO contact = new BloodyFriendVO();
                    contact.setContactId(cursor.getInt(cursor.getColumnIndex("contact_id")));
                    contact.setBloodGroup(cursor.getString(cursor.getColumnIndex("blood_group")));
                    contact.setLookUpId(cursor.getString(cursor.getColumnIndex("lookup_id")));
                    contact.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                    contact.setUpdate_status(cursor.getInt(cursor.getColumnIndex("update_status")));
                    contact.setContactName(cursor.getString(cursor.getColumnIndex("phone_name")));
                    contact.setContactType(cursor.getInt(cursor.getColumnIndex("contact_type")));
                    contact.setContact(cursor.getString(cursor.getColumnIndex("phone_number")));
                    if (cursor.getInt(cursor.getColumnIndex("user_invited")) == 1) {
                        contact.setUserInvited(true);
                    } else {
                        contact.setUserInvited(false);
                    }
                    contactslist.add(contact);

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
        return contactslist;
    }

    public ArrayList<BloodyFriendVO> getAllPhoneContactsFromDB() {

        ArrayList<BloodyFriendVO> contactslist = new ArrayList<BloodyFriendVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM blood_friends_tbl";

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    BloodyFriendVO contact = new BloodyFriendVO();
                    contact.setContactId(cursor.getInt(cursor.getColumnIndex("contact_id")));
                    contact.setBloodGroup(cursor.getString(cursor.getColumnIndex("blood_group")));
                    contact.setLookUpId(cursor.getString(cursor.getColumnIndex("lookup_id")));
                    contact.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                    contact.setUpdate_status(cursor.getInt(cursor.getColumnIndex("update_status")));
                    contact.setContactName(cursor.getString(cursor.getColumnIndex("phone_name")));
                    contact.setContactType(cursor.getInt(cursor.getColumnIndex("contact_type")));
                    contact.setContact(cursor.getString(cursor.getColumnIndex("phone_number")));
                    if (cursor.getInt(cursor.getColumnIndex("user_invited")) == 1) {
                        contact.setUserInvited(true);
                    } else {
                        contact.setUserInvited(false);
                    }
                    contactslist.add(contact);

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
        return contactslist;
    }

    public ArrayList<BloodyFriendVO> getSelected_5_SyncPhoneContacts() {

        ArrayList<BloodyFriendVO> contactslist = new ArrayList<BloodyFriendVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * from blood_friends_tbl where user_invited = '" + 1 + "'" + " and blood_group IS NULL";

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    BloodyFriendVO contact = new BloodyFriendVO();
                    contact.setContactId(cursor.getInt(cursor.getColumnIndex("contact_id")));
                    contact.setBloodGroup(cursor.getString(cursor.getColumnIndex("blood_group")));
                    contact.setLookUpId(cursor.getString(cursor.getColumnIndex("lookup_id")));
                    contact.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                    contact.setUpdate_status(cursor.getInt(cursor.getColumnIndex("update_status")));
                    contact.setContactName(cursor.getString(cursor.getColumnIndex("phone_name")));
                    contact.setContactType(cursor.getInt(cursor.getColumnIndex("contact_type")));
                    contact.setContact(cursor.getString(cursor.getColumnIndex("phone_number")));
                    if (cursor.getInt(cursor.getColumnIndex("user_invited")) == 1) {
                        contact.setUserInvited(true);
                    } else {
                        contact.setUserInvited(false);
                    }
                    contactslist.add(contact);

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
        return contactslist;
    }

    public ArrayList<BloodyFriendVO> getAllPhoneContactsSearch(String search_text) {

        ArrayList<BloodyFriendVO> contactslist = new ArrayList<BloodyFriendVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM blood_friends_tbl where phone_name like " + "'%" + search_text + "%' or phone_number like " + "'%" + search_text + "%'";

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    BloodyFriendVO contact = new BloodyFriendVO();
                    contact.setContactId(cursor.getInt(cursor.getColumnIndex("contact_id")));
                    contact.setBloodGroup(cursor.getString(cursor.getColumnIndex("blood_group")));
                    contact.setLookUpId(cursor.getString(cursor.getColumnIndex("lookup_id")));
                    contact.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                    contact.setUpdate_status(cursor.getInt(cursor.getColumnIndex("update_status")));
                    contact.setContactName(cursor.getString(cursor.getColumnIndex("phone_name")));
                    contact.setContactType(cursor.getInt(cursor.getColumnIndex("contact_type")));
                    contact.setContact(cursor.getString(cursor.getColumnIndex("phone_number")));
                    if (cursor.getInt(cursor.getColumnIndex("user_invited")) == 1) {
                        contact.setUserInvited(true);
                    } else {
                        contact.setUserInvited(false);
                    }
                    contactslist.add(contact);

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
        return contactslist;
    }


    public int getPhoneContactsInfoCount() {

        Cursor cursor = null;
        int count = 0;
        String selectQuery = "SELECT * from blood_friends_tbl";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            count = cursor.getCount();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            count = 0;
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            count = 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return count;
    }

    public int updatePhoneContactsStatus(int _id) {
        int update = -1;
        try {
            openDatabaseAndBeginTransactionBF();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("update_status", 1);
            mContentValues.put("update_time", AppUtil.getDate());
            String[] args = new String[]{String.valueOf(_id)};
            update = database.update("blood_friends_tbl", mContentValues, "_id=?", args);
            setTransactionSuccessful();
            ;
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabaseBF();
        }
        return update;
    }

    public int updatePhoneInviteStatus(int _id, int user_invited) {
        int update = -1;
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("user_invited", user_invited);
            String[] args = new String[]{String.valueOf(_id)};
            update = database.update("blood_friends_tbl", mContentValues, "_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
        return update;
    }

    public void updatePhoneContactsDetails(ArrayList<BloodyFriendVO> contactslist) throws Exception {
        try {
            openDatabaseAndBeginTransaction();

            com.patientz.utils.Log.d(TAG, "contactslist ---> " + contactslist.size());

            for (int i = 0; i < contactslist.size(); i++) {

                BloodyFriendVO bloodyFriendVO = contactslist.get(i);

                ContentValues mContentValues = new ContentValues();
                mContentValues.put("blood_group", bloodyFriendVO.getBloodGroup());
                Log.d(TAG, "blood_group=" + bloodyFriendVO.getBloodGroup());
                mContentValues.put("server_name", bloodyFriendVO.getServerContactName());
                mContentValues.put("user_invited", bloodyFriendVO.isUserInvited());
                mContentValues.put("download_time", AppUtil.getDate());

                database.update("blood_friends_tbl", mContentValues, "contact_type ='" + bloodyFriendVO.getContactType() + "'" + " and phone_number=" + "'" + bloodyFriendVO.getContact() + "'", null);
//                database.update("blood_friends_tbl", mContentValues, "lookup_id ='" + bloodyFriendVO.getLookUpId() + "' and contact_type ='" + bloodyFriendVO.getContactType() + "'" + " and phone_number=" + "'" + bloodyFriendVO.getContact() + "'", null);

                Log.d(TAG, "$$$----> " + "contact_type ='" + bloodyFriendVO.getContactType() + "'" + " and phone_number=" + "'" + bloodyFriendVO.getContact() + "'");
            }
            setTransactionSuccessful();
            EndTransaction();


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
    }

    public void insertPhoneContactsInfo(ArrayList<BloodyFriendVO> contactslist) throws Exception {

        if (contactslist.size() > 0) {
            try {
                openDatabaseAndBeginTransaction();

                com.patientz.utils.Log.d(TAG, "contactslist ---> " + contactslist.size());

                for (int i = 0; i < contactslist.size(); i++) {

                    BloodyFriendVO bloodyFriendVO = contactslist.get(i);

                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put("contact_id", bloodyFriendVO.getContactId());
                    mContentValues.put("lookup_id", bloodyFriendVO.getLookUpId());
                    mContentValues.put("phone_number", bloodyFriendVO.getContact());
                    mContentValues.put("phone_name", bloodyFriendVO.getContactName());
                    mContentValues.put("contact_type", bloodyFriendVO.getContactType());
                    mContentValues.put("user_invited", bloodyFriendVO.isUserInvited());
                    mContentValues.put("update_status", 0);

                    database.insertWithOnConflict("blood_friends_tbl", null, mContentValues, SQLiteDatabase.CONFLICT_REPLACE);

                }
                setTransactionSuccessful();
                EndTransaction();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                closeDatabase();
            }
        }
    }

    public void insertPhoneContactsInfo2(ArrayList<BloodyFriendVO> contactslist) throws Exception {
        try {
            openDatabaseAndBeginTransaction();
            for (BloodyFriendVO bloodyFriendVO : contactslist) {
                Log.d("phoneBookContact>>Contact=", bloodyFriendVO.getContact() + "");
                if (!TextUtils.isEmpty(bloodyFriendVO.getContact())) {
                    if (checkIfContactExistInDB(bloodyFriendVO.getContact()) == 0) {
                        Log.d(TAG, "***inserting " + bloodyFriendVO.getContact() + " into DB***");
                        ContentValues mContentValues = new ContentValues();
                        mContentValues.put("contact_id", bloodyFriendVO.getContactId());
                        mContentValues.put("lookup_id", bloodyFriendVO.getLookUpId());
                        mContentValues.put("phone_number", bloodyFriendVO.getContact());
                        mContentValues.put("phone_name", bloodyFriendVO.getContactName());
                        mContentValues.put("contact_type", bloodyFriendVO.getContactType());
                        mContentValues.put("user_invited", bloodyFriendVO.isUserInvited());
                        mContentValues.put("update_status", 0);
                        database.insertWithOnConflict("blood_friends_tbl", null, mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setTransactionSuccessful();
            EndTransaction();
            closeDatabase();
        }
    }

    public void insertPhoneContactsInfoDUPLICATE(ArrayList<BloodyFriendVO> contactslist) throws Exception {

        if (contactslist.size() > 0) {
            try {

                com.patientz.utils.Log.d(TAG, "contactslist ---> " + contactslist.size());
                openDatabaseAndBeginTransaction();

                for (int i = 0; i < contactslist.size(); i++) {

                    BloodyFriendVO bloodyFriendVO = contactslist.get(i);

                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put("contact_id", bloodyFriendVO.getContactId());
                    mContentValues.put("lookup_id", bloodyFriendVO.getLookUpId());
                    mContentValues.put("phone_number", bloodyFriendVO.getContact());
                    mContentValues.put("phone_name", bloodyFriendVO.getContactName());
                    mContentValues.put("contact_type", bloodyFriendVO.getContactType());
                    mContentValues.put("user_invited", bloodyFriendVO.isUserInvited());
                    mContentValues.put("blood_group", bloodyFriendVO.getBloodGroup());
                    mContentValues.put("update_status", -1);

                    database.insertWithOnConflict("blood_friends_tbl", null, mContentValues, SQLiteDatabase.CONFLICT_REPLACE);

                }
                setTransactionSuccessful();
                EndTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                closeDatabase();
            }
        }
    }


    public void insertUserInfo(UserRecordVO userRecordVO, String bloodGroup, String patientHandle) throws Exception {
        //Log.d(TAG, "Checking user name =" + user.getFirstName());
        Log.d(TAG, "****** insertUserInfo******");

        UserInfoVO userInfoVO = userRecordVO.getUserInfoVO();
        if (userRecordVO != null && userInfoVO != null) {
            try {
                openDatabaseAndBeginTransaction();
                ContentValues mContentValues = new ContentValues();
                mContentValues.put("patient_id", userInfoVO.getPatientId());
                Log.d(TAG, "PATIENT NAME=" + userInfoVO.getFirstName());
                mContentValues.put("first_name", userInfoVO.getFirstName());
                mContentValues.put("last_name", userInfoVO.getLastName());
                mContentValues.put("relationship", userInfoVO.getRelationship());
                mContentValues.put("role", userInfoVO.getRole());
                mContentValues.put("address", userInfoVO.getAddress());
                mContentValues.put("company_name", userInfoVO.getCompanyName());
                mContentValues.put("city", userInfoVO.getCity());
                mContentValues.put("state", userInfoVO.getState());
                mContentValues.put("country", userInfoVO.getCountry());
                mContentValues.put("pin_code", userInfoVO.getPinCode());
                mContentValues.put("phone_number", AppUtil.getPhNoWithoutIsdCode(userInfoVO.getPhoneNumber()));
                mContentValues.put("phone_number_isd_code", userInfoVO.getPhoneNumberIsdCode());
                mContentValues.put("alt_phone_number", AppUtil.getPhNoWithoutIsdCode(userInfoVO.getAltPhoneNumber()));
                Log.d(TAG, "HELLO");
                mContentValues.put("alt_phoneNumber_isd_code", userInfoVO.getAltPhoneNumberIsdCode());
                mContentValues.put("gender", userInfoVO.getGender());
                mContentValues.put("age", userInfoVO.getAge());
                mContentValues.put("email_id", userInfoVO.getEmailId());
                mContentValues.put("pic_file_id", userInfoVO.getPicFileId());
                mContentValues.put("pic_file_name", userInfoVO.getPicFileName());
                mContentValues.put("emergency_level", userInfoVO.getEmergencyLevel());
                mContentValues.put("financial_status", userInfoVO.getFinancialStatus());
                Log.d(TAG, "HELLO");

                mContentValues.put("family_history", userInfoVO.getFamilyHistory());
                mContentValues.put("habits", userInfoVO.getHabits());
                mContentValues.put("remarks", userInfoVO.getRemarks());

                Log.d(TAG, "getBloodGroup===" + bloodGroup);

                mContentValues.put("patient_handle", patientHandle);

                Log.d(TAG, "patientHandle===" + patientHandle);

                if (bloodGroup == null) {
                    mContentValues.put("blood_group", "");
                } else {
                    mContentValues.put("blood_group", bloodGroup);
                }
                CommonUtils.getSP(context).edit().putString("bloodGroup", bloodGroup).apply();

                mContentValues.put("blood_donation", userInfoVO.getBloodDonation());
                mContentValues.put("organ_donation", userInfoVO.getOrganDonation());
                mContentValues.put("marital_status", userInfoVO.getMaritalStatus());
                mContentValues.put("food_habits", userInfoVO.getFoodHabits());
                mContentValues.put("date_of_birth", userInfoVO.getDateOfBirth() != null ? userInfoVO.getDateOfBirth().getTime() : null);
                Log.d(TAG, "LAst blood donated date=" + userInfoVO.getLastBloodDonationDate());
                Log.d(TAG, "notifyBloodDonationRequest=" + userInfoVO.isNotifyBloodDonationRequest());
                Log.d(TAG, "preferred_blood_bank_id=" + userInfoVO.getPreferredBloodBankId());

                mContentValues.put("last_blood_donation_date", userInfoVO.getLastBloodDonationDate() != null ? userInfoVO.getLastBloodDonationDate().getTime() : null);
                mContentValues.put("notify_blood_donation_request", userInfoVO.isNotifyBloodDonationRequest());


                mContentValues.put("created_by", userInfoVO.getCreatedBy());
                mContentValues.put("updated_by", userInfoVO.getUpdatedBy());

                mContentValues.put("created_date", userInfoVO.getCreatedDate() != null ? userInfoVO.getCreatedDate().getTime() : null);
                mContentValues.put("updated_date", userInfoVO.getUpdatedDate() != null ? userInfoVO.getUpdatedDate().getTime() : null);
                Log.d(TAG, "Preferred ambulance provider is " + userInfoVO.getPreferredOrgBranchId());

                if (userInfoVO.getPreferredOrgBranchId() == 0) {
                    Log.d(TAG, "Preferred ambulance provider is " + userInfoVO.getPreferredOrgBranchId());
                    if (userRecordVO != null) {
                        Log.d(TAG, "User Record VO is" + userRecordVO);

                        if (userRecordVO.getHealthRecordVO() != null) {
                            Log.d(TAG, "Emhr Exists" + userRecordVO);
                            String preferredOrgBranchId = userRecordVO.getHealthRecordVO().getHealthRecord().get("preferredOrgBranchId");
                            Log.d(TAG, "Preferred Org Branch id=" + preferredOrgBranchId);
                            mContentValues.put("preferred_org_branch_id", preferredOrgBranchId);
                        }
                    }
                } else {
                    Log.d(TAG, "Preferred Org Branch id>>>>" + userInfoVO.getPreferredOrgBranchId());
                    mContentValues.put("preferred_org_branch_id", userInfoVO.getPreferredOrgBranchId());
                }

                mContentValues.put("preferred_blood_bank_id", userInfoVO.getPreferredBloodBankId());

                database.insertWithOnConflict("user_info_tbl", null,
                        mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
            } catch (SQLException e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } finally {
                setTransactionSuccessful();
                EndTransaction();
                closeDatabase();
            }
        }
    }


    public UserInfoVO getUserInfo(long patientId) throws Exception {
        UserInfoVO userInfoVO = new UserInfoVO();
        Cursor cursor = null;
        // Select All Query
        String selectQuery = "SELECT * from user_info_tbl WHERE patient_id ='" + patientId + "'";
        Log.d(TAG, "Query : " + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {

                Log.d(TAG, "@patient_handle@ : " + cursor.getString(cursor
                        .getColumnIndex("patient_handle")));
                Log.d(TAG, "@blood_group@ : " + cursor.getString(cursor
                        .getColumnIndex("blood_group")));

                userInfoVO.setPatientId(cursor.getLong(cursor
                        .getColumnIndex("patient_id")));
                userInfoVO.setFirstName(cursor.getString(cursor
                        .getColumnIndex("first_name")));
                userInfoVO.setLastName(cursor.getString(cursor
                        .getColumnIndex("last_name")));
                userInfoVO.setRelationship(cursor.getString(cursor
                        .getColumnIndex("relationship")));
                userInfoVO.setAddress(cursor.getString(cursor
                        .getColumnIndex("address")));
                userInfoVO.setCountry(cursor.getString(cursor
                        .getColumnIndex("country")));
                userInfoVO.setCity(cursor.getString(cursor
                        .getColumnIndex("city")));
                userInfoVO.setRole(cursor.getString(cursor
                        .getColumnIndex("role")));
                userInfoVO.setCompanyName(cursor.getString(cursor
                        .getColumnIndex("company_name")));
                userInfoVO.setState(cursor.getString(cursor
                        .getColumnIndex("state")));
                userInfoVO.setPinCode(cursor.getString(cursor
                        .getColumnIndex("pin_code")));
                userInfoVO.setPhoneNumber(cursor.getString(cursor
                        .getColumnIndex("phone_number")));
                userInfoVO.setPhoneNumberIsdCode(cursor.getString(cursor
                        .getColumnIndex("phone_number_isd_code")));
                userInfoVO.setAltPhoneNumber(cursor.getString(cursor
                        .getColumnIndex("alt_phone_number")));
                userInfoVO.setAltPhoneNumberIsdCode(cursor.getString(cursor
                        .getColumnIndex("alt_phoneNumber_isd_code")));
                userInfoVO.setGender(cursor.getInt(cursor
                        .getColumnIndex("gender")));
                userInfoVO.setAge(cursor.getInt(cursor
                        .getColumnIndex("age")));
                userInfoVO.setEmailId(cursor.getString(cursor
                        .getColumnIndex("email_id")));
                userInfoVO.setPicFileId(cursor.getString(cursor
                        .getColumnIndex("pic_file_id")));
                userInfoVO.setPicFileName(cursor.getString(cursor
                        .getColumnIndex("pic_file_name")));
                userInfoVO.setEmergencyLevel(cursor.getInt(cursor
                        .getColumnIndex("emergency_level")));
                userInfoVO.setFinancialStatus(cursor.getString(cursor
                        .getColumnIndex("financial_status")));
                userInfoVO.setFamilyHistory(cursor.getString(cursor
                        .getColumnIndex("family_history")));
                userInfoVO.setHabits(cursor.getString(cursor
                        .getColumnIndex("habits")));
                userInfoVO.setRemarks(cursor.getString(cursor
                        .getColumnIndex("remarks")));
                userInfoVO.setBloodGroup(cursor.getString(cursor
                        .getColumnIndex("blood_group")));
                userInfoVO.setPatientHandle(cursor.getString(cursor
                        .getColumnIndex("patient_handle")));
                userInfoVO.setBloodDonation(cursor.getString(cursor
                        .getColumnIndex("blood_donation")));
                userInfoVO.setOrganDonation(cursor.getString(cursor
                        .getColumnIndex("organ_donation")));
                userInfoVO.setMaritalStatus(cursor.getString(cursor
                        .getColumnIndex("marital_status")));
                userInfoVO.setFoodHabits(cursor.getString(cursor
                        .getColumnIndex("food_habits")));
                userInfoVO.setNotifyBloodDonationRequest(cursor.getInt(cursor
                        .getColumnIndex("notify_blood_donation_request")) == 1 ? true : false);

                Calendar dob = Calendar.getInstance();
                dob.setTimeInMillis(cursor.getLong(cursor
                        .getColumnIndex("date_of_birth")));
                userInfoVO.setDateOfBirth(dob.getTime());

                if (cursor.getLong(cursor
                        .getColumnIndex("last_blood_donation_date")) != 0) {
                    Calendar calLastBloodDonatedDate = Calendar.getInstance();
                    calLastBloodDonatedDate.setTimeInMillis(cursor.getLong(cursor
                            .getColumnIndex("last_blood_donation_date")));
                    userInfoVO.setLastBloodDonationDate(calLastBloodDonatedDate.getTime());
                }

                userInfoVO.setPreferredOrgBranchId(cursor.getLong(cursor
                        .getColumnIndex("preferred_org_branch_id")));
                userInfoVO.setPreferredBloodBankId(cursor.getLong(cursor
                        .getColumnIndex("preferred_blood_bank_id")));
                userInfoVO.setCreatedBy(cursor.getLong(cursor
                        .getColumnIndex("created_by")));
                userInfoVO.setUpdatedBy(cursor.getLong(cursor
                        .getColumnIndex("updated_by")));
                Calendar createDate = Calendar.getInstance();
                createDate.setTimeInMillis(cursor.getLong(cursor
                        .getColumnIndex("created_date")));
                userInfoVO.setCreatedDate(createDate.getTime());
                Calendar updateDate = Calendar.getInstance();
                updateDate.setTimeInMillis(cursor.getLong(cursor
                        .getColumnIndex("updated_date")));
                userInfoVO.setUpdatedDate(updateDate.getTime());
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return userInfoVO;
    }


    public void insertUserInsuranceDetails(InsuranceVO insuranceVO) {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        if (insuranceVO != null) {
            try {
                openDatabaseAndBeginTransaction();
                ContentValues mContentValues = new ContentValues();
                mContentValues.put("insurance_id", insuranceVO.getInsuranceId());
                mContentValues.put("ins_policy_company", insuranceVO.getInsPolicyCompany());
                mContentValues.put("ins_policy_coverage", insuranceVO.getInsPolicyCoverage());
                mContentValues.put("ins_policy_name", insuranceVO.getInsPolicyName());
                mContentValues.put("ins_policy_number", insuranceVO.getInsPolicyNo());
                mContentValues.put("patient_id", insuranceVO.getPatientId());
                if (insuranceVO.getInsPolicyStartDate() != null) {
                    mContentValues.put("ins_policy_start_date", insuranceVO.getInsPolicyStartDate().getTime());
                }
                if (insuranceVO.getInsPolicyEndDate() != null) {
                    mContentValues.put("ins_policy_end_date", insuranceVO.getInsPolicyEndDate().getTime());
                }
                mContentValues.put("ins_policy_claim_number", insuranceVO.getClaimPhoneNumber());
                if (insuranceVO.getCreatedDate() != null) {
                    mContentValues.put("date_created", insuranceVO.getCreatedDate().getTime());
                }
                if (insuranceVO.getUpdatedDate() != null) {
                    mContentValues.put("date_updated", insuranceVO.getUpdatedDate().getTime());
                }
                mContentValues.put("created_by", insuranceVO.getCreatedBy());
                mContentValues.put("updated_by", insuranceVO.getUpdatedBy());
                mContentValues.put("insurance_upload_id", insuranceVO.getInsuranceUploadId());
                mContentValues.put("paytm_ref_id", insuranceVO.getPaytmRefId());


                long i = database.insertWithOnConflict("insurance_info_tbl", null,
                        mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "insertUserInsuranceDetails " + mContentValues + "\n db insert : " + i);
                // sendBroadcast(Constant.ACTION_INSURANCE_DATA_CHANGED);
            } catch (SQLException e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } finally {
                setTransactionSuccessful();
                EndTransaction();
                closeDatabase();
            }
        }
    }

    public ArrayList<InsuranceVO> getAllInsurances(long patientId)
            throws Exception {
        ArrayList<InsuranceVO> insuranceList = new ArrayList<InsuranceVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM insurance_info_tbl WHERE patient_id='"
                + patientId + "'";
        Log.d(TAG, "getAllInsurances=\n" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    InsuranceVO insuranceVO = new InsuranceVO();
                    insuranceVO.setInsuranceId(cursor.getLong(cursor
                            .getColumnIndex("insurance_id")));

                    Log.d(TAG, "insurance_id=" + cursor.getLong(cursor
                            .getColumnIndex("insurance_id")));
                    insuranceVO.setInsPolicyCompany(cursor.getString(cursor
                            .getColumnIndex("ins_policy_company")));


                    insuranceVO.setInsPolicyCoverage(cursor.getString(cursor
                            .getColumnIndex("ins_policy_coverage")));


                    insuranceVO.setInsPolicyName(cursor.getString(cursor
                            .getColumnIndex("ins_policy_name")));


                    insuranceVO.setInsPolicyNo(cursor.getString(cursor
                            .getColumnIndex("ins_policy_number")));


                    insuranceVO.setPatientId(cursor.getLong(cursor
                            .getColumnIndex("patient_id")));


                    insuranceVO.setInsPolicyStartDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("ins_policy_start_date"))));
                    insuranceVO.setInsPolicyEndDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("ins_policy_end_date"))));
                    insuranceVO.setClaimPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("ins_policy_claim_number")));


                    insuranceVO.setCreatedDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("date_created"))));
                    insuranceVO.setUpdatedDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("date_updated"))));
                    insuranceVO.setCreatedBy(cursor.getLong(cursor
                            .getColumnIndex("created_by")));


                    insuranceVO.setUpdatedBy(cursor.getLong(cursor
                            .getColumnIndex("updated_by")));


                    insuranceVO.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));


                    insuranceVO.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));


                    insuranceVO.setNotExisitingPolicy(cursor.getInt(cursor
                            .getColumnIndex("is_not_existing_insurance_policy")) == 1 ? true : false);


                    insuranceVO.setEmailVerified(cursor.getInt(cursor
                            .getColumnIndex("is_email_verified")) == 1 ? true : false);


                    insuranceVO.setAadharVerified(cursor.getInt(cursor
                            .getColumnIndex("is_aadhar_verified")) == 1 ? true : false);


                    insuranceVO.setMobileNumberVerified(cursor.getInt(cursor
                            .getColumnIndex("is_mobile_number_verified")) == 1 ? true : false);


                    insuranceVO.setPolicyDoc((UserUploadedMedia) AppUtil.convertJsonToGson(cursor.getString(cursor
                            .getColumnIndex("policy_doc"))));


                    if (cursor.getLong(cursor
                            .getColumnIndex("sent_date")) != 0) {
                        insuranceVO.setSentDate(getDate(cursor.getLong(cursor
                                .getColumnIndex("sent_date"))));
                    }
                    insuranceVO.setSentDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("sent_date"))));
                    insuranceVO.setStatus(cursor.getInt(cursor
                            .getColumnIndex("status")));


                    insuranceVO.setCustomerId(cursor.getString(cursor
                            .getColumnIndex("customer_id")));


                    insuranceVO.setPinCode(cursor.getString(cursor
                            .getColumnIndex("pincode")));


                    insuranceVO.setCity(cursor.getString(cursor
                            .getColumnIndex("city")));


                    insuranceVO.setAddress2(cursor.getString(cursor
                            .getColumnIndex("address2")));


                    insuranceVO.setAddress1(cursor.getString(cursor
                            .getColumnIndex("address1")));


                    insuranceVO.setMobileNumber(cursor.getString(cursor
                            .getColumnIndex("mobile_number")));


                    insuranceVO.setEmail(cursor.getString(cursor
                            .getColumnIndex("email")));


                    insuranceVO.setAadharNo(cursor.getString(cursor
                            .getColumnIndex("aadhar_no")));

                    insuranceVO.setDateOfBirth(getDate(cursor.getLong(cursor
                            .getColumnIndex("date_of_birth"))));

                    insuranceVO.setGender(cursor.getInt(cursor
                            .getColumnIndex("gender")));
                    insuranceVO.setInsuranceUploadId(cursor.getLong(cursor
                            .getColumnIndex("insurance_upload_id")));
                    insuranceVO.setPaytmRefId(cursor.getString(cursor
                            .getColumnIndex("paytm_ref_id")));

                    insuranceList.add(insuranceVO);
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            Log.i(TAG, "getAllInsurances>>Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "getAllInsurances>>Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        Log.d("insuranceList size", insuranceList.size() + "");
        return insuranceList;
    }

    public InsuranceVO getInsurance(long patientId, long insuranceID)
            throws Exception {
        InsuranceVO insuranceVO = new InsuranceVO();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM insurance_info_tbl WHERE patient_id=" + patientId + " AND insurance_id='"
                + insuranceID + "'";
        Log.d(TAG, "selectQuery=" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                insuranceVO.setInsuranceId(cursor.getLong(cursor
                        .getColumnIndex("insurance_id")));
                insuranceVO.setInsPolicyCompany(cursor.getString(cursor
                        .getColumnIndex("ins_policy_company")));
                insuranceVO.setInsPolicyCoverage(cursor.getString(cursor
                        .getColumnIndex("ins_policy_coverage")));
                insuranceVO.setInsPolicyName(cursor.getString(cursor
                        .getColumnIndex("ins_policy_name")));
                insuranceVO.setInsPolicyNo(cursor.getString(cursor
                        .getColumnIndex("ins_policy_number")));
                insuranceVO.setPatientId(cursor.getLong(cursor
                        .getColumnIndex("patient_id")));
                Log.d(TAG, "ins_policy_start_date=" + getDate(cursor.getLong(cursor
                        .getColumnIndex("ins_policy_start_date"))));
                insuranceVO.setInsPolicyStartDate(getDate(cursor.getLong(cursor
                        .getColumnIndex("ins_policy_start_date"))));
                Log.d(TAG, "ins_policy_start_date=" + getDate(cursor.getLong(cursor
                        .getColumnIndex("ins_policy_end_date"))));
                insuranceVO.setInsPolicyEndDate(getDate(cursor.getLong(cursor
                        .getColumnIndex("ins_policy_end_date"))));
                insuranceVO.setClaimPhoneNumber(cursor.getString(cursor
                        .getColumnIndex("ins_policy_claim_number")));
                insuranceVO.setCreatedDate(getDate(cursor.getLong(cursor
                        .getColumnIndex("date_created"))));
                insuranceVO.setUpdatedDate(getDate(cursor.getLong(cursor
                        .getColumnIndex("date_updated"))));
                insuranceVO.setCreatedBy(cursor.getLong(cursor
                        .getColumnIndex("created_by")));
                insuranceVO.setUpdatedBy(cursor.getLong(cursor
                        .getColumnIndex("updated_by")));

                insuranceVO.setFirstName(cursor.getString(cursor
                        .getColumnIndex("first_name")));
                insuranceVO.setLastName(cursor.getString(cursor
                        .getColumnIndex("last_name")));
                insuranceVO.setNotExisitingPolicy(cursor.getInt(cursor
                        .getColumnIndex("is_not_existing_insurance_policy")) == 1 ? true : false);
                insuranceVO.setEmailVerified(cursor.getInt(cursor
                        .getColumnIndex("is_email_verified")) == 1 ? true : false);
                insuranceVO.setAadharVerified(cursor.getInt(cursor
                        .getColumnIndex("is_mobile_number_verified")) == 1 ? true : false);
                Log.d(TAG, "MobileVerified=" + cursor.getInt(cursor
                        .getColumnIndex("is_mobile_number_verified")));
                insuranceVO.setMobileNumberVerified(cursor.getInt(cursor
                        .getColumnIndex("is_mobile_number_verified")) == 1 ? true : false);
                insuranceVO.setPolicyDoc((UserUploadedMedia) AppUtil.convertJsonToGson(cursor.getString(cursor
                        .getColumnIndex("policy_doc"))));
                insuranceVO.setSentDate(getDate(cursor.getLong(cursor
                        .getColumnIndex("sent_date"))));
                insuranceVO.setStatus(cursor.getInt(cursor
                        .getColumnIndex("status")));
                insuranceVO.setCustomerId(cursor.getString(cursor
                        .getColumnIndex("customer_id")));
                insuranceVO.setPinCode(cursor.getString(cursor
                        .getColumnIndex("pincode")));
                insuranceVO.setCity(cursor.getString(cursor
                        .getColumnIndex("city")));
                insuranceVO.setAddress2(cursor.getString(cursor
                        .getColumnIndex("address2")));
                insuranceVO.setAddress1(cursor.getString(cursor
                        .getColumnIndex("address1")));
                insuranceVO.setMobileNumber(cursor.getString(cursor
                        .getColumnIndex("mobile_number")));
                insuranceVO.setEmail(cursor.getString(cursor
                        .getColumnIndex("email")));
                insuranceVO.setAadharNo(cursor.getString(cursor
                        .getColumnIndex("aadhar_no")));
                insuranceVO.setDateOfBirth(getDate(cursor.getLong(cursor
                        .getColumnIndex("date_of_birth"))));
                insuranceVO.setGender(cursor.getInt(cursor
                        .getColumnIndex("gender")));
                Log.d(TAG, "IUploadId=" + cursor.getLong(cursor
                        .getColumnIndex("insurance_upload_id")));
                insuranceVO.setInsuranceUploadId(cursor.getLong(cursor
                        .getColumnIndex("insurance_upload_id")));
                insuranceVO.setPaytmRefId(cursor.getString(cursor
                        .getColumnIndex("paytm_ref_id")));
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
        return insuranceVO;
    }


    private Date getDate(long time) {
        if (time == 0)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.getTime();
    }


    private String decryptApiKey(String encryptedKey) {
        Log.d(TAG, "encypted pin.." + encryptedKey);
        String key = "D0ctrz12";
        String apiKey = null;
        try {
            SecretKey mSecretKey = AppCrypto.getSecretKey(key, key);
            apiKey = AppCrypto.decrypt(mSecretKey, encryptedKey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return apiKey;
    }

    public void markUserAsCurrentlySelected(long patientId)
            throws Exception {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues valuesOld = new ContentValues();
            valuesOld.put("current_selected_patient", false);
            ContentValues valuesNew = new ContentValues();
            valuesNew.put("current_selected_patient", true);
            database.update("user_tbl", valuesOld, null, null);
            database.update("user_tbl", valuesNew, "patient_id=?",
                    new String[]{String.valueOf(patientId)});
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            // Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            // Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            closeDatabase();
        }
    }


    public PatientUserVO getProfile(long patientId) throws Exception {
        PatientUserVO user = new PatientUserVO();
        Cursor cursor = null;
        // Select All Query
        String selectQuery = "SELECT * from user_tbl WHERE patient_id ='"
                + patientId + "'";
        Log.d(TAG, "getProfile query=" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                user.setUserProfileId(cursor.getLong(cursor
                        .getColumnIndex("user_id")));
                user.setPatientId(cursor.getLong(cursor
                        .getColumnIndex("patient_id")));
                user.setFirstName(cursor.getString(cursor
                        .getColumnIndex("first_name")));
                user.setLastName(cursor.getString(cursor
                        .getColumnIndex("last_name")));
                user.setEventLogId(cursor.getLong(cursor
                        .getColumnIndex("event_log_id")));
                user.setPhoneNumber(cursor.getString(cursor
                        .getColumnIndex("phone_number")));
                user.setFileName(cursor.getString(cursor
                        .getColumnIndex("filename")));
                user.setSharePoints(cursor.getLong(cursor
                        .getColumnIndex("share_points")));
                Log.d(TAG, "SHARE POINTS=" + cursor.getLong(cursor
                        .getColumnIndex("share_points")));
                user.setRelationship(cursor.getString(cursor
                        .getColumnIndex("relationship")));
                user.setEmergencyToken(cursor.getString(cursor
                        .getColumnIndex("emergency_token")));
                user.setRole(cursor.getString(cursor.getColumnIndex("role")));
                user.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                user.setPatientAccessId(cursor.getLong(cursor
                        .getColumnIndex("patient_access_id")));
                user.setPicId(cursor.getLong(cursor.getColumnIndex("pic_id")));
                user.setPicName(cursor.getString(cursor
                        .getColumnIndex("pic_name")));
                user.setPreferredAmbulancePhoneNo(cursor.getString(cursor
                        .getColumnIndex("preffered_ambulanc_provider_pno")));
                user.setCurrentSelectedPatient(cursor.getString(cursor
                        .getColumnIndex("current_selected_patient")));
                Log.d(TAG, "getProfile>>PREFFERED AMBULANCE PROVIDER=" + cursor.getString(cursor
                        .getColumnIndex("preffered_ambulanc_provider_pno")));
                user.setEmergencyTokenUrl(cursor.getString(cursor
                        .getColumnIndex("emergency_url_token")));
                user.setUnderEmergency((cursor.getInt(cursor
                        .getColumnIndex("under_emergency"))) == 1 ? true
                        : false);
                String apiKey = cursor.getString(cursor
                        .getColumnIndex("emergency_api_key"));
                user.setEmergencyAPIKEY(apiKey);
                Log.d(TAG, user.getEmergencyAPIKEY());
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return user;
    }

    public PatientUserVO getEmergencyProfile(long patientId)
            throws Exception {
        Log.d(TAG, "getEmergencyProfile");
        PatientUserVO user = new PatientUserVO();
        Cursor cursor = null;
        // Select All Query
        String selectQuery = "SELECT * from user_tbl WHERE patient_id ='"
                + patientId + "'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                user.setUserProfileId(cursor.getLong(cursor
                        .getColumnIndex("user_id")));
                user.setPatientId(cursor.getLong(cursor
                        .getColumnIndex("patient_id")));
                user.setFirstName(cursor.getString(cursor
                        .getColumnIndex("first_name")));
                user.setLastName(cursor.getString(cursor
                        .getColumnIndex("last_name")));
                user.setEventLogId(cursor.getLong(cursor
                        .getColumnIndex("event_log_id")));
                user.setPhoneNumber(cursor.getString(cursor
                        .getColumnIndex("phone_number")));
                user.setFileName(cursor.getString(cursor
                        .getColumnIndex("filename")));
                user.setSharePoints(cursor.getLong(cursor
                        .getColumnIndex("share_points")));
                user.setRelationship(cursor.getString(cursor
                        .getColumnIndex("relationship")));
                user.setEmergencyToken(cursor.getString(cursor
                        .getColumnIndex("emergency_token")));
                user.setRole(cursor.getString(cursor.getColumnIndex("role")));
                user.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                user.setPatientAccessId(cursor.getLong(cursor
                        .getColumnIndex("patient_access_id")));
                user.setPicId(cursor.getLong(cursor.getColumnIndex("pic_id")));
                user.setPicName(cursor.getString(cursor
                        .getColumnIndex("pic_name")));
                user.setEmergencyTokenUrl(cursor.getString(cursor
                        .getColumnIndex("emergency_url_token")));
                user.setCurrentSelectedPatient(cursor.getString(cursor
                        .getColumnIndex("current_selected_patient")));
                Log.d(TAG, "getEmergencyProfile>>CURRENT SELECTED =" + cursor.getString(cursor
                        .getColumnIndex("current_selected_patient")));
                Log.d(TAG, "getEmergencyProfile>>PREFERRED AMBULANCE PROVIDER=" + cursor.getString(cursor
                        .getColumnIndex("preffered_ambulanc_provider_pno")));
                user.setUnderEmergency((cursor.getInt(cursor
                        .getColumnIndex("under_emergency"))) == 1 ? true
                        : false);
                String apiKey = cursor.getString(cursor
                        .getColumnIndex("emergency_api_key"));
                user.setEmergencyAPIKEY(apiKey);
                Log.d(TAG, user.getEmergencyAPIKEY());
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return user;
    }

    public PatientUserVO getProfile(String relationship) throws Exception {
        PatientUserVO user = new PatientUserVO();
        Cursor cursor = null;
        // Select All Query
        String selectQuery = "SELECT * from user_tbl WHERE relationship ='"
                + relationship + "'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                user.setUserProfileId(cursor.getLong(cursor
                        .getColumnIndex("user_id")));
                user.setPatientId(cursor.getLong(cursor
                        .getColumnIndex("patient_id")));
                user.setFirstName(cursor.getString(cursor
                        .getColumnIndex("first_name")));
                user.setLastName(cursor.getString(cursor
                        .getColumnIndex("last_name")));
                user.setEventLogId(cursor.getLong(cursor
                        .getColumnIndex("event_log_id")));
                user.setPhoneNumber(cursor.getString(cursor
                        .getColumnIndex("phone_number")));
                user.setSharePoints(cursor.getLong(cursor
                        .getColumnIndex("share_points")));
                user.setFileName(cursor.getString(cursor
                        .getColumnIndex("filename")));
                user.setRelationship(cursor.getString(cursor
                        .getColumnIndex("relationship")));
                user.setEmergencyToken(cursor.getString(cursor
                        .getColumnIndex("emergency_token")));
                user.setRole(cursor.getString(cursor.getColumnIndex("role")));
                user.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                user.setPatientAccessId(cursor.getLong(cursor
                        .getColumnIndex("patient_access_id")));
                user.setPicId(cursor.getLong(cursor.getColumnIndex("pic_id")));
                user.setPicName(cursor.getString(cursor
                        .getColumnIndex("pic_name")));

                user.setPreferredAmbulancePhoneNo(cursor.getString(cursor
                        .getColumnIndex("preffered_ambulanc_provider_pno")));
                user.setCurrentSelectedPatient(cursor.getString(cursor
                        .getColumnIndex("current_selected_patient")));

                user.setEmergencyTokenUrl(cursor.getString(cursor
                        .getColumnIndex("emergency_url_token")));
                user.setUnderEmergency((cursor.getInt(cursor
                        .getColumnIndex("under_emergency"))) == 1 ? true
                        : false);
                String apiKey = cursor.getString(cursor
                        .getColumnIndex("emergency_api_key"));
                user.setEmergencyAPIKEY(apiKey);
                Log.d(TAG, user.getFirstName());
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return user;
    }

    public ArrayList<PatientUserVO> getAllUser() throws Exception {
        ArrayList<PatientUserVO> userList = new ArrayList<PatientUserVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * from user_tbl where status IS NOT 'Pending'" + " ORDER BY first_name";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    PatientUserVO user = new PatientUserVO();
                    user.setUserProfileId(cursor.getLong(cursor
                            .getColumnIndex("user_id")));
                    user.setPatientId(cursor.getLong(cursor
                            .getColumnIndex("patient_id")));
                    user.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    user.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    user.setEventLogId(cursor.getLong(cursor

                            .getColumnIndex("event_log_id")));
                    user.setPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("phone_number")));
                    user.setFileName(cursor.getString(cursor
                            .getColumnIndex("filename")));
                    user.setRelationship(cursor.getString(cursor
                            .getColumnIndex("relationship")));
                    user.setEmergencyToken(cursor.getString(cursor
                            .getColumnIndex("emergency_token")));
                    user.setRole(cursor.getString(cursor.getColumnIndex("role")));
                    user.setStatus(cursor.getString(cursor
                            .getColumnIndex("status")));
                    user.setPicId(cursor.getLong(cursor
                            .getColumnIndex("pic_id")));
                    user.setPicName(cursor.getString(cursor
                            .getColumnIndex("pic_name")));
                    user.setSharePoints(cursor.getLong(cursor
                            .getColumnIndex("share_points")));
                    user.setUnderEmergency((cursor.getInt(cursor
                            .getColumnIndex("under_emergency"))) == 1 ? true
                            : false);
                    Log.d(TAG, user.getFirstName());
                    userList.add(user);
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
        Log.d(TAG, "userList size=" + userList.size());
        return userList;
    }

    public ArrayList<PatientUserVO> getAllInvitationsForUser(long userProfileId) throws Exception {
        ArrayList<PatientUserVO> userList = new ArrayList<PatientUserVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * from user_tbl where user_id=" + userProfileId + " AND status IS 'Pending'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    PatientUserVO user = new PatientUserVO();
                    user.setUserProfileId(cursor.getLong(cursor
                            .getColumnIndex("user_id")));
                    user.setPatientId(cursor.getLong(cursor
                            .getColumnIndex("patient_id")));
                    user.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    user.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    user.setSharePoints(cursor.getLong(cursor
                            .getColumnIndex("share_points")));
                    user.setEventLogId(cursor.getLong(cursor

                            .getColumnIndex("event_log_id")));
                    user.setPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("phone_number")));
                    user.setSharePoints(cursor.getLong(cursor
                            .getColumnIndex("share_points")));
                    user.setFileName(cursor.getString(cursor
                            .getColumnIndex("filename")));
                    user.setRelationship(cursor.getString(cursor
                            .getColumnIndex("relationship")));
                    user.setEmergencyToken(cursor.getString(cursor
                            .getColumnIndex("emergency_token")));
                    user.setRole(cursor.getString(cursor.getColumnIndex("role")));
                    user.setStatus(cursor.getString(cursor
                            .getColumnIndex("status")));
                    user.setPicId(cursor.getLong(cursor
                            .getColumnIndex("pic_id")));
                    user.setPicName(cursor.getString(cursor
                            .getColumnIndex("pic_name")));
                    user.setPatientAccessId(cursor.getLong(cursor
                            .getColumnIndex("patient_access_id")));
                    /*user.setEmergencyTokenUrl(cursor.getString(cursor
                            .getColumnIndex("emergency_url_token")));
					user.setPreferredAmbulancePhoneNo(cursor.getString(cursor
							.getColumnIndex("preffered_ambulanc_provider_pno")));
					user.setCurrentSelectedPatient(cursor.getString(cursor
							.getColumnIndex("current_selected_patient")));
					Log.d(TAG, "PAP=" + cursor.getString(cursor
							.getColumnIndex("preffered_ambulanc_provider_pno")));*/
                    user.setUnderEmergency((cursor.getInt(cursor
                            .getColumnIndex("under_emergency"))) == 1 ? true
                            : false);
                    /*String apiKey = cursor.getString(cursor
                            .getColumnIndex("emergency_api_key"));

					user.setEmergencyAPIKEY(apiKey);*/
                    Log.d(TAG, user.getFirstName());
                    userList.add(user);
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
        return userList;
    }


    public String getEmergencyApiKeyOfPatient(long patientId) {
        String emergencyApiKey = null;
        String query = "Select emergency_api_key from user_tbl where patient_id=" + patientId;
        Cursor cursor = null;
        try {
            openDatabase();
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    emergencyApiKey = cursor.getString(cursor
                            .getColumnIndex("emergency_api_key"));
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
        return emergencyApiKey;
    }

    public void updateUserTable(long patientId, String emergencyToken, boolean underEmergency) {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            //mContentValues.put("emergency_token", emergencyToken);
            mContentValues.put("under_emergency", underEmergency);
            String[] args = new String[]{String.valueOf(patientId)};
            database.update("user_tbl", mContentValues, "patient_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public void updateEmergencyTableWithWebserviceCallStatus(long patientId, int webserviceStatus) {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("webservice_status", webserviceStatus);
            String[] args = new String[]{String.valueOf(patientId)};
            database.update("emergency_tbl", mContentValues, "patient_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public void updateSmsToServerStatus(long patientId, int smsToServerStatus) {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("sms_to_server_status", smsToServerStatus);
            String[] args = new String[]{String.valueOf(patientId)};
            database.update("emergency_tbl", mContentValues, "patient_id=?", args);

        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            setTransactionSuccessful();
            EndTransaction();
            closeDatabase();
        }
    }

    public EmergencyStatusVO getUserNotInEmergency() {
        EmergencyStatusVO mStatusVO = new EmergencyStatusVO();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM emergency_tbl";// where webservice_status!=" + Constant.STATUS_WEBSERVICE_SUCCESS + " AND sms_to_server_status!=" + Constant.STATUS_SMS_SERVER_SENT + " ORDER BY ROWID ASC LIMIT 1";
        Log.d(TAG, "getUserNotInEmergency = " + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToNext()) {
                do {
                    mStatusVO.setPatientId(cursor.getLong(cursor
                            .getColumnIndex("patient_id")));
                    mStatusVO.setEmergencyId(cursor.getLong(cursor
                            .getColumnIndex("emergency_id")));
                    mStatusVO.setCallStatus(cursor.getInt(cursor
                            .getColumnIndex("call_emergency_status")));
                    mStatusVO.setInvokeFromDeviceStatus(cursor.getInt(cursor
                            .getColumnIndex("invoke_from_device")));
                    mStatusVO.setWebserviceStatus(cursor.getInt(cursor
                            .getColumnIndex("webservice_status")));
                    mStatusVO.setSmsToContactsStatus(cursor.getInt(cursor
                            .getColumnIndex("sms_to_contacts_status")));
                    mStatusVO.setSmsToServerStatus(cursor.getInt(cursor
                            .getColumnIndex("sms_to_server_status")));
                    mStatusVO.setLocationUpdateStatus(cursor.getInt(cursor
                            .getColumnIndex("location_update_status")));
                    mStatusVO.setEmergencyStatus(cursor.getString(cursor.getColumnIndex("emergency_status")));
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("launch_loc_provider")))) {
                        Location launchLocation = new Location(
                                cursor.getString(cursor
                                        .getColumnIndex("launch_loc_provider")));
                        launchLocation.setLatitude(Double.parseDouble(cursor
                                .getString(cursor
                                        .getColumnIndex("launch_loc_lat"))));
                        launchLocation.setLongitude(Double.parseDouble(cursor
                                .getString(cursor
                                        .getColumnIndex("launch_loc_long"))));
                        launchLocation.setAccuracy(cursor.getFloat(cursor
                                .getColumnIndex("launch_loc_accuracy")));
                        mStatusVO.setLaunchLocation(launchLocation);
                    }
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("current_loc_provider")))) {

                        Location currentLocation = new Location(
                                cursor.getString(cursor
                                        .getColumnIndex("current_loc_provider")));
                        currentLocation.setLatitude(Double.parseDouble(cursor
                                .getString(cursor
                                        .getColumnIndex("current_loc_lat"))));
                        currentLocation.setLongitude(Double.parseDouble(cursor
                                .getString(cursor
                                        .getColumnIndex("current_loc_long"))));
                        currentLocation.setAccuracy(cursor.getFloat(cursor
                                .getColumnIndex("current_loc_accuracy")));
                        currentLocation
                                .setTime(cursor.getLong(cursor
                                        .getColumnIndex("current_loc_last_update_time")));
                        mStatusVO.setCurrentLocation(currentLocation);

                    }
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
        return mStatusVO;
    }

    public String getEmergencyTokenOfUser(long patientId) {

        String emergencyToken = null;
        String query = "Select emergency_token from user_tbl where patient_id=" + patientId;
        Cursor cursor = null;
        try {
            openDatabase();
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    emergencyToken = cursor.getString(cursor
                            .getColumnIndex("emergency_token"));
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
        return emergencyToken;
    }


    public void deleteAllEmergencyNotInvokedUsersFromEmergencyTable() {
        try {
            openDatabaseAndBeginTransaction();
            database.execSQL("Delete from emergency_tbl where webservice_status!=" + Constant.STATUS_WEBSERVICE_SUCCESS + " AND sms_to_server_status!=" + Constant.STATUS_SMS_SERVER_SENT + " AND emergency_status!=" + "'" + Constant.EMERGENCY_STATUS_TEST + "'");
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public PatientUserVO getUserDetailsForActivityEmergencyTrackAndRevokeScreen(long patientId) {
        PatientUserVO user = null;
        Cursor cursor = null;
        String selectQuery = "SELECT first_name,last_name,emergency_api_key,emergency_token from user_tbl where patient_id=" + patientId;
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    user = new PatientUserVO();

                    user.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    user.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    user.setEmergencyAPIKey(cursor.getString(cursor
                            .getColumnIndex("emergency_api_key")));
                    user.setEmergencyToken(cursor.getString(cursor
                            .getColumnIndex("emergency_token")));
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
        return user;
    }

    public EmergencyStatusVO getUserDetailsForActivityEmergencyTrackAndRevokeScreenFromEmergencyTable(long patientId) {
        EmergencyStatusVO user = new EmergencyStatusVO();
        Cursor cursor = null;
        Log.d(TAG, "PATIENT ID=" + patientId);
        String selectQuery = "SELECT has_got_emergency_location,has_notified_emri,has_notified_contacts,has_called_ambulance,emergency_status from emergency_tbl where patient_id=" + patientId;
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    user.setHasGotEmergencyLocation(cursor.getInt(cursor
                            .getColumnIndex("has_got_emergency_location")) == 1 ? true : false);
                    Log.d(TAG, "GOT EMERGENCY LOCATION=" + user.isHasGotEmergencyLocation());
                    user.setHasEmriNotified(cursor.getInt(cursor
                            .getColumnIndex("has_notified_emri")) == 1 ? true : false);
                    Log.d(TAG, "EMRI NOTIFIED=" + user.isHasEmriNotified());

                    user.setHasNotifiedContacts(cursor.getInt(cursor
                            .getColumnIndex("has_notified_contacts")) == 1 ? true : false);
                    Log.d(TAG, "CONTACTS NOTIFIED=" + user.isHasNotifiedContacts());

                    user.setHasCalledAmbulance(cursor.getInt(cursor
                            .getColumnIndex("has_called_ambulance")) == 1 ? true : false);
                    user.setEmergencyStatus(cursor.getString(cursor.getColumnIndex("emergency_status")));
                    Log.d(TAG, "CALL AMBULANCE NOTIFIED=" + user.isHasCalledAmbulance());

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
        return user;
    }

    public void updateCallToEmriStatus(long patientId, Boolean status) {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("has_notified_emri", status);
            String[] args = new String[]{String.valueOf(patientId)};
            database.update("emergency_tbl", mContentValues, "patient_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public void updateNotifiedContactStatus(long patientId, Boolean status) {
        Log.i(TAG, "updateNotifiedContactStatus");

        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("has_notified_contacts", status);
            String[] args = new String[]{String.valueOf(patientId)};
            database.update("emergency_tbl", mContentValues, "patient_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public void updateCallToAmbulanceProviderStatus(long patientId, Boolean status) {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("has_called_ambulance", status);
            String[] args = new String[]{String.valueOf(patientId)};
            database.update("emergency_tbl", mContentValues, "patient_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public void updatePreferredOrgBranchId(long patientId, long preferredOrgId) {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("preferred_org_branch_id", preferredOrgId);
            String[] args = new String[]{String.valueOf(patientId)};
            database.update("user_info_tbl", mContentValues, "patient_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }


    public void updateCurrentLocation(long patientId, Location loc) {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            if (loc != null) {
                Log.d(TAG, "current location: " + loc.toString());
                mContentValues.put("current_loc_lat", loc.getLatitude());
                mContentValues.put("current_loc_long", loc.getLongitude());
                mContentValues.put("current_loc_accuracy",
                        loc.getAccuracy());
                mContentValues.put("current_loc_last_update_time",
                        loc.getTime());
            }
            String[] args = new String[]{String.valueOf(patientId)};
            database.update("emergency_tbl", mContentValues, "patient_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public void updateUserUnderEmergencyStatus(long patientId, boolean status) {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("has_called_ambulance", status);
            String[] args = new String[]{String.valueOf(patientId)};
            database.update("user_tbl", mContentValues, "patient_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
    }

    /*public void insertPrefferedAmbulanceProvidersList(ArrayList<AmbulanceProviderVO> ambulanceProviderVOs) throws Exception {
        Log.d(TAG, "AMBULANCE PROVIDERS SIZE=" + ambulanceProviderVOs.size());
        for (AmbulanceProviderVO ambulanceProviderVO : ambulanceProviderVOs) {

            try {
                openDatabaseAndBeginTransaction();
                ContentValues mContentValues = new ContentValues();
                Log.d(TAG, "NAME=" + ambulanceProviderVO.getOrganizationName());
                Log.d(TAG, "PHONE=" + ambulanceProviderVO.getPhoneNumber());

                mContentValues.put("name", ambulanceProviderVO.getOrganizationName());
                mContentValues.put("phone", ambulanceProviderVO.getPhoneNumber());
                mContentValues.put("emergency_org_setting_id", ambulanceProviderVO.getEmergencyOrgSettingId());
                mContentValues.put("org_id", ambulanceProviderVO.getOrgId());
                mContentValues.put("org_logo_id", ambulanceProviderVO.getOrgLogoId());
                mContentValues.put("street_address", ambulanceProviderVO.getLocationName() != null ? ambulanceProviderVO.getLocationName() : "");

                OrgLocationVO mOrgLocationVO = ambulanceProviderVO.getLocation();

                if (mOrgLocationVO != null) {
                    mContentValues.put("branch_name", mOrgLocationVO.getBranchName());
                    if (mOrgLocationVO.getLocationAddress() != null) {
                        if (mOrgLocationVO.getLocationAddress().getLatitude() != null) {
                            mContentValues.put("lat", mOrgLocationVO.getLocationAddress().getLatitude());
                        } else {
                            mContentValues.put("lat", "0");
                        }
                        if (mOrgLocationVO.getLocationAddress().getLongitude() != null) {
                            mContentValues.put("lon", mOrgLocationVO.getLocationAddress().getLongitude());
                        } else {
                            mContentValues.put("lon", "0");
                        }
                        mContentValues.put("street_address", mOrgLocationVO.getLocationAddress().getStreetAddress());

                    }
                }

                database.insertWithOnConflict("preferred_ambulance_providers_tbl", null,
                        mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                setTransactionSuccessful();
                EndTransaction();

            } catch (SQLException e) {
                Log.i(TAG, "Exception:" + e.getMessage());
                throw new Exception(e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e.getMessage());
                throw new Exception(e.getMessage());
            } finally {
                closeDatabase();
            }
        }
    }*/

    public String getSelectedPreferredAmbulacneNo(long patientId) throws Exception {
        String pno = null;
        Cursor cursor = null;
        String selectQuery = "SELECT preffered_ambulanc_provider_pno from user_info_tbl where patient_id=" + patientId;
        Log.d(TAG, "query=" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            Log.d(TAG, "cursor count=" + cursor.getCount());
            if (cursor.moveToFirst()) {
                pno = cursor.getString(cursor
                        .getColumnIndex("preffered_ambulanc_provider_pno"));
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
        Log.d(TAG, "PREFERRED PHONE NUMBER=" + pno);
        return pno;
    }

    public long getAmbulanceProviderLogoId(String pno) {
        String phoneNumber = null;
        if (pno != null) {
            phoneNumber = pno.trim();
        }
        long ambulanceProviderOrgLogoId = 0;
        Cursor cursor = null;
        String selectQuery = "SELECT org_logo_id from preferred_ambulance_providers_tbl where phone=" + "'" + phoneNumber + "'";
        Log.d(TAG, "QUERY=" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            Log.d(TAG, "cursor count=" + cursor.getCount());
            if (cursor.moveToFirst()) {
                ambulanceProviderOrgLogoId = cursor.getLong(cursor
                        .getColumnIndex("org_logo_id"));
            }


        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        Log.d(TAG, "AMBULANCE PROVIDER ORG LOGO ID=" + ambulanceProviderOrgLogoId);
        return ambulanceProviderOrgLogoId;
    }

    public String getAmbulanceProviderName(String pno) {
        String phoneNumber = null;
        if (pno != null) {
            phoneNumber = pno.trim();
        }
        String name = "";
        Cursor cursor = null;
        String selectQuery = "SELECT name from preferred_ambulance_providers_tbl where phone = " + "'" + phoneNumber + "'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            Log.d(TAG, "cursor count=" + cursor.getCount());
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor
                        .getColumnIndex("name"));
            }


        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        Log.d(TAG, "AMBULANCE PROVIDER name=" + name);
        return name;
    }

    public PatientUserVO getLoggedInUserSlidingScreenDetails() {
        Cursor cursor = null;
        PatientUserVO user = null;
        String selectQuery = "SELECT * from user_tbl where relationship=" + "'" + "Self" + "'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    user = new PatientUserVO();
                    user.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    user.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    user.setPicId(cursor.getLong(cursor
                            .getColumnIndex("pic_id")));
                    user.setPicName(cursor.getString(cursor
                            .getColumnIndex("pic_name")));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return user;
    }


    public void updateEmergencyTable(EmergencyStatusVO mStatusVO) {

        if (mStatusVO != null) {
            try {
                openDatabaseAndBeginTransaction();
                ContentValues mContentValues = new ContentValues();
                mContentValues.put("emergency_id", mStatusVO.getEmergencyId());
                mContentValues.put("patient_id", mStatusVO.getPatientId());
                if (mStatusVO.getLaunchLocation() != null) {
                    Log.d(TAG, "Launch location: "
                            + mStatusVO.getLaunchLocation().toString());
                    mContentValues.put("launch_loc_lat", mStatusVO
                            .getLaunchLocation().getLatitude());
                    mContentValues.put("launch_loc_long", mStatusVO
                            .getLaunchLocation().getLongitude());
                    mContentValues.put("launch_loc_accuracy", mStatusVO
                            .getLaunchLocation().getAccuracy());
                    mContentValues.put("launch_loc_provider", mStatusVO
                            .getLaunchLocation().getProvider());
                }
                if (mStatusVO.getCurrentLocation() != null) {
                    Location loc = mStatusVO.getCurrentLocation();
                    Log.d(TAG, "current location: " + loc.toString());
                    mContentValues.put("current_loc_lat", loc.getLatitude());
                    mContentValues.put("current_loc_long", loc.getLongitude());
                    mContentValues.put("current_loc_accuracy",
                            loc.getAccuracy());
                    mContentValues.put("current_loc_last_update_time",
                            loc.getTime());
                    mContentValues.put("launch_loc_accuracy", mStatusVO
                            .getLaunchLocation().getAccuracy());
                    mContentValues.put("current_loc_provider", mStatusVO
                            .getLaunchLocation().getProvider());
                }
                mContentValues.put("invoke_from_device",
                        mStatusVO.getInvokeFromDeviceStatus());
                mContentValues.put("webservice_status",
                        mStatusVO.getWebserviceStatus());
                mContentValues.put("sms_to_server_status",
                        mStatusVO.getSmsToServerStatus());
                mContentValues.put("sms_to_contacts_status",
                        mStatusVO.getSmsToContactsStatus());
                mContentValues.put("call_emergency_status",
                        mStatusVO.getCallStatus());
                Log.d(TAG, "HAS GOT EMERGENCY LOCATION=" + mStatusVO.isHasGotEmergencyLocation());
                mContentValues.put("has_got_emergency_location",
                        mStatusVO.isHasGotEmergencyLocation());
                mContentValues.put("location_update_status",
                        mStatusVO.getLocationUpdateStatus());
                Log.d(TAG, "emergency_status" + mStatusVO.getEmergencyStatus());
                mContentValues.put("emergency_status",
                        mStatusVO.getEmergencyStatus());
                database.insertWithOnConflict("emergency_tbl", null,
                        mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                setTransactionSuccessful();
                EndTransaction();
            } catch (SQLException e) {
                Log.d(TAG, "SQL EXCEPTION=" + e.getMessage());
                Log.i(TAG, "Exception:" + e.getMessage());
            } catch (Exception e) {
                Log.d(TAG, " EXCEPTION=" + e.getMessage());

                Log.i(TAG, "Exception:" + e.getMessage());
            } finally {

                closeDatabase();
            }
        }
        Log.d(TAG, 3 + "");

    }

    public String getPreferredAmbulanceNumber(long patientId) throws Exception {
        String pno = null;
        Cursor cursor = null;
        String selectQuery = "SELECT preffered_ambulanc_provider_pno from user_info_tbl where patient_id=" + patientId;
        Log.d(TAG, "QUERY=" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            Log.d(TAG, "cursor count=" + cursor.getCount());
            if (cursor.moveToFirst()) {
                pno = cursor.getString(cursor
                        .getColumnIndex("preffered_ambulanc_provider_pno"));
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
        Log.d(TAG, "PREFERRED PHONE NUMBER=" + pno);
        return pno;
    }

    public ArrayList<PrefferedAmbulanceProviderLocationVO> getPrefferedAmbulanceProviderLocation(String phoneNumber) {
        ArrayList<PrefferedAmbulanceProviderLocationVO> ambulanceProviderLocationVOs = new ArrayList<PrefferedAmbulanceProviderLocationVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * from preferred_ambulance_providers_tbl where phone=" + "'" + phoneNumber + "'";
        Log.d(TAG, "QUERY=" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            Log.d(TAG, "cursor count=" + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    PrefferedAmbulanceProviderLocationVO ambulanceProviderLocationVO = new PrefferedAmbulanceProviderLocationVO();

                    ambulanceProviderLocationVO.setLatitude(cursor.getString(cursor
                            .getColumnIndex("lat")));
                    ambulanceProviderLocationVO.setLongitude(cursor.getString(cursor
                            .getColumnIndex("lon")));
                    /*ambulanceProviderLocationVO.setLocationName(cursor.getString(cursor
                            .getColumnIndex("location_name")));*/

                    ambulanceProviderLocationVOs.add(ambulanceProviderLocationVO);
                } while (cursor.moveToNext());
            }


        } catch (SQLException e) {
            Log.d(TAG, "EXCEPTION=" + e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION=" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return ambulanceProviderLocationVOs;
    }

    public void removeEmergency(long patientId) {
        try {
            openDatabaseAndBeginTransaction();
            database.delete("emergency_tbl", "patient_id=" + patientId, null);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public void deleteEContacts(long patientId) {
        try {
            openDatabaseAndBeginTransaction();
            database.delete("contact_tbl", "owner_patient_id="
                    + patientId, null);
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            setTransactionSuccessful();
            EndTransaction();
            closeDatabase();
        }
    }


    public UserInfoVO getDashBoardData() {
        Cursor cursor = null;
        UserInfoVO userInfoVO = null;
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(context.getString(R.string.date_format3), Locale.ENGLISH);
        String selectQuery = "SELECT * from user_complete_details_tbl where relationship=" + "'" + "Self" + "'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    userInfoVO = new UserInfoVO();
                    userInfoVO.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    userInfoVO.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    userInfoVO.setDateOfBirth(mSimpleDateFormat.parse(cursor.getString(cursor
                            .getColumnIndex("date_of_birth"))));
                    userInfoVO.setGender(cursor.getInt(cursor
                            .getColumnIndex("gender")));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return userInfoVO;
    }

    public ArrayList<EmergencyContactsVO> getEmergencyContacts(long ownerPatientId, String role)
            throws Exception {
        ArrayList<EmergencyContactsVO> userList = new ArrayList<EmergencyContactsVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM contact_tbl WHERE owner_patient_id="
                + ownerPatientId + " AND role='" + role + "'";
        Log.d(TAG, "getAllContacts query=" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    EmergencyContactsVO mEmergencyContactsVO = new EmergencyContactsVO();
                    mEmergencyContactsVO
                            .setOwnerPatientId(cursor.getLong(cursor
                                    .getColumnIndex("owner_patient_id")));
                    mEmergencyContactsVO.setContactId(cursor.getLong(cursor
                            .getColumnIndex("contact_id")));
                    mEmergencyContactsVO.setUserProfileId(cursor.getLong(cursor
                            .getColumnIndex("user_id")));
                    mEmergencyContactsVO.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    mEmergencyContactsVO.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    mEmergencyContactsVO.setPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("phone_number")));
                    mEmergencyContactsVO.setAddress(cursor.getString(cursor
                            .getColumnIndex("address")));
                    mEmergencyContactsVO.setCountry(cursor.getString(cursor
                            .getColumnIndex("country")));
                    mEmergencyContactsVO.setEmailID(cursor.getString(cursor
                            .getColumnIndex("email_id")));
                    mEmergencyContactsVO.setCreatedBy(cursor.getLong(cursor
                            .getColumnIndex("created_by_id")));
                    mEmergencyContactsVO.setProfilePicId(cursor.getLong(cursor
                            .getColumnIndex("profile_pic_id")));
                    mEmergencyContactsVO.setPatientAccessId(cursor.getLong(cursor
                            .getColumnIndex("patient_access_id")));
                    mEmergencyContactsVO.setSpecialities(AppUtil.getSpecialities(context, cursor.getString(cursor
                            .getColumnIndex("specialities"))));
                    // mEmergencyContactsVO.setCreatedDate(sdf.parse(cursor.getString(cursor.getColumnIndex("created_date"))));
                    mEmergencyContactsVO.setUpdatedBy(cursor.getLong(cursor
                            .getColumnIndex("last_updated_by_id")));
                    // mEmergencyContactsVO.setUpdatedDate(sdf.parse(cursor.getString(cursor.getColumnIndex("last_updated_date"))));
                    mEmergencyContactsVO.setRelationship(cursor
                            .getString(cursor.getColumnIndex("relationship")));
                    Log.d(TAG, "getAllContacts role=" + cursor.getString(cursor
                            .getColumnIndex("role")));

                    mEmergencyContactsVO.setRole(cursor.getString(cursor
                            .getColumnIndex("role")));
                    mEmergencyContactsVO.setStatus(cursor.getString(cursor
                            .getColumnIndex("status")));
                    Log.d(TAG, "status=" + cursor.getString(cursor
                            .getColumnIndex("status")));
                    userList.add(mEmergencyContactsVO);
                    Log.d(TAG,
                            "emergecy contact: "
                                    + mEmergencyContactsVO.getFirstName());
                } while (cursor.moveToNext());
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        Log.d(TAG, "getAllContacts size=" + userList.size() + "role=" + role);
        return userList;
    }

    public ArrayList<EmergencyContactsVO> getAllContacts(long ownerPatientId, String role)
            throws Exception {
        ArrayList<EmergencyContactsVO> userList = new ArrayList<EmergencyContactsVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM contact_tbl WHERE owner_patient_id="
                + ownerPatientId + " AND role='" + role + "'";
        Log.d(TAG, "getAllContacts query=" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    EmergencyContactsVO mEmergencyContactsVO = new EmergencyContactsVO();
                    mEmergencyContactsVO
                            .setOwnerPatientId(cursor.getLong(cursor
                                    .getColumnIndex("owner_patient_id")));
                    mEmergencyContactsVO.setContactId(cursor.getLong(cursor
                            .getColumnIndex("contact_id")));
                    mEmergencyContactsVO.setUserProfileId(cursor.getLong(cursor
                            .getColumnIndex("user_id")));
                    mEmergencyContactsVO.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    mEmergencyContactsVO.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    mEmergencyContactsVO.setPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("phone_number")));
                    mEmergencyContactsVO.setAddress(cursor.getString(cursor
                            .getColumnIndex("address")));
                    mEmergencyContactsVO.setCountry(cursor.getString(cursor
                            .getColumnIndex("country")));
                    mEmergencyContactsVO.setEmailID(cursor.getString(cursor
                            .getColumnIndex("email_id")));
                    mEmergencyContactsVO.setCreatedBy(cursor.getLong(cursor
                            .getColumnIndex("created_by_id")));
                    mEmergencyContactsVO.setProfilePicId(cursor.getLong(cursor
                            .getColumnIndex("profile_pic_id")));
                    mEmergencyContactsVO.setPatientAccessId(cursor.getLong(cursor
                            .getColumnIndex("patient_access_id")));
                    mEmergencyContactsVO.setSpecialities(AppUtil.getSpecialities(context, cursor.getString(cursor
                            .getColumnIndex("specialities"))));
                    // mEmergencyContactsVO.setCreatedDate(sdf.parse(cursor.getString(cursor.getColumnIndex("created_date"))));
                    mEmergencyContactsVO.setUpdatedBy(cursor.getLong(cursor
                            .getColumnIndex("last_updated_by_id")));
                    // mEmergencyContactsVO.setUpdatedDate(sdf.parse(cursor.getString(cursor.getColumnIndex("last_updated_date"))));
                    mEmergencyContactsVO.setRelationship(cursor
                            .getString(cursor.getColumnIndex("relationship")));
                    Log.d(TAG, "getAllContacts role=" + cursor.getString(cursor
                            .getColumnIndex("role")));

                    mEmergencyContactsVO.setRole(cursor.getString(cursor
                            .getColumnIndex("role")));
                    mEmergencyContactsVO.setStatus(cursor.getString(cursor
                            .getColumnIndex("status")));
                    Log.d(TAG, "status=" + cursor.getString(cursor
                            .getColumnIndex("status")));
                    userList.add(mEmergencyContactsVO);
                    Log.d(TAG,
                            "emergecy contact: "
                                    + mEmergencyContactsVO.getFirstName());
                } while (cursor.moveToNext());
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        Log.d(TAG, "getAllContacts size=" + userList.size() + "role=" + role);
        return userList;
    }


    public ArrayList<EmergencyContactsVO> getAllContacts(long ownerPatientId)
            throws Exception {
        ArrayList<EmergencyContactsVO> userList = new ArrayList<EmergencyContactsVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM contact_tbl WHERE owner_patient_id="
                + ownerPatientId;
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    EmergencyContactsVO mEmergencyContactsVO = new EmergencyContactsVO();
                    mEmergencyContactsVO
                            .setOwnerPatientId(cursor.getLong(cursor
                                    .getColumnIndex("owner_patient_id")));
                    mEmergencyContactsVO.setContactId(cursor.getLong(cursor
                            .getColumnIndex("contact_id")));
                    mEmergencyContactsVO.setUserProfileId(cursor.getLong(cursor
                            .getColumnIndex("user_id")));
                    mEmergencyContactsVO.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    mEmergencyContactsVO.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    mEmergencyContactsVO.setPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("phone_number")));
                    mEmergencyContactsVO.setAddress(cursor.getString(cursor
                            .getColumnIndex("address")));
                    mEmergencyContactsVO.setCountry(cursor.getString(cursor
                            .getColumnIndex("country")));
                    mEmergencyContactsVO.setEmailID(cursor.getString(cursor
                            .getColumnIndex("email_id")));
                    mEmergencyContactsVO.setCreatedBy(cursor.getLong(cursor
                            .getColumnIndex("created_by_id")));
                    // mEmergencyContactsVO.setCreatedDate(sdf.parse(cursor.getString(cursor.getColumnIndex("created_date"))));
                    mEmergencyContactsVO.setUpdatedBy(cursor.getLong(cursor
                            .getColumnIndex("last_updated_by_id")));
                    // mEmergencyContactsVO.setUpdatedDate(sdf.parse(cursor.getString(cursor.getColumnIndex("last_updated_date"))));
                    mEmergencyContactsVO.setRelationship(cursor
                            .getString(cursor.getColumnIndex("relationship")));
                    mEmergencyContactsVO.setRole(cursor.getString(cursor
                            .getColumnIndex("role")));
                    mEmergencyContactsVO.setStatus(cursor.getString(cursor
                            .getColumnIndex("status")));
                    userList.add(mEmergencyContactsVO);
                    Log.d(TAG,
                            "emergecy contact: "
                                    + mEmergencyContactsVO.getFirstName());
                } while (cursor.moveToNext());
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return userList;
    }


    public void insertContacts(PatientUserVO mPatientUserVO) {
        Log.d(TAG, "INSERTING USERS CONTACT ");

        ArrayList<EmergencyContactsVO> mContactsVOs = new ArrayList<EmergencyContactsVO>();
        if (mPatientUserVO.getRecordVO() != null) {
            Log.d(TAG, "USER RECORD VO NOT NULL");
            if (mPatientUserVO.getRecordVO().getEmergencyContactVO() != null) {
                Log.d(TAG, "NO EMERGENCY CONTACTS");
                mContactsVOs = mPatientUserVO.getRecordVO()
                        .getEmergencyContactVO();
                Log.d(TAG, "EMERGENCY CONTACTS SIZE= " + mContactsVOs.size());
            }
        }
        if (mContactsVOs.size() != 0) {
            try {
                deleteEContacts(mPatientUserVO.getPatientId());
                openDatabaseAndBeginTransaction();
                for (EmergencyContactsVO emergencyContactsVO : mContactsVOs) {
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put("owner_patient_id",
                            emergencyContactsVO.getOwnerPatientId());
                    mContentValues.put("contact_id",
                            emergencyContactsVO.getContactId());
                    mContentValues.put("user_id",
                            emergencyContactsVO.getUserProfileId());
                    mContentValues.put("first_name",
                            emergencyContactsVO.getFirstName());
                    mContentValues.put("last_name",
                            emergencyContactsVO.getLastName());
                    mContentValues.put("email_id",
                            emergencyContactsVO.getEmailID());
                    mContentValues.put("phone_number",
                            emergencyContactsVO.getPhoneNumber());
                    mContentValues.put("relationship",
                            emergencyContactsVO.getRelationship());
                    mContentValues.put("address",
                            emergencyContactsVO.getAddress());
                    mContentValues.put("pic_id",
                            emergencyContactsVO.getPicId());
                    mContentValues.put("profile_pic_id",
                            emergencyContactsVO.getPicId());
                    mContentValues.put("country",
                            emergencyContactsVO.getCountry());
                    mContentValues.put("specialities", AppUtil.convertToJsonString(emergencyContactsVO.getSpecialities()));

                    Log.d(TAG, "role=" + emergencyContactsVO.getRole());

                    mContentValues.put("role", emergencyContactsVO.getRole());
                    mContentValues.put("patient_access_id", emergencyContactsVO.getPatientAccessId());
                    mContentValues.put("pic_id", emergencyContactsVO.getPicId());
                    mContentValues.put("status",
                            emergencyContactsVO.getStatus());
                    mContentValues.put("created_by_id",
                            emergencyContactsVO.getCreatedBy());
                    if (emergencyContactsVO.getCreatedDate() != null) {
                        mContentValues.put("created_date", (String) DateFormat
                                .format("yyyy-MM-dd HH:mm:ss",
                                        emergencyContactsVO.getCreatedDate()));
                    }
                    if (emergencyContactsVO.getUpdatedDate() != null) {
                        mContentValues.put("last_updated_date",
                                (String) DateFormat.format(
                                        "yyyy-MM-dd HH:mm:ss",
                                        emergencyContactsVO.getUpdatedDate()));
                    }
                    mContentValues.put("last_updated_by_id",
                            emergencyContactsVO.getUpdatedBy());
                    Log.d(TAG, "status=" + emergencyContactsVO.getStatus());
                    if (!TextUtils.equals(emergencyContactsVO.getStatus(),
                            "Deleted")) {
                        database.insert("contact_tbl", null, mContentValues);
                    }
                }

            } catch (SQLException e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } finally {
                setTransactionSuccessful();
                EndTransaction();
                closeDatabase();
            }
        }
    }


    public EmergencyStatusVO getEmergencyStatus(long patientId) {
        EmergencyStatusVO mStatusVO = new EmergencyStatusVO();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM emergency_tbl WHERE patient_id="
                + patientId;
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                mStatusVO.setPatientId(cursor.getLong(cursor
                        .getColumnIndex("patient_id")));
                mStatusVO.setEmergencyId(cursor.getLong(cursor
                        .getColumnIndex("emergency_id")));
                mStatusVO.setCallStatus(cursor.getInt(cursor
                        .getColumnIndex("call_emergency_status")));
                mStatusVO.setInvokeFromDeviceStatus(cursor.getInt(cursor
                        .getColumnIndex("invoke_from_device")));
                mStatusVO.setWebserviceStatus(cursor.getInt(cursor
                        .getColumnIndex("webservice_status")));
                mStatusVO.setSmsToContactsStatus(cursor.getInt(cursor
                        .getColumnIndex("sms_to_contacts_status")));
                mStatusVO.setSmsToServerStatus(cursor.getInt(cursor
                        .getColumnIndex("sms_to_server_status")));
                mStatusVO.setLocationUpdateStatus(cursor.getInt(cursor
                        .getColumnIndex("location_update_status")));
                mStatusVO.setEmergencyStatus(cursor.getString(cursor.getColumnIndex("emergency_status")));

                if (!TextUtils.isEmpty(cursor.getString(cursor
                        .getColumnIndex("launch_loc_provider")))) {
                    Location launchLocation = new Location(
                            cursor.getString(cursor
                                    .getColumnIndex("launch_loc_provider")));
                    launchLocation
                            .setLatitude(Double.parseDouble(cursor
                                    .getString(cursor
                                            .getColumnIndex("launch_loc_lat"))));
                    launchLocation
                            .setLongitude(Double.parseDouble(cursor
                                    .getString(cursor
                                            .getColumnIndex("launch_loc_long"))));
                    launchLocation.setAccuracy(cursor.getFloat(cursor
                            .getColumnIndex("launch_loc_accuracy")));
                    mStatusVO.setLaunchLocation(launchLocation);
                }
                if (!TextUtils.isEmpty(cursor.getString(cursor
                        .getColumnIndex("current_loc_provider")))) {

                    Location currentLocation = new Location(
                            cursor.getString(cursor
                                    .getColumnIndex("current_loc_provider")));
                    currentLocation
                            .setLatitude(Double.parseDouble(cursor
                                    .getString(cursor
                                            .getColumnIndex("current_loc_lat"))));
                    currentLocation.setLongitude(Double.parseDouble(cursor
                            .getString(cursor
                                    .getColumnIndex("current_loc_long"))));
                    currentLocation.setAccuracy(cursor.getFloat(cursor
                            .getColumnIndex("current_loc_accuracy")));
                    currentLocation.setTime(cursor.getLong(cursor
                            .getColumnIndex("current_loc_last_update_time")));
                    mStatusVO.setCurrentLocation(currentLocation);
                }
            }

        } catch (SQLException e) {
            // Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            // Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return mStatusVO;
    }

    public ArrayList<EmergencyStatusVO> getAllEmergencyInitiatedFromDevice()
            throws Exception {
        ArrayList<EmergencyStatusVO> mEmergencyStatusVOs = new ArrayList<EmergencyStatusVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM emergency_tbl";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToNext()) {
                do {
                    EmergencyStatusVO mStatusVO = new EmergencyStatusVO();
                    mStatusVO.setPatientId(cursor.getLong(cursor
                            .getColumnIndex("patient_id")));
                    mStatusVO.setEmergencyId(cursor.getLong(cursor
                            .getColumnIndex("emergency_id")));
                    mStatusVO.setCallStatus(cursor.getInt(cursor
                            .getColumnIndex("call_emergency_status")));
                    mStatusVO.setInvokeFromDeviceStatus(cursor.getInt(cursor
                            .getColumnIndex("invoke_from_device")));
                    mStatusVO.setWebserviceStatus(cursor.getInt(cursor
                            .getColumnIndex("webservice_status")));
                    mStatusVO.setSmsToContactsStatus(cursor.getInt(cursor
                            .getColumnIndex("sms_to_contacts_status")));
                    mStatusVO.setSmsToServerStatus(cursor.getInt(cursor
                            .getColumnIndex("sms_to_server_status")));
                    mStatusVO.setLocationUpdateStatus(cursor.getInt(cursor
                            .getColumnIndex("location_update_status")));
                    mStatusVO.setEmergencyStatus(cursor.getString(cursor.getColumnIndex("emergency_status")));

                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("launch_loc_provider")))) {
                        Location launchLocation = new Location(
                                cursor.getString(cursor
                                        .getColumnIndex("launch_loc_provider")));
                        launchLocation.setLatitude(Double.parseDouble(cursor
                                .getString(cursor
                                        .getColumnIndex("launch_loc_lat"))));
                        launchLocation.setLongitude(Double.parseDouble(cursor
                                .getString(cursor
                                        .getColumnIndex("launch_loc_long"))));
                        launchLocation.setAccuracy(cursor.getFloat(cursor
                                .getColumnIndex("launch_loc_accuracy")));
                        mStatusVO.setLaunchLocation(launchLocation);
                    }
                    if (!TextUtils.isEmpty(cursor.getString(cursor
                            .getColumnIndex("current_loc_provider")))) {

                        Location currentLocation = new Location(
                                cursor.getString(cursor
                                        .getColumnIndex("current_loc_provider")));
                        currentLocation.setLatitude(Double.parseDouble(cursor
                                .getString(cursor
                                        .getColumnIndex("current_loc_lat"))));
                        currentLocation.setLongitude(Double.parseDouble(cursor
                                .getString(cursor
                                        .getColumnIndex("current_loc_long"))));
                        currentLocation.setAccuracy(cursor.getFloat(cursor
                                .getColumnIndex("current_loc_accuracy")));
                        currentLocation
                                .setTime(cursor.getLong(cursor
                                        .getColumnIndex("current_loc_last_update_time")));
                        mStatusVO.setCurrentLocation(currentLocation);
                    }
                    mEmergencyStatusVOs.add(mStatusVO);
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return mEmergencyStatusVOs;
    }

    public ArrayList<AmbulanceProviderVO> getAllPrefferedAmbulanceProvidersList()
            throws Exception {
        ArrayList<AmbulanceProviderVO> ambulanceProviderVOs = new ArrayList<AmbulanceProviderVO>();

        Cursor cursor = null;
        String selectQuery = "SELECT * FROM preferred_ambulance_providers_tbl ORDER BY name";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToNext()) {
                do {
                    AmbulanceProviderVO mAmbulanceProviderVO = new AmbulanceProviderVO();
                    mAmbulanceProviderVO.setOrganizationName(cursor.getString(cursor
                            .getColumnIndex("name")));
                    mAmbulanceProviderVO.setPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("phone")));
                    OrgLocationVO mOrgLocationVO = new OrgLocationVO();
                    mOrgLocationVO.setLocationName(cursor.getString(cursor
                            .getColumnIndex("location_name")));
                    mAmbulanceProviderVO.setLocation(mOrgLocationVO);
                    ambulanceProviderVOs.add(mAmbulanceProviderVO);
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return ambulanceProviderVOs;
    }

    public void insertUserHealthRecord(HealthRecordVO healthRecordVO, long patientId) throws Exception {
        //Log.d(TAG, "Checking user name =" + user.getFirstName());
        Log.d(TAG, "****************** insertUserHealthRecord *********************");
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);

        if (healthRecordVO != null) {
            try {
                openDatabaseAndBeginTransaction();
                Log.d(TAG, "RECORD TYPE=" + healthRecordVO.getRecordType());
                ContentValues mContentValues = new ContentValues();
                mContentValues.put("record_id", healthRecordVO.getId());
                mContentValues.put("patient_id", patientId);
                mContentValues.put("attended_by_id", healthRecordVO.getAttendedById());
                mContentValues.put("doctor_id", healthRecordVO.getDoctorId());
                mContentValues.put("org_id", healthRecordVO.getOrgId());
                //mContentValues.put("org_name", healthRecordVO.getOrgName());
                //mContentValues.put("attended_by_display_name", healthRecordVO.getAttendedByDisplayName());
                mContentValues.put("role", healthRecordVO.getRole());
                //mContentValues.put("role_description", healthRecordVO.getRoleDescription());
                mContentValues.put("record_type", healthRecordVO.getRecordType());
                //mContentValues.put("start_date", healthRecordVO.getStartDate().getTime());
                //mContentValues.put("end_date", healthRecordVO.getEndDate().getTime());
                mContentValues.put("visit_record_id", healthRecordVO.getVisitRecordId());
                mContentValues.put("last_updated_user_role_id", healthRecordVO.getLastUpdatedUserRoleId());
                mContentValues.put("last_updated_user_display_name", healthRecordVO.getLastUpdatedUserDisplayName());
                mContentValues.put("last_updated_date", healthRecordVO.getLastUpdatedDate());
                //mContentValues.put("un_reg_doctor_name", healthRecordVO.getUnRegDoctorName());
                //mContentValues.put("un_reg_org_name", healthRecordVO.getUnRegOrgName());
                mContentValues.put("health_record_map_key", healthRecordVO.getRecordType());
                mContentValues.put("health_record_map_value", AppUtil.convertToJsonString(healthRecordVO.getHealthRecord()));
                //mContentValues.put("attachment_id", healthRecordVO.getAttendedById());
                long i = database.insertWithOnConflict("health_record_tbl", null,
                        mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "health record inserted : " + i);
                setTransactionSuccessful();
                EndTransaction();
               /* Broasenddcast(
                        Constant.ACTION_EMHR_DATA_CHANGED);*/
            } catch (SQLException e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } finally {

                closeDatabase();
            }
        }
    }

    public void sendBroadcast(String key) {
        Intent mIntent = new Intent(key);
        /*mIntent.putExtra("value", value);
        mIntent.putExtra("statusCode", statusCode);
        mIntent.putExtra("patientId", userNotInEmergency != null ? userNotInEmergency.getPatientId() : "0");*/
        //mLocalBroadcastManager.sendBroadcast(mIntent);
    }

    public HealthRecordVO getUserHealthRecord(String recordTypeEhr, long patientId) throws Exception {
        HealthRecordVO healthRecordVO = new HealthRecordVO();
        Cursor cursor = null;
        // Select All Query
        String selectQuery = "SELECT * from health_record_tbl where record_type=" + "'" + recordTypeEhr + "'" + " AND patient_id=" + patientId;
        Log.d(TAG, "QUERY=" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                healthRecordVO.setId(cursor.getLong(cursor
                        .getColumnIndex("record_id")));
                healthRecordVO.setRecordType(cursor.getString(cursor
                        .getColumnIndex("record_type")));
                healthRecordVO.setLastUpdatedUserRoleId(cursor.getLong(cursor
                        .getColumnIndex("last_updated_user_role_id")));
                healthRecordVO.setLastUpdatedUserDisplayName(cursor.getString(cursor
                        .getColumnIndex("last_updated_user_display_name")));
                healthRecordVO.setLastUpdatedDate(cursor.getString(cursor
                        .getColumnIndex("last_updated_date")));
                healthRecordVO.setHealthRecord(AppUtil.getMap(context, cursor.getString(cursor
                        .getColumnIndex("health_record_map_value"))));
            }

        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return healthRecordVO;
    }

    public void deleteDatabaseAndClearStaticVariables(Context context) {
        if (database != null) {
            context.deleteDatabase("callambulance.sqlite");
            if (database.isOpen()) {
                Log.d(TAG, "CLOSING DATABASE");
                database.close();
            }
            Log.d(TAG, "deleteDatabaseAndClearStaticVariables>>>DATABASE IS NOT NULL");
            database = null;
            openConnections = 0;
            dbHandler = null;
        }
    }

    public long getLatestEventLogID(Context context) {
        String selectQuery = "SELECT max(event_log_id) FROM user_tbl";
        Cursor cursor = null;
        long serverTS = 0;

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                serverTS = cursor.getLong(0);
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return serverTS;
    }

    public void insertDeviceDetails(Context context, String number,
                                    TelephonyManager tm) throws Exception {
        // GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();
        Log.d(TAG, "device_id" + tm.getDeviceId());
        Log.d(TAG, "mobile_no" + number);
        Log.d(TAG, "sim_serial_no" + tm.getSimSerialNumber());
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("device_id", tm.getDeviceId());
            mContentValues.put("mobile_no", number);
            mContentValues.put("sim_serial_no", tm.getSimSerialNumber());
            Log.d(TAG,
                    " device details values inserted: "
                            + mContentValues.toString());
            database.insertWithOnConflict("device_tbl", null, mContentValues,
                    SQLiteDatabase.CONFLICT_REPLACE);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public ContentValues getDeviceDetails() throws Exception {
        Cursor cursor = null;
        ContentValues mContentValues = new ContentValues();
        String selectQuery = "SELECT * FROM device_tbl ";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            /*cursor = database.query("device_tbl", null, null, null, null, null,
                    null);*/
            if (cursor.moveToFirst()) {
                mContentValues.put("device_id",
                        (cursor.getString(cursor.getColumnIndex("device_id"))));
                mContentValues.put("mobile_no",
                        (cursor.getString(cursor.getColumnIndex("mobile_no"))));
                mContentValues.put("sim_serial_no", (cursor.getString(cursor
                        .getColumnIndex("sim_serial_no"))));
            }

            Log.d(TAG,
                    " device details values: "
                            + mContentValues.toString());

        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return mContentValues;
    }

    public void updateUserOrgRecord(UserRecordVO userRecord, long patientId) {
        if (userRecord != null && userRecord.getPatientOrgs() != null) {
            // database.delete("user_org_table","patient_id=" + patientId,null);
            for (OrganisationVO organisationVO : userRecord.getPatientOrgs()) {
                try {
                    insertUserOrgRecord(organisationVO, patientId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void insertUserOrgRecord(OrganisationVO organisationVO, long patientId) throws Exception {
        Log.d(TAG, "****************** insertUserOrgRecord *********************");

        if (organisationVO != null) {
            try {
                openDatabaseAndBeginTransaction();
                Log.d(TAG, "Org Name=" + organisationVO.getOrgName());

                ContentValues mContentValues = new ContentValues();
                mContentValues.put("org_id", organisationVO.getOrgId());
                mContentValues.put("patient_id", patientId);
                mContentValues.put("email", organisationVO.getEmail());
                if (organisationVO.getOrgLogo() != null) {
                    mContentValues.put("org_logo_id", organisationVO.getOrgLogo().getFileId());
                }
                mContentValues.put("org_name", organisationVO.getOrgName());
                mContentValues.put("org_type", organisationVO.getOrgType());
                mContentValues.put("phone", organisationVO.getPhone());
                Log.d(TAG, "Org Status=" + organisationVO.getStatus());

                mContentValues.put("status", organisationVO.getStatus());
                long i = database.insertWithOnConflict("user_org_table", null,
                        mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "org list inserted : " + i);
                setTransactionSuccessful();
                EndTransaction();
            } catch (SQLException e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } finally {

                closeDatabase();
            }
        }
    }

    public ArrayList<OrganisationVO> getAllOrgs(long patientId)
            throws Exception {
        ArrayList<OrganisationVO> orgList = new ArrayList<OrganisationVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM user_org_table WHERE patient_id="
                + patientId + " AND org_id!=1";
        Log.d(TAG, "Ogrs list query=" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    OrganisationVO mOrganisationVO = new OrganisationVO();
                    mOrganisationVO.setOrgId(cursor.getLong(cursor
                            .getColumnIndex("org_id")));
                    mOrganisationVO.setEmail(cursor.getString(cursor
                            .getColumnIndex("email")));
                    mOrganisationVO.setOrgLogoId(cursor.getLong(cursor
                            .getColumnIndex("org_logo_id")));
                    mOrganisationVO.setOrgName(cursor.getString(cursor
                            .getColumnIndex("org_name")));
                    mOrganisationVO.setOrgType(cursor.getString(cursor
                            .getColumnIndex("org_type")));
                    mOrganisationVO.setPhone(cursor.getString(cursor
                            .getColumnIndex("phone")));
                    mOrganisationVO.setStatus(cursor.getString(cursor
                            .getColumnIndex("status")));
                    Log.d(TAG, "Org name : " + mOrganisationVO.getOrgName());
                    orgList.add(mOrganisationVO);
                } while (cursor.moveToNext());
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return orgList;
    }

    public ArrayList<OrganisationVO> getAllHospitalsAndClinic(long patientId)
            throws Exception {
        ArrayList<OrganisationVO> orgList = new ArrayList<OrganisationVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM user_org_table WHERE patient_id="
                + patientId + " AND org_id!=1 AND org_type=" + "'" + Constant.ORG_TYPE_URL_HOSPITAL + "'" + " OR org_type=" + "'" + Constant.ORG_TYPE_URL_CLINIC + "'";
        Log.d(TAG, "URL=" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    OrganisationVO mOrganisationVO = new OrganisationVO();
                    mOrganisationVO.setOrgId(cursor.getLong(cursor
                            .getColumnIndex("org_id")));
                    mOrganisationVO.setEmail(cursor.getString(cursor
                            .getColumnIndex("email")));
                    mOrganisationVO.setOrgLogoId(cursor.getLong(cursor
                            .getColumnIndex("org_logo_id")));
                    mOrganisationVO.setOrgName(cursor.getString(cursor
                            .getColumnIndex("org_name")));
                    mOrganisationVO.setOrgType(cursor.getString(cursor
                            .getColumnIndex("org_type")));
                    mOrganisationVO.setPhone(cursor.getString(cursor
                            .getColumnIndex("phone")));
                    mOrganisationVO.setStatus(cursor.getString(cursor
                            .getColumnIndex("status")));
                    Log.d(TAG, "Org name : " + mOrganisationVO.getOrgName());
                    orgList.add(mOrganisationVO);
                } while (cursor.moveToNext());
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return orgList;
    }


    public ArrayList<EmergencyContactsVO> getInvitations(long patientId) throws Exception {
        ArrayList<EmergencyContactsVO> userList = new ArrayList<EmergencyContactsVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM contact_tbl WHERE owner_patient_id="
                + patientId + " AND status='Pending'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    EmergencyContactsVO mEmergencyContactsVO = new EmergencyContactsVO();
                    mEmergencyContactsVO
                            .setOwnerPatientId(cursor.getLong(cursor
                                    .getColumnIndex("owner_patient_id")));
                    mEmergencyContactsVO.setContactId(cursor.getLong(cursor
                            .getColumnIndex("contact_id")));
                    mEmergencyContactsVO.setUserProfileId(cursor.getLong(cursor
                            .getColumnIndex("user_id")));
                    mEmergencyContactsVO.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    mEmergencyContactsVO.setPatientAccessId(cursor.getLong(cursor
                            .getColumnIndex("patient_access_id")));
                    mEmergencyContactsVO.setPicId(cursor.getLong(cursor
                            .getColumnIndex("pic_id")));
                    mEmergencyContactsVO.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    mEmergencyContactsVO.setPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("phone_number")));
                    mEmergencyContactsVO.setAddress(cursor.getString(cursor
                            .getColumnIndex("address")));
                    mEmergencyContactsVO.setCountry(cursor.getString(cursor
                            .getColumnIndex("country")));
                    mEmergencyContactsVO.setEmailID(cursor.getString(cursor
                            .getColumnIndex("email_id")));
                    mEmergencyContactsVO.setCreatedBy(cursor.getLong(cursor
                            .getColumnIndex("created_by_id")));
                    // mEmergencyContactsVO.setCreatedDate(sdf.parse(cursor.getString(cursor.getColumnIndex("created_date"))));
                    mEmergencyContactsVO.setUpdatedBy(cursor.getLong(cursor
                            .getColumnIndex("last_updated_by_id")));
                    // mEmergencyContactsVO.setUpdatedDate(sdf.parse(cursor.getString(cursor.getColumnIndex("last_updated_date"))));
                    mEmergencyContactsVO.setRelationship(cursor
                            .getString(cursor.getColumnIndex("relationship")));
                    mEmergencyContactsVO.setRole(cursor.getString(cursor
                            .getColumnIndex("role")));
                    mEmergencyContactsVO.setStatus(cursor.getString(cursor
                            .getColumnIndex("status")));
                    userList.add(mEmergencyContactsVO);
                    Log.d(TAG,
                            "emergecy contact: "
                                    + mEmergencyContactsVO.getFirstName());
                } while (cursor.moveToNext());
            }


        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return userList;
    }

    public void deletePatient(long acceptedOrRejectedPatientId) {
        try {
            openDatabaseAndBeginTransaction();
            database.delete("user_tbl", "patient_id="
                    + acceptedOrRejectedPatientId, null);

        } catch (Exception exc) {

        } finally {
            setTransactionSuccessful();
            EndTransaction();
            closeDatabase();
        }
    }

    public ArrayList<PatientUserVO> getUsersInEmergencies() {
        ArrayList<PatientUserVO> userList = new ArrayList<PatientUserVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * from user_tbl WHERE status IS NOT 'Pending' AND under_emergency=1";

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    PatientUserVO user = new PatientUserVO();
                    user.setUserProfileId(cursor.getLong(cursor
                            .getColumnIndex("user_id")));
                    user.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    user.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    user.setPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("phone_number")));
                    user.setFileName(cursor.getString(cursor
                            .getColumnIndex("filename")));
                    user.setRelationship(cursor.getString(cursor
                            .getColumnIndex("relationship")));
                    user.setEmergencyToken(cursor.getString(cursor
                            .getColumnIndex("emergency_token")));
                    user.setRole(cursor.getString(cursor.getColumnIndex("role")));
                    user.setStatus(cursor.getString(cursor
                            .getColumnIndex("status")));
                    user.setPatientAccessId(cursor.getLong(cursor
                            .getColumnIndex("patient_access_id")));
                    user.setPatientId(cursor.getLong(cursor
                            .getColumnIndex("patient_id")));
                    user.setPicId(cursor.getLong(cursor
                            .getColumnIndex("pic_id")));
                    user.setPreferredAmbulancePhoneNo(cursor.getString(cursor
                            .getColumnIndex("preffered_ambulanc_provider_pno")));
                    user.setPicName(cursor.getString(cursor
                            .getColumnIndex("pic_name")));
                    Log.d(TAG, user.getFirstName());
                    userList.add(user);
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
        return userList;
    }


    public Cursor getCursorForAllUsers() {
        Cursor cursor = null;
        String selectQuery = "SELECT * from user_tbl where status IS NOT 'Pending'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            Log.d(TAG, "CURSOR COUNT=" + cursor.getCount());
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
        return cursor;
    }

    public PatientUserVO getLoggedInUserDetails() {
        Cursor cursor = null;
        PatientUserVO user = new PatientUserVO();

        String selectQuery = "SELECT * from user_tbl where status IS NOT 'Pending'" + " AND relationship=" + "'" + "Self" + "'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    user.setUserProfileId(cursor.getLong(cursor
                            .getColumnIndex("user_id")));
                    user.setPatientId(cursor.getLong(cursor
                            .getColumnIndex("patient_id")));
                    user.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    user.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    user.setEventLogId(cursor.getLong(cursor

                            .getColumnIndex("event_log_id")));
                    user.setPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("phone_number")));
                    user.setFileName(cursor.getString(cursor
                            .getColumnIndex("filename")));
                    user.setRelationship(cursor.getString(cursor
                            .getColumnIndex("relationship")));
                    user.setEmergencyToken(cursor.getString(cursor
                            .getColumnIndex("emergency_token")));
                    user.setRole(cursor.getString(cursor.getColumnIndex("role")));
                    user.setStatus(cursor.getString(cursor
                            .getColumnIndex("status")));
                    user.setPicId(cursor.getLong(cursor
                            .getColumnIndex("pic_id")));
                    user.setPicName(cursor.getString(cursor
                            .getColumnIndex("pic_name")));
                    user.setSharePoints(cursor.getLong(cursor
                            .getColumnIndex("share_points")));
                    user.setUnderEmergency((cursor.getInt(cursor
                            .getColumnIndex("under_emergency"))) == 1 ? true
                            : false);
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
        return user;
    }

    public String getSelfUsersPhoneNumber() {
        String pno = "";
        Cursor cursor = null;
        String selectQuery = "SELECT phone_number from user_tbl where relationship=" + "'" + "Self" + "'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    pno = cursor.getString(cursor
                            .getColumnIndex("phone_number"));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return pno;
    }

    public UserVO getSelfProfile() {
        UserVO userVO = null;
        Cursor cursor = null;
        String selectQuery = "SELECT * from user_tbl where relationship=" + "'" + "Self" + "'";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    userVO = new UserVO();
                    userVO.setUserId(cursor.getLong(cursor
                            .getColumnIndex("user_id")));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return userVO;
    }

    public long getPreferredOrgBranchId(long patientId) {

        Cursor cursor = null;
        String selectQuery = "SELECT preferred_org_branch_id FROM user_info_tbl where patient_id=" + patientId;
        Log.d(TAG, "getPreferredOrgBranchId() -->" + selectQuery);
        long preferredOrgBranchId = 0;
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToNext()) {
                do {
                    preferredOrgBranchId = cursor.getLong(cursor
                            .getColumnIndex("preferred_org_branch_id"));
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
        return preferredOrgBranchId;
    }

    public long getPreferredBloodBankId(long patientId) {

        Cursor cursor = null;
        String selectQuery = "SELECT preferred_blood_bank_id FROM user_info_tbl where patient_id=" + patientId;
        Log.d(TAG, "getPreferredBloodBankId() -->" + selectQuery);
        long preferredOrgBranchId = 0;
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToNext()) {
                do {
                    preferredOrgBranchId = cursor.getLong(cursor
                            .getColumnIndex("preferred_blood_bank_id"));
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
        return preferredOrgBranchId;
    }


    public void insertEmergencyNumbers(BufferedReader row1) throws IOException {
        String csvLine;
        try {
            openDatabaseAndBeginTransaction();
            while ((csvLine = row1.readLine()) != null) {
                String[] row = csvLine.split(",");
                if (row != null) {

                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put("sno", row[0]);
                    mContentValues.put("country", row[1]);
                    //mContentValues.put("state", row[2]);
                    mContentValues.put("state_short_name", row[2]);
                    mContentValues.put("country_short_name", row[3]);
                    mContentValues.put("police_no", row[4]);
                    mContentValues.put("ambulance_no", row[5]);
                    mContentValues.put("fire_no", row[6]);
                    database.insertWithOnConflict("emergency_numbers_tbl", null,
                            mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public int getEmergencyNosCount() {

        Cursor cursor = null;
        int count = 0;
        String selectQuery = "SELECT * from emergency_numbers_tbl";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            count = cursor.getCount();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            count = 0;
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
            count = 0;
        } finally {
            if (cursor != null) {
                if (cursor != null) {
                    cursor.close();
                }
            }
            closeDatabase();
        }
        return count;
    }

    /*public PublicProviderVO getPublicProvider(String country, String state, int emergencyType) {
        PublicProviderVO mPublicProviderVO=new PublicProviderVO();
        String query;
        Cursor cursor;
        if (!TextUtils.isEmpty(state)) {
            query = "SELECT * FROM emergency_numbers_tbl where country_short_name=" + "'" + country + "'" + " AND state=" + "'" + state + "'";
        } else {
            query="SELECT * FROM emergency_numbers_tbl where country_short_name=" + "'" + country + "'";
        }
        Log.d(TAG, "QUERY TO GET EMERGENCY NUMBER=" + query);
        try {
            openDatabase();
            cursor = database.rawQuery(query, null);
            if (cursor.moveToNext()) {
                do {
                    if(cursor.getString(cursor
                            .getColumnIndex("ambulance_no")).equalsIgnoreCase("108"))
                    {
                        mPublicProviderVO.setName("EMRI - "+cursor.getString(cursor
                                .getColumnIndex("ambulance_no")));

                    }else
                    {
                        mPublicProviderVO.setName(cursor.getString(cursor
                                .getColumnIndex("ambulance_no")));
                        Log.d(TAG,"");
                    }
                    mPublicProviderVO.setPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("ambulance_no")));

                    *//*if (emergencyType == Constant.EMERGENCTY_TYPE_FIRE) {
                        mPublicProviderVO.setPhoneNumber(cursor.getString(cursor
                                .getColumnIndex("fire_no")));
                        mPublicProviderVO.setName("FIRE ("+cursor.getString(cursor
                                .getColumnIndex("fire_no"))+")");
                    } else if (emergencyType == Constant.EMERGENCTY_TYPE_POLICE) {
                        mPublicProviderVO.setPhoneNumber(cursor.getString(cursor
                                .getColumnIndex("police_no")));
                    } else {*//*

                    //}
                    Log.d(TAG,"AMB NUMBER="+cursor.getString(cursor
                            .getColumnIndex("ambulance_no")));

                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
        return mPublicProviderVO;

    }*/
    public PublicProviderVO getPublicProvider(String country, String state, int emergencyType) {
        PublicProviderVO mPublicProviderVO = null;
        String query;
        Cursor cursor = null;
        query = "SELECT * FROM emergency_numbers_tbl where country_short_name=" + "'" + country + "'" + " AND state_short_name=" + "'" + state + "'";
        Log.d(TAG, "QUERY TO GET EMERGENCY NUMBER=" + query);
        try {
            openDatabase();
            cursor = database.rawQuery(query, null);
            if (cursor.moveToNext()) {
                mPublicProviderVO = new PublicProviderVO();
                do {
                    switch (emergencyType) {
                        case Constant.EMERGENCTY_TYPE_FIRE:
                            mPublicProviderVO.setDisplayName(cursor.getString(cursor
                                    .getColumnIndex("fire_no")));
                            mPublicProviderVO.setEmergencyPhoneNo(cursor.getString(cursor
                                    .getColumnIndex("fire_no")));
                            break;
                        case Constant.EMERGENCTY_TYPE_POLICE:
                            mPublicProviderVO.setDisplayName(cursor.getString(cursor
                                    .getColumnIndex("police_no")));
                            mPublicProviderVO.setEmergencyPhoneNo(cursor.getString(cursor
                                    .getColumnIndex("police_no")));
                            break;
                        default:
                            if (cursor.getString(cursor
                                    .getColumnIndex("ambulance_no")).equalsIgnoreCase("108")) {
                                mPublicProviderVO.setDisplayName("EMRI - " + cursor.getString(cursor
                                        .getColumnIndex("ambulance_no")));

                            } else {
                                mPublicProviderVO.setDisplayName(cursor.getString(cursor
                                        .getColumnIndex("ambulance_no")));
                                Log.d(TAG, "");
                            }
                            mPublicProviderVO.setEmergencyPhoneNo(cursor.getString(cursor
                                    .getColumnIndex("ambulance_no")));
                            break;
                    }
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
        return mPublicProviderVO;

    }

    public void deleteAllPendingUsers() {
        try {
            openDatabaseAndBeginTransaction();
            database.delete("user_tbl", "status=" + "'" + "Pending" + "'", null);
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            setTransactionSuccessful();
            EndTransaction();
            closeDatabase();
        }
    }

    public void deleteContact(long patientAccessId) {
        try {
            openDatabaseAndBeginTransaction();
            database.delete("contact_tbl", "patient_access_id=" + patientAccessId, null);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public void deleteOrganisation(long orgId) {
        try {
            openDatabaseAndBeginTransaction();
            database.delete("user_org_table", "org_id=" + orgId, null);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public ArrayList<UserInfoVO> getAllUsersInfoVO() {
        ArrayList<UserInfoVO> userInfoVOS = new ArrayList<UserInfoVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * from user_info_tbl";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    UserInfoVO userInfoVO = new UserInfoVO();
                    userInfoVO.setPatientId(cursor.getLong(cursor
                            .getColumnIndex("patient_id")));
                    userInfoVO.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    userInfoVO.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    userInfoVO.setRelationship(cursor.getString(cursor
                            .getColumnIndex("relationship")));
                    userInfoVO.setAddress(cursor.getString(cursor
                            .getColumnIndex("address")));
                    userInfoVO.setCountry(cursor.getString(cursor
                            .getColumnIndex("country")));
                    userInfoVO.setCity(cursor.getString(cursor
                            .getColumnIndex("city")));
                    userInfoVO.setRole(cursor.getString(cursor
                            .getColumnIndex("role")));
                    userInfoVO.setCompanyName(cursor.getString(cursor
                            .getColumnIndex("company_name")));
                    userInfoVO.setState(cursor.getString(cursor
                            .getColumnIndex("state")));
                    userInfoVO.setPinCode(cursor.getString(cursor
                            .getColumnIndex("pin_code")));
                    userInfoVO.setPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("phone_number")));
                    userInfoVO.setPhoneNumberIsdCode(cursor.getString(cursor
                            .getColumnIndex("phone_number_isd_code")));
                    userInfoVO.setAltPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("alt_phone_number")));
                    userInfoVO.setAltPhoneNumberIsdCode(cursor.getString(cursor
                            .getColumnIndex("alt_phoneNumber_isd_code")));
                    userInfoVO.setGender(cursor.getInt(cursor
                            .getColumnIndex("gender")));
                    userInfoVO.setAge(cursor.getInt(cursor
                            .getColumnIndex("age")));
                    userInfoVO.setEmailId(cursor.getString(cursor
                            .getColumnIndex("email_id")));
                    userInfoVO.setPicFileId(cursor.getString(cursor
                            .getColumnIndex("pic_file_id")));
                    userInfoVO.setPicFileName(cursor.getString(cursor
                            .getColumnIndex("pic_file_name")));
                    userInfoVO.setEmergencyLevel(cursor.getInt(cursor
                            .getColumnIndex("emergency_level")));
                    userInfoVO.setFinancialStatus(cursor.getString(cursor
                            .getColumnIndex("financial_status")));
                    userInfoVO.setFamilyHistory(cursor.getString(cursor
                            .getColumnIndex("family_history")));
                    userInfoVO.setHabits(cursor.getString(cursor
                            .getColumnIndex("habits")));
                    userInfoVO.setRemarks(cursor.getString(cursor
                            .getColumnIndex("remarks")));
                    userInfoVO.setBloodGroup(cursor.getString(cursor
                            .getColumnIndex("blood_group")));
                    userInfoVO.setPatientHandle(cursor.getString(cursor
                            .getColumnIndex("patient_handle")));
                    userInfoVO.setBloodDonation(cursor.getString(cursor
                            .getColumnIndex("blood_donation")));
                    userInfoVO.setOrganDonation(cursor.getString(cursor
                            .getColumnIndex("organ_donation")));
                    userInfoVO.setMaritalStatus(cursor.getString(cursor
                            .getColumnIndex("marital_status")));
                    userInfoVO.setFoodHabits(cursor.getString(cursor
                            .getColumnIndex("food_habits")));
                    userInfoVO.setNotifyBloodDonationRequest(cursor.getInt(cursor
                            .getColumnIndex("notify_blood_donation_request")) == 1 ? true : false);

                    Calendar dob = Calendar.getInstance();
                    dob.setTimeInMillis(cursor.getLong(cursor
                            .getColumnIndex("date_of_birth")));
                    userInfoVO.setDateOfBirth(dob.getTime());

                    if (cursor.getLong(cursor
                            .getColumnIndex("last_blood_donation_date")) != 0) {
                        Calendar calLastBloodDonatedDate = Calendar.getInstance();
                        calLastBloodDonatedDate.setTimeInMillis(cursor.getLong(cursor
                                .getColumnIndex("last_blood_donation_date")));
                        userInfoVO.setLastBloodDonationDate(calLastBloodDonatedDate.getTime());
                    }

                    userInfoVO.setPreferredOrgBranchId(cursor.getLong(cursor
                            .getColumnIndex("preferred_org_branch_id")));
                    userInfoVO.setPreferredBloodBankId(cursor.getLong(cursor
                            .getColumnIndex("preferred_blood_bank_id")));
                    userInfoVO.setCreatedBy(cursor.getLong(cursor
                            .getColumnIndex("created_by")));
                    userInfoVO.setUpdatedBy(cursor.getLong(cursor
                            .getColumnIndex("updated_by")));
                    Calendar createDate = Calendar.getInstance();
                    createDate.setTimeInMillis(cursor.getLong(cursor
                            .getColumnIndex("created_date")));
                    userInfoVO.setCreatedDate(createDate.getTime());
                    Calendar updateDate = Calendar.getInstance();
                    updateDate.setTimeInMillis(cursor.getLong(cursor
                            .getColumnIndex("updated_date")));
                    userInfoVO.setUpdatedDate(updateDate.getTime());
                    userInfoVOS.add(userInfoVO);
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
        Log.d(TAG, "userList size=" + userInfoVOS.size());
        return userInfoVOS;
    }

    public void insertUserUploadInsurance(InsuranceUpload insuranceVO) {
        if (insuranceVO != null) {
            try {
                openDatabaseAndBeginTransaction();
                ContentValues mContentValues = new ContentValues();
                mContentValues.put("insurance_id", -insuranceVO.getPatientId());
                mContentValues.put("insurance_upload_id", insuranceVO.getId());
                mContentValues.put("first_name", insuranceVO.getFirstName());
                mContentValues.put("last_name", insuranceVO.getLastName());
                mContentValues.put("gender", insuranceVO.getGender());
                mContentValues.put("date_of_birth", insuranceVO.getDateOfBirth().getTime());
                mContentValues.put("aadhar_no", insuranceVO.getAadharNo());
                mContentValues.put("email", insuranceVO.getEmail());
                mContentValues.put("mobile_number", insuranceVO.getMobileNumber());
                mContentValues.put("address1", insuranceVO.getAddress1());
                mContentValues.put("address2", insuranceVO.getAddress2());
                mContentValues.put("city", insuranceVO.getCity());
                mContentValues.put("district", insuranceVO.getDistrict());
                mContentValues.put("pincode", insuranceVO.getPinCode());
                mContentValues.put("nominee_name", insuranceVO.getNomineeName());
                mContentValues.put("nominee_relationship", insuranceVO.getNomineeRelationShip());
                mContentValues.put("patient_id", insuranceVO.getPatientId());
                mContentValues.put("customer_id", insuranceVO.getCustomerId());
                mContentValues.put("status", insuranceVO.getStatus());
                if (insuranceVO.getSentDate() != null) {
                    mContentValues.put("sent_date", insuranceVO.getSentDate().getTime());

                }
                mContentValues.put("customer_id", insuranceVO.getPatientId());
                mContentValues.put("ins_policy_number", insuranceVO.getPolicyNo());
                if (insuranceVO.getStartDate() != null) {
                    mContentValues.put("ins_policy_start_date", insuranceVO.getStartDate().getTime());
                }
                Log.d(TAG, "ins_policy_start_date=" + insuranceVO.getStartDate());
                if (insuranceVO.getEndDate() != null) {
                    mContentValues.put("ins_policy_end_date", insuranceVO.getEndDate().getTime());
                }
                Log.d(TAG, "ins_policy_end_date=" + insuranceVO.getEndDate());

                mContentValues.put("is_mobile_number_verified", insuranceVO.isMobileNumberVerified());
                mContentValues.put("is_aadhar_verified", insuranceVO.isAadharVerified());
                mContentValues.put("is_email_verified", insuranceVO.isEmailVerified());
                if (insuranceVO.getDateCreated() != null) {
                    mContentValues.put("date_created", insuranceVO.getDateCreated().getTime());
                }
                if (insuranceVO.getLastUpdated() != null) {
                    mContentValues.put("date_updated", insuranceVO.getLastUpdated().getTime());
                }
                mContentValues.put("created_by", insuranceVO.getCreatedByProfileId());
                mContentValues.put("updated_by", insuranceVO.getUpdatedByProfileId());
                mContentValues.put("is_not_existing_insurance_policy", true);
                mContentValues.put("ins_policy_coverage", 100000);
                mContentValues.put("ins_policy_company", "Apollo Munich Health Insurance");
                mContentValues.put("ins_policy_name", "Group Emergency Accident Insurance Policy");
                mContentValues.put("ins_policy_number", insuranceVO.getPolicyNo());
                mContentValues.put("paytm_ref_id", insuranceVO.getPaytmRefId());
                mContentValues.put("ins_policy_claim_number", "18001020333");


                mContentValues.put("policy_doc", insuranceVO.getPolicyDoc() != null ? AppUtil.convertToJsonString(insuranceVO.getPolicyDoc()) : null);
                long i = database.insertWithOnConflict("insurance_info_tbl", null,
                        mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "insertUserInsuranceDetails " + mContentValues + "\n db insert : " + i);
                setTransactionSuccessful();
                EndTransaction();
                // sendBroadcast(Constant.ACTION_INSURANCE_DATA_CHANGED);
            } catch (SQLException e) {
                Log.i(TAG, "insertUserUploadInsurance>>Exception:" + e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, "insertUserUploadInsurance>>Exception:" + e.getMessage());
            } finally {
                closeDatabase();
            }
        }
        //clearInsuranceStickyNotification(insuranceVO);
    }

    private void clearInsuranceStickyNotification(InsuranceUpload insuranceVO) {
        if (AppUtil.isMyServiceRunning(context, StickyNotificationInsuranceFGService.class)) {
            if (insuranceVO.isEmailVerified() && insuranceVO.isMobileNumberVerified() && insuranceVO.isAadharVerified()) {
                if (context != null) {
                    Log.d("stopping service", "stopping service");
                    Intent startIntent = new Intent(context, StickyNotificationInsuranceFGService.class);
                    startIntent.setAction(Constant.ACTION.STOP);
                    context.startService(startIntent);
                }
            }
        }
    }

    public void updateIsMobileVerified(InsuranceVO mInsuranceVO) {
        Log.d("insurance_upload_id=", mInsuranceVO.getInsuranceUploadId() + "");
        try {
            openDatabaseAndBeginTransaction();
            database.execSQL("UPDATE insurance_info_tbl SET is_mobile_number_verified=1" + " WHERE insurance_upload_id=" + mInsuranceVO.getInsuranceUploadId() + " AND patient_id=" + mInsuranceVO.getPatientId());
/*
                ContentValues mContentValues = new ContentValues();
                mContentValues.put("is_mobile_number_verified", true);
                String[] args = new String[]{String.valueOf(mInsuranceVO.getInsuranceUploadId()),String.valueOf(mInsuranceVO.getPatientId())};
                long result=database.update("insurance_info_tbl", mContentValues, "insurance_upload_id=? AND patient_id=?", args);*/
            //Log.d("result=",result+"");
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {

            closeDatabase();
        }
    }

    public void updateInsuranceTblVerifyEmail(long insuranceId, boolean isEmailVerified) {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("is_email_verified", isEmailVerified);
            String[] args = new String[]{String.valueOf(insuranceId)};
            database.update("insurance_info_tbl", mContentValues, "insurance_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
    }

    public InsuranceVO getBoughtInsurance(long patientId, long insuranceUploadId) {
        InsuranceVO insuranceVO = null;
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM insurance_info_tbl WHERE patient_id='"
                + patientId + "'" + " AND insurance_upload_id=" + insuranceUploadId;
        Log.d(TAG, "getAllInsurances=\n" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    insuranceVO = new InsuranceVO();
                    insuranceVO.setInsuranceId(cursor.getLong(cursor
                            .getColumnIndex("insurance_id")));
                    Log.d(TAG, "1");

                    Log.d(TAG, "insurance_id=" + cursor.getLong(cursor
                            .getColumnIndex("insurance_id")));
                    insuranceVO.setInsPolicyCompany(cursor.getString(cursor
                            .getColumnIndex("ins_policy_company")));
                    Log.d(TAG, "1");

                    insuranceVO.setInsPolicyCoverage(cursor.getString(cursor
                            .getColumnIndex("ins_policy_coverage")));
                    Log.d(TAG, "1");

                    insuranceVO.setInsPolicyName(cursor.getString(cursor
                            .getColumnIndex("ins_policy_name")));
                    Log.d(TAG, "1");

                    insuranceVO.setInsPolicyNo(cursor.getString(cursor
                            .getColumnIndex("ins_policy_number")));
                    Log.d(TAG, "1");

                    insuranceVO.setPatientId(cursor.getLong(cursor
                            .getColumnIndex("patient_id")));
                    Log.d(TAG, "1");

                    insuranceVO.setInsPolicyStartDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("ins_policy_start_date"))));
                    insuranceVO.setInsPolicyEndDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("ins_policy_end_date"))));
                    insuranceVO.setClaimPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("ins_policy_claim_number")));
                    Log.d(TAG, "1");

                    insuranceVO.setCreatedDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("date_created"))));
                    insuranceVO.setUpdatedDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("date_updated"))));
                    insuranceVO.setCreatedBy(cursor.getLong(cursor
                            .getColumnIndex("created_by")));
                    Log.d(TAG, "1");

                    insuranceVO.setUpdatedBy(cursor.getLong(cursor
                            .getColumnIndex("updated_by")));
                    Log.d(TAG, "1");


                    insuranceVO.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    Log.d(TAG, "1");

                    insuranceVO.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    Log.d(TAG, "1");

                    insuranceVO.setNotExisitingPolicy(cursor.getInt(cursor
                            .getColumnIndex("is_not_existing_insurance_policy")) == 1 ? true : false);
                    Log.d(TAG, "1");

                    insuranceVO.setEmailVerified(cursor.getInt(cursor
                            .getColumnIndex("is_email_verified")) == 1 ? true : false);
                    Log.d(TAG, "1");

                    insuranceVO.setAadharVerified(cursor.getInt(cursor
                            .getColumnIndex("is_aadhar_verified")) == 1 ? true : false);
                    Log.d(TAG, "1");

                    insuranceVO.setMobileNumberVerified(cursor.getInt(cursor
                            .getColumnIndex("is_mobile_number_verified")) == 1 ? true : false);
                    Log.d(TAG, "1");

                    insuranceVO.setPolicyDoc((UserUploadedMedia) AppUtil.convertJsonToGson(cursor.getString(cursor
                            .getColumnIndex("policy_doc"))));
                    Log.d(TAG, "1");

                    if (cursor.getLong(cursor
                            .getColumnIndex("sent_date")) != 0) {
                        insuranceVO.setSentDate(getDate(cursor.getLong(cursor
                                .getColumnIndex("sent_date"))));
                    }
                    insuranceVO.setSentDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("sent_date"))));
                    insuranceVO.setStatus(cursor.getInt(cursor
                            .getColumnIndex("status")));
                    Log.d(TAG, "1");

                    insuranceVO.setCustomerId(cursor.getString(cursor
                            .getColumnIndex("customer_id")));
                    Log.d(TAG, "1");

                    insuranceVO.setPinCode(cursor.getString(cursor
                            .getColumnIndex("pincode")));
                    Log.d(TAG, "1");

                    insuranceVO.setCity(cursor.getString(cursor
                            .getColumnIndex("city")));
                    Log.d(TAG, "1");

                    insuranceVO.setAddress2(cursor.getString(cursor
                            .getColumnIndex("address2")));
                    Log.d(TAG, "1");

                    insuranceVO.setAddress1(cursor.getString(cursor
                            .getColumnIndex("address1")));
                    Log.d(TAG, "1");

                    insuranceVO.setMobileNumber(cursor.getString(cursor
                            .getColumnIndex("mobile_number")));
                    Log.d(TAG, "1");

                    insuranceVO.setEmail(cursor.getString(cursor
                            .getColumnIndex("email")));
                    Log.d(TAG, "1");

                    insuranceVO.setAadharNo(cursor.getString(cursor
                            .getColumnIndex("aadhar_no")));
                    Log.d(TAG, "1");

                    insuranceVO.setDateOfBirth(getDate(cursor.getLong(cursor
                            .getColumnIndex("date_of_birth"))));
                    Log.d(TAG, "1");

                    insuranceVO.setGender(cursor.getInt(cursor
                            .getColumnIndex("gender")));
                    insuranceVO.setInsuranceUploadId(cursor.getLong(cursor
                            .getColumnIndex("insurance_upload_id")));
                    insuranceVO.setPaytmRefId(cursor.getString(cursor
                            .getColumnIndex("paytm_ref_id")));
                    Log.d(TAG, "1");

                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            Log.i(TAG, "getAllInsurances>>Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "getAllInsurances>>Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return insuranceVO;
    }

    public InsuranceVO getPurchasedInsurances(long patientId) {
        InsuranceVO insuranceVO = null;
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM insurance_info_tbl WHERE patient_id='"
                + patientId + "'" + " AND insurance_upload_id!=0";
        Log.d(TAG, "getAllInsurances=\n" + selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    insuranceVO = new InsuranceVO();
                    insuranceVO.setInsuranceId(cursor.getLong(cursor
                            .getColumnIndex("insurance_id")));
                    Log.d(TAG, "1");

                    Log.d(TAG, "insurance_id=" + cursor.getLong(cursor
                            .getColumnIndex("insurance_id")));
                    insuranceVO.setInsPolicyCompany(cursor.getString(cursor
                            .getColumnIndex("ins_policy_company")));
                    Log.d(TAG, "1");

                    insuranceVO.setInsPolicyCoverage(cursor.getString(cursor
                            .getColumnIndex("ins_policy_coverage")));
                    Log.d(TAG, "1");

                    insuranceVO.setInsPolicyName(cursor.getString(cursor
                            .getColumnIndex("ins_policy_name")));
                    Log.d(TAG, "1");
                    insuranceVO.setNomineeName(cursor.getString(cursor
                            .getColumnIndex("nominee_name")));

                    insuranceVO.setNomineeRelationShip(cursor.getString(cursor
                            .getColumnIndex("nominee_relationship")));

                    insuranceVO.setInsPolicyNo(cursor.getString(cursor
                            .getColumnIndex("ins_policy_number")));
                    Log.d(TAG, "1");

                    insuranceVO.setPatientId(cursor.getLong(cursor
                            .getColumnIndex("patient_id")));
                    Log.d(TAG, "1");

                    insuranceVO.setInsPolicyStartDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("ins_policy_start_date"))));
                    insuranceVO.setInsPolicyEndDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("ins_policy_end_date"))));
                    insuranceVO.setClaimPhoneNumber(cursor.getString(cursor
                            .getColumnIndex("ins_policy_claim_number")));
                    Log.d(TAG, "1");

                    insuranceVO.setCreatedDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("date_created"))));
                    insuranceVO.setUpdatedDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("date_updated"))));
                    insuranceVO.setCreatedBy(cursor.getLong(cursor
                            .getColumnIndex("created_by")));
                    Log.d(TAG, "1");

                    insuranceVO.setUpdatedBy(cursor.getLong(cursor
                            .getColumnIndex("updated_by")));
                    Log.d(TAG, "1");


                    insuranceVO.setFirstName(cursor.getString(cursor
                            .getColumnIndex("first_name")));
                    Log.d(TAG, "1");

                    insuranceVO.setLastName(cursor.getString(cursor
                            .getColumnIndex("last_name")));
                    Log.d(TAG, "1");

                    insuranceVO.setNotExisitingPolicy(cursor.getInt(cursor
                            .getColumnIndex("is_not_existing_insurance_policy")) == 1 ? true : false);
                    Log.d(TAG, "1");

                    insuranceVO.setEmailVerified(cursor.getInt(cursor
                            .getColumnIndex("is_email_verified")) == 1 ? true : false);
                    Log.d(TAG, "1");

                    insuranceVO.setAadharVerified(cursor.getInt(cursor
                            .getColumnIndex("is_aadhar_verified")) == 1 ? true : false);
                    Log.d(TAG, "1");

                    insuranceVO.setMobileNumberVerified(cursor.getInt(cursor
                            .getColumnIndex("is_mobile_number_verified")) == 1 ? true : false);
                    Log.d(TAG, "1");

                    insuranceVO.setPolicyDoc((UserUploadedMedia) AppUtil.convertJsonToGson(cursor.getString(cursor
                            .getColumnIndex("policy_doc"))));
                    Log.d(TAG, "1");

                    if (cursor.getLong(cursor
                            .getColumnIndex("sent_date")) != 0) {
                        insuranceVO.setSentDate(getDate(cursor.getLong(cursor
                                .getColumnIndex("sent_date"))));
                    }
                    insuranceVO.setSentDate(getDate(cursor.getLong(cursor
                            .getColumnIndex("sent_date"))));
                    insuranceVO.setStatus(cursor.getInt(cursor
                            .getColumnIndex("status")));
                    Log.d(TAG, "1");

                    insuranceVO.setCustomerId(cursor.getString(cursor
                            .getColumnIndex("customer_id")));
                    Log.d(TAG, "1");

                    insuranceVO.setPinCode(cursor.getString(cursor
                            .getColumnIndex("pincode")));
                    Log.d(TAG, "1");

                    insuranceVO.setCity(cursor.getString(cursor
                            .getColumnIndex("city")));
                    Log.d(TAG, "1");

                    insuranceVO.setAddress2(cursor.getString(cursor
                            .getColumnIndex("address2")));
                    Log.d(TAG, "1");
                    insuranceVO.setDistrict(cursor.getString(cursor
                            .getColumnIndex("district")));
                    Log.d(TAG, "1");

                    insuranceVO.setAddress1(cursor.getString(cursor
                            .getColumnIndex("address1")));
                    Log.d(TAG, "1");

                    insuranceVO.setMobileNumber(cursor.getString(cursor
                            .getColumnIndex("mobile_number")));
                    Log.d(TAG, "1");

                    insuranceVO.setEmail(cursor.getString(cursor
                            .getColumnIndex("email")));
                    Log.d(TAG, "1");

                    insuranceVO.setAadharNo(cursor.getString(cursor
                            .getColumnIndex("aadhar_no")));
                    Log.d(TAG, "1");

                    insuranceVO.setDateOfBirth(getDate(cursor.getLong(cursor
                            .getColumnIndex("date_of_birth"))));
                    Log.d(TAG, "1");

                    insuranceVO.setGender(cursor.getInt(cursor
                            .getColumnIndex("gender")));
                    insuranceVO.setInsuranceUploadId(cursor.getLong(cursor
                            .getColumnIndex("insurance_upload_id")));
                    insuranceVO.setPaytmRefId(cursor.getString(cursor
                            .getColumnIndex("paytm_ref_id")));
                    Log.d(TAG, "1");

                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            Log.i(TAG, "getAllInsurances>>Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "getAllInsurances>>Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return insuranceVO;
    }

    public void updateIsAadharVerified(InsuranceVO insuranceVO) {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("is_aadhar_verified", true);
            String[] args = new String[]{String.valueOf(insuranceVO.getInsuranceUploadId())};
            database.update("insurance_info_tbl", mContentValues, "insurance_upload_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
    }

    public long checkIfContactExistInDB(String contact) {
        openDatabase();
        long rowCount = DatabaseUtils.queryNumEntries(database, "blood_friends_tbl",
                "phone_number=?", new String[]{contact});
        closeDatabase();
        Log.d("MATCHING_ROWS_COUNT", rowCount + "");
        return rowCount;
    }

    public ArrayList<AmbulanceDetailsVO> getNearbyAmbulanceList(int status) { // status = Online/offile
        ArrayList<AmbulanceDetailsVO> ambulanceList = new ArrayList<>();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM ambulance_details_tbl WHERE ambulance_status=" + status + " ORDER BY distance ASC";    /*WHERE status="+status+" ORDER BY distance"*/
        ;
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    AmbulanceDetailsVO ambulanceDetailsVO = new AmbulanceDetailsVO();
                    ambulanceDetailsVO.setAmbulanceId(cursor.getLong(cursor
                            .getColumnIndex("ambulance_id")));
                    ambulanceDetailsVO.setLatitude(cursor.getString(cursor
                            .getColumnIndex("latitude")));
                    ambulanceDetailsVO.setLongitude(cursor.getString(cursor
                            .getColumnIndex("longitude")));
                    ambulanceDetailsVO.setLicensePlateNo(cursor.getString(cursor
                            .getColumnIndex("license_plate_no")));
                    ambulanceDetailsVO.setOrgName(cursor.getString(cursor
                            .getColumnIndex("org_name")));
                    ambulanceDetailsVO.setCategory(cursor.getString(cursor
                            .getColumnIndex("category")));
                    ambulanceDetailsVO.setAmbManagerPhoneNo(cursor.getString(cursor
                            .getColumnIndex("amb_manager_phone_no")));
                    ambulanceDetailsVO.setMakeAndModel(cursor.getString(cursor
                            .getColumnIndex("make_and_model")));
                    ambulanceDetailsVO.setYearOfManufacture(cursor.getString(cursor
                            .getColumnIndex("year_of_manufacture")));
                    ambulanceDetailsVO.setMinFare(cursor.getFloat(cursor
                            .getColumnIndex("min_fare")));
                    ambulanceDetailsVO.setPriceRate(cursor.getFloat(cursor
                            .getColumnIndex("price_rate")));
                    ambulanceDetailsVO.setRideTimeRate(cursor.getFloat(cursor
                            .getColumnIndex("ride_time_rate")));
                    ambulanceDetailsVO.setOxygen(cursor.getInt(cursor
                            .getColumnIndex("oxygen")) == 1 ? true : false);
                    ambulanceDetailsVO.setWheelChair(cursor.getInt(cursor
                            .getColumnIndex("wheel_chair")) == 1 ? true : false);
                    ambulanceDetailsVO.setStretcher(cursor.getInt(cursor
                            .getColumnIndex("stretcher")) == 1 ? true : false);
                    ambulanceDetailsVO.setFreezerBox(cursor.getInt(cursor
                            .getColumnIndex("freezer_box")) == 1 ? true : false);
                    ambulanceDetailsVO.setVentilator(cursor.getInt(cursor
                            .getColumnIndex("ventilator")) == 1 ? true : false);
                    ambulanceDetailsVO.setAirConditioner(cursor.getInt(cursor
                            .getColumnIndex("air_conditioner")) == 1 ? true : false);
                    ambulanceDetailsVO.setEMTAvailability(cursor.getInt(cursor
                            .getColumnIndex("emt_availability")) == 1 ? true : false);
                    ambulanceDetailsVO.setAED(cursor.getInt(cursor
                            .getColumnIndex("AED")) == 1 ? true : false);
                    ambulanceDetailsVO.setSeatingCapacityNumber(cursor.getInt(cursor
                            .getColumnIndex("seating_capacity_number")));
                    Log.d(TAG, "GET ambulance_status=" + cursor.getInt(cursor
                            .getColumnIndex("ambulance_status")) + "");
                    ambulanceDetailsVO.setAmbulanceStatus(cursor.getInt(cursor
                            .getColumnIndex("ambulance_status")));
                    Log.d(TAG, "getNearbyAmbulanceList>>distanceInKms=" + cursor.getDouble(cursor
                            .getColumnIndex("distance")));

                    ambulanceDetailsVO.setDistanceInKms(cursor.getDouble(cursor
                            .getColumnIndex("distance")));
                    ambulanceList.add(ambulanceDetailsVO);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.i(TAG, "getAllInsurances>>Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "getAllInsurances>>Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        Log.d(TAG, "getNearbyAmbulanceList>>amblist=" + ambulanceList.size());
        return ambulanceList;
    }

    public void insertNearbyAmbulances(ArrayList<AmbulanceDetailsVO> ambulanceDetailsVOS, int status) {
        if (ambulanceDetailsVOS.size() != 0) {
            Log.d(TAG, "insertNearbyAmbulances>>amblist=" + ambulanceDetailsVOS.size());
            try {
                openDatabaseAndBeginTransaction();
                for (AmbulanceDetailsVO ambulanceDetailsVO : ambulanceDetailsVOS) {
                    Log.d("AMBULANCE_ID=", ambulanceDetailsVO.getAmbulanceId() + "");
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put("ambulance_id",
                            ambulanceDetailsVO.getAmbulanceId());
                    mContentValues.put("latitude",
                            ambulanceDetailsVO.getLatitude());
                    mContentValues.put("longitude",
                            ambulanceDetailsVO.getLongitude());
                    mContentValues.put("license_plate_no",
                            ambulanceDetailsVO.getLicensePlateNo());
                    mContentValues.put("org_name",
                            ambulanceDetailsVO.getOrgName());
                    mContentValues.put("category",
                            ambulanceDetailsVO.getCategory());
                    mContentValues.put("amb_manager_phone_no",
                            ambulanceDetailsVO.getAmbManagerPhoneNo());
                    mContentValues.put("make_and_model",
                            ambulanceDetailsVO.getMakeAndModel());
                    mContentValues.put("year_of_manufacture",
                            ambulanceDetailsVO.getYearOfManufacture());
                    mContentValues.put("min_fare",
                            ambulanceDetailsVO.getMinFare());
                    mContentValues.put("price_rate",
                            ambulanceDetailsVO.getPriceRate());
                    mContentValues.put("ride_time_rate",
                            ambulanceDetailsVO.getRideTimeRate());
                    mContentValues.put("oxygen",
                            ambulanceDetailsVO.isOxygen());
                    mContentValues.put("wheel_chair",
                            ambulanceDetailsVO.isWheelChair());
                    mContentValues.put("stretcher", ambulanceDetailsVO.isWheelChair());
                    mContentValues.put("freezer_box", ambulanceDetailsVO.isFreezerBox());
                    mContentValues.put("ventilator", ambulanceDetailsVO.isVentilator());
                    mContentValues.put("air_conditioner", ambulanceDetailsVO.isAirConditioner());
                    mContentValues.put("emt_availability",
                            ambulanceDetailsVO.isEMTAvailability());
                    mContentValues.put("AED",
                            ambulanceDetailsVO.isAED());
                    mContentValues.put("seating_capacity_number", ambulanceDetailsVO.getSeatingCapacityNumber());
                    mContentValues.put("ambulance_status", status);
                    Log.d(TAG, "insertNearbyAmbulances>>distanceInKms=" + ambulanceDetailsVO.getDistanceInKms());
                    mContentValues.put("distance",
                            ambulanceDetailsVO.getDistanceInKms());
                    database.insertWithOnConflict("ambulance_details_tbl", null,
                            mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                }

            } catch (SQLException e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e.getMessage());
            } finally {
                setTransactionSuccessful();
                EndTransaction();
                closeDatabase();
            }
        }
    }

    public void updateDistanceNearbyAmbulances(double distance, long ambulanceId) {
        try {
            openDatabaseAndBeginTransaction();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("distance", distance);
            String[] args = new String[]{String.valueOf(ambulanceId)};
            database.update("ambulance_details_tbl", mContentValues, "ambulance_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
    }

    public void deleteNearbyAmbulances(int ambStatus) {
        try {
            openDatabaseAndBeginTransaction();
            database.delete("ambulance_details_tbl", "ambulance_status=" + ambStatus, null);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabase();
        }
    }

    public ArrayList<AmbulanceDetailsVO> getNearbyAmbulanceListFromLocalDB(int status) {
        int distanceInKms = 50;
        ArrayList<AmbulanceDetailsVO> ambulanceList = new ArrayList<>();
        Float currentLocationLat, currentLocationLon;
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        double earthRadius = 6371;  // earth's mean radius, km
        if (status == Constant.ONLINE) {
            currentLocationLat = defaultSharedPreferences.getFloat(Constant.LAT_NEARBY_AMB, 0);
            currentLocationLon = defaultSharedPreferences.getFloat(Constant.LON_NEARBY_AMB, 0);
        } else {
            currentLocationLat = defaultSharedPreferences.getFloat(Constant.LAT_NEARBY_AMB_OFF, 0);
            currentLocationLon = defaultSharedPreferences.getFloat(Constant.LON_NEARBY_AMB_OFF, 0);
        }
        // first-cut bounding box (in degrees)
        double maxLat = currentLocationLat + Math.toDegrees(distanceInKms / earthRadius);
        double minLat = currentLocationLat - Math.toDegrees(distanceInKms / earthRadius);
        double maxLon = currentLocationLon + Math.toDegrees(Math.asin(distanceInKms / earthRadius) / Math.cos(Math.toRadians(currentLocationLat)));
        double minLon = currentLocationLon - Math.toDegrees(Math.asin(distanceInKms / earthRadius) / Math.cos(Math.toRadians(currentLocationLat)));

        Cursor cursor = null;
        String selectQuery = "SELECT * FROM ambulance_details_tbl WHERE" + " (latitude >= " + minLat + " AND latitude <= " + maxLat + ")" + " AND (longitude >= " + minLon + " AND longitude <=" + maxLon + ")" + " AND ambulance_status=" + status + " ORDER BY distance ASC LIMIT 50";
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    AmbulanceDetailsVO ambulanceDetailsVO = new AmbulanceDetailsVO();
                    ambulanceDetailsVO.setAmbulanceId(cursor.getLong(cursor
                            .getColumnIndex("ambulance_id")));
                    ambulanceDetailsVO.setLatitude(cursor.getString(cursor
                            .getColumnIndex("latitude")));
                    ambulanceDetailsVO.setLongitude(cursor.getString(cursor
                            .getColumnIndex("longitude")));
                    ambulanceDetailsVO.setLicensePlateNo(cursor.getString(cursor
                            .getColumnIndex("license_plate_no")));
                    ambulanceDetailsVO.setOrgName(cursor.getString(cursor
                            .getColumnIndex("org_name")));
                    ambulanceDetailsVO.setCategory(cursor.getString(cursor
                            .getColumnIndex("category")));
                    ambulanceDetailsVO.setAmbManagerPhoneNo(cursor.getString(cursor
                            .getColumnIndex("amb_manager_phone_no")));
                    ambulanceDetailsVO.setMakeAndModel(cursor.getString(cursor
                            .getColumnIndex("make_and_model")));
                    ambulanceDetailsVO.setYearOfManufacture(cursor.getString(cursor
                            .getColumnIndex("year_of_manufacture")));
                    ambulanceDetailsVO.setMinFare(cursor.getFloat(cursor
                            .getColumnIndex("min_fare")));
                    ambulanceDetailsVO.setPriceRate(cursor.getFloat(cursor
                            .getColumnIndex("price_rate")));
                    ambulanceDetailsVO.setRideTimeRate(cursor.getFloat(cursor
                            .getColumnIndex("ride_time_rate")));
                    ambulanceDetailsVO.setOxygen(cursor.getInt(cursor
                            .getColumnIndex("oxygen")) == 1 ? true : false);
                    ambulanceDetailsVO.setWheelChair(cursor.getInt(cursor
                            .getColumnIndex("wheel_chair")) == 1 ? true : false);
                    ambulanceDetailsVO.setStretcher(cursor.getInt(cursor
                            .getColumnIndex("stretcher")) == 1 ? true : false);
                    ambulanceDetailsVO.setFreezerBox(cursor.getInt(cursor
                            .getColumnIndex("freezer_box")) == 1 ? true : false);
                    ambulanceDetailsVO.setVentilator(cursor.getInt(cursor
                            .getColumnIndex("ventilator")) == 1 ? true : false);
                    ambulanceDetailsVO.setAirConditioner(cursor.getInt(cursor
                            .getColumnIndex("air_conditioner")) == 1 ? true : false);
                    ambulanceDetailsVO.setEMTAvailability(cursor.getInt(cursor
                            .getColumnIndex("emt_availability")) == 1 ? true : false);
                    ambulanceDetailsVO.setAED(cursor.getInt(cursor
                            .getColumnIndex("AED")) == 1 ? true : false);
                    ambulanceDetailsVO.setSeatingCapacityNumber(cursor.getInt(cursor
                            .getColumnIndex("seating_capacity_number")));
                    Log.d(TAG, "GET ambulance_status=" + cursor.getInt(cursor
                            .getColumnIndex("ambulance_status")) + "");
                    ambulanceDetailsVO.setAmbulanceStatus(cursor.getInt(cursor
                            .getColumnIndex("ambulance_status")));
                    Log.d(TAG, "getNearbyAmbulanceList>>distanceInKms=" + cursor.getDouble(cursor
                            .getColumnIndex("distance")));

                    ambulanceDetailsVO.setDistanceInKms(cursor.getDouble(cursor
                            .getColumnIndex("distance")));
                    ambulanceList.add(ambulanceDetailsVO);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.i(TAG, "getAllInsurances>>Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "getAllInsurances>>Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        Log.d(TAG, "getNearbyAmbulanceList>>amblist=" + ambulanceList.size());
        return ambulanceList;
    }

    public void updateNearbyAmbLoc(long ambId, double distance) {
        int update = -1;
        try {
            openDatabaseAndBeginTransactionBF();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("distance", distance);
            String[] args = new String[]{String.valueOf(ambId)};
            update = database.update("ambulance_details_tbl", mContentValues, "ambulance_id=?", args);
            setTransactionSuccessful();
            EndTransaction();
        } catch (SQLException e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            closeDatabaseBF();
        }
    }

    public Date getFacilitiesLastUpdatedTimeStamp() throws Exception {
        Cursor cursor = null;
        Date lastUpdatedRecordTimeStamp = null;
        String selectQuery = "SELECT MAX (last_updated) AS Max_Date FROM facilities_tbl";
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(context.getString(R.string.ins_date_format), Locale.ENGLISH);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
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

    public Date getSpecialitiesLastUpdatedTimeStamp() throws Exception {
        Cursor cursor = null;
        Date lastUpdatedRecordTimeStamp = null;
        String selectQuery = "SELECT MAX (last_updated) AS Max_Date FROM specialities_tbl";
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(context.getString(R.string.ins_date_format), Locale.ENGLISH);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
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

    public Date getAvailabilityCapabilityTblLastUpdatedTimeStamp() throws Exception {
        Cursor cursor = null;
        Date lastUpdatedRecordTimeStamp = null;
        String selectQuery = "SELECT MAX (last_updated) AS Max_Date FROM availability_capability_tbl";
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
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

    public void insertFacilities(List<Facility> facilities) {
        if (facilities != null) {
            try {
                openDatabaseAndBeginTransaction();
                for (Facility facility : facilities) {
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put("facility_id", facility.getId());
                    mContentValues.put("code", facility.getCode());
                    mContentValues.put("display_name", facility.getDisplayName());
                    mContentValues.put("notes", facility.getNotes());
                    mContentValues.put("date_created", sdff.format(facility.getDateCreated()));
                    mContentValues.put("last_updated", sdff.format(facility.getLastUpdated()));
                    database.insertWithOnConflict("facilities_tbl", null, mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                }
                setTransactionSuccessful();
                EndTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeDatabase();
            }
        }
    }

    public void insertSpecialities(List<Speciality> specialities) {
        if (specialities != null) {
            try {
                openDatabaseAndBeginTransaction();

                for (Speciality speciality : specialities) {

                    ContentValues mContentValues = new ContentValues();

                    mContentValues.put("facility_id", speciality.getId());
                    mContentValues.put("code", speciality.getCode());
                    mContentValues.put("display_name", speciality.getDisplayName());
                    mContentValues.put("notes", speciality.getNotes());
                    mContentValues.put("date_created", sdff.format(speciality.getDateCreated()));
                    mContentValues.put("last_updated", sdff.format(speciality.getLastUpdated()));

                    database.insertWithOnConflict("specialities_tbl", null, mContentValues, SQLiteDatabase.CONFLICT_REPLACE);

                }
                setTransactionSuccessful();
                EndTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeDatabase();
            }
        }
    }

    public void insertMasterDataFacilities(List<Facility> facilities) throws Exception {

        if (facilities != null) {
            try {
                openDatabaseAndBeginTransaction();

                for (Facility facility : facilities) {

                    ContentValues mContentValues = new ContentValues();

                    mContentValues.put("facility_id", facility.getId());
                    mContentValues.put("code", facility.getCode());
                    mContentValues.put("display_name", facility.getDisplayName());
                    mContentValues.put("notes", facility.getNotes());
                    mContentValues.put("date_created", sdff.format(facility.getDateCreated()));
                    mContentValues.put("last_updated", sdff.format(facility.getLastUpdated()));

                    database.insertWithOnConflict("facilities_tbl", null, mContentValues, SQLiteDatabase.CONFLICT_REPLACE);

                }
                setTransactionSuccessful();
                EndTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeDatabase();
            }
        }
    }

    public void insertMasterDataSpecialities(List<Speciality> specialities) throws Exception {

        if (specialities != null) {
            try {
                openDatabaseAndBeginTransaction();

                for (Speciality speciality : specialities) {

                    ContentValues mContentValues = new ContentValues();

                    mContentValues.put("speciality_id", speciality.getId());
                    mContentValues.put("code", speciality.getCode());
                    mContentValues.put("display_name", speciality.getDisplayName());
                    mContentValues.put("notes", speciality.getNotes());
                    mContentValues.put("date_created", sdff.format(speciality.getDateCreated()));
                    mContentValues.put("last_updated", sdff.format(speciality.getLastUpdated()));

                    database.insertWithOnConflict("specialities_tbl", null, mContentValues, SQLiteDatabase.CONFLICT_REPLACE);

                }
                setTransactionSuccessful();
                EndTransaction();
            } catch (Exception e) {
                android.util.Log.d("exception", e.getMessage());
                e.printStackTrace();
            } finally {
                closeDatabase();
            }
        }
    }

    public void insertAvailabilityCapabilityVOList(List<AvailabilityCapabilityVO> availabilityCapabilityVOS) throws Exception {
        Log.d("availability_capability_list_size=", availabilityCapabilityVOS.size() + "");
        if (availabilityCapabilityVOS != null) {
            try {
                openDatabaseAndBeginTransaction();
                for (AvailabilityCapabilityVO availabilityCapabilityVO : availabilityCapabilityVOS) {
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put("org_id", availabilityCapabilityVO.getOrgId());
                    mContentValues.put("org_branch_id", availabilityCapabilityVO.getOrgBranchId());
                    mContentValues.put("org_type", availabilityCapabilityVO.getOrgType());
                    mContentValues.put("flag", availabilityCapabilityVO.getFlag());
                    mContentValues.put("speciality_id", availabilityCapabilityVO.getSpecialityId());
                    mContentValues.put("facility_id", availabilityCapabilityVO.getFacilityId());
                    mContentValues.put("profile_id", availabilityCapabilityVO.getProfileId());
                    mContentValues.put("day_of_week", availabilityCapabilityVO.getDayOfWeek());
                    mContentValues.put("is_uploaded", availabilityCapabilityVO.isUploaded());
                    mContentValues.put("start_time", availabilityCapabilityVO.getStartTime());
                    mContentValues.put("end_time", availabilityCapabilityVO.getEndTime());
                    mContentValues.put("certification_id", availabilityCapabilityVO.getCertificationId());
                    mContentValues.put("insurance_company_id", availabilityCapabilityVO.getInsuranceCompanyId());
                    mContentValues.put("availability_status", availabilityCapabilityVO.isAvailabilityStatus());
                    Log.d("LAST_UPDATED_DATE=", availabilityCapabilityVO.getLastUpdated() + "");
                    mContentValues.put("last_updated", dateFormat2.format(availabilityCapabilityVO.getLastUpdated()));
                    mContentValues.put("diagnostic_tests_id", availabilityCapabilityVO.getDiagnosticTestsId());
                    database.insertWithOnConflict("availability_capability_tbl", null, mContentValues, SQLiteDatabase.CONFLICT_REPLACE);
                }
                setTransactionSuccessful();
                EndTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeDatabase();
            }
        }
    }

    @SuppressLint("LongLogTag")
    public ArrayList<AvailabilityCapabilityVO> getOrgBranchCapabilityAvailabilityList(long orgBranchId, String req_type) {
        ArrayList<AvailabilityCapabilityVO> mAvailabilityCapabilityVOS = new ArrayList<>();
        Cursor cursor = null;
        String selectQuery = null;
        if (req_type.equals(Constant.FACILITY)) {
            selectQuery = "SELECT * from availability_capability_tbl where org_branch_id =" + orgBranchId + " and facility_id !=0" + " GROUP BY facility_id";
        } else {
            selectQuery = "SELECT * from availability_capability_tbl where org_branch_id =" + orgBranchId + " and speciality_id !=0" + " GROUP BY speciality_id";
        }
        android.util.Log.d("getCapabilityAvailabilityList>>selectQuery", selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    AvailabilityCapabilityVO availabilityCapabilityVO = new AvailabilityCapabilityVO();
                    if (req_type.equals(Constant.FACILITY)) {
                        availabilityCapabilityVO.setFacilityId(cursor.getLong(cursor.getColumnIndex("facility_id")));
                    } else {
                        availabilityCapabilityVO.setSpecialityId(cursor.getLong(cursor.getColumnIndex("speciality_id")));
                    }
                    mAvailabilityCapabilityVOS.add(availabilityCapabilityVO);
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return mAvailabilityCapabilityVOS;
    }

    @SuppressLint("LongLogTag")
    public ArrayList<AvailabilityCapabilityVO> getSpecialityTimings(long specialityId, long orgBranchId, String dayOfWeek) {
        ArrayList<AvailabilityCapabilityVO> mAvailabilityCapabilityVOS = new ArrayList<AvailabilityCapabilityVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * from availability_capability_tbl where speciality_id=" + specialityId + " and org_branch_id=" + orgBranchId + " and day_of_week=" + "'" + dayOfWeek + "'";
        android.util.Log.d("getSpecialityTimings>>selectQuery", selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    AvailabilityCapabilityVO mAvailabilityCapabilityVO = new AvailabilityCapabilityVO();
                    mAvailabilityCapabilityVO.setStartTime(cursor.getString(cursor
                            .getColumnIndex("start_time")));
                    mAvailabilityCapabilityVO.setEndTime(cursor.getString(cursor
                            .getColumnIndex("end_time")));
                    mAvailabilityCapabilityVOS.add(mAvailabilityCapabilityVO);

                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return mAvailabilityCapabilityVOS;
    }

    @SuppressLint("LongLogTag")
    public ArrayList<AvailabilityCapabilityVO> getFacilityTimings(long facilityId, long orgBranchId, String dayOfWeek) {
        ArrayList<AvailabilityCapabilityVO> mAvailabilityCapabilityVOS = new ArrayList<AvailabilityCapabilityVO>();
        Cursor cursor = null;
        String selectQuery = "SELECT * from availability_capability_tbl where facility_id=" + facilityId + " and org_branch_id=" + orgBranchId + " and day_of_week=" + "'" + dayOfWeek + "'";
        android.util.Log.d("getSpecialityTimings>>selectQuery", selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    AvailabilityCapabilityVO mAvailabilityCapabilityVO = new AvailabilityCapabilityVO();
                    mAvailabilityCapabilityVO.setStartTime(cursor.getString(cursor
                            .getColumnIndex("start_time")));
                    mAvailabilityCapabilityVO.setEndTime(cursor.getString(cursor
                            .getColumnIndex("end_time")));
                    mAvailabilityCapabilityVOS.add(mAvailabilityCapabilityVO);

                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return mAvailabilityCapabilityVOS;
    }

    public Speciality getSpecialityDetails(long specialityId) {
        Speciality speciality = new Speciality();
        Cursor cursor = null;
        String selectQuery = "";

        selectQuery = "SELECT * from specialities_tbl where speciality_id=" + specialityId;
        Log.d("getSpecialityDetails>>", selectQuery);

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    speciality.setId(cursor.getLong(cursor.getColumnIndex("speciality_id")));
                    speciality.setCode(cursor.getString(cursor.getColumnIndex("code")));
                    speciality.setDisplayName(cursor.getString(cursor.getColumnIndex("display_name")));
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }

        return speciality;
    }

    public Facility getFacilityDetails(long facilityId) {

        Facility facility = new Facility();
        Cursor cursor = null;
        String selectQuery = "";

        selectQuery = "SELECT * from facilities_tbl where facility_id=" + facilityId;
        Log.d("getFacilityDetails>>", selectQuery);

        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    facility.setId(cursor.getLong(cursor.getColumnIndex("facility_id")));
                    facility.setCode(cursor.getString(cursor.getColumnIndex("code")));
                    facility.setDisplayName(cursor.getString(cursor.getColumnIndex("display_name")));
                    facility.setNotes(cursor.getString(cursor.getColumnIndex("notes")));
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }

        return facility;
    }


    /*public ArrayList<AvailabilityCapabilityVO> getOrgBranchSpecialitiesList(long orgBranchId) {
        ArrayList<AvailabilityCapabilityVO> mAvailabilityCapabilityVOS = new ArrayList<>();
        Cursor cursor = null;
        String selectQuery = null;
        if (req_type.equals("facility")) {
            selectQuery = "SELECT * from availability_capability_tbl where org_branch_id =" + orgBranchId + " and facility_id !=0 GROUP BY facility_id";
        } else if (req_type.equals("speciality")) {
            selectQuery = "SELECT * from availability_capability_tbl where org_branch_id =" + orgBranchId + " and speciality_id !=0" + " GROUP BY speciality_id";
        }
        android.util.Log.d("getCapabilityAvailabilityList>>selectQuery", selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    AvailabilityCapabilityVO availabilityCapabilityVO = new AvailabilityCapabilityVO();
                    if (req_type.equals("facility")) {
                        availabilityCapabilityVO.setFacilityId(cursor.getLong(cursor.getColumnIndex("facility_id")));
                    } else if (req_type.equals("speciality")) {
                        availabilityCapabilityVO.setSpecialityId(cursor.getLong(cursor.getColumnIndex("speciality_id")));
                    }
                    mAvailabilityCapabilityVOS.add(availabilityCapabilityVO);
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return mAvailabilityCapabilityVOS;
    }*/

    public ArrayList<AvailabilityCapabilityVO> getTimings(long orgBranchId, String dayOfWeek, long id, String reqType) {
        ArrayList<AvailabilityCapabilityVO> timingsList = new ArrayList<>();
        Cursor cursor = null;
        String selectQuery;
        if (reqType.equals(Constant.SPECIALITY)) {
            selectQuery = "SELECT * from availability_capability_tbl where org_branch_id=" + orgBranchId + " and day_of_week=" + "'" + dayOfWeek + "'" + " COLLATE NOCASE and speciality_id=" + id+" and start_time NOTNULL"+" and end_time NOTNULL"+" and start_time!="+"'"+"'"+" and end_time!="+"'"+"'";
        } else {
            selectQuery = "SELECT * from availability_capability_tbl where org_branch_id=" + orgBranchId + " and day_of_week=" + "'" + dayOfWeek + "'" + " COLLATE NOCASE and facility_id=" + id+" and start_time NOTNULL"+" and end_time NOTNULL"+" and start_time!="+"'"+"'"+" and end_time!="+"'"+"'";
        }
        Log.d("getTimings>>", selectQuery);
        try {
            openDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    AvailabilityCapabilityVO mAvailabilityCapabilityVO = new AvailabilityCapabilityVO();
                    mAvailabilityCapabilityVO.setStartTime(cursor.getString(cursor
                            .getColumnIndex("start_time")));
                    mAvailabilityCapabilityVO.setEndTime(cursor.getString(cursor
                            .getColumnIndex("end_time")));
                    timingsList.add(mAvailabilityCapabilityVO);

                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } catch (Exception e) {
            android.util.Log.i(TAG, "Exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return timingsList;
    }
}