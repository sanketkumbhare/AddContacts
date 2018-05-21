package com.sanketkumbhare.addcontacts.activities;

import android.app.NotificationManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.sanketkumbhare.addcontacts.R;
import com.sanketkumbhare.addcontacts.models.Contact;
import com.sanketkumbhare.addcontacts.uitls.CredentialsValidator;
import com.sanketkumbhare.addcontacts.uitls.Utilities;

public class ContactDetailActivity extends AppCompatActivity {
    TextInputEditText nameEditText, phoneEditText, emailEditText;
    TextInputLayout nameLayout, phoneLayout, emailLayout;

    Contact mContact;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        setupActionBar();
        initViews();
        getData();
    }

    private void initViews() {
        nameEditText = findViewById(R.id.edit_text_name);
        phoneEditText = findViewById(R.id.edit_text_phone);
        emailEditText = findViewById(R.id.edit_text_email);

        nameLayout = findViewById(R.id.input_layout_name);
        emailLayout = findViewById(R.id.input_layout_email);
        phoneLayout = findViewById(R.id.input_layout_phone);

        layout = findViewById(R.id.layout);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Contact Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.delete:
                delete();
                return true;
            case R.id.edit:
                edit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void edit() {
        String newName, newPhone, newEmail;
        newName = nameEditText.getText().toString();
        newPhone = phoneEditText.getText().toString();
        newEmail = emailEditText.getText().toString();

        CredentialsValidator validator = new CredentialsValidator();
        validator.setmName(newName);
        validator.setmPhone(newPhone);
        validator.setmEmail(newEmail);

        boolean isFilled = true;
        if (!validator.isNameValid()) {
            isFilled = false;
            nameLayout.setError("Invalid Name");
        }
        if (!validator.isEmailValid()) {
            isFilled = false;
            nameLayout.setError("Invalid Email");
        }
        if (!validator.isPhoneValid()) {
            isFilled = false;
            nameLayout.setError("Invalid Phone");
        }
        if (isFilled) {
            delete();
            Utilities.saveContact(this, newName, newPhone, newEmail);
            updateViews(newName, newPhone, newEmail);
            Snackbar snackbar = Snackbar.make(layout, "Edit Successful", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void updateViews(String name, String phone, String email) {
        nameEditText.setText(name);
        phoneEditText.setText(phone);
        emailEditText.setText(email);
    }

    private void delete() {
        boolean result = Utilities.deleteContact(this, mContact.getmPhone(), mContact.getmName());
        Log.d("success", String.valueOf(result));
        if (result) {
            final Snackbar snackbar = Snackbar
                    .make(layout, "Deleted", Snackbar.LENGTH_SHORT)
                    .setAction("undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utilities.saveContact(ContactDetailActivity.this
                                    , mContact.getmName(),
                                    mContact.getmPhone(),
                                    mContact.getmEmail());
                        }
                    });
            snackbar.show();
        } else {
            final Snackbar snackbar = Snackbar
                    .make(layout,
                            "Cannot Delete Contact",
                            Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void getData() {
        mContact = getIntent().getParcelableExtra("contact");
        if (mContact != null) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(1);
            nameEditText.setText(mContact.getmName());
            emailEditText.setText(mContact.getmEmail());
            phoneEditText.setText(mContact.getmPhone());
        }
    }
}
