package com.example.natureguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class AddLocation extends Fragment {
    EditText name, title, description, img;
    double lat, lng;
    NatureLocation nl;
    //data base ref
    FirebaseDatabase database;
    DatabaseReference userRef, myRef;
    //firebase auth user
    private FirebaseAuth mAuth;
    //storage
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE = 1;
    ImageView imageView;
    FirebaseStorage storage;
    Uri imageUri;
    StorageReference storageReference;
    Bitmap imageBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_location, container, false);
        name = view.findViewById(R.id.AddName);
        title = view.findViewById(R.id.AddTitle);
        description = view.findViewById(R.id.AddDescription);
        img = view.findViewById(R.id.AddImage);
        imageView = view.findViewById(R.id.AddLocation);
        //firebase
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        imageBitmap = null;


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    Intent gallery = new Intent();
                    gallery.setType("image/*");
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
                } else {
                    requestPermission();
                }
            }
        });
        (view.findViewById(R.id.AddBtn)).setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (name.getText().toString().equals("") || title.getText().toString().equals("") || description.getText().toString().equals("") || img.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "שם לב שיש שדה ריק ולכן המיקום לא עלה", Toast.LENGTH_LONG).show();
                        return;
                    }
                    nl = new NatureLocation(name.getText().toString(), title.getText().toString(),
                            description.getText().toString(), img.getText().toString(), lat, lng
                    );
                    Toast.makeText(getActivity(), nl.toString(), Toast.LENGTH_LONG).show();
                    myRef = database.getReference("Locations").child(name.getText().toString());
                    myRef.setValue(nl);
                    uploadImagetoServer();
                    ((FragmentActivity) (getContext())).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainPage()).commit();
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), "OPPS..\nSomething went wrong", Toast.LENGTH_LONG).show();
                }
            }

        }));
        LocationManager locationManager;

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (gps_loc != null) {
            double latitude = gps_loc.getLatitude();
            double longitude = gps_loc.getLongitude();
            lat = latitude;
            lng = longitude;
        } else {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (gps_loc != null) {
                double latitude = gps_loc.getLatitude();
                double longitude = gps_loc.getLongitude();
                lat = latitude;
                lng = longitude;
            }
        }
        return view;
    }

    private void uploadImagetoServer() {
        if (imageUri != null) {
            //StorageReference ref = storageReference.child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".png");
            StorageReference ref = storageReference.child("images/" + img.getText() + ".png");
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Toast.makeText(SignActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}