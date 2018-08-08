package com.patientz.utils;

public class Constant {

    //skuCode
    public static final String APPOINTMENTREQUEST = "APPOINTMENTREQUEST";

    //service
    public static final String APPOINTMENT = "APPOINTMENT";


    public static final String EVENT_LAUNCH = "LCH";//Launch activity
    public static final String EVENT_LOGIN = "LIN";//Login activity
    public static final String EVENT_EHR = "EHR";//EHR activity
    public static final String EVENT_BLOODY_FRIENDS = "BFN";//Bloody Friends activity
    public static final String EVENT_REGISTRATION = "REG";//Registration activity


    public static final String TOKEN_TYPE = "token_type";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_ID = "userId";
    public static final String K_REG_ID = "registrationID";
    public static final String PASSWORD = "password";
    public static final String IS_USER_LOGGED_IN = "isUserLoggedIn";
    public static final String EMT_SP = "EMT_SP";
    public static final String AUTHORIZATION = "Authorization";
    public static final String INCOMING_DYNAMIC_LINK = "incomingDeepLink";
    public static final String DYNAMIC_LINK_2 = "dynamicLink2";
    public static final String APP_KEY = "doctrz4callambulance";
    public static final String APP_SALT = "mobile12";

    public static final String ROLE = "ROLE_PATIENT";
    public static final String NEW_USER_ROLE = "ROLE_NEW_USER_PATIENT";
    public static final String ROLE_REGISTERED_MEMBER = "ROLE_REGISTERED_MEMBER";
    public static final String ROLE_VERIFIED_MEMBER = "ROLE_VERIFIED_MEMBER";
    public static final String DISTANCE_IN_KMS = "distanceInKms";


    //
    public static final String LOGIN_STATUS = "LOGIN_STATUS";
    //    public static final String K_SECRET = "K_SECRET";
//    public static final String K_ID = "K_ID";
    public static final String APP_SHARED_PREFERENCE = "CallAmbulance";


    public static final String EMERGENCY_SHARED_PREFERENCE = "Emergency_Store";

    //EMERGENCY EVENT BRODCAST

    public static final String EMERGENCY_EVENT_LOCATION = "LocationEvent";
    public static final String EMERGENCY_EVENT_WEBSERVICE = "webservice";
    public static final String EMERGENCY_EVENT_SMS = "SMSContacts";
    public static final String EMERGENCY_EVENT_CALL = "CallEmergencyNUmber";

    // USER ROLES
    public static final long ROLE_UNVERIFIED_MEMBER = 1;
    // public static final long ROLE_REGISTERED_MEMBER = 2;
    public static final long ROLE_MEDICAL_STUDENT_DOCTOR = 3;
    public static final long ROLE_NURSE = 4;
    public static final long ROLE_DOCTOR = 5;
    public static final long ROLE_MEDICAL_STUDENT_NURSE = 6;
    public static final long ROLE_ADMIN_USERS_ADMIN = 7;
    public static final long ROLE_ADMIN_DATA_ADMIN = 8;
    public static final long ROLE_ADMIN_QNA_MODERATOR = 9;
    public static final long ROLE_ADMIN_SUPER_ADMIN = 10;
    public static final long ROLE_ADMIN_ORGS_MODERATOR = 11;
    public static final long ROLE_ADMIN_ORG_ADMIN = 12;
    // public static final long ROLE_VERIFIED_MEMBER = 13;
    public static final long ROLE_DOCTOR_FACULTY = 14;
    public static final long ROLE_HEALTHCARE_WORKER = 15;
    public static final long ROLE_PATIENT = 16;
    public static final long ROLE_NUTRITIONIST = 17;
    public static final long ROLE_DIETICIAN = 18;
    public static final long ROLE_OPTOMETRIST = 19;
    public static final long ROLE_AUDIOMETRIST = 20;
    public static final long ROLE_PHYSIOTHERAPIST = 21;
    public static final long ROLE_MARKETING_PERSONNEL = 22;
    public static final long ROLE_HELP_DESK = 23;
    public static final long ROLE_NOTIFIER = 24;
    public static final long ROLE_LAB_TECHNICIAN = 25;
    public static final long ROLE_PHARMACIST = 26;
    public static final long ROLE_NEW_USER_PATIENT = 27;
    public static final long ROLE_NEW_USER = 28;
    public static final long ROLE_ADMIN_LIFESTYLE_DATA_ADMIN = 29;
    // SERVER ERROR CODES

    public static final int RESPONSE_SERVER_ERROR = 501;
    public static final int RESPONSE_SUCCESS = 00;
    public static final int RESPONSE_FAILURE = -1;
    public static final int RESPONSE_NOT_LOGGED_IN = 500;
    public static final int RESPONSE_DUPLICATE_EMAIL = 101;
    public static final int RESPONSE_REQUEST_FOR_ORG = 1105;
    public static final int RESPONSE_WIPE_STATUS = 555;
    public static final int RESPONSE_EMAIL_TOKEN_FAILURE = 1010;
    public static final int RESPONSE_EMERGENCY_REVOKED = 944;
    public static final int RESPONSE_EMERGENCY_LOC_UPDATED = 933;
    public static final int RESPONSE_PATIENT_ALREADY_IN_EMERGENCY = 922;
    public static final int RESPONSE_PATIENT_IN_EMERGENCY = 911;
    public static final int RESPONSE_WRONG_EMAILID_PWD = 201;
    public static final int RESPONSE_WRONG_EMAILID_PASSWORD = 202;
    public static final int RESPONSE_ID_PROOF_UPLOAD_PENDING = 203;
    public static final int RESPONSE_ACCOUNT_NOT_ACTIVE = 204;
    public static final int RESPONSE_CODE_INCOMPLETE_REGISTRATION = 207;
    public static final int RESPONSE_CODE_PASSWORD_EXPIRE = 206;
    public static final int TYPE_DOCTOR = 1;
    public static final int DIAGNOSTIC_QUEUE_STATUS_SCHEDULED = 1;
    public static final int DIAGNOSTIC_QUEUE_STATUS_PENDING = 2;
    public static final int DIAGNOSTIC_QUEUE_STATUS_REVIEW = 3;
    public static final int DIAGNOSTIC_QUEUE_STATUS_COMPLETED = 4;
    public static final String DIAGNOSTIC_QUEUE_STATUS_SCHEDULED_LABEL = "Scheduled";
    public static final String DIAGNOSTIC_QUEUE_STATUS_PENDING_LABEL = "Pending";
    public static final String DIAGNOSTIC_QUEUE_STATUS_REVIEW_LABEL = "Review";
    public static final String DIAGNOSTIC_QUEUE_STATUS_COMPLETED_LABEL = "Completed";
    public static final int REQUEST_CODE_REGISTRATION = 2000;
    public static final int REQUEST_CODE_OTP = 3000;


    //EMRI response code
    public static final String RESPONSE_EMRI_REGISTRATION = "";
    public static final String RESPONSE_EMRI_EMERGENCY = "EMRIEmergencyInvokeResponse";


    // Device ERROR CODES

    public static final int REQUEST_CODE_REGISTRATION_EMRI = 700;
    public static final int REQUEST_CODE_NORMAL_PIN_TIME_OUT = 800;
    public static final int REQUEST_CODE_WEBVIEW_PIN_TIME_OUT = 900;

    // Authenticator Activity
    public static final String PIN = "PIN";
    public static final String password = "password";
    public static final String email = "username";
    public static final String PARAM_USER_KEY = "PIN";
    public static final String ACTION_STARTED = "EMERGENCY STARTED";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_STOPPED = "EMERGENCY STOPPED";
    public static final String ACTION_SMS_UPDATE = "UPDATE_SMS";

    // Network exception
    public static final int TERMINATE = 2003;
    public static final int NETWORK_ERROR = 2004;
    public static final int NETWORK_OFFLINE = 2005;

    // Emergency map activity
    public static final int REQUEST_CODE_MAP = 3000;
    public static final int REQUEST_CODE_MAP_STRANGER = 4000;

    // EMERGENCY RESPONSES
    public static final int EMERGENCY_INVOKED_SUCCESSFULLY = 911;
    public static final int EMERGENCY_ALREADY_EXISTS = 922;
    public static final int EMERGENCY_LOCATION_UPDATE_SUCCESSFULLY = 933;
    public static final int EMERGENCY_REVOKED = 944;
    public static final int EMERGENCY_VERIFICATION_FAILED = 955;
    // emergency call status
    public static final int EMERGENCY_INVOKE_FROM_DEVICE_SUCCESS = 1;
    public static final int EMERGENCY_INVOKE_FROM_DEVICE_FAILED = 2;

