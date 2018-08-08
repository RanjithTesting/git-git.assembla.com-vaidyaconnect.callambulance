package com.patientz.webservices;

/**
 * Created by windows.7 on 7/7/2016.
 */
public class WebServiceUrls {
    //Update app ids for upshot
   /*PROD*/
    public static String UPSHOT_APPLICATION_ID = "266ce024-dfd1-4714-ab57-bdf76dab5974";
    public static String UPSHOT_APPLICATION_OWNER_ID = "7712a501-7476-4c30-8e61-6b91e4945b84";

    /*public static final String serverUrl = "https://www.callambulance.in/";
    public static final String serverEmergencyUrl = "https://www.callambulance.in/et/";
    public static final String test_emergency_url = "https://www.callambulance.in/et/testEmergency/";//AsyncAlertingEmri
*/
   /*PROD*/

    //QA,Stage & Demo

    public static final String serverUrl = "https://dev.callambulance.in/";
   public static final String serverEmergencyUrl = "https://dev.callambulance.in/et/";
    public static final String test_emergency_url = "https://dev.callambulance.in/et/testEmergency/";//AsyncAlertingEmri


//    public static final String serverUrl = "https://alpha.callambulance.in/";
//    public static final String serverEmergencyUrl = "https://alpha.callambulance.in/et/";
//    public static final String test_emergency_url = "https://alpha.callambulance.in/et/testEmergency/";//AsyncAlertingEmri

//    public static String UPSHOT_APPLICATION_ID = "06435a68-c1bf-425c-8c65-910c551a4fb4";
//    public static String UPSHOT_APPLICATION_OWNER_ID = "7712a501-7476-4c30-8e61-6b91e4945b84";
//    //////
////////    //qa
//    public static final String serverUrl = "https://qaca.doctrz.in/";// QACA
//    public static final String serverEmergencyUrl = "https://qaca.doctrz.in/et/";
//    public static final String test_emergency_url = "https://qaca.doctrz.in/et/testEmergency";//AsyncAlertingEmri

//    public static final String serverUrl = "https://dev.callambulance.in/";// QACA
//    public static final String serverEmergencyUrl = "https://dev.callambulance.in/et/";
//    public static final String test_emergency_url = "https://dev.callambulance.in/et/testEmergency";//AsyncAlertingEmri

//    public static final String serverUrl = "https://qaca.doctrz.in/";// QACA
//    public static final String serverEmergencyUrl = "https://qaca.doctrz.in/et/";
//    public static final String test_emergency_url = "https://qaca.doctrz.in/et/testEmergency";//AsyncAlertingEmri

//    stage
//    public static final String serverUrl = "https://stage.doctrz.in/";
//    public static final String serverEmergencyUrl = "https://stage.doctrz.in/et/";
//    public static final String test_emergency_url = "https://stage.doctrz.in/et/testEmergency";//AsyncAlertingEmri

//Demo
//    public static final String serverUrl = "https://demo.doctrz.in/"; // DEMO
//    public static final String serverEmergencyUrl = "https://demo.doctrz.in/et/"; // DEMO

    //Other Webservices & Urls


    public static final String GET_PREFERRED_ORG_BRANCH = "orgBranch/getPreferredOrgBranch?patientId=";
    ;
    public static final String stranger_emergency_url = "https://www.callambulance.in/home/stranger";//AsyncAlertingEmri
    public static final String emthelp = "https://www.callambulance.in/home/emt-help/";//FirstResponderHelpWebViewActivity
    public static final String videos = "https://www.callambulance.in/home/videos/";//ActivityWebViewVedioes
    public static final String facebook = "https://www.facebook.com/callambulance/";//AboutActivity
    public static final String twitter = "https://twitter.com/callambulance4u";//AboutActivity
    public static final String web_url_link = "https://www.callambulance.in";//AboutActivity
    public static final String NutrifiImages = "/Nutrifi/Images/";//ImageLruCache
    public static final String DoctrzImages = "/Doctrz/Images/";//ImageLruCache
    public static final String CallAmbulanceQaImages = "/CallAmbulanceQa/Images/";//ImageLruCache
    public static final String getCallAmbulanceQaImages = "/CallAmbulanceQa/Images/";//ImageLruCache

