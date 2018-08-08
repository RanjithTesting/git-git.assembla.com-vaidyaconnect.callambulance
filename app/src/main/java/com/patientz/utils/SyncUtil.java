package com.patientz.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.patientz.VO.HealthRecordVO;
import com.patientz.VO.InsuranceVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.UserRecordVO;
import com.patientz.databases.DatabaseHandler;

import java.util.ArrayList;

import javax.crypto.SecretKey;

public class SyncUtil {
    private static final String TAG = "SyncUtil";
    static Context mContext;

    public SyncUtil(Context context) {
        mContext = context;
    }

    public static boolean removeFile(Context context,
                                     PatientUserVO mPatientUserVO) {
        mContext = context;
        boolean check = mContext.deleteFile(String.valueOf(mPatientUserVO
                .getPatientId()));
        Log.d("SyncUtil", mPatientUserVO.getFileName() + " is deleted: "
                + check);
        return check;
    }

    public static boolean checkFile(Context context,
                                    PatientUserVO mPatientUserVO) {
        mContext = context;
        boolean check = false;
        String[] files = mContext.fileList();
        for (String name : files) {
            Log.d("SyncUtil", "file name: " + name);
            if (TextUtils.equals(name,
                    String.valueOf(mPatientUserVO.getPatientId()))) {
                check = true;
                break;
            }
        }
        return check;
    }

