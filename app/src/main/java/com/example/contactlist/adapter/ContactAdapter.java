package com.example.contactlist.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.contactlist.Contacts;
import com.example.contactlist.R;
import com.example.contactlist.database.SqliteDatabase;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Contacts> listContacts;
    private ArrayList<Contacts> mArrayList;

    private SqliteDatabase mDatabase;

    public ContactAdapter(Context context, ArrayList<Contacts> listContacts) {
        this.context = context;
        this.listContacts = listContacts;
        this.mArrayList = listContacts;
        mDatabase = new SqliteDatabase(context);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final Contacts contacts = listContacts.get(position);

        holder.name.setText(contacts.getName());
        holder.ph_no.setText(contacts.getPhno());

        holder.editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTaskDialog(contacts);
            }
        });

        holder.deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete row from database
                mDatabase.deleteContact(contacts.getId());

                // Refresh the activity page
                ((Activity) context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listContacts.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString().toLowerCase();

                // If the search query is empty, return the complete list
                if (query.isEmpty()) {
                    listContacts = mArrayList;
                } else {
                    ArrayList<Contacts> filteredList = new ArrayList<>();

                    // Check if any contact's name or phone number contains the search query
                    for (Contacts contacts : mArrayList) {
                        if (contacts.getName().toLowerCase().contains(query) ||
                                contacts.getPhno().toLowerCase().contains(query)) {
                            filteredList.add(contacts);
                        }
                    }

                    listContacts = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listContacts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listContacts = (ArrayList<Contacts>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void editTaskDialog(final Contacts contacts) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.add_contact_layout, null);

        final EditText nameField = (EditText) subView.findViewById(R.id.enter_name);
        final EditText contactField = (EditText) subView.findViewById(R.id.enter_phno);

        if (contacts != null) {
            nameField.setText(contacts.getName());
            contactField.setText(contacts.getPhno());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("éditer contact");
        builder.setView(subView);

        builder.setPositiveButton("Edition CONTACT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameField.getText().toString();
                String ph_no = contactField.getText().toString();

                if (TextUtils.isEmpty(name) || ph_no.isEmpty() || ph_no.length()!=8) {
                    Toast.makeText(context, "Vérifier vos entrées", Toast.LENGTH_LONG).show();
                } else {
                    mDatabase.updateContacts(new Contacts(contacts.getId(), name, ph_no));

                    // Refresh the activity
                    ((Activity) context).finish();
                    context.startActivity(((Activity) context).getIntent());
                }
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Tache Annulée", Toast.LENGTH_LONG).show();
            }
        });

        builder.show();
    }
}
