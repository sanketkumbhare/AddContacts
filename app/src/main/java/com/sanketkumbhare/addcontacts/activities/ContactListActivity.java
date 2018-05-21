package com.sanketkumbhare.addcontacts.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sanketkumbhare.addcontacts.R;
import com.sanketkumbhare.addcontacts.adapter.ContactAdapter;
import com.sanketkumbhare.addcontacts.models.Contact;
import com.sanketkumbhare.addcontacts.uitls.Constants;
import com.sanketkumbhare.addcontacts.uitls.Utilities;

import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ContactAdapter contactAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        setupActionBar();
        //runtime permissions for android marshmallow.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            showPermissionDialogs();
        else
            readAndShowResults();

    }
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.contacts);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showPermissionDialogs() {
        boolean readPermission = Utilities.isPermitted(this, Manifest.permission.READ_CONTACTS);
        //if not permitted, show the dialog.
        if (!readPermission) {
            //show permission dialog.
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, Constants.REQUEST_CODE_READ_CONTACTS);
        }
        //show the contacts directly.
        else
            readAndShowResults();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //read the contacts here.
            //put it into the recycler view.
            readAndShowResults();
            if (requestCode == Constants.REQUEST_CODE_READ_CONTACTS) {
            } else {
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void readAndShowResults() {
        setupRecyclerView();

        List<Contact> mContacts = Utilities.getContactList(this);

        //if contact size > 0, then only show the recycler view

        if (mContacts.size() > 0) {
            contactAdapter = new ContactAdapter(this, mContacts);
            recyclerView.setAdapter(contactAdapter);
        } else {
            TextView messageTextView = findViewById(R.id.text_view_message);
            messageTextView.setText(R.string.message_no_contact);
            messageTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean readPermission = Utilities.isPermitted(this, Manifest.permission.READ_CONTACTS);
        //if not permitted, show the dialog.
        if (readPermission) {
            readAndShowResults();
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void addContact(View view) {
        Intent intent = new Intent(this, AddContactActivity.class);
        startActivity(intent);
    }

}
