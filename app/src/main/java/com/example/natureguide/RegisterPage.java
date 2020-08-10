package com.example.natureguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPage extends AppCompatActivity {

    //fields
    EditText userEmail, userPass1, userPass2, userFirstname, userLastname;//userPass1 and userPass2 exist to check pass identity
    Button btnRegister;
    //firebase auth
    private FirebaseAuth mAuth;
    //real time data base
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //connect object to views
        //user info
        userEmail = findViewById(R.id.RegisterUserEmail);
        userPass1 = findViewById(R.id.RegisterUserPassword1);
        userPass2 = findViewById(R.id.RegisterUserPassword2);
        userFirstname = findViewById(R.id.RegisterUserFirstname);
        userLastname = findViewById(R.id.RegisterUserLastname);
        //buttons
        btnRegister = findViewById(R.id.RegisterBtnRegister);
        //firebase
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();

        //Registration
        (findViewById(R.id.RegisterBtnRegister)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checking if first password field matches the second
                if (userPass1.getText().toString() != userPass2.getText().toString())

                    mAuth.createUserWithEmailAndPassword(userEmail.getText().toString(), userPass1.getText().toString())
                            .addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        //write data about the user on our data base
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        //write on data base at Users section the new user under user uid object
                                        myRef = database.getReference("Users").child(user.getUid());
                                        User temp = new User(userFirstname.getText().toString(), userLastname.getText().toString(), userEmail.getText().toString(), userPass1.getText().toString());
                                        myRef.setValue(temp);

                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        updateUI(null);
                                    }

                                    // ...
                                }
                            });
            }
        });

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(RegisterPage.this, MainActivity.class));

        } else {
            Toast.makeText(RegisterPage.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