    public static final int EMERGENCY_CALL_NOT_INITIATED = 1;
    public static final int EMERGENCY_CALL_SKIPED = 2;
    public static final int EMERGENCY_CALL_DONE = 3;

    public static final int EMERGENCY_START_LOCATION_UPDATE = 1;

    public static final int EMERGENCY_WEBSERVICE_NOT_INITIATED = 1;
    public static final int EMERGENCY_WEBSERVICE_INITIATED = 2;
    public static final int EMERGENCY_WEBSERVICE_SUCCESS = 3;
    public static final int EMERGENCY_WEBSERVICE_FAILED = 4;

    public static final int EMERGENCY_SMS_SERVER_NOT_INITIATED = 1;
    public static final int EMERGENCY_SMS_SERVER_INITIATED = 2;
    public static final int EMERGENCY_SMS_SERVER_SENT_FROM_DEVICE = 3;
    public static final int EMERGENCY_SMS_SERVER_DELIVERED = 4;
    public static final int EMERGENCY_SMS_SERVER_FAILED = 5;

    public static final int EMERGENCY_SMS_CONTACT_NOT_INITIATED = 1;
    public static final int EMERGENCY_SMS_CONTACT_INITIATED = 2;
    public static final int EMERGENCY_SMS_CONTACT_SENT_FROM_DEVICE = 3;
    public static final int EMERGENCY_SMS_CONTACT_DELIVERED = 4;
    public static final int EMERGENCY_SMS_CONTACT_FAILED = 5;
    public static final int RESPONSE_ACCESS_DENIED = 400;
    public static final int NO_NETWORK = 2;
    public static final int RESPONSE_DUPTICATE_USER = 101;

