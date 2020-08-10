package com.example.natureguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MainPage extends Fragment {

    public ListView mainActivityListView;
    DatabaseReference favLocationRef;
    ArrayList<NatureLocation> nlList;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page,container,false);
        nlList = new ArrayList<>();
        //connect to list view
        favLocationRef = FirebaseDatabase.getInstance().getReference("Locations");

        mainActivityListView  = (ListView) view.findViewById(R.id.list_item);;


        //Read from the database
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    NatureLocation temp = ds.getValue(NatureLocation.class);
                    nlList.add(temp);
                    //CustomAdapter instance and connection to list view
                    final CustomAdapter adapter = new CustomAdapter(getActivity(), nlList.toArray(new NatureLocation[nlList.size()]),false);
                    adapter.setCallbackActivity(getActivity());
                    mainActivityListView.setAdapter(adapter);
                }

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
