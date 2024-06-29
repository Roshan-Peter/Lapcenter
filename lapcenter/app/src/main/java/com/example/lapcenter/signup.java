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
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    EditText email,name,number,postal,address;
    EditText Password;
    Button btn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView error,login;
    String PostalCode = "^[A-Za-z]\\d[A-Za-z] \\d[A-Za-z]\\d$";
    String phoneNumber= "^[0-9]{10}$";
    Map<String, Object> userData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        btn = findViewById(R.id.signup);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbar);
        error = findViewById(R.id.err);
        name = findViewById(R.id.name);
        number = findViewById(R.id.phone);
        postal = findViewById(R.id.postal);
        address = findViewById(R.id.address);
        login = findViewById(R.id.Login);

        email.setPadding(15,0,0,0);
        Password.setPadding(15,0,0,0);
        name.setPadding(15,0,0,0);
        number.setPadding(15,0,0,0);
        postal.setPadding(15,0,0,0);
        address.setPadding(15,0,0,0);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup.this,login.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            progressBar.setVisibility(View.VISIBLE);
            String useremail, userpassword,username,usernumber,Zip,Addr;
            useremail = email.getText().toString();
            userpassword = Password.getText().toString();
            username = name.getText().toString();
            usernumber = number.getText().toString();
            Zip = postal.getText().toString();
            Addr = address.getText().toString();


            if(TextUtils.isEmpty(useremail))   {
//                Toast.makeText(signup.this,"Enter Email", Toast.LENGTH_SHORT).show();
                error.setVisibility(View.VISIBLE);
                error.setText("Enter a Valid Email and Password");
                progressBar.setVisibility(View.GONE);
                return;
            }else if(TextUtils.isEmpty(userpassword)){
//                    Toast.makeText(signup.this,"Enter Password", Toast.LENGTH_SHORT).show();
                    error.setVisibility(View.VISIBLE);
                    error.setText("Enter a Valid Email and Password");
                    progressBar.setVisibility(View.GONE);
                    return;
            } else if (TextUtils.isEmpty(username)) {
                error.setVisibility(View.VISIBLE);
                error.setText("Enter a Valid Name");
                progressBar.setVisibility(View.GONE);
                return;
            } else if (TextUtils.isEmpty(usernumber)) {
                error.setVisibility(View.VISIBLE);
                error.setText("Enter a Valid Number");
                progressBar.setVisibility(View.GONE);
                return;
            } else if (!usernumber.matches(phoneNumber)) {
                error.setVisibility(View.VISIBLE);
                error.setText("Enter a Valid Number");
                progressBar.setVisibility(View.GONE);
                return;
            } else if (!Zip.matches(PostalCode)) {
                error.setVisibility(View.VISIBLE);
                error.setText("Enter a Valid Postal Code");
                progressBar.setVisibility(View.GONE);
                return;
            }
            else {


                mAuth.createUserWithEmailAndPassword(useremail, userpassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                error.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    DatabaseReference userInsert = FirebaseDatabase.getInstance().getReference("user");

                                    String userKey = userInsert.push().getKey();
                                    DatabaseReference Insert = userInsert.child(userKey);

                                    userData.put("userName",username);
                                    userData.put("userEmail",useremail);
                                    userData.put("phoneNumber",usernumber);
                                    userData.put("address",Addr);
                                    userData.put("postal",Zip);

                                    Insert.setValue(userData);
                                    Toast.makeText(signup.this, "Registration Successful.", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(signup.this, Home.class);
                                    startActivity(intent);


                                } else {
//                                    Toast.makeText(signup.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                    error.setVisibility(View.VISIBLE);
                                    error.setText("User Registration Failed");
                                }
                            }
                        });


            }

            }
        });


    }
}