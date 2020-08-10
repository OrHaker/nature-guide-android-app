package com.example.natureguide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,CustomAdapter.callback {
    private DrawerLayout drawer;
    private ListView mainActivityListView;

    private FirebaseAuth mAuth;
    private DatabaseReference favLocationRef, userRef;


    boolean doubleBackToExitPressedOnce = false;//click back twice to exit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolber);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainPage()).commit();
            navigationView.setCheckedItem(R.id.nav_main_page);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mAuth = FirebaseAuth.getInstance();

        final TextView headerUserName,headerUserEmail;

        headerUserName = ((TextView) headerView.findViewById(R.id.nav_header_userName));
        headerUserEmail = ((TextView) headerView.findViewById(R.id.nav_header_userEmail));



        //read from data base
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String userEmail = dataSnapshot.child("email").getValue(String.class);
                headerUserName.setText(firstName + " " + lastName);
                headerUserEmail.setText(userEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_Favorites:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Favorites()).commit();

                break;
            case R.id.nav_logOut:
                mAuth.signOut();
                startActivity(new Intent(this, LoginPage.class));
                break;
            case R.id.nav_main_page:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MainPage()).commit();
                break;
            case R.id.nav_add_location:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddLocation()).commit();
                break;
            case R.id.nav_maps:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MapsActivity()).commit();
                //startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EditProfileActivity()).commit();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //close app in two back button presses
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        }
        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //in 2 sec it will turn back doubleBackToExitPressedOnce flag off
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void setLocation(String description, String name, double l, double l1) {
        MapsActivity mapsActivity = new MapsActivity();
        mapsActivity.leng1 = l;
        mapsActivity.leng2 = l1;
        mapsActivity.name = name;
        mapsActivity.Description = description;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,mapsActivity).commit();
    }
}
