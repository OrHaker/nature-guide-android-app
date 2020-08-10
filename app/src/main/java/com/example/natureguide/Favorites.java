package com.example.natureguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Favorites extends Fragment {

    private DrawerLayout drawer;
    private NatureLocation[] listToShow;
    private ListView favoritesListView;
    //data base ref
    FirebaseDatabase database;
    DatabaseReference favLocationRef, userRef;
    //firebase auth user
    private FirebaseAuth mAuth;
    //nl list
    ArrayList<NatureLocation> nlList;
    NatureLocation[] listOfPlaces;
    CustomAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_favorites, container, false);
        super.onCreate(savedInstanceState);
        //connect to view
        favoritesListView = (ListView) view.findViewById(R.id.favoritesListView);
        //details to show
        nlList = new ArrayList<NatureLocation>();
        // Read from the database
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        favLocationRef = database.getReference("Users").child(mAuth.getUid()).child("favLocation");
        userRef = database.getReference("Users").child(mAuth.getUid());

        listOfPlaces = new NatureLocation[]{new NatureLocation("אין עדיין דברים במועדפים", "אין עדיין דברים במועדפים", "אין עדיין דברים במועדפים", "אין תמונה", 30.627331, 34.887882)};

        //CustomAdapter instance and connection to list view
        adapter = new CustomAdapter(getActivity(), listOfPlaces,true);
        favoritesListView.setAdapter(adapter);


        //Read from the database
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    NatureLocation temp = ds.getValue(NatureLocation.class);
                    nlList.add(temp);
                }
//                listOfPlaces = new NatureLocation[nlList.size()];
//                for (int i = 0; i < listOfPlaces.length; i++) {
//                    listOfPlaces[i] = new NatureLocation(nlList.get(i).getName(), nlList.get(i).getTitle(), nlList.get(i).getDescription(), nlList.get(i).getImage(), nlList.get(i).getLatLangv(), nlList.get(i).getLatLangv1());
//                }
//                adapter = new CustomAdapter(Favorites.this, listOfPlaces,true);
                adapter = new CustomAdapter(getActivity(), nlList.toArray(new NatureLocation[nlList.size()]),true);

                favoritesListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Toast.makeText(getActivity(), "Failed to read value", Toast.LENGTH_SHORT).show();
            }

        };
        favLocationRef.addListenerForSingleValueEvent(valueEventListener);
        return view;
    }
}