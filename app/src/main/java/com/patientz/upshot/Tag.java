package com.patientz.upshot;

import com.patientz.activity.AboutActivity;
import com.patientz.activity.ActivityAddMember;
import com.patientz.activity.ActivityCurrentEmergencies;
import com.patientz.activity.ActivityEHRUpdate;
import com.patientz.activity.ActivityEditInsurance;
import com.patientz.activity.ActivityEditProfile;
import com.patientz.activity.ActivityEmergencyStepsListener;
import com.patientz.activity.ActivityEmergencyTrackAndRevoke;
import com.patientz.activity.ActivityEmergencyType;
import com.patientz.activity.ActivityInvitation;
import com.patientz.activity.ActivityMaps;
import com.patientz.activity.ActivityNearbyHospitals;
import com.patientz.activity.ActivitySelectPatientInEmergency;
import com.patientz.activity.ActivitySplashScreen;
import com.patientz.activity.ActivityTermsAndConditions;
import com.patientz.activity.ActivityWebViewVedioes;
import com.patientz.activity.AddEmployerActivity;
import com.patientz.activity.AddInsuredDetailsActivity;
import com.patientz.activity.AddNewCareGiver;
import com.patientz.activity.AddNewContactActivity;
import com.patientz.activity.BloodyFriendsActivity;
import com.patientz.activity.BloodyFriendsAdditionActivity;
import com.patientz.activity.ChangePassWordActivity;
import com.patientz.activity.ContactsActivity;
import com.patientz.activity.CurrentAmbulanceRequestActivity;
import com.patientz.activity.CustomMapActivity;
import com.patientz.activity.EmergencyForumActivity;
import com.patientz.activity.FirstResponderHelpWebViewActivity;
import com.patientz.activity.ForgotPasswordActivity;
import com.patientz.activity.InfoSliderActivity;
import com.patientz.activity.InsuranceCapturedActivity;
import com.patientz.activity.InsuranceDetailsActivity;
import com.patientz.activity.InsurancePolicyCoverageInfoActivity;
import com.patientz.activity.LocationSearchActivity;
import com.patientz.activity.LoginActivity;
import com.patientz.activity.MainActivity;
import com.patientz.activity.MarketingCampaignActivity;
import com.patientz.activity.MyUploadReportsActivity;
import com.patientz.activity.NearbyOrgsActivity;
import com.patientz.activity.NotifyDoctorActivity;
import com.patientz.activity.OTPRegisterActivity;
import com.patientz.activity.OffersActivity;
import com.patientz.activity.OrgBranchesActivity;
import com.patientz.activity.PDFView;
import com.patientz.activity.ProfileActivity;
import com.patientz.activity.ProfileRegistrationActivity;
import com.patientz.activity.RegistrationActivity;
import com.patientz.activity.ReportRoadHazardActivity;
import com.patientz.activity.SearchCareGiver;
import com.patientz.activity.SearchEmergencyContact;
import com.patientz.activity.SearchHospitalsAndOrganisationsActivity;
import com.patientz.activity.SettingActivity2;
import com.patientz.activity.UpdatePassWordActivity;
import com.patientz.activity.UploadReportsActivity;
import com.patientz.activity.UploadReportsTagActivity;

/**
 * Created by sukesh on 26/8/16.
 */
public class Tag {

