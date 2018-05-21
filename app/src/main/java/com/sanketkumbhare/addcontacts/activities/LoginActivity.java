package com.sanketkumbhare.addcontacts.activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sanketkumbhare.addcontacts.R;
import com.sanketkumbhare.addcontacts.uitls.CredentialsValidator;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout mEmailLayout, mPasswordLayout;
    private TextInputEditText mEmailEditText, mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar();
        initViews();

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.login));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        mEmailLayout = findViewById(R.id.input_layout_email);
        mEmailEditText = findViewById(R.id.edit_text_email);
        mPasswordLayout = findViewById(R.id.input_layout_password);
        mPasswordEditText = findViewById(R.id.edit_text_password);
    }
    public void login(View view) {
        login();
    }

    private void login() {
        boolean isFinished = true;
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        CredentialsValidator validator = new CredentialsValidator(email, password);
        if (!validator.isEmailValid()) {
            isFinished = false;
            mEmailLayout.setError("Invalid email");
        } else
            mEmailLayout.setError(null);
        if (!validator.isPasswordValid()) {
            isFinished = false;
            mPasswordLayout.setError("Invalid password");
        } else
            mPasswordLayout.setError(null);
        if (isFinished) {
            //contacts Activity.
            Intent intent = new Intent(this, ContactListActivity.class);
            startActivity(intent);
            //don't show login after a successful one.
            finish();
        }
    }
}