    //Gcm
    public static final String saveGCMInfo = "webservices/saveGCMInfo";//GcmRegistrationIntentService

    //Web Services

    public static final String LOGIN_VERIFY = "callAmbulanceWebservices/verifyLogin";//(Login Rest API) LoginActivity
    public static final String LOGIN_API_URL = "api/login";//(Login Rest API) LoginActivity
    //  public static final String j_spring_security_check = "j_spring_security_check";
    public static final String savePatientOwnerFromMobile = "patientWebservices/savePatientOwnerFromMobile";//ActivityEditProfile,ActivityAddMember,AddNewContactActivityFragment
    public static final String saveInsuranceFromMobile = "patientWebservices/saveInsuranceFromMobile";//ActivityEditInsurance
    public static final String updateEHR = "patientWebservices/updateEHR";//ActivityEHRUpdate
    public static final String callEmergency = "patientWebservices/callEmergency";//ActivityEmergencyTrackAndRevoke,ServiceEmergencyStepsBroadCaster
    public static final String WEBSERVICE_INVOKE_EMERGENCY_FOR_STRANGER = "EMTWebservices/invokeEmergencyForStranger";

    public static final String getNearestHospitals = "EMTWebservices/getNearestHospitals";//ActivityNearbyHospitals
    public static final String saveUnregisteredEC = "patientWebservices/saveUnregisteredEC?patientId=";//AddContactFragment
    public static final String savePatientDoctorCGInvite = "patientWebservices/savePatientDoctorCGInvite";//AddNewCareGiverFragment
    public static final String forgotPassword = "public/forgotPassword";//LoginActivity
    public static final String processForgotPassword = "webservices/processForgotPassword";//LoginActivity
    public static final String insertPatientProfilePicture = "patientWebservices/insertPatientProfilePicture";//MainActivity,ProfileActivity
    public static final String webservice_safety_road_hazard = "callAmbulanceWebservices/saveSafetyHazardReport";//MainActivity,ProfileActivity
    public static final String resumeSaveSimplePatientRegistration = "webservices/resumeSaveSimplePatientRegistration";//ProfileRegistrationActivity
    public static final String simplePatientRegistration = "webservices/simplePatientRegistration";//RegistrationActivity
    public static final String searchCareGiverOrEC = "patientWebservices/searchCareGiverOrEC";//SearchCareGiverFragment,SearchEmergencyContactFragment
    public static final String searchOrgForPatient = "patientWebservices/searchOrgForPatient";//SearchHospitalsAndOrganisationsActivityFragment
    public static final String patientCompleteRecord = "patientWebservices/patientCompleteRecord?patientId=";//SyncDataActivity
    public static final String saveUpdatePassword = "webservices/saveUpdatePassword";//UpdatePassWordActivity
    public static final String getUploadedImage = "webservices/getUploadedImage/";//AdapterCurrentEmergencies,InvitationAdapter,AdapterSelectPatientInEmergency,
    public static final String saveCareGiverOrEC = "patientWebservices/saveCareGiverOrEC";//EmergencyContactsAdapter
    public static final String sendOrgRequestForPatient = "patientWebservices/sendOrgRequestForPatient";//HospitalsAndOrganisationSearchAdapter
    public static final String acceptRejectPatientAccessRequest = "patientWebservices/acceptRejectPatientAccessRequest";//InvitationAdapter
    public static final String getOrgBranches = "webservices/getOrgBranchesList?timeStamp=";//AdapterCustomAmbulanceProvidersList
    public static final String getOrgBranchAvailabilityCapabilityList = "rest/orgBranchCapabilityAvailability/list";//ImageLruCache
    public static final String master_facilities = "rest/facility/list";
    public static final String master_specialities = "rest/speciality/list";
    public static final String getHospitalsList = "webservices/getHospitalsList?timeStamp=";//CallAmbulanceSyncService
    public static final String addContact = "bloodyFriend/addContact";//BloodyFriendsSyncService
    public static final String getSyncedContacts = "bloodyFriend/getSyncedContacts";//BloodFriendsActivity
    public static final String WEBSERVICE_EMERGENCY_AMBULANCE_PROVIDERS_LISTING = "EMTWebservices/getHospitalsListWithAmbulanceProvider";
    public static final String uploadAll = "patientFileCollection/uploadAll";//
    public static final String createfile = "patientFileCollection/create";//
    public static final String searchfile = "patientFileCollection/search?patientId=";//
    public static final String getPatientAttachment = "webservices/getPatientAttachment?id=";//
    public static final String getImageThumbnail = "patientWebservices/getImageThumbnail?id=";//
    public static final String marketingCampaign = "marketingCampaign/saveOrUpdate";//ImageLruCache
    public static final String offers = "https://www.callambulance.in/home/exhibition-2017-offers/";//Offers
    public static final String getNearbyAmbulances = "ambulanceDetails/getNearByAmbulances";
    public static final String getNearbyOfflineAmbulances = "ambulanceDetails/getNearByOfflineAmbulances";
    public static final String requestAmbulance = "ambulanceDetails/requestAmbulance";
    public static final String getCurrentAmbulanceRequest = "ambulanceDetails/getCurrentAmbulanceRequest";
    public static final String cancelAmbulanceRequest = "ambulanceDetails/cancelAmbulanceRequest";
    public static final String EMTWEBSERVICES_UPDATE_EMERGENCY_STATUS = "EMTWebservices/updateEmergencyStatus";//NearbyEmergenciesActivity,NBECAdapter,NearByHospitalsActivity,PatientPickupActivity,

