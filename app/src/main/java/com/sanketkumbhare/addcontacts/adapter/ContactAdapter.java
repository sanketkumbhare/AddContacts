package com.sanketkumbhare.addcontacts.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.sanketkumbhare.addcontacts.R;
import com.sanketkumbhare.addcontacts.activities.ContactDetailActivity;
import com.sanketkumbhare.addcontacts.models.Contact;

import java.util.List;

/**
 * Created by sanket on 2/3/18.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyCustomViewHolder> {

    private Context mContext;
    private List<Contact> mContacts;

    public ContactAdapter(Context mContext, List<Contact> mContacts) {
        this.mContext = mContext;
        this.mContacts = mContacts;
    }


    @Override
    public ContactAdapter.MyCustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (inflater != null) {
            view = inflater.inflate(R.layout.item_contact, parent, false);
            return new MyCustomViewHolder(view);
        } else
            return null;
    }


    @Override
    public void onBindViewHolder(ContactAdapter.MyCustomViewHolder holder, final int position) {
        holder.mNameTextView.setText(mContacts.get(position).getmName());
        String phone = mContacts.get(position).getmPhone(), 
                email = mContacts.get(position).getmEmail();
        holder.mContactCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailsIntent = new Intent(mContext, ContactDetailActivity.class);
                detailsIntent.putExtra("contact", mContacts.get(position));
                mContext.startActivity(detailsIntent);
            }
        });
        if (phone == null)
            holder.mPhoneTextView.setVisibility(View.GONE);
        else
            holder.mPhoneTextView.setText(phone);
        if (email == null)
            holder.mEmailTextView.setVisibility(View.GONE);
        else
            holder.mEmailTextView.setText(email);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class MyCustomViewHolder extends RecyclerView.ViewHolder {
        private CardView mContactCard;
        private AppCompatTextView mNameTextView;
        private AppCompatTextView mEmailTextView;
        private AppCompatTextView mPhoneTextView;

        public MyCustomViewHolder(View itemView) {
            super(itemView);
            mContactCard = itemView.findViewById(R.id.card_contact);
            mNameTextView = itemView.findViewById(R.id.text_view_contact_name);
            mEmailTextView = itemView.findViewById(R.id.text_view_email);
            mPhoneTextView = itemView.findViewById(R.id.text_view_phone);
        }
    }
}