    private static String encryptApikey(String apiKey) {
        // Log.d(TAG, "apiKey pin.."+apiKey);
        String key = "D0ctrz12";
        String encryptApiKey = null;
        try {
            SecretKey mSecretKey = AppCrypto.getSecretKey(key, key);
            encryptApiKey = AppCrypto.encrypt(mSecretKey, apiKey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encryptApiKey;
    }

    public static boolean updateAllPatientInformations(Context context,
                                                       ArrayList<PatientUserVO> mList, String pin) throws Exception {
        mContext = context;
        long currentUserId = 0;
/*		if (PatientDataHandler.getInstance().getmPatientUserVO() != null)
            currentUserId = PatientDataHandler.getInstance()
					.getmPatientUserVO().getPatientId();*/
        try {
            SecretKey mSecretKey = AppCrypto.getSecretKey(pin, pin);
            for (PatientUserVO patientUserVO : mList) {
                String key = encryptApikey(patientUserVO.getEmergencyAPIKEY());
                patientUserVO.setEmergencyAPIKEY(key);
                updateSinglePatientInformations(context, patientUserVO,
                        mSecretKey);
                if (currentUserId != 0)
                    if (patientUserVO.getPatientId() == currentUserId) {
                        //	SyncUtil.loadUpdatedRecord(mContext, patientUserVO, pin);
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void updateSinglePatientInformations(Context context,
                                                       PatientUserVO mVO, SecretKey mSecretKey) throws Exception {
        mContext = context;
        if (checkFile(context, mVO)) {
            if (removeFile(mContext, mVO)) {
                saveUserRecord(mContext, mVO, mSecretKey);
            }
        } else {
            saveUserRecord(mContext, mVO, mSecretKey);
        }
    }

    public static void saveUserRecord1(Context context, PatientUserVO mPatientUserVO, SecretKey mSecretKey)
            throws Exception {
        Log.d("saveUserRecord", "A");
        mContext = context;
        DatabaseHandler dh = DatabaseHandler.dbInit(mContext);
        if (TextUtils.equals(mPatientUserVO.getStatus(), "Deleted")
                || TextUtils.equals(mPatientUserVO.getStatus(), "Rejected")) {
            Log.d("SyncUtil", "removing contact");
            removeFile(context, mPatientUserVO);
            //dh.removeProfile(mPatientUserVO.getPatientId());
            //dh.removeContacts(mPatientUserVO.getPatientId());
            Log.d("saveUserRecord", "B");
        } else {
            try {
                //dh.insertUser(mPatientUserVO);

                dh.insertContacts(mPatientUserVO);
                if (mPatientUserVO.getRecordVO() != null && mPatientUserVO.getRecordVO().getUserInfoVO() != null) {
                    Log.d("SyncUtil", "INserting user info vo");
                    dh.insertUserInfo(mPatientUserVO.getRecordVO(), mPatientUserVO.getBloodGroup(), mPatientUserVO.getPatientHandle());
                }
            } catch (Exception e) {
                Log.d(TAG, "EXCEPTION=" + e.getMessage());
                e.printStackTrace();
            }
            UserRecordVO userRecord = mPatientUserVO.getRecordVO();
            //storePrefferedAmbulanceProviderInSP(userRecord);
            /* saving user details to DB */
            //dh.insertUser(mPatientUserVO);
            //dh.insertUserInfo(mPatientUserVO.getRecordVO().getUserInfoVO());
            Log.d("saveUserRecord", "C");
            //dh.insertContacts(mPatientUserVO);
            if (userRecord != null && userRecord.getInsuranceVOs() != null) {
                Log.d("saveUserRecord", "D");
                Log.d("insurances list size",userRecord.getInsuranceVOs().size()+"");
                for (InsuranceVO insuranceVO : userRecord.getInsuranceVOs()) {
                    Log.d("saveUserRecord", "E -- for patient id " + mPatientUserVO.getPatientId());
                    Log.d("saveUserRecord", "E -- for insurance id " + insuranceVO.getInsuranceId());
                    dh.insertUserInsuranceDetails(insuranceVO);
                    RequestQueue mRequestQueue= AppVolley.getRequestQueue();
                    /*if(insuranceVO.getInsuranceUploadId()!=0)
                    {
                        mRequestQueue.add(getInsuranceUpload());
                    }*/
                }
            }
            if (userRecord != null && userRecord.getHealthRecordVO() != null) {
                HealthRecordVO healthRecordVO = userRecord.getHealthRecordVO();
                if (healthRecordVO.getHealthRecord() != null) {
                    SchemaUtils.saveEmergencyHealthRecord(healthRecordVO.getHealthRecord(), context);
                    dh.insertUserHealthRecord(healthRecordVO, mPatientUserVO.getPatientId());
                }
            }

            // Update User organisation list

            dh.updateUserOrgRecord(userRecord, mPatientUserVO.getPatientId());


			/* saving record to a file */
        /*	Gson gson = new Gson();
            String record = gson.toJson(userRecord);
			String encryptJson = AppCrypto.encrypt(mSecretKey, record);
			// file name based on patient ID
			AppUtil.saveDataToFile(mContext,
					String.valueOf(mPatientUserVO.getPatientId()), encryptJson);*/
        }
    }

    public static void saveUserRecord(Context context,
                                      PatientUserVO mPatientUserVO, SecretKey mSecretKey)
            throws Exception {
        Log.d("saveUserRecord", "A");
        mContext = context;
        DatabaseHandler dh = DatabaseHandler.dbInit(mContext);
        if (TextUtils.equals(mPatientUserVO.getStatus(), "Deleted")
                || TextUtils.equals(mPatientUserVO.getStatus(), "Rejected")) {
            Log.d("SyncUtil", "removing contact");
            removeFile(context, mPatientUserVO);
            //dh.removeProfile(mPatientUserVO.getPatientId());
            //dh.removeContacts(mPatientUserVO.getPatientId());
            Log.d("saveUserRecord", "B");
        } else {
            try {
                dh.insertUser(mPatientUserVO);
                dh.insertContacts(mPatientUserVO);
                if (mPatientUserVO.getRecordVO() != null && mPatientUserVO.getRecordVO().getUserInfoVO() != null) {
                    dh.insertUserInfo(mPatientUserVO.getRecordVO(), mPatientUserVO.getBloodGroup(), mPatientUserVO.getPatientHandle());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            UserRecordVO userRecord = mPatientUserVO.getRecordVO();
            //storePrefferedAmbulanceProviderInSP(userRecord);
			/* saving user details to DB */
            //dh.insertUser(mPatientUserVO);
            //dh.insertUserInfo(mPatientUserVO.getRecordVO().getUserInfoVO());
            Log.d("saveUserRecord", "C");
            //dh.insertContacts(mPatientUserVO);
            if (userRecord != null && userRecord.getInsuranceVOs() != null) {
                Log.d("saveUserRecord", "D");
                for (InsuranceVO insuranceVO : userRecord.getInsuranceVOs()) {
                    Log.d("saveUserRecord", "E -- for patient id " + mPatientUserVO.getPatientId());
                    Log.d("saveUserRecord", "E -- for insurance id " + insuranceVO.getInsuranceId());
                    dh.insertUserInsuranceDetails(insuranceVO);
                }
            }
            if (userRecord != null && userRecord.getHealthRecordVO() != null) {
                HealthRecordVO healthRecordVO = userRecord.getHealthRecordVO();
                if (healthRecordVO.getHealthRecord() != null) {
                    SchemaUtils.saveEmergencyHealthRecord(healthRecordVO.getHealthRecord(), context);
                    dh.insertUserHealthRecord(healthRecordVO, mPatientUserVO.getPatientId());
                }
            }

            // Update User organisation list

            dh.updateUserOrgRecord(userRecord, mPatientUserVO.getPatientId());
        }
    }

    private static void storePrefferedAmbulanceProviderInSP(UserRecordVO userRecord) {
        if (userRecord != null) {
            HealthRecordVO mHealthRecordVO = userRecord.getHealthRecordVO();
            if (mHealthRecordVO != null) {
                SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                String prefferedAmbulanceProvider = mHealthRecordVO.getHealthRecord().get("preferredAmbulanceProvider");
                String[] prefferedAmbulanceProviderAfterSplit = prefferedAmbulanceProvider.split(",");

                if (prefferedAmbulanceProviderAfterSplit.length == 3) {
                    mEditor.putString("preferredAmbulanceProviderPhoneNo", prefferedAmbulanceProviderAfterSplit[2]);
                } else if (prefferedAmbulanceProviderAfterSplit.length == 2) {
                    mEditor.putString("preferredAmbulanceProviderPhoneNo", prefferedAmbulanceProviderAfterSplit[1]);
                } else {
                }
                mEditor.commit();
            }
        }
    }
//	public static StringRequest getSession(Context context) {
//		if (AppUtil.isOnline(context)) {
//			try {
//				SharedPreferences sharedPreferences = context
//						.getSharedPreferences(Constant.APP_SHARED_PREFERENCE,
//								Context.MODE_PRIVATE);// getPreferences(getActivity().MODE_PRIVATE);
//				SecretKey secretKey = AppCrypto.getSecretKey(
//						context.getString(R.string.app_key), context.getString(R.string.app_namak));
//				String userEmail = sharedPreferences.getString(Constant.K_ID,
//						"");
//				String en_pass = sharedPreferences.getString(
//						Constant.K_SECRET, "");
//				String userSecret = AppCrypto.decrypt(secretKey, en_pass);
//				//Log.d(TAG, "User Email : " + userEmail + " /n pass: " + userSecret);
//				StringRequest loginRequest = createSessionRequest(context,
//						userEmail, userSecret);
//				return loginRequest;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else {
//			AppUtil.showToast(context, context.getString(R.string.offlineMode));
//		}
//		return null;
//	}

//	public static StringRequest createSessionRequest(final Context context,
//													 final String email, final String password) {
//		Log.d(TAG, "createLoginRequest");
//		String szServerUrl = context.getResources().getString(
//				R.string.serverUrl)
//				+ "j_spring_security_check";
//		StringRequest mRequest = new StringRequest(Request.Method.POST,
//				szServerUrl, new Response.Listener<String>() {
//			@Override
//			public void onResponse(String response) {
//				 Log.d(TAG, "login response : " + response);
//
//			}
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				Log.d(TAG, "Json response : " + error.getMessage());
//				AppUtil.showToast(context, context.getResources()
//						.getString(R.string.networkError));
//			}
//
//		}) {
//			@Override
//			protected Map<String, String> getParams() throws AuthFailureError {
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("j_username", email);
//				params.put("j_password", password);
//				return params;
//			}
//
//			@Override
//			public Map<String, String> getHeaders() throws AuthFailureError {
//				return AppUtil.addHeadersForApp(context,super.getHeaders());
//			}
//		};
//		return mRequest;
//	}


}
