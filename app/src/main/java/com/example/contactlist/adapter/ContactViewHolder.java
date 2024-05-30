package com.example.contactlist.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.contactlist.R;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public TextView name,ph_no;
    public ImageView deleteContact;
    public  ImageView editContact;

    public ContactViewHolder(View itemView) {
        super(itemView);
        name = (TextView)itemView.findViewById(R.id.contact_name);
        ph_no = (TextView)itemView.findViewById(R.id.ph_no);
        deleteContact = (ImageView)itemView.findViewById(R.id.delete_contact);
        editContact = (ImageView)itemView.findViewById(R.id.edit_contact);
    }
}
