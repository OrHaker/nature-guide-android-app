package com.example.natureguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends Fragment {

    //fields
    EditText userEmail, userPass, userFirstname, userLastname;
    Button btnRegister;
    //firebase auth
    private FirebaseAuth mAuth;
    //real time data base
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private User userDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_profile, container, false);
        userDetails = new User();
        //connect object to views
        //user info
        userEmail = view.findViewById(R.id.EditUserEmail);
        userEmail.setFocusable(false);
        userPass = view.findViewById(R.id.EditUserPassword);
        userFirstname = view.findViewById(R.id.EditUserFirst);
        userLastname = view.findViewById(R.id.EditUserLastname);
        //buttons
        btnRegister = view.findViewById(R.id.EditBtnUpdate);
        //firebase
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(mAuth.getUid());

        //Read from the database
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userDetails = dataSnapshot.getValue(User.class);
                userEmail.setText(userDetails.getEmail());
                userPass.setText(userDetails.getPassword());
                userFirstname.setText(userDetails.getFirstName());
                userLastname.setText(userDetails.getLastName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Toast.makeText(getActivity(), "Failed to read value", Toast.LENGTH_SHORT).show();
            }

        };
        myRef.addListenerForSingleValueEvent(valueEventListener);

        //Update
        (view.findViewById(R.id.EditBtnUpdate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = mAuth.getCurrentUser();
                //write on data base at Users section the new user under user uid object
                myRef = database.getReference("Users").child(mAuth.getUid());

                user.updatePassword(userPass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    User temp = new User(userFirstname.getText().toString(), userLastname.getText().toString(), userEmail.getText().toString(), userPass.getText().toString());
                                    myRef.setValue(temp);
                                    updateUI(user);
                                } else
                                    Toast.makeText(getActivity(), "Failed to update Auth pass", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        return view;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(getActivity(), MainActivity.class));
        } else {
            Toast.makeText(getActivity(), "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }


}
