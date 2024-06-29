package com.example.lapcenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    ImageView logo;
    EditText email;
    EditText Password;
    Button btn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView error,register;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(login.this, Home.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logo = findViewById(R.id.logo);
        email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        btn = findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbar);
        error = findViewById(R.id.error);
        register = findViewById(R.id.register);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });



        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, Home.class);
                startActivity(intent);
            }
        });


        email.setPadding(15,0,0,0);
        Password.setPadding(15,0,0,0);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressBar.setVisibility(View.VISIBLE);
                String useremail, userpassword;
                useremail = email.getText().toString();
                userpassword = Password.getText().toString();

                if(TextUtils.isEmpty(useremail))  {
                    Toast.makeText(login.this,"Enter Email", Toast.LENGTH_SHORT).show();
                    error.setVisibility(View.VISIBLE);
                    error.setText("Invalid Input");
                    progressBar.setVisibility(View.GONE);
                    return;
                }if(TextUtils.isEmpty(userpassword)){
                    Toast.makeText(login.this,"Enter Password", Toast.LENGTH_SHORT).show();
                    error.setVisibility(View.VISIBLE);
                    error.setText("Invalid Input");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(useremail, userpassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                error.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(login.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(login.this, Home.class);
                                    startActivity(intent);
                                } else {
//                                    Toast.makeText(login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    error.setVisibility(View.VISIBLE);
                                    error.setText("Invalid Input");
                                }
                            }
                        });

            }
        });

    }
}