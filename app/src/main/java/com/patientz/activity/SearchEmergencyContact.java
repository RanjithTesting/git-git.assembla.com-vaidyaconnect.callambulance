package com.patientz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.HashMap;

public class SearchEmergencyContact extends BaseActivity {
    public final static int REQUEST_CODE_ADD_CONTACT = 786;
    private static final String TAG ="SearchEmergencyContact" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_emergency_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_search_emergency_contact);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_add_new_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_new_contact:
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.CT_EC_ADD_NEW_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_EC_ADD_NEW_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                FireMissilesDialogFragment fdf2 = new FireMissilesDialogFragment();
                fdf2.show(getFragmentManager(), "FireMissilesDialogFragment");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class FireMissilesDialogFragment extends DialogFragment implements View.OnClickListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View view = inflater.inflate(R.layout.select_type_contact, null);
            builder.setView(view);
            FloatingActionButton phoneFab = (FloatingActionButton) view.findViewById(R.id.fabFromPhone);
            FloatingActionButton newContactFab = (FloatingActionButton) view.findViewById(R.id.fabAddNewContact);
            phoneFab.setOnClickListener(this);
            newContactFab.setOnClickListener(this);
            return builder.create();
        }

        @Override
        public void onClick(View v) {
            HashMap<String, Object> upshotData = new HashMap<>();

            int id = v.getId();
            switch (id) {
                case R.id.fabFromPhone:
                    upshotData.put(Constant.UpshotEvents.CT_ADD_EC_FROM_PHONE_BOOK_CLICKED,true);
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_ADD_EC_FROM_PHONE_BOOK_CLICKED);
                    Log.d(TAG,"upshot data="+upshotData.entrySet());
                    Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    contactPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    getActivity().startActivityForResult(contactPickerIntent, REQUEST_CODE_ADD_CONTACT);
                    this.dismiss();
                    break;
                case R.id.fabAddNewContact:
                    upshotData.put(Constant.UpshotEvents.CT_ADD_EC_NEW_CONTACT_CLICKED,true);
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_ADD_EC_NEW_CONTACT_CLICKED);
                    Log.d(TAG,"upshot data="+upshotData.entrySet());
                    Intent intentAddContact = new Intent(getActivity(), AddNewContactActivity.class);
                    startActivity(intentAddContact);
                    getActivity().finish();
                    this.dismiss();
                    break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "requestCode : " + requestCode + " , result code : " + resultCode);
        if (requestCode == REQUEST_CODE_ADD_CONTACT) {
            if (resultCode == RESULT_OK) {
                Log.d("onActivityResult", "Contact selected  : " + data.getDataString());
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String phoneNo = cursor.getString(phoneIndex);
                String name = cursor.getString(nameIndex);

                Log.d("onActivityResult", "ZZZ number : " + phoneNo + " , name : " + name);
                Intent intent = new Intent(this, AddNewContactActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("number", phoneNo);
                startActivity(intent);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
