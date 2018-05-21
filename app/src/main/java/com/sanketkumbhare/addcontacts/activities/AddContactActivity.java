package com.sanketkumbhare.addcontacts.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sanketkumbhare.addcontacts.R;
import com.sanketkumbhare.addcontacts.reciever.SaveContactReceiver;
import com.sanketkumbhare.addcontacts.uitls.Constants;
import com.sanketkumbhare.addcontacts.uitls.CredentialsValidator;
import com.sanketkumbhare.addcontacts.uitls.Utilities;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

public class AddContactActivity extends AppCompatActivity {
    private TextInputEditText mNameET, mPhoneET, mEmailET;
    private TextInputLayout mNameLayout, mPhoneLayout, mEmailLayout;
    private AppCompatCheckBox scheduleRadioBtn;
    private String mTime;
    private Intent mAction;
    private PendingIntent mActionPendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        setupActionBar();
        initViews();
    }

    private void initViews() {
        mNameET = findViewById(R.id.edit_text_name);
        mPhoneET = findViewById(R.id.edit_text_phone);
        mEmailET = findViewById(R.id.edit_text_email);
        mNameLayout = findViewById(R.id.input_layout_name);
        mEmailLayout = findViewById(R.id.input_layout_email);
        mPhoneLayout = findViewById(R.id.input_layout_phone);
        scheduleRadioBtn = findViewById(R.id.scheduleBtn);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.add_contact);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.save:
                saveContact();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveContact() {
        String name = mNameET.getText().toString();
        String email = mEmailET.getText().toString();
        String phone = mPhoneET.getText().toString();
        CredentialsValidator validator = new CredentialsValidator();
        validator.setmEmail(email);
        validator.setmPhone(phone);
        validator.setmName(name);
        boolean isFilled = true;
        if (!validator.isEmailValid()) {
            mEmailLayout.setError("Invalid Email");
            isFilled = false;
        }
        if (!validator.isPhoneValid()) {
            mPhoneLayout.setError("Invalid Phone");
            isFilled = false;
        }
        if (!validator.isNameValid()) {
            mNameLayout.setError("Invalid Name");
            isFilled = false;
        }
        if (isFilled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean isPermitted = Utilities.isPermitted(this, Manifest.permission.WRITE_CONTACTS);
                if (!isPermitted)
                    requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, name, phone, email}, Constants.REQUEST_CODE_WRITE_CONTACTS);
                else {
                    contactHandler(name, phone, email);
                }
            } else {
                contactHandler(name, phone, email);
            }
        }
    }

    private void contactHandler(String name, String phone, String email) {
        if (scheduleRadioBtn.isChecked()) {
            //show time dialog
            mAction = new Intent(this, SaveContactReceiver.class);
            //passing the data to the broadcast register
            mAction.putExtra("name", name);
            mAction.putExtra("email", email);
            mAction.putExtra("phone", phone);

            mActionPendingIntent = PendingIntent.getBroadcast(this, 10, mAction, 0);
            showTimePickerDialog();
        } else {
            if (Utilities.saveContact(this, name, phone, email)) {
                onResume();
                clearFields();
            }
        }
    }

    private void clearFields() {
        mNameET.setText("");
        mEmailET.setText("");
        mPhoneET.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_CODE_WRITE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Utilities.saveContact(this, permissions[1], permissions[2], permissions[3])) {
                    onResume();
                    clearFields();
                }
            } else {
                Toast.makeText(this, "Cannot save Contact", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showTimePickerDialog() {
        final Map<Integer, Integer> timeMap = Utilities.getTime();

        TimePickerDialog dialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mTime = hourOfDay + ":" + minute;
                        try {
                            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            manager.set(AlarmManager.RTC_WAKEUP, Utilities.getTimeinMillis(mTime,
                                    "hh:mm"), mActionPendingIntent);
                            clearFields();
                            showContactSavedToast();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, timeMap.get(Calendar.HOUR_OF_DAY), timeMap.get(Calendar.MINUTE), true);
        dialog.show();
    }

    private void showContactSavedToast() {
        Toast.makeText(this, "Scheduler Started", Toast.LENGTH_SHORT).show();
    }

}