    public static String getAllDoctorsInOrganisation = "patientAccess/doctorsListInPatientOrgs";
    public static String addDoctorAsNotifyingDoctor = "patientAccess/addNotifyDoctor";
    public static String getNearbyDoctorsList = "OrgBranch/getNearbyDoctorsList";
    public static String addEmployer = "patientAccess/addPatientEmployeeDetails";
    public static String getNearbyAEDsList = "AED/nearBy?";
    public static String deleteContacts = "patientAccess/deleteContact?";
    public static String deleteReport = "patientFileCollection/delete?";
    public static final String searchURL = "rest/webservices/searchQuestions";
    public static final String ca_forum_url = "rest/webservices/questionsList?forumId=";

    public static final String generateChecksum = "paytmPayment/generateMobileRequest";//


    public static String accident_coverage_webview_url = "home/insurance-webview";
    public static String verifyEmail = "v1/insuranceUpload/verifyEmail?";
    public static String verifyMobile = "v1/insuranceUpload/verifyMobileNumber?";
    public static String confirmMobile = "v1/insuranceUpload/confirmMobileNumber?";
    public static String insuranceUpload = "v1/insuranceUpload/save";
    public static String insuranceUploadList = "v1/insuranceUpload/get";
    public static String apollo_munich_network_hospitals = "http://www.apollomunichinsurance.com/our-hospital-network.aspx";
    public static String apollo_munich_info = "https://www.callambulance.in/home/insurance-policy-details/";

    public static String verifyAadhar = "v1/insuranceUpload/addAadharNo";
    public static String confirmEmail = "v1/insuranceUpload/confirmEmail";

    public static String verifyPromoCode = "v1/couponCode/get?couponCode=";
    public static String logGCMCommunication = "GCMCommunicationLog/save/";
    public static String getOrgBranchBloodAvailability = "bloodAvailability/listBloodAvailabilitiesFromBranch?";
    public static String SAVE_USER_LOG = "webservices/saveUserLog";
    public static String LOGOUT_API_URL = "v1/user/logout";

}
