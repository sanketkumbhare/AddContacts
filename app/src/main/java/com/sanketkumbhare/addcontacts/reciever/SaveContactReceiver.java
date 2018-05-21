package com.sanketkumbhare.addcontacts.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.sanketkumbhare.addcontacts.uitls.Utilities;

/**
 * Created by sanket on 2/3/18.
 */

public class SaveContactReceiver extends BroadcastReceiver {

    //empty constructor
    public SaveContactReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("name"),
                phone = intent.getStringExtra("phone"),
                email = intent.getStringExtra("email");

        if (name != null && phone != null && email != null) {
            if (Utilities.saveContact(context, name, phone, email)) {
                Toast.makeText(context, "Contact Saved", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, "Couldn't Save Contact", Toast.LENGTH_SHORT).show();
        }
    }
}
