package com.example.natureguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CustomAdapter extends BaseAdapter {
    //CUSTOM ADAPTER (LIST VIEW)מובנה משמש לחיבור בין ה רשימה לכל פריט ברשימה
    NatureLocation[] locationArr;
    static LayoutInflater inflater;  //for inflate layout items
    private GoogleMap mMap;
    //data base ref
    FirebaseDatabase database;
    DatabaseReference favLocationRef, userRef, locationsRef;
    //firebase auth user
    private FirebaseAuth mAuth;
    ArrayList<NatureLocation> nlList;
    ArrayList<String> namesLike;
    //flag that uses to check if the context is in Favorites or Main
    private boolean isOnFav;
    private StorageReference firebaseStorage;
    private Bitmap imageBitmap;
    public callback callback;


    ImageView img_whatsapp;
    ImageView img_like;

    public void setCallbackActivity(Activity callback) {
        this.callback = (callback) callback;
    }

    public interface callback {
        public void setLocation(String description, String name, double l, double l1);
    }

    public CustomAdapter(Context context, NatureLocation arr[], boolean isOnFav) {
        this.locationArr = arr;
        this.isOnFav = isOnFav;
        inflater = (LayoutInflater.from(context));
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance().getReference().child("images");
        favLocationRef = database.getReference("Users").child(mAuth.getUid()).child("favLocation");
        userRef = database.getReference("Users").child(mAuth.getUid());
        locationsRef = database.getReference("Locations");
        this.nlList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return locationArr.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.favorites_list_view_item, viewGroup, false);
        //favOnClick
        final Button btnAddToFavorites = (Button) view.findViewById(R.id.btnAddToFavorites);
        btnAddToFavorites.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Read from the database
                nlList = new ArrayList<>();
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            NatureLocation temp = ds.getValue(NatureLocation.class);
                            nlList.add(temp);
                        }

                        if (!isOnFav) {
                            if (!nlList.contains(locationArr[position])) {
                                nlList.add(locationArr[position]);
                                favLocationRef.setValue(nlList);
                            } else {
                                Toast.makeText(inflater.getContext(), "המיקום כבר במועדפים", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            nlList.remove(locationArr[position]);
                            favLocationRef.setValue(nlList);
                            Toast.makeText(inflater.getContext(), locationArr[position].getTitle() + " הוסר מהמועדפים", Toast.LENGTH_SHORT).show();
                            ((FragmentActivity) inflater.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Favorites()).commit();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Failed to read value
                        Toast.makeText(inflater.getContext(), "Failed to read value", Toast.LENGTH_SHORT).show();
                    }
                };
                favLocationRef.addListenerForSingleValueEvent(valueEventListener);
            }
        });
        //btnWhatsappOnClick

        //btnOpenInMapsOnClick
        Button btnOpenInMaps = (Button) view.findViewById(R.id.btnOpenInMaps);
        btnOpenInMaps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                callback.setLocation(locationArr[position].getDescription(), locationArr[position].getName(), locationArr[position].getLatLangv(), locationArr[position].getLatLangv1());

            }
        });
        //set a title to button if we are on favorites activity
        if (isOnFav)
            btnAddToFavorites.setText("הסר מהמועדפים");
        final ImageView imgCountry = view.findViewById(R.id.img_location);
        TextView txtName = view.findViewById(R.id.txt_title);
        TextView txtDescription = view.findViewById(R.id.txt_description);
        final TextView txtLike = view.findViewById(R.id.txt_like);

        try {
            final File localFile = File.createTempFile("images", "png");
            StorageReference ref = FirebaseStorage.getInstance()
                    .getReference()
                    .child("images/" + locationArr[position].getImage() + ".png");
            ref.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            imageBitmap = BitmapFactory.decodeFile(localFile.getPath());
                            imgCountry.setImageBitmap(imageBitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
        txtName.setText(locationArr[position].getTitle());
        txtDescription.setText(locationArr[position].getDescription());
        if (locationArr[position].getListLike() != null) {
            txtLike.setText(locationArr[position].getListLike().size() + " אוהבים את זה");
        } else {
            txtLike.setText(0 + " אוהבים את זה");
        }
        img_whatsapp = view.findViewById(R.id.img_whatsapp);
        img_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = inflater.getContext().getPackageManager();
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String string = locationArr[position].getName() + "\n" + locationArr[position].getDescription();
                    PackageInfo packageInfo = packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    intent.setPackage("com.whatsapp");
                    intent.putExtra(intent.EXTRA_TEXT, string);
                    v.getContext().startActivity(intent);


                } catch (PackageManager.NameNotFoundException E) {
                    Toast.makeText(inflater.getContext().getApplicationContext(), "whatsapp not found !!", Toast.LENGTH_LONG).show();
                }
            }
        });


        img_like = view.findViewById(R.id.img_like);
        img_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener valueEventListener = new ValueEventListener() {
                    ArrayList<NatureLocation> tempList = new ArrayList<NatureLocation>();

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            NatureLocation temp = ds.getValue(NatureLocation.class);
                            tempList.add(temp);
                        }
                        NatureLocation clicked = null;
                        for (NatureLocation n : tempList)
                            if (n.equals(locationArr[position]))
                                clicked = n;

                        if (clicked != null) {
                            if (!(clicked.getListLike().contains(mAuth.getUid()))) {
                                clicked.getListLike().add(mAuth.getUid());
                                locationsRef.setValue(tempList);
                                txtLike.setText(tempList.size() + " אוהבים את זה ");
                            } else {
                                Toast.makeText(inflater.getContext(), "לא ניתן לתת לייק לאותו מקום יותר מפעם אחת", Toast.LENGTH_SHORT).show();
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Failed to read value
                        Toast.makeText(inflater.getContext(), "Failed to read value", Toast.LENGTH_SHORT).show();
                    }
                };
                locationsRef.addListenerForSingleValueEvent(valueEventListener);
            }
        });
        if (isOnFav)
            img_like.setVisibility(View.INVISIBLE);
        return view;
    }
}
