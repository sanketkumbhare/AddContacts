package com.sanketkumbhare.addcontacts.uitls;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sanket on 2/3/18.
 * <p>
 * Utility class to handle email and password validation jobs.
 */
public class CredentialsValidator {

    private final int THRESHOLD = 8;
    private String mEmail;
    private String mPassword;
    private String mPhone;
    private String mName;

    public CredentialsValidator() {
    }

    public CredentialsValidator(String email, String password) {
        this.mEmail = email;
        this.mPassword = password;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public boolean isEmailValid() {
        if (mEmail == null || mEmail.equals("")) {
            return false;
        }
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = emailPattern.matcher(mEmail);
        return matcher.matches();
    }

    public boolean isPasswordValid() {
        return !(mPassword == null || mPassword.length() < THRESHOLD);
    }

    //threshold = 3 because one can save emergency number.
    public boolean isPhoneValid() {
        return !(mPhone == null || mPhone.length() < 3);
    }

    public boolean isNameValid() {
        return !(mName == null || mName.length() <= 0);
    }


    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
