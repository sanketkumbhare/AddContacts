package com.sanketkumbhare.addcontacts.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sanket on 2/3/18.
 */

public class Contact implements Parcelable {

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
    private int mId;
    private String mName;
    private String mEmail;
    private String mPhone;
    private String lookupKey;

    public Contact() {
    }

    public Contact(int id, String name, String email, String phone) {
        this.mId = id;
        this.mName = name;
        this.mEmail = email;
        this.mPhone = phone;
    }

    protected Contact(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mEmail = in.readString();
        mPhone = in.readString();
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mEmail);
        dest.writeString(mPhone);
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }
}