    public static String getTagForScreen(String screenClass) {
        String tag = "";
        switch (screenClass) {
            case "LoginActivity":
                tag = "Login";
                break;
            case "MainActivity":
                tag = "Dashboard";
                break;
            case "OTPRegisterActivity":
                tag = "OTP";
                break;
            case "ProfileActivity":
                tag = "Profile";
                break;
            case "ProfileRegistrationActivity":
                tag = "NU_BasicProfile";
                break;
            case "RegistrationActivity":
                tag = "Signup";
                break;
            case "ActivityTermsAndConditions":
                tag = "TNM";
                break;
            case "ActivitySplashScreen":
                tag = "Splash";
                break;
            case "ActivityEditProfile":
                tag = "EditProfile";
                break;
            case "ActivityEHRUpdate":
                tag = "EditHealthRecord";
                break;
            case "ActivityEditInsurance":
                tag = "EditInsurance";
                break;
            case "ActivityWebViewVedioes":
                tag = "Videos";
                break;
            case "InsuranceDetailsActivity":
                tag = "InsuranceDetail";
                break;
            case "FirstResponderHelpWebViewActivity":
                tag = "FirstResponder";
                break;
            case "NearbyOrgsActivity":
                tag = "NearbyOrgs";
                break;
            case "ActivityInvitation":
                tag = "Invites";
                break;
            case "ContactsActivity":
                tag = "CareTeamDashboard";
                break;
            case "SearchCareGiver":
                tag = "SearchCareGiver";
                break;
            case "AddNewCareGiver":
                tag = "AddCareGiver";
                break;
            case "SearchEmergencyContact":
                tag = "SearchEmergencyContact";
                break;
            case "AddNewContactActivity":
                tag = "AddEmergencyContact";
                break;
            case "SearchHospitalsAndOrganisationsActivity":
                tag = "SearchOrg";
                break;
            case "ActivityAddMember":
                tag = "AddMember";
                break;
            case "ForgotPasswordActivity":
                tag = "ForgotPassword";
                break;
            case "ReportRoadHazardActivity":
                tag = "ReportRoadHazard";
                break;

            case "MyUploadReportsActivity":
                tag = "MyReports";
                break;
            case "NearbyAmbulancesListActivityNew":
                tag = "NearbyAmbulance";
                break;
            case "InfoSliderActivity":
                tag = "infosliderscreen";
                break;
            case "EmergencyForumActivity":
                tag = "EmergencyForum";
                break;
            case "ActivitySelectPatientInEmergency":
                tag = "SelectPatientInEmergencyScreen";
                break;
            case "BloodyFriendsAdditionActivity":
                tag = "BloodyFriendsAdditionScreen";
                break;
            case "AboutActivity":
                tag = "AppInfoScreen";
                break;
            case "ActivityCurrentEmergencies":
                tag = "EmergenciesListScreen";
                break;
            case "ActivityEmergencyStepsListener":
                tag = "EmergencyInvocationScreen";
                break;
            case "ActivityEmergencyTrackAndRevoke":
                tag = "EmergencyRevokeScreen";
                break;
            case "ActivityEmergencyType":
                tag = "EmergencyTypeScreen";
                break;
            case "ActivityMaps":
                tag = "EmergencyMapScreen";
                break;
            case "AddEmployerActivity":
                tag = "AddEmployerScreen";
                break;
            case "ChangePassWordActivity":
                tag = "ChangePasswordScreen";
                break;
            case "CurrentAmbulanceRequestActivity":
                tag = "AmbulanceTrackerScreen";
                break;

            case "CustomMapActivity":
                tag = "SetLocationMapScreen";
                break;
            case "LocationSearchActivity":
                tag = "LocationSearchScreen";
                break;
            case "MarketingCampaignActivity":
                tag = "MarketingCampaignScreen";
                break;

            case "NotifyDoctorActivity":
                tag = "AddDoctorScreen";
                break;
            case "OffersActivity":
                tag = "OffersScreen";
                break;
            case "OrgBranchesActivity":
                tag = "SelectPreferredAmbulanceInEmergency";
                break;
            case "PDFView":
                tag = "DownloadReports";
                break;
            case "SettingActivity2":
                tag = "SettingsScreen";
                break;
            case "UpdatePassWordActivity":
                tag = "UpdatePasswordScreen";
                break;
            case "UploadReportsActivity":
                tag = "AddReportsScreen";
                break;
            case "UploadReportsTagActivity":
                tag = "UploadReportsScreen";
                break;
            case "InsuranceCapturedActivity":
                tag = "InsuranceSummaryScreen";
                break;
            case "InsurancePolicyCoverageInfoActivity":
                tag = "InsuranceCoverageInfoScreen";
                break;
            case "AddInsuredDetailsActivity":
                tag = "AddInsuredDetailsScreen";
            case "BloodyFriendsActivity":
                tag = "BloodyFriends";
                break;

        }
        return tag;
    }