    // DatbaseHandler
    public static final String user_id = "user_id";
    public static final String patient_id = "patient_id";
    public static final String first_name = "first_name";
    public static final String last_name = "last_name";
    public static final String event_log_id = "event_log_id";
    public static final String status = "status";
    public static final String patient_access_id = "patient_access_id";
    public static final String phone_number = "phone_number";
    public static final String relationship = "relationship";
    public static final String pic_id = "pic_id";
    public static final String pic_name = "pic_name";
    public static final String filename = "filename";
    public static final String under_emergency = "under_emergency";
    public static final String emergency_url_token = "emergency_url_token";
    public static final String emergency_api_key = "emergency_api_key";
    public static final String user_tbl = "user_tbl";
    public static final String current_selected_patient = "current_selected_patient";
    public static final String role = "role";
    public static final String emergency_id = "emergency_id";
    public static final String call_emergency_status = "call_emergency_status";
    public static final String invoke_from_device = "invoke_from_device";
    public static final String webservice_status = "webservice_status";
    public static final String sms_to_contacts_status = "sms_to_contacts_status";
    public static final String sms_to_server_status = "sms_to_server_status";
    public static final String location_update_status = "location_update_status";
    public static final String launch_loc_provider = "launch_loc_provider";
    public static final String launch_loc_long = "launch_loc_long";
    public static final String current_loc_last_update_time = "current_loc_last_update_time";
    public static final String launch_loc_lat = "launch_loc_lat";
    public static final String current_loc_accuracy = "current_loc_accuracy";
    public static final String current_loc_long = "current_loc_long";
    public static final String current_loc_lat = "current_loc_lat";
    public static final String current_loc_provider = "current_loc_provider";
    public static final String launch_loc_accuracy = "launch_loc_accuracy";
    public static final String address = "address";
    public static final String country = "country";
    public static final String email_id = "email_id";
    public static final String last_updated_by_id = "last_updated_by_id";
    public static final String created_by_id = "created_by_id";
    public static final String firstName = "firstName";
    public static final String lastName = "lastName";
    public static final String email2 = "email";
    public static final String emailll = "emailID";
    public static final String phoneNumber = "phoneNumber";
    public static final String pinCode = "pinCode";
    public static final String city = "city";
    public static final String state = "state";
    public static final String relation = "relation";
    public static final String emergencyContactId = "emergencyContactId";
    public static final String lastSyncId = "lastSyncId";
    public static final String insPolicyCoverageKey = "insPolicyCoverage";
    public static final String insPolicyStartDateKey = "insPolicyStartDate";
    public static final String insPolicyEndDateKey = "insPolicyEndDate";
    public static final String insPolicyNameKey = "insPolicyName";
    public static final String insPolicyNoKey = "insPolicyNo";
    public static final String insuranceIdKey = "insuranceId";
    public static final String claimPhoneNumberKey = "claimPhoneNumber";
    public static final String patientIdKey = "patientId";
    public static final String lastSyncIdKey = "lastSyncId";
    public static final String insPolicyCompanyKey = "insPolicyCompany";
    public static final String homePhoneKey = "homePhone";
    public static final String dateOfBirthKey = "dateOfBirth";
    public static final String emailIdKey = "emailId";
    public static final String familyHistoryKey = "familyHistory";
    public static final String habitsKey = "habits";
    public static final String remarksKey = "remarks";
    public static final String genderKey = "gender";
    public static final String financialStatusKey = "financialStatus";
    public static final String phoneNumberIsdCodeKey = "phoneNumberIsdCode";
    public static final String altPhoneNumberIsdCodeKey = "altPhoneNumberIsdCode";
    public static final String countryId = "country";
    public static final String contactId = "contactId";
    public static final String userProfileId = "userProfileId";
    public static final String selectBloodGroupKey = "Select Blood Group";
    public static final String organDonateKey = "Organ_Donation";
    public static final String donateBloodKey = "Donate_Blood";
    public static final String pregnantKey = "Pregnant?";
    public static final String medicalCondition = "Medical_Condition";
    public static final String medicationForChronicIllnessKey = "Medication_for_Chronic_Illness";
    public static final String medicalInstruction = "Medical_Instruction";
    public static final String preferredEmergencyHospitalKey = "Preferred_Emergency_Hospital";
    public static final String allergiesKey = "Allergies";
    public static final CharSequence select = "Select";
    public static final String BloodGroupKey = "Blood_Group";
    public static final CharSequence SPACE = " ";
    public static final CharSequence PLUS = "+";
    public static final int RESPONSE_EMRI_SUCCESS = 1;
    public static final String LOCATION_DATA_LATITUDE = "latitude";
    public static final String LOCATION_DATA_LONGITUDE = "longitude";
    public static final int REQUEST_CODE_GRANT_LOCATION_PERMISSION = 1000;
    public static final long INTERVAL_FOR_LOCATION_UPDATE = 10000;
    public static final long FASTEST_INTERVAL_FOR_LOCATION_UPDATED = 10000;
    public static final String ACTION_LOCATION_BROADCAST = "ACTION_LOCATION_BROADCAST";
    public static final int STATUS_CODE_PROCESSING = 3;
    public static final int STATUS_CODE_COMPLETED = 2;
    public static final int STATUS_CODE_FAILED = 0;
    public static final String ACTION_EMRI_ALERT = "ACTION_EMRI_ALERT";
    public static final int RESPONSE_SUCCESS_EMRI = 1;
    public static final int RESPONSE_FAILED_EMRI = 0;
    public static final String ACTION_NOTIFY_CONTACTS = "ACTION_NOTIFY_CONTACTS";
    public static final int STATUS_WEBSERVICE_SUCCESS = 3;
    public static final int STATUS_SMS_SERVER_SENT = 3;
    public static final String ACTION_WEB_SERVICE_CALL = "ACTION_WEB_SERVICE_CALL";
    public static final int STATUS_CODE_OTHER = 5;
    public static final int STATUS_CODE_SUCCESS = 1;
    public static final String ACTION_INVOKE_EMERGENCY_FOR_ANOTHER_USER = "ACTION_INVOKE_EMERGENCY_FOR_ANOTHER_USER";
    public static final String ACTION_CALL_EMERGENCY_NUMBER = "ACTION_CALL_EMERGENCY_NUMBER";
    public static final String ACTION_STOP_CALL_AMBULANCE_COUNTER = "ACTION_STOP_CALL_AMBULANCE_COUNTER";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_DONE = "DONE";
    public static final String ACTION_FAILED = "FAILED";
    public static final String RECORD_TYPE_EHR = "EHR";
    public static final int REQUEST_CODE_ALLERGIES = 200;
    public static final int REQUEST_CODE_MEDICATION = 201;
    public static final int REQUEST_CODE_BLOOD_GROUP = 203;
    public static final int MALE = 1;
    public static final int RESPONSE_CODE_INVITATION_STATU_UPDATE_FAILED = 102;
    public static final String REJECTED = "";
    public static final String ACTION_INSURANCE_DATA_CHANGED = "ACTION_INSURANCE_DATA_CHANGED";
    public static final String ACTION_EMHR_DATA_CHANGED = "ACTION_EMHR_DATA_CHANGED";
    public static final String ACTION_AMBULANCE_PROVIDER_DATA_CHANGED = "ACTION_AMBULANCE_PROVIDER_DATA_CHANGED";
    public static final int REQUEST_CODE_CAMERA_INTENT = 1;
    public static final int REQUEST_CODE_GALLERY_INTENT = 2;
    public static final int REQUEST_CHECK_SETTINGS = 2324;
    public static final String EMERGENCY_CONTACT = "Emergency Contact";
    public static final String ACTION_SYNC_STARTED = "ACTION_SYNC_STARTED";
    public static final int ACTION_SYNC_IN_PROGREES = 1212;
    public static final String BLOOD_GROUP = "bloodGroup";
    public static final String STATUS = "status";
    public static final String PATIENT_ID = "patientId";
    public static final String REPORTED_ISSUE = "reportedIssue";
    public static final String INITIATED_LAT = "initiatedLat";
    public static final String INITIATED_LON = "initiatedLon";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final int EMERGENCTY_TYPE_POLICE = 4;
    public static final int EMERGENCTY_TYPE_FIRE = 6;
    public static final String FORMATTED_ADDRESS = "Formatted_address";
    public static final String COUNTRY_SHORT_NAME = "country_short_name";
    public static final String COUNTRY = "country";
    public static final String STATE = "state";
    public static final String STATE_SHORT_NAME = "state_short_name";
    public static final String INDIA_SHORT_NAME ="IN" ;
    public static final String SUB_LOCALITY = "sublocality_level_1";
    public static final String LOCALITY = "locality";
    public static final String HOSPITAL_ID ="hospitalId";
    public static final int REQUEST_CODE_LOCATION_SEARCH_RESULT =1208 ;
    public static final int REQUEST_CODE_AMBULANCE_REQUEST = 1212;
    public static final String KEY_SERIALIZED_EIVO = "emergencyInfoVO";
    public static final String GCM_EVENT_TYPE_AMBULANCE_REQUEST_STATUS ="AMBULANCE_REQUEST_STATUS" ;
    public static final int DRIVER_STATUS_REQUEST_SENT =7 ;
    public static final String ANALYTICS_ACTION_AMBULANCE_REQUEST ="Ambulance Request" ;
    public static final String ANALYTICS_ACTION_ADD_MEMBER = "Add Member";
    public static final String CANCEL_VOLLEY_REQUEST = "cancelVolleyRequest";
    public static final String SP_KEY_TIME_STAMP = "timeStamp";
    public static final int PERMISSIONS_REQUEST_CODE_CONTACTS =1 ;
    public static final int PERMISSIONS_REQUEST_CODE_LOCATION =2 ;
    public static final int PERMISSIONS_REQUEST_CODE_PHONE =3 ;
    public static final int PERMISSIONS_REQUEST_CODE_SMS =4;
    public static final int PERMISSIONS_REQUEST_CODE_STORAGE =5 ;
    public static final String PERMISSION_ASKED_STATUS = "permissionsAsked";
    public static final String FRAGMENT_DASHBOARD_TAG_NAME = "FRAGMENT_DASHBOARD";
    public static final int PERMISSIONS_REQUEST_CODE_CAMERA =6;
    public static final int ACTION_SYNC_STOPPED =2121 ;
    public static final String INITIATE_EMERGENCY = "INITIATE_EMERGENCY";
    public static final int PERMISSIONS_REQUEST_CODE_CONTACTS_CARE_TEAM = 16;
    public static final int PERMISSIONS_REQUEST_CODE_CONTACTS_BLOODY_FRIENDS = 17;
    public static final int PERMISSIONS_REQUEST_CODE_STORAGE_REPORT_HAZARDS =18 ;
    public static final int PERMISSIONS_REQUEST_CODE_STORAGE_REPORTS =19;
    public static final String MOBILE_NO ="mobilenumber" ;
    public static final String PARAM_KEY_USERNAME ="username" ;
    public static final String PARAM_KEY_PASSWORD ="password" ;
    public static final int PERMISSIONS_REQUEST_CODE_DASHBOARD_BFRIENDS_SYNC =20 ;
    public static final String CARE_GIVER = "Care Giver";
    public static final String NOTIFYING_DOCTOR = "Notifying Doctor";
    public static final String CALL_AMBULANCE_MISS_CALL_NO = "9392100108";
    public static final String ACTION_PROFILE_SYNC_COMPLETE = "ACTION_PROFILE_SYNC_COMPLETE";
    public static final int HTTP_CODE_SERVER_BUSY = 503;
    public static final int REGISTRATION_STICKY_NOTIFICATION_ID = 34;
    public static final String EMERGENCY_PREPAREDNESS_PROGRESS_STATUS = "EMERGENCY_PREPAREDNESS_PROGRESS_STATUS";
    public static final String IS_TEST_EMERGENCY ="isTestEmergency" ;
    public static final String EMERGENCY_STATUS_INVOKE = "inv";
    public static final String EMERGENCY_STATUS_TEST = "test";
    public static final String FOCUS_BLOOD_GROUP ="FOCUS_BLOOD_GROUP" ;
    public static final String FOCUS_PREFERRED_HOSPITAL = "FOCUS_PREFERRED_HOSPITAL";
    public static final String PREFERRED_HOSPITAL = "Preferred Hospital";
    public static final String PREFERRED_EMERGENCY_PROVIDER ="Preferred Emergency Provider";
    public static final String EHR_VIEW = "ehrView";
    public static final int EMHR_VIEW_SCREEN = 1;
    public static final int INSURANCE_VIEW_SCREEN = 2;
    public static final String ACTION_TEST_EMERGENCY_COUNTER = "ACTION_TEST_EMERGENCY_COUNTER";
    public static final String INSURANCE = "INSURANCE";
    public static final String INSURANCEPAYMENT ="INSURANCEPAYMENT";
    public static final String GCM_EVENT_TYPE_INSURANCE_ISSUED ="INSURANCE_ISSUED" ;
    public static final int HTTP_CODE_FORBIDDEN = 403;
    public static final int HTTP_CODE_BAD_REQUEST = 403;
    public static final int IMAGE_MODULE_TYPE_REPORTS = 2;
    public static final int IMAGE_MODULE_TYPE_PROFILE_PIC= 1;
    public static final int IMAGE_MODULE_TYPE_INSURANCE_POLICY = 3;
    public static final int INSURANCE_STATUS_ISSUED = 2;
    public static final String SP_INSURANCE_PAYMENT_DETAILS ="SP_INSURANCE_PAYMENT_DETAILS";
    public static final String EVENT_GCM_RECEIVED ="EVENT_GCM_RECEIVED" ;
    public static final String EVENT_GCM_CLICKED ="EVENT_GCM_CLICKED" ;
    public static final int ONLINE =1;
    public static final int OFFLINE = 2;
    public static final String LOAD_AMBULANCES_FROM_LOCAL_DB = "LOAD_AMBULANCES_FROM_LOCAL_DB";
    public static final String LAT_NEARBY_AMB = "LAT_NEARBY_AMB";
    public static final String LON_NEARBY_AMB ="LON_NEARBY_AMB" ;
    public static final String LAT_NEARBY_AMB_OFF ="LAT_NEARBY_AMB_OFF" ;
    public static final String LON_NEARBY_AMB_OFF = "LON_NEARBY_AMB_OFF";
    public static final String LAST_KNOWN_TIME_NEARBY_ONLINE_AMB = "LAST_KNOWN_TIME_NEARBY_ONLINE_AMB";
    public static final String LAST_KNOWN_TIME_NEARBY_OFFLINE_AMB ="LAST_KNOWN_TIME_NEARBY_OFFLINE_AMB" ;
    public static final String ACTION_LOGIN_VERIFY_COMPLETE = "ACTION_LOGIN_VERIFY_COMPLETE";
    public static final int SUCCESS =0 ;
    public static final int FAILED = 1;
    public static final String ORG_BRANCH_ID = "ORG_BRANCH_ID";
    public static final String SPECIALITY ="SPECIALITY" ;
    public static final String FACILITY = "FACILITY";
    public static final int PERMISSIONS_REQUEST_CALLPHONE =134 ;
    public static final Object TAG_GOOGLE_DISTANCE_CALL = "TAG_GOOGLE_DISTANCE_CALL";
    public static final String APP_PACKAGE_NAME = "appPackageName";
    public static final String IMEI = "IMEI";

