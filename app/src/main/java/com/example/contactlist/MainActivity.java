package com.example.contactlist;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.contactlist.adapter.ContactAdapter;
import com.example.contactlist.database.SqliteDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SqliteDatabase mDatabase;
    private ArrayList<Contacts> allContacts = new ArrayList<>();
    private ContactAdapter mAdapter;

    private Toolbar toolbar; // Declare Toolbar here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar); // Initialize Toolbar
        setSupportActionBar(toolbar); // Set Toolbar as ActionBar

        RecyclerView contactView = findViewById(R.id.product_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        contactView.setLayoutManager(linearLayoutManager);
        contactView.setHasFixedSize(true);
        mDatabase = new SqliteDatabase(this);
        allContacts = mDatabase.listContacts();
        mAdapter = new ContactAdapter(this, allContacts);
        contactView.setAdapter(mAdapter);
        if (allContacts.size() > 0) {
            contactView.setVisibility(View.VISIBLE);
            mAdapter = new ContactAdapter(this, allContacts);
            contactView.setAdapter(mAdapter);

        } else {
            contactView.setVisibility(View.GONE);
            Toast.makeText(this, "Liste des Contacts Vide.\n Ajouter Nouveaux Contacts.", Toast.LENGTH_LONG).show();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskDialog();
            }
        });
    }

    private void addTaskDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_contact_layout, null);

        final EditText nameField = subView.findViewById(R.id.enter_name);
        final EditText noField = subView.findViewById(R.id.enter_phno);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter Nouveaux Contacts");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("Ajouter Contact", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = nameField.getText().toString();
                final String ph_no = noField.getText().toString();

                if (TextUtils.isEmpty(name) || ph_no.isEmpty() || ph_no.length()!=8) {
                    Toast.makeText(MainActivity.this, "Vérifier Vos Entrées", Toast.LENGTH_LONG).show();
                } else {
                    Contacts newContact = new Contacts(name, ph_no);
                    mDatabase.addContacts(newContact);

                    finish();

                    startActivity(getIntent());
                    Toast.makeText(MainActivity.this, "Contact Ajouté.", Toast.LENGTH_LONG).show();

                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Tache annulée", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView(); // Cast to androidx.appcompat.widget.SearchView

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the contacts based on the search query
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });        return true;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search submission
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search text changes
                return true;
            }
        });
    }
}