    public static Class getActivityFromTag(String tag) {
        switch (tag) {
            case "Login":
                return LoginActivity.class;
            case "Dashboard":
                return MainActivity.class;
            case "OTP":
                return OTPRegisterActivity.class;
            case "Profile":
                return ProfileActivity.class;
            case "NU_BasicProfile":
                return ProfileRegistrationActivity.class;
            case "Signup":
                return RegistrationActivity.class;
            case "TNM":
                return ActivityTermsAndConditions.class;
            case "Splash":
                return ActivitySplashScreen.class;
            case "EditProfile":
                return ActivityEditProfile.class;
            case "EditHealthRecord":
                return ActivityEHRUpdate.class;
            case "EditInsurance":
                return ActivityEditInsurance.class;
            case "Videos":
                return ActivityWebViewVedioes.class;
            case "InsuranceDetail":
                return InsuranceDetailsActivity.class;
            case "FirstResponder":
                return FirstResponderHelpWebViewActivity.class;
            case "NearByHospitals":
                return ActivityNearbyHospitals.class;
            case "Invites":
                return ActivityInvitation.class;
            case "CareTeamDashboard":
                return ContactsActivity.class;
            case "SearchCareGiver":
                return SearchCareGiver.class;
            case "AddCareGiver":
                return AddNewCareGiver.class;
            case "SearchEmergencyContact":
                return SearchEmergencyContact.class;
            case "AddEmergencyContact":
                return AddNewContactActivity.class;
            case "SearchOrg":
                return SearchHospitalsAndOrganisationsActivity.class;
            case "AddMember":
                return ActivityAddMember.class;
            case "ForgotPassword":
                return ForgotPasswordActivity.class;
            case "ReportRoadHazard":
                return ReportRoadHazardActivity.class;
            case "BloodyFriends":
                return BloodyFriendsActivity.class;
            case "MyReports":
                return MyUploadReportsActivity.class;
            case "NearbyOrgs":
                return NearbyOrgsActivity.class;
            case "infosliderscreen":
                return InfoSliderActivity.class;
            case "EmergencyForum":
                return EmergencyForumActivity.class;
            case "SelectPatientInEmergencyScreen":
                return ActivitySelectPatientInEmergency.class;
            case "BloodyFriendsAdditionScreen":
                return BloodyFriendsAdditionActivity.class;
            case "AppInfoScreen":
                return AboutActivity.class;
            case "EmergenciesListScreen":
                return ActivityCurrentEmergencies.class;
            case "EmergencyInvocationScreen":
                return ActivityEmergencyStepsListener.class;
            case "EmergencyRevokeScreen":
                return ActivityEmergencyTrackAndRevoke.class;
            case "EmergencyTypeScreen":
                return ActivityEmergencyType.class;
            case "EmergencyMapScreen":
                return ActivityMaps.class;
            case "AddEmployerScreen":
                return AddEmployerActivity.class;
            case "ChangePasswordScreen":
                return ChangePassWordActivity.class;
            case "AmbulanceTrackerScreen":
                return CurrentAmbulanceRequestActivity.class;
            case "SetLocationMapScreen":
                return CustomMapActivity.class;
            case "LocationSearchScreen":
                return LocationSearchActivity.class;
            case "MarketingCampaignScreen":
                return MarketingCampaignActivity.class;
            case "AddDoctorScreen":
                return NotifyDoctorActivity.class;
            case "OffersScreen":
                return OffersActivity.class;
            case "SelectPreferredAmbulanceInEmergency":
                return OrgBranchesActivity.class;
            case "DownloadReports":
                return PDFView.class;
            case "SettingsScreen":
                return SettingActivity2.class;
            case "UpdatePasswordScreen":
                return UpdatePassWordActivity.class;
            case "AddReportsScreen":
                return UploadReportsActivity.class;
            case "UploadReportsScreen":
                return UploadReportsTagActivity.class;

            case "InsuranceSummaryScreen":
                return InsuranceCapturedActivity.class;
            case "InsuranceCoverageInfoScreen":
                return InsurancePolicyCoverageInfoActivity.class;
            case "AddInsuredDetailsScreen":
                return AddInsuredDetailsActivity.class;
            case "BloodyFriendsScreen":
                return BloodyFriendsActivity.class;
        }
        return null;
    }
}