    public static String Contact_Type = "Contact_Type";
    public static String ZIP_Code = "ZIP Code";
    public static String Email_ID = "Email ID";
    public static String PHONE_NUMBER = "Phone Number";
    public static String NAME = "Name";
    public static String UNREGISTERED = "Unregistered";
    public static String UNAME = "uName";
    public static String PASS = "pass";
    public static String TOKEN = "token";
    public static String ID = "id";
    public static String SMSVO = "smsVO";
    public static String EMERGENCY_API_KEY = "emergencyAPIKey";
    public static String DISPLAY_NAME = "displayName";
    public static String DIAGNOSIS_NAME = "Diagnosis Name";
    public static String IS_ACTIVE = "Current";
    public static String DIAGNOSIS_LAST_DATE = "Diagnosis Start Date";
    public static String DIAGNOSIS_END_DATE = "Diagnosis End Date";
    public static String EMAIL = "email";
    public static String INDIA = "India";
    public static String ALT_PHONE_NO = "homePhone";
    public static String PATIENT_VISIT_SCHEMA_ATTR_PREF_FILENAME = "patientVisitSchemaKeysAttrFileName";
    public static String PATIENT_DIAGNOSIS_REPORT_SCHEMA_ATTR_PREF_FILENAME = "patientDiagnosisReportSchemaKeysAttrFileName";
    public static String PATIENT_RECEIPTS_SCHEMA_ATTR_PREF_FILENAME = "patientReceiptsSchemaKeysAttrFileName";

    public static String PATIENT_RECEIPTS_SCHEMA_ATTR_PREF_FILENAME_KEY = "patientReceiptsSchemaKeysAttrFileNameKey";

    public static String PATIENT_VISIT_SCHEMA_ATTR_PREF_FILENAME_KEY = "patientVisitSchemaKeysAttrKey";
    public static String PATIENT_DIAGNOSIS_REPORT_SCHEMA_ATTR_PREF_FILENAME_KEY = "patientDiagnosisReportSchemaKeysAttrKey";

    public static String PATIENT_DIAGN_REPORT_SCHEMA_ATTR_PREF_FILENAME_KEY = "patientDiagReportSchemaKeysAttrKey";
    public static String PATIENT_VISIT_SCHEMA_KEYS_PREF_FILENAME = "patientVisitSchemaKeysPrefFileName";
    public static String PATIENT_DIAG_REPORT_SCHEMA_KEYS_PREF_FILENAME = "patientDiagReportSchemaKeysPrefFileName";
    public static String PATIENT_RECEIPTS_SCHEMA_KEYS_PREF_FILENAME = "patientReceiptsSchemaKeysPrefFileName";

    public static int VERSIONS_MATCHING = 1100;
    public static String VERSION_KEY = "versionKey";
    public static String DOCTOR = "Doctor";
    public static String ATTACHMENT = "ATTACHMENT";
    public static String DIAGNOSTIC_REPORT = "Diagnostic Report";
    public static String RECEIPT = "Receipt";
    public static String VACCINATION = "Vaccination";
    public static String TYPE_VACCINATION = "Vaccination";
    public static int TYPE_DIAGNOSTIC = 2;
    public static int TYPE_MEDICINE = 3;

    public static String CHAR_SET_NAME = "UTF-8";
    public static final int MEDICINE_QUEUE_STATUS_NEW = 0;
    public static final int MEDICINE_QUEUE_STATUS_PROCESSING = 1;
    public static final int MEDICINE_QUEUE_STATUS_READY_FOR_PICKUP = 2;
    public static final int MEDICINE_QUEUE_STATUS_DISPATCHED = 3;
    public static final int MEDICINE_QUEUE_STATUS_NO_SHOW = 4;
    public static final int MEDICINE_QUEUE_STATUS_CLOSED = 5;
    public static final int MEDICINE_QUEUE_STATUS_NOT_AVAILABLE = 6;

    public static final String MEDICINE_QUEUE_STATUS_NEW_LABEL = "New";
    public static final String MEDICINE_QUEUE_STATUS_PROCESSING_LABEL = "Processing";
    public static final String MEDICINE_QUEUE_STATUS_READY_FOR_PICKUP_LABEL = "Ready for Pickup";
    public static final String MEDICINE_QUEUE_STATUS_DISPATCHED_LABEL = "Dispatched";
    public static final String MEDICINE_QUEUE_STATUS_NO_SHOW_LABEL = "No Show";
    public static final String MEDICINE_QUEUE_STATUS_CLOSED_LABEL = "Closed";
    public static final String MEDICINE_QUEUE_STATUS_NOT_AVAILABLE_LABEL = "Not Available";

    public static final int RESPONSE_NULL = 1234;

    public static final String VISIT_SCHEMA_KEYS_PREF_FILENAME = "visitSchemaKeysPrefFileName";

    public static final String DIAGNOSIS_REPORT_SCHEMA_KEYS_PREF_FILENAME = "diagnosisReportSchemaKeysPrefFileName";
    public static final String RECEIPTS_SCHEMA_KEYS_PREF_FILENAME = "receiptsSchemaKeysPrefFileName";

    public static final String EHR_SCHEMA_KEYS_PREF_FILENAME = "ehrSchemaKeysPrefFileName";
    public static final String VISIT_SCHEMA_KEYS_PREF_FILENAME_KEY = "visitSchemaKeysPrefFileNameKey";
    public static final String EHR_SCHEMA_KEYS_PREF_FILENAME_KEY = "ehrSchemaKeysPrefFileNameKey";
    public static final String VISIT_SCHEMA_ATTR_PREF_FILENAME = "visitSchemaKeysAttrFileName";
    public static final String DIAGNOSIS_REPORT_SCHEMA_ATTR_PREF_FILENAME = "diagnosisReportSchemaKeysAttrFileName";
    public static final String RECEIPTS_SCHEMA_ATTR_PREF_FILENAME = "receiptsSchemaKeysAttrFileName";

    public static final String EHR_SCHEMA_ATTR_PREF_FILENAME = "ehrSchemaKeysAttrFileName";
    public static final String VISIT_SCHEMA_ATTR_PREF_FILENAME_KEY = "visitSchemaKeysAttrKey";
    public static final String DIAGNOSIS_REPORT_SCHEMA_ATTR_PREF_FILENAME_KEY = "diagnosisReportSchemaKeysAttrKey";
    public static final String RECEIPTS_SCHEMA_ATTR_PREF_FILENAME_KEY = "receiptsSchemaKeysAttrKey";

    public static final String EHR_SCHEMA_ATTR_PREF_FILENAME_KEY = "ehrSchemaKeysAttrFileNameKey";

    public static final String TEXTAREA = "TEXTAREA";
    public static final String DROPDOWN = "DROPDOWN";

    public static final String CHECKBOX = "CHECKBOX";
    public static final Object RADIOBUTTON = "RADIOBUTTON";
    public static final Object TEXTFIELD = "TEXTFIELD";
    public static final String MARITAL_STATUS = "maritalStatus";
    public static final String FOOD_HABITS = "foodHabits";
    public static final String DONATE_BLOOD = "bloodDonation";
    public static final String ORGAN_DONATE = "organDonation";
    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final String ATT_TYPE_RECORD = "Record";
    public static final String[] ATTACHMENT_TYPE = {"Prescription",
            "DiagnosticReport", "Patient Images", "Receipt",
            "Discharge Summary"};

    // referral types

    public static final int REFERRAL_TYPE_SENT_RECEIVED = 0;
    public static final int REFERRAL_TYPE_SENT = 1;
    public static final int REFERRAL_TYPE_RECEIVED = 2;
    public static final int REFERRAL_TYPE_PATIENT = 3;

    // PatientAccess status
    public static final String PATIENT_ACESS_STATUS_PENDING = "Pending";
    public static final String PATIENT_ACESS_STATUS_ACCEPTED = "Accepted";
    public static final String PATIENT_ACESS_STATUS_REJECTED = "Rejected";
    public static final String PATIENT_ACESS_STATUS_DELETED = "Deleted";
    public static final String PATIENT_ACESS_STATUS_PATIENT_APPROVAL_PENDING = "PatAppPending";

    // HealthRecordSchemaListFragment
    public static final int VISITS = 0;
    public static final String VISIT = "visit";
    public static final String FITNESS_VITALS = "FitnessVitals";

