package com.sanketkumbhare.addcontacts.uitls;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.sanketkumbhare.addcontacts.R;
import com.sanketkumbhare.addcontacts.activities.ContactDetailActivity;
import com.sanketkumbhare.addcontacts.models.Contact;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sanket on 2/3/18.
 */

public class Utilities {

    /**
     * Method to check whether a given permission is permitted or not.
     *
     * @param context:          This value holds reference of the calling activity
     * @param permissionString: This holds what permission is to be checked
     */
    public static boolean isPermitted(Context context, String permissionString) {
        return ContextCompat.
                checkSelfPermission(context, permissionString)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @return : Map<Integer, Integer> this method returns a hashmap that contains the current
     * hour of the day and the minute. and can be accessed by using calendar Constants.
     */
    public static Map<Integer, Integer> getTime() {
        Calendar calendar = Calendar.getInstance();
        Map<Integer, Integer> map = new HashMap<>();
        map.put(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        map.put(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        return map;
    }

    public static long getTimeinMillis(String timeStamp, String currentFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(currentFormat);
        Date ts = simpleDateFormat.parse(timeStamp);
        return ts.getTime();
    }

    //works fine
    public static boolean saveContact(Context context, String name, String phone, String email) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //adding name
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());

        //adding phone
        ops.add(ContentProviderOperation.
                newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        //adding email
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        // asking content provider to create new contacts
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Contact contact = new Contact();
            contact.setmName(name);
            contact.setmPhone(phone);
            contact.setmEmail(email);
            notifyAddedContact(context, contact);
            return true;
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
            Toast.makeText(context, "Couldn't Save contacts", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static List<Contact> getContactList(Context context) {
        Cursor cursor = context.getContentResolver()
                .query(ContactsContract.Contacts.CONTENT_URI,
                        null, null,
                        null, null);

        if (cursor.getCount() == 0) {
            cursor.close();
            return new ArrayList<>();
        }
        //if count >0
        List<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            Contact contact = new Contact();

            int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

            int phoneCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (phoneCount > 0) {
                ArrayList<String> phones = new ArrayList<>();
                ArrayList<String> emails = new ArrayList<>();

                Cursor pCursor = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{String.valueOf(id)}, null);

                Cursor eCursor = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{String.valueOf(id)}, null);


                if (pCursor != null) {
                    if (pCursor.moveToNext()) {
                        phones.add(pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    }
                }

                if (eCursor != null) {
                    if (eCursor.moveToNext()) {
                        emails.add(eCursor.getString(eCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
                    }
                }

                if (pCursor != null) {
                    pCursor.close();
                }
                if (eCursor != null) {
                    eCursor.close();
                }
                contact.setmPhone(phones.get(0));
                if (emails.size() > 0)
                    contact.setmEmail(emails.get(0));
            }

            //set the properties to the model
            contact.setmId(id);
            contact.setmName(name);
            contact.setLookupKey(lookupKey);
            //set the model to the list
            contacts.add(contact);
        }
        return contacts;
    }

    public static boolean deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phone));
        Cursor cur = ctx.getContentResolver()
                .query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                        cur.close();
                        return true;
                    }
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        cur.close();
        return false;
    }
//notification of added contact
    private static void notifyAddedContact(Context context, Contact contact) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;
        String id = "1";
        String name = "Added Contact Notification";
        int requestCode = 10;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            builder = new Notification.Builder(context, "1");
        } else {
            builder = new Notification.Builder(context);
        }

        Intent intent = new Intent(context, ContactDetailActivity.class);
        intent.putExtra("contact", contact);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, 0);

        builder.setContentTitle("New Contact Added")
                .setContentText(contact.getmName()
                        .concat(" - ")
                        .concat(contact.getmPhone()
                                .concat(" - ")
                                .concat(contact.getmEmail())))
                .setSmallIcon(R.drawable.ic_perm_contact_calendar_black_24dp)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }

}
