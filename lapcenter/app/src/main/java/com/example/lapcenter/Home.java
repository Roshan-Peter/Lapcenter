package com.example.lapcenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    TextView login;
    TextView signup;
    ImageView cart;
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<LapData> myDataset = new ArrayList<>();

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FirebaseDatabase firebase = FirebaseDatabase.getInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        cart = findViewById(R.id.cart);
        recyclerView = findViewById(R.id.mainView);
        layoutManager = new LinearLayoutManager(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user == null){
            login.setText("Login");
            signup.setText("Sign Up");

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Home.this, login.class);
                    startActivity(intent);
                }
            });

            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Home.this, signup.class);
                    startActivity(intent);
                }
            });

            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   Toast.makeText(Home.this,"Please Login", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            login.setText("Logout");
            signup.setText("");



            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    builder.setTitle("Confirm")
                            .setMessage("Are you sure you want to logout?")
                            .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(Home.this, MainActivity.class);
                                    startActivity(intent);
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            });

            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Home.this, cart.class);
                    startActivity(intent);
                }
            });
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myDataset.clear();
                for(DataSnapshot laptop: snapshot.child("laptop").getChildren()){


                    if(laptop.hasChild("name") && laptop.hasChild("description") && laptop.hasChild("image") && laptop.hasChild("fulldescription") && laptop.hasChild("price") && laptop.hasChild("id")){

                        String brand = laptop.child("name").getValue(String.class);
                        String description = laptop.child("description").getValue(String.class);
                        String image = laptop.child("image").getValue(String.class);
                        String fulldescription = laptop.child("fulldescription").getValue(String.class);
                        String fullprice = laptop.child("price").getValue(String.class);
                        int id = laptop.child("id").getValue(int.class);

                        myDataset.add(new LapData(id, brand,description, fullprice, image,fulldescription));

                        mAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MainAdapter(Home.this,myDataset,user,firebase);
        recyclerView.setAdapter(mAdapter);


    }
}