    public static final int VITALS = 1;
    public static final String VITALS_SCHEMA_KEYS_PREF_FILENAME = "vitalsSchemaKeysPrefFileName";
    public static final String PATIENT_VITALS_SCHEMA_KEYS_PREF_FILENAME = "patientVitalsSchemaKeysPrefFileName";

    public static final String VITALS_SCHEMA_KEYS_PREF_FILENAME_KEY = "vitalsSchemaKeysPrefFileNameKey";
    public static final String VITALS_SCHEMA_ATTR_PREF_FILENAME = "vitalsSchemaKeysAttrFileName";
    public static final String VITALS_SCHEMA_ATTR_PREF_FILENAME_KEY = "vitalsSchemaKeysAttrKey";
    public static final String PATIENT_VITALS_SCHEMA_ATTR_PREF_FILENAME = "patientVitalsSchemaKeysAttrFileName";
    public static final String PATIENT_VITALS_SCHEMA_ATTR_PREF_FILENAME_KEY = "patientVitalsSchemaKeysAttrKey";

    // Response Codes

    public static final int RESPONSE_INTERNAL_SERVER_ERROR_1 = -1;

    public static final int RESPONSE_INTERNAL_SERVER_ERROR = 501;
    public static final int RESPONSE_SUCCESS_RESPONSE_CODE = 999;

    // OrgTypeCodes

    public static final String ORG_TYPE_CODE_HOSPITAL = "1";
    public static final String ORG_TYPE_CODE_MEDICAL_STORE = "4";
    public static final String ORG_TYPE_CODE_DIAGNOSTIC_LAB = "5";
    public static final String ORG_TYPE_CODE_CLINIC = "10";
    public static final String ORG_TYPE_CODE_EMPLOYER = "12";
    public static final String ORG_TYPE_CODE_EDUCATIONAL_INSTITUTE = "13";
    public static final String PRESCRIPTION = "Prescription";
    public static final String DISCHARGE_SUMMARY = "Discharge Summary";
    public static final String DIAGNOSTIC_REPORT_TYPE = "DiagnosticReports";
    // prescriptions schema data
    public static final String PRESCRIPTIONS_SCHEMA_KEYS_PREF_FILENAME = "prescriptionSchemaKeysFileName";
    public static final String PRESCRIPTIONS_SCHEMA_ATTR_PREF_FILENAME = "prescriptionSchemaAttrFileName";
    public static final String PRESCRIPTIONS_SCHEMA_ATTR_PREF_FILENAME_KEY = "prescriptionSchemaAttrFileNameKey";
    public static final String PATIENT_PRESCRIPTIONS_SCHEMA_KEYS_PREF_FILENAME = "pateintPrescriptionSchemaKeysFileName";
    public static final String PATIENT_PRESCRIPTIONS_SCHEMA_ATTR_PREF_FILENAME = "patientPrescriptionSchemaAttrFileName";
    public static final String PATIENT_PRESCRIPTIONS_SCHEMA_ATTR_PREF_FILENAME_KEY = "patientPrescriptionSchemaAttrFileNameKey";
    public static final String PRESCRIPTIONS_TYPE = "Prescription";

    // prescriptions schema data
    // DISCHARGE_SUMMARY schema data
    public static final String DISCHARGE_SUMMARY_SCHEMA_KEYS_PREF_FILENAME = "dischargeSummarySchemaKeysFileName";
    public static final String DISCHARGE_SUMMARY_SCHEMA_ATTR_PREF_FILENAME = "dischargeSummarySchemaAttrFileName";
    public static final String DISCHARGE_SUMMARY_SCHEMA_ATTR_PREF_FILENAME_KEY = "dischargeSummarySchemaAttrFileNameKey";
    public static final String PATIENT_DISCHARGE_SUMMARY_SCHEMA_KEYS_PREF_FILENAME = "pateintDischargeSummarySchemaKeysFileName";
    public static final String PATIENT_DISCHARGE_SUMMARY_SCHEMA_ATTR_PREF_FILENAME = "patientDischargeSummarySchemaAttrFileName";
    public static final String PATIENT_DISCHARGE_SUMMARY_SCHEMA_ATTR_PREF_FILENAME_KEY = "patientDischargeSummarySchemaAttrFileNameKey";
    public static final String DISCHARGE_SUMMARY_TYPE = "DischargeSummary";

    // DISCHARGE_SUMMARY schema data

    // vaccination schema data
    public static final String VACCINATION_SCHEMA_KEYS_PREF_FILENAME = "vaccinationSchemaKeysFileName";
    public static final String VACCINATION_SCHEMA_ATTR_PREF_FILENAME = "vaccinationSchemaAttrFileName";
    public static final String VACCINATION_SCHEMA_ATTR_PREF_FILENAME_KEY = "vaccinationSchemaAttrFileNameKey";
    public static final String PATIENT_VACCINATION_SCHEMA_KEYS_PREF_FILENAME = "pateintVaccinationSchemaKeysFileName";
    public static final String PATIENT_VACCINATION_SCHEMA_ATTR_PREF_FILENAME = "pateintVaccinationSchemaKeysFileName";
    public static final String PATIENT_VACCINATION_SCHEMA_ATTR_PREF_FILENAME_KEY = "pateintVaccinationSchemaKeysFileName";
    public static final String RECORD_TYPE_VACCINATION = "Vaccination";

    public static final int ATTACHMENT_TYPE_PRESCRIPTION = 0;

    public static final int ATTACHMENT_TYPE_DISCHARGE_SUMMARY = 1;

    public static final int ATTACHMENT_TYPE_LAB_REPORT = 2;

    public static final int ATTACHMENT_TYPE_RECEIPT = 3;

    public static final String PREFIX_DR = "Dr.";

    public static final String RECORD_TYPE_VISIT = "Visit";

    public static final String FILE_ATTACHMENT_URI_CONTAINER = "fileAttachmentUriContainer";

    public static final int ATTACHMENT_TYPE_PATIENT_IMAGES = 3;

    public static final String RECORD_TYPE_LAB_REPORTS = "DiagnosticReports";

    public static final String RECORD_TYPE_RECEIPT = "Receipt";
    public static final int RESPONSE_FIELDS_EMPTY = -3;

    public static final String COMPANY_NAME_KEY = "companyName";
    public static final String ATTRITBUTE_TYPE_TIME = "Time";
    public static final String ATTRITBUTE_TYPE_ALPHA_NUMERIC = "Alpha_numeric";

    public static final String COMMON_SP_FILE = "commonSpFile";

    public static final int RESPONSE_CODE_PASSWORD_UPDATE = 214;
    public static final int SUCCESS_RESULT = 0;
    public static final int RESPONSE_CODE_SUCCESS = 0;

    public static final int FAILURE_RESULT = 1;
    public static final String RECEIVER = ".RECEIVER";
    public static final String RESULT_DATA_KEY = ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = ".LOCATION_DATA_EXTRA";
    public static final String GCM_TOKEN_SENT_TO_SERVER_STATUS = "GCM_TOKEN_SENT_TO_SERVER_STATUS";


    public static final String CONTACTS_RECEIVER = ".CONTACTS_RECEIVER";
    public static final String RESULT_CONTACTS_KEY = ".RESULT_CONTACTS_KEY";

    public static final int SYNC_CONTACTS_RESPONSE = 404;


    public static final int ACCOUNT_EXPIRED = 460;
    public static final int CREDENTIALS_INCORRECT = 461;
    public static final int ACCOUNT_DISABLED = 462;
    public static final int ACCOUNT_LOCKED = 463;
    public static final int REFUSING_RESPOND = 403;
    public static final String NOTIFY_BLOOD_DONATION_REQUEST = "notifyBloodDonationRequest";
    public static String lastBloodDonatedDate="lastBloodDonationDate";

    public static final long ORG_TYPE_ID_HOSPITAL= 1;
    public static final long ORG_TYPE_ID_COLLEGE= 2;
    public static final long ORG_TYPE_ID_BLOOD_BANK= 3;
    public static final long ORG_TYPE_ID_MEDICAL_STORE = 4;
    public static final long ORG_TYPE_ID_DIAGNOSTIC_LAB= 5;
    public static final long ORG_TYPE_ID_ASSOCIATION= 6;
    public static final long ORG_TYPE_ID_OPTICAL= 7;
    public static final long ORG_TYPE_ID_MED_SPECIALITY= 8;
    public static final long ORG_TYPE_ID_OTHER= 9;
    public static final long ORG_TYPE_ID_CLINIC= 10;
    public static final long ORG_TYPE_ID_HEALTHCARE_NGO= 11;
    public static final long ORG_TYPE_ID_EMPLOYER= 12;
    public static final long ORG_TYPE_ID_EDUCATIONALINSTITUTE = 13;
    public static final long ORG_TYPE_ID_AMBULANCE_PROVIDER = 14;

