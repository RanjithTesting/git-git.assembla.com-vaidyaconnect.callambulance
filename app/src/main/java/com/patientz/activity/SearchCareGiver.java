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
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.HashMap;

public class SearchCareGiver extends BaseActivity {
    public final static int REQUEST_CODE_ADD_CONTACT = 786;
    private static final String TAG = "SearchCareGiver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_care_giver);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_search_care_giver);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
                upshotData.put(Constant.UpshotEvents.CT_CG_ADD_NEW_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_CG_ADD_NEW_CLICKED);
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
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
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
                    upshotData.put(Constant.UpshotEvents.CT_ADD_CG_FROM_PHONE_BOOK_CLICKED,true);
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_ADD_CG_FROM_PHONE_BOOK_CLICKED);
                    Log.d(TAG,"upshot data="+upshotData.entrySet());
                    Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    contactPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    getActivity().startActivityForResult(contactPickerIntent, REQUEST_CODE_ADD_CONTACT);
                    this.dismiss();
                    break;
                case R.id.fabAddNewContact:
                    upshotData.put(Constant.UpshotEvents.CT_ADD_CG_NEW_CONTACT_CLICKED,true);
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_ADD_CG_NEW_CONTACT_CLICKED);
                    Log.d(TAG,"upshot data="+upshotData.entrySet());
                    Intent intentAddContact = new Intent(getActivity(), AddNewCareGiver.class);
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
                Intent intent = new Intent(this, AddNewCareGiver.class);
                intent.putExtra("name", name);
                intent.putExtra("number", phoneNo);
                startActivity(intent);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