    public static final String ORG_TYPE_URL_HOSPITAL= "Hospital";
    public static final String ORG_TYPE_URL_COLLEGE= "College";
    public static final String ORG_TYPE_URL_BLOOD_BANK= "Blood Bank";
    public static final String ORG_TYPE_URL_MEDICAL_STORE = "Medical Store";
    public static final String ORG_TYPE_URL_DIAGNOSTIC_LAB= "Diagnostic Lab";
    public static final String ORG_TYPE_URL_ASSOCIATION= "Association";
    public static final String ORG_TYPE_URL_OPTICAL= "Optical";
    public static final String ORG_TYPE_URL_MED_SPECIALITY= "Med Speciality";
    public static final String ORG_TYPE_URL_CLINIC= "Clinic";
    public static final String ORG_TYPE_URL_HEALTHCARE_NGO= "HealthCareNGO";
    public static final String ORG_TYPE_URL_OTHER= "Other";
    public static final String ORG_TYPE_URL_EMPLOYER= "Employer";
    public static final String ORG_TYPE_URL_EDUCATIONALINSTITUTE = "EducationalInstitute";
    public static final String ORG_TYPE_URL_AMBULANCE_PROVIDER = "AmbulanceProvider";

    public static final String SP_KEY_PREFERRED_BLOOD_BANK_BRANCH_ID = "preferredBloodBankOrgBranchId";
    public static final int STATUS_OFFLINE = 1;
    public static final int STATUS_READY = 2;
    public static final int STATUS_IN_USE = 3;
    public static final int STATUS_ON_MY_WAY =4;
    public static final int STATUS_PICKED_UP = 5;
    public static final int STATUS_DROPPED = 6;

    public static final String GYNAECOLOGY = "OBGY";
    public static final String PAEDIATRICS = "PEDI";
    public static final String ORTHOPAEDIC ="ORTH" ;
    public static final String ICU ="ICU" ;
    public static final String CATH_LAB ="CATH" ;
    public static final String CT_SCAN ="CTSC" ;
    public static int REQUEST_CODE_INSURANCE_UPDATE=3;
    public static int APOLLO_MUNICH_INS_AMOUNT=249;
    public static int MAX_ORG_BRANCH_LIMIT=50;
    public static String FROM_LOC_SEARCH_NEARBY_AMBs="FROM_LOC_SEARCH_NEARBY_AMBs";
    public static String POSTAL_CODE="pinCode";
    public static String DISTRICT="DISTRICT";
    public static String CITY="CITY";
    public static String ADDRESS_LINE_1="ADDRESS_LINE_1";
    public static String ADDRESS_LINE_2="ADDRESS_LINE_2";
    public static String imei="imei";

    public interface ACTION {
        public static String EMERGENCY_PREPAREDNESS = "com.patientz.activity.stickyforegroundservice.action.emergencypreparedness";
        public static String STOP_STICKY_FOREGROUND_ACTION = "com.patientz.activity.stickyforegroundservice.action.stopforeground";
        String COMPLETE_REGISTRATION = "com.patientz.activity.stickyforegroundservice.action.completeregistration";
        String STOP = "STOP";
        String START = "START";

    }
    public interface NOTIFICATION_ID {
        public static int STICKY_NOTIFICATION_FOREGROUND_SERVICE = 101;
        int STICKY_NOTIFICATION_INSURANCE_FOREGROUND_SERVICE = 102;
    }
    public interface UpshotEvents {
        String OTP_REQUESTED = "OTP_REQUESTED";
        String OTP_RECEIVED = "OTP_RECEIVED";
        String NEW_USER ="NEW_USER" ;
        String NU_BASIC_PROFILE_COMPLETED ="NU_BASIC_PROFILE_COMPLETED" ;
        String T_AND_C_ACCEPTED = "T_AND_C_ACCEPTED";
        String DOCTOR_ADDED ="DOCTOR_ADDED" ;
        String ORGANISATION_ADDED = "ORGANISATION_ADDED";
        String NEARBY_SCREEN ="NEARBY_SCREEN" ;
        String NEARBY_ONLINE_AMB_CLICKED = "NEARBY_ONLINE_AMB_CLICKED";
        String NEARBY_OFFLINE_AMB_SCREEN = "NEARBY_OFFLINE_AMB_SCREEN";
        String SHARE_CLICKED ="SHARE_CLICKED" ;
        String DASHBOARD_INFO_CLICKED = "DASHBOARD_INFO_CLICKED";
        String REPORT_ADDED = "REPORT_ADDED";
        String SINGLE_REPORT_UPLOADED ="SINGLE_REPORT_UPLOADED" ;
        String MULTIPLE_REPORTS_UPLOADED ="MULTIPLE_REPORTS_UPLOADED" ;
        String ADD_BLOODY_FRIENDS_CLICKED ="ADD_BLOODY_FRIENDS_CLICKED" ;
        String DASHBOARD_LANGUAGE_CHANGE = "DASHBOARD_LANGUAGE_CHANGE";
        String MEMBER_ADDED = "MEMBER_ADDED";
        String HAS_LOGGED_OUT = "HAS_LOGGED_OUT";
        String PAGE = "PAGE";
        String SIGNUP_PAGE_CHANGE_LANGUAGE = "SIGNUP_PAGE_CHANGE_LANGUAGE";
        String T_AND_C_REJECTED ="T_AND_C_REJECTED" ;
        String LN_EMERGENCY = "LN_EMERGENCY";
        String LN_PROFILE= "LN_PROFILE";
        String LN_CARE_TEAM = "LN_CARE_TEAM";
        String LN_NEARBY = "LN_NEARBY";
        String LN_NEARBY_AMBULANCES = "LN_NEARBY_AMBULANCES";
        String LN_FIRST_RESPONDER = "LN_FIRST_RESPONDER";
        String LN_BLOODY_FRIENDS = "LN_BLOODY_FRIENDS";
        String LN_MY_REPORTS = "LN_MY_REPORTS";
        String LN_REPORT_HAZARDS = "LN_REPORT_HAZARDS";
        String LN_VIDEOS = "LN_VIDEOS";
        String LN_SHARE = "LN_SHARE";
        String LN_ABOUT = "LN_ABOUT";


        String PROFILE_VIEW_CLICKED = "PROFILE_VIEW_CLICKED";
        String EMHR_VIEW_CLICKED = "EMHR_VIEW_CLICKED";
        String INSURANCE_VIEW_CLICKED = "INSURANCE_VIEW_CLICKED";

        String PROFILE_EDIT_CLICKED ="PROFILE_EDIT_CLICKED";
        String EMHR_EDIT_CLICKED ="EMHR_EDIT_CLICKED" ;
        String INSURANCE_EDIT_CLICKED ="INSURANCE_EDIT_CLICKED" ;
        String PROFILE_EDIT_SUBMIT_CLICKED ="PROFILE_EDIT_SUBMIT_CLICKED";
        String EMHR_EDIT_SUBMIT_CLICKED ="EMHR_EDIT_SUBMIT_CLICKED" ;
        String INSURANCE_EDIT_SUBMIT_CLICKED ="INSURANCE_EDIT_SUBMIT_CLICKED" ;

        String CT_MY_DOCTORS_CLICKED ="CT_MY_DOCTORS_CLICKED" ;
        String CT_CG_CLICKED = "CT_CG_CLICKED";
        String CT_EC_CLICKED ="CT_EC_CLICKED" ;
        String CT_ORGANISATION_CLICKED = "CT_ORGANISATION_CLICKED";
        String CT_CG_SEARCH_CLICKED = "CT_CG_SEARCH_CLICKED";
        String CT_CG_ADD_NEW_CLICKED ="CT_CG_ADD_NEW_CLICKED" ;
        String CT_ADD_CG_FROM_PHONE_BOOK_CLICKED ="CT_ADD_CG_FROM_PHONE_BOOK_CLICKED" ;
        String CT_ADD_CG_NEW_CONTACT_CLICKED ="CT_ADD_CG_NEW_CONTACT_CLICKED" ;
        String CT_CG_UNREGISTERED_CONTACT_SUBMIT_CLICKED = "CT_CG_UNREGISTERED_CONTACT_SUBMIT_CLICKED";
        String CT_CG_REGISTERED_CONTACT_ADD_CLICKED ="CT_CG_REGISTERED_CONTACT_ADD_CLICKED" ;
        String CT_EC_REGISTERED_CONTACT_ADD_CLICKED ="CT_EC_REGISTERED_CONTACT_ADD_CLICKED" ;
        String CT_EC_ADD_NEW_CLICKED = "CT_EC_ADD_NEW_CLICKED";
        String CT_ADD_EC_FROM_PHONE_BOOK_CLICKED ="CT_ADD_EC_FROM_PHONE_BOOK_CLICKED" ;
        String CT_ADD_EC_NEW_CONTACT_CLICKED = "CT_ADD_EC_NEW_CONTACT_CLICKED";
        String CT_EC_SEARCH_CLICKED ="CT_EC_SEARCH_CLICKED" ;
        String CT_ORGANISATION_SEARCH_CLICKED = "CT_ORGANISATION_SEARCH_CLICKED";
        String CT_ORGANISATION_ADD_CLICKED ="CT_ORGANISATION_ADD_CLICKED" ;
        String CT_MY_DOCTORS_ADD_CLICKED ="CT_MY_DOCTORS_ADD_CLICKED" ;
        String CT_INVITATION_ICON_CLICKED ="CT_INVITATION_ICON_CLICKED" ;
        String CT_INVITATION_ACCEPTED ="CT_INVITATION_ACCEPTED" ;
        String CT_INVITATION_REJECTED ="CT_INVITATION_REJECTED" ;
        String NEARBY_HOSPITALS_CLICKED = "NEARBY_HOSPITALS_CLICKED";
        String NEARBY_AED_CLICKED ="NEARBY_AED_CLICKED" ;
        String NEARBY_DOCTORS_CLICKED = "NEARBY_DOCTORS_CLICKED";
        String NEARBY_BLOOD_BANKS_CLICKED = "NEARBY_BLOOD_BANKS_CLICKED";
        String NEARBY_PHARMACY_CLICKED ="NEARBY_PHARMACY_CLICKED" ;
        String NEARBY_DIAGNOSTICS_CLICKED = "NEARBY_DIAGNOSTICS_CLICKED";

        String BLOODY_FRIENDS_LANDING_PAGE_ADD_CLICKED ="BLOODY_FRIENDS_LANDING_PAGE_ADD_CLICKED" ;
        String BLOODY_FRIENDS_LANDING_PAGE_UPDATE_BLOOD_GROUP_CLICKED ="BLOODY_FRIENDS_LANDING_PAGE_UPDATE_BLOOD_GROUP_CLICKED" ;
        String REPORTS_LIST_ADD_CLICKED ="REPORTS_LIST_ADD_CLICKED";
        String ADD_REPORTS_PAGE_UPLOAD_CLICKED ="ADD_REPORTS_PAGE_UPLOAD_CLICKED";
        String REPORTS_UPLOAD_SUBMIT_CLICKED ="REPORTS_UPLOAD_SUBMIT_CLICKED" ;
        String ROAD_HAZARDS_SUBMIT ="ROAD_HAZARDS_SUBMIT" ;
        String ABOUT_SCREEN_FOLLOW_ON_FB ="ABOUT_SCREEN_FOLLOW_ON_FB" ;
        String ABOUT_SCREEN_FOLLOW_ON_TWITTER ="ABOUT_SCREEN_FOLLOW_ON_TWITTER" ;
        String ABOUT_SCREEN_INVITE_FRIENDS ="ABOUT_SCREEN_INVITE_FRIENDS" ;
        String DASHBOARD_ADD_MEMBER_CLICKED = "DASHBOARD_ADD_MEMBER_CLICKED";
        String DASHBOARD_SWITCH_MEMBER_CLICKED = "DASHBOARD_SWITCH_MEMBER_CLICKED";
        String DASHBOARD_SHARE_CLICKED = "DASHBOARD_SHARE_CLICKED";
        String DASHBOARD_LOGOUT_CLICKED ="DASHBOARD_LOGOUT_CLICKED" ;
        String DASHBOARD_CHANGE_PWD_CLICKED = "DASHBOARD_CHANGE_PWD_CLICKED";
        String DASHBOARD_SETTINGS_CLICKED = "DASHBOARD_SETTINGS_CLICKED";
        String DASHBOARD_EC_CLICKED ="DASHBOARD_EC_CLICKED" ;
        String DASHBOARD_BLOOD_GROUP_CLICKED ="DASHBOARD_EC_CLICKED" ;
        String DASHBOARD_HEALTH_RECORD_CLICKED ="DASHBOARD_HEALTH_RECORD_CLICKED" ;
        String DASHBOARD_EMERGENCY_PROVIDER_CLICKED ="DASHBOARD_EMERGENCY_PROVIDER_CLICKED" ;
        String DASHBOARD_INSURANCE_CLICKED ="DASHBOARD_INSURANCE_CLICKED" ;
        String DASHBOARD_TEST_EMERGENCY_CLICKED ="DASHBOARD_TEST_EMERGENCY_CLICKED" ;

        String DASHBOARD_EMERGENCY_BUTTON_CLICKED = "DASHBOARD_EMERGENCY_BUTTON_CLICKED";
        String DASHBOARD_CURRENT_EMERGENCIES_BUTTON_CLICKED = "DASHBOARD_CURRENT_EMERGENCIES_BUTTON_CLICKED";
        String DASHBOARD_CURRENT_ADDRESS_BUTTON_CLICKED = "DASHBOARD_CURRENT_ADDRESS_BUTTON_CLICKED";
        String EMERGENCY_PATIENT_SELECTED ="EMERGENCY_PATIENT_SELECTED" ;
        String EMERGENCY_TYPE_SELECTED = "EMERGENCY_TYPE_SELECTED";
        String EMERGENCY_TYPE = "EMERGENCY_TYPE";
        String EMERGENCY_AMBULANCE_PROVIDER_SELECTED = "EMERGENCY_AMBULANCE_PROVIDER_SELECTED";
        String EMERGENCY_SKIP_CALL_CLICKED = "EMERGENCY_SKIP_CALL_CLICKED";
        String EMERGENCY_CALL_HANDLED = "EMERGENCY_CALL_HANDLED";
        String EMERGENCY_TRACK_EMERGENCY_CLICKED = "EMERGENCY_TRACK_EMERGENCY_CLICKED";
        String EMERGENCY_REDIAL_CLICKED = "EMERGENCY_REDIAL_CLICKED";
        String EMERGENCY_108_CLICKED = "EMERGENCY_108_CLICKED";
        String EMERGENCY_SHARE_CLICKED ="EMERGENCY_SHARE_CLICKED" ;
        String EMERGENCY_NEARBY_HOSPITALS_CLICKED ="EMERGENCY_NEARBY_HOSPITALS_CLICKED" ;
        String EMERGENCY_REVOKE_CLICKED ="EMERGENCY_REVOKE_CLICKED" ;
        String CUSTOM_EVENT_NOTIFICATION ="CUSTOM_EVENT_NOTIFICATION" ;
        String INSURANCE_ADD_EXISTING_POLICY_CLICKED ="INSURANCE_ADD_EXISTING_POLICY_CLICKED" ;
        String INSURANCE_BUY_NOW_CLICKED ="INSURANCE_BUY_NOW_CLICKED" ;
        String INSURANCE_EXISTING_SAVE_CLICKED = "INSURANCE_EXISTING_SAVE_CLICKED";
        String INSURANCE_SUPPORT_CLICKED ="INSURANCE_SUPPORT_CLICKED" ;
        String INSURANCE_COVERAGE_INFO_BUY_CLICKED ="INSURANCE_COVERAGE_INFO_BUY_CLICKED" ;
        String INSURANCE_COVERAGE_INFO_TnC_CLICKED="INSURANCE_COVERAGE_INFO_TnC_CLICKED";
        String INSURANCE_ADD_INSURED_DETAILS_SAVE_CLICKED ="INSURANCE_ADD_INSURED_DETAILS_SAVE_CLICKED" ;
        String INSURANCE_SUMMARY_DETAILS_SHARE_CLICKED = "INSURANCE_SUMMARY_DETAILS_SHARE_CLICKED";
        String INSURANCE_SUMMARY_DETAILS_CONTINUE_CLICKED ="INSURANCE_SUMMARY_DETAILS_CONTINUE_CLICKED" ;
        String INSURANCE_VERIFY_MOBILE_CLICKED = "INSURANCE_VERIFY_MOBILE_CLICKED";
        String INSURANCE_VERIFY_EMAIL_CLICKED ="INSURANCE_VERIFY_EMAIL_CLICKED" ;
        String APP_LAUNCH_ADDRESS ="APP_LAUNCH_ADDRESS" ;
    }

    public interface UpshotEventsId {
        int OTP_REQUESTED = 1000;
        int OTP_RECEIVED =1001 ;
        int NEW_USER = 1002;
        int NU_BASIC_PROFILE_COMPLETED = 1003;
        int T_AND_C_ACCEPTED = 1004;
        int DOCTOR_ADDED =1005 ;
        int ORGANISATION_ADDED=1006;
        int NEARBY_SCREEN =1007 ;
        int NEARBY_ONLINE_AMB_SCREEN =1008 ;
        int NEARBY_OFFLINE_AMB_SCREEN = 1009;
        int SHARE_CLICKED = 1010;
        int DASHBOARD_INFO_CLICKED = 1011;
        int REPORT_ADDED = 1012;
        int SINGLE_REPORT_UPLOADED =1012 ;
        int MULTIPLE_REPORTS_UPLOADED =1013;
        int ADD_BLOODY_FRIENDS_CLICKED = 1014;
        int LANGUAGE_CHANGED = 1015;
        int MEMBER_ADDED = 1016;
        int HAS_LOGGED_OUT = 1017;
        int SIGNUP_PAGE_CHANGE_LANGUAGE = 1018;
        int T_AND_C_REJECTED =1019 ;
        int LN_EMERGENCY = 1010;
        int LN_PROFILE= 1011;
        int LN_CARE_TEAM = 1012;
        int LN_NEARBY = 1013;
        int LN_NEARBY_AMBULANCES = 1014;
        int LN_FIRST_RESPONDER = 1015;
        int LN_BLOODY_FRIENDS = 1016;
        int LN_MY_REPORTS = 1017;
        int LN_REPORT_HAZARDS = 1018;
        int LN_VIDEOS = 1019;
        int LN_SHARE = 1020;
        int LN_ABOUT = 1021;
        int PROFILE_VIEW_CLICKED = 1022;
        int EMHR_VIEW_CLICKED =1023;
        int INSURANCE_VIEW_CLICKED = 1024;
        int PROFILE_EDIT_CLICKED =1025;
        int EMHR_EDIT_CLICKED =1026 ;
        int INSURANCE_EDIT_CLICKED =1027;
        int PROFILE_EDIT_SUBMIT_CLICKED =1028;
        int EMHR_EDIT_SUBMIT_CLICKED =1029 ;
        int INSURANCE_EDIT_SUBMIT_CLICKED =1030;

        int CT_MY_DOCTORS_CLICKED =1031;
        int CT_CG_CLICKED = 1032;
        int CT_EC_CLICKED = 1033;
        int CT_ORGANISATION_CLICKED =1034;
        int CT_CG_SEARCH_CLICKED = 1035;
        int CT_CG_ADD_NEW_CLICKED = 1036;
        int CT_ADD_CG_FROM_PHONE_BOOK_CLICKED = 1037;
        int CT_ADD_CG_NEW_CONTACT_CLICKED =1038 ;
        int CT_CG_UNREGISTERED_CONTACT_SUBMIT_CLICKED = 1039;
        int CT_CG_REGISTERED_CONTACT_ADD_CLICKED =1040;
        int CT_EC_REGISTERED_CONTACT_ADD_CLICKED = 1041;

        int CT_EC_ADD_NEW_CLICKED = 1042;
        int CT_ADD_EC_FROM_PHONE_BOOK_CLICKED =1043 ;
        int CT_ADD_EC_NEW_CONTACT_CLICKED =1044 ;
        int CT_EC_SEARCH_CLICKED =1045 ;
        int CT_ORGANISATION_SEARCH_CLICKED =1046 ;
        int CT_ORGANISATION_ADD_CLICKED = 1047;
        int CT_MY_DOCTORS_ADD_CLICKED =1048 ;
        int CT_INVITATION_ICON_CLICKED =1049 ;
        int CT_INVITATION_ACCEPTED =1050 ;
        int CT_INVITATION_REJECTED = 1051;
        int NEARBY_HOSPITALS_CLICKED = 1052;
        int NEARBY_AED_CLICKED =1053;
        int NEARBY_DOCTORS_CLICKED = 1054;
        int NEARBY_BLOOD_BANKS_CLICKED = 1055;
        int NEARBY_PHARMACY_CLICKED =1056 ;
        int NEARBY_DIAGNOSTICS_CLICKED = 1057;
        int BLOODY_FRIENDS_LANDING_PAGE_ADD_CLICKED = 1058;
        int BLOODY_FRIENDS_LANDING_PAGE_UPDATE_BLOOD_GROUP_CLICKED = 1059;
        int REPORTS_LIST_ADD_CLICKED =1060 ;
        int ADD_REPORTS_PAGE_UPLOAD_CLICKED =1061 ;

        int REPORTS_UPLOAD_SUBMIT_CLICKED =1062 ;
        int ROAD_HAZARDS_SUBMIT = 1063;
        int ABOUT_SCREEN_FOLLOW_ON_FB =1064;
        int ABOUT_SCREEN_FOLLOW_ON_TWITTER =1065;
        int ABOUT_SCREEN_INVITE_FRIENDS = 1066;
        int DASHBOARD_ADD_MEMBER_CLICKED = 1067;
        int DASHBOARD_SWITCH_MEMBER_CLICKED = 1068;
        int DASHBOARD_SHARE_CLICKED =1069 ;
        int DASHBOARD_LOGOUT_CLICKED =1070 ;
        int DASHBOARD_CHANGE_PWD_CLICKED = 1071;
        int DASHBOARD_SETTINGS_CLICKED =1072 ;
        int DASHBOARD_EC_CLICKED =1073 ;
        int DASHBOARD_BLOOD_GROUP_CLICKED =1074 ;
        int DASHBOARD_HEALTH_RECORD_CLICKED =1075 ;
        int DASHBOARD_EMERGENCY_PROVIDER_CLICKED =1076 ;
        int DASHBOARD_INSURANCE_CLICKED =1077 ;
        int DASHBOARD_TEST_EMERGENCY_CLICKED =1078 ;
        int DASHBOARD_EMERGENCY_BUTTON_CLICKED =1079 ;
        int DASHBOARD_CURRENT_EMERGENCIES_BUTTON_CLICKED =1080 ;
        int DASHBOARD_CURRENT_ADDRESS_BUTTON_CLICKED =1081 ;
        int EMERGENCY_PATIENT_SELECTED =1082 ;
        int EMERGENCY_TYPE_SELECTED =1083 ;
        int EMERGENCY_AMBULANCE_PROVIDER_SELECTED =1084 ;
        int EMERGENCY_SKIP_CALL_CLICKED =1085 ;
        int EMERGENCY_CALL_HANDLED = 1086;
        int EMERGENCY_TRACK_EMERGENCY_CLICKED =1087 ;
        int EMERGENCY_REDIAL_CLICKED = 1088;
        int EMERGENCY_108_CLICKED = 1089;
        int EMERGENCY_SHARE_CLICKED = 1090;
        int EMERGENCY_NEARBY_HOSPITALS_CLICKED=1091;
        int EMERGENCY_REVOKE_CLICKED = 1092;
        int CUSTOM_EVENT_NOTIFICATION =1093 ;
        int INSURANCE_ADD_EXISTING_POLICY_CLICKED = 1094;
        int INSURANCE_BUY_NOW_CLICKED =1095 ;
        int INSURANCE_EXISTING_SAVE_CLICKED =1096 ;
        int INSURANCE_SUPPORT_CLICKED = 1097;
        int INSURANCE_COVERAGE_INFO_BUY_CLICKED =1098 ;
        int INSURANCE_COVERAGE_INFO_TnC_CLICKED=1099;
        int INSURANCE_ADD_INSURED_DETAILS_SAVE_CLICKED =1100 ;
        int INSURANCE_SUMMARY_DETAILS_SHARE_CLICKED =1101 ;
        int INSURANCE_SUMMARY_DETAILS_CONTINUE_CLICKED =1102 ;
        int INSURANCE_VERIFY_MOBILE_CLICKED =1103 ;
        int INSURANCE_VERIFY_EMAIL_CLICKED = 1104;
        int APP_LAUNCH_ADDRESS = 1105;
    }
    public static final String GCM_COMMUNICATION_ID ="gcmCommunicationId" ;
    public static final String EVENT ="event" ;
}
