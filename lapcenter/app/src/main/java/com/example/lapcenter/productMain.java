package com.example.lapcenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class productMain extends AppCompatActivity {

    TextView login, signup;
    ImageView logo,cart;
    RecyclerView recyclerView;
    List<LapData> myDataset = new ArrayList<>();
    RecyclerView.Adapter pAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_main);


        recyclerView = findViewById(R.id.productdisplay);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        logo = findViewById(R.id.logo);
        cart = findViewById(R.id.cart);
        layoutManager = new LinearLayoutManager(this);


        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(productMain.this, Home.class);
                startActivity(intent);
            }
        });


        if(user == null){
            login.setText("Login");
            signup.setText("Sign Up");

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(productMain.this, login.class);
                    startActivity(intent);
                }
            });

            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(productMain.this, signup.class);
                    startActivity(intent);
                }
            });

            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(productMain.this,"Please Login", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            login.setText("Logout");
            signup.setText("");

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(productMain.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(productMain.this,cart.class);
                    startActivity(intent);
                }
            });
        }




        Intent Lid = getIntent();
        String LapId = Lid.getStringExtra("id");

//        Toast.makeText(productMain.this, LapId, Toast.LENGTH_SHORT).show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myDataset.clear();
                for(DataSnapshot laptop: snapshot.child("laptop").getChildren()){


                    if(laptop.hasChild("name") && laptop.hasChild("description") && laptop.hasChild("image") && laptop.hasChild("fulldescription") && laptop.hasChild("price") && laptop.hasChild("id")){


                        int id = laptop.child("id").getValue(int.class);

                        String laid = String.valueOf(id);

                        if(LapId.equals(laid)){
                            String brand = laptop.child("name").getValue(String.class);
                            String description = laptop.child("description").getValue(String.class);
                            String image = laptop.child("image").getValue(String.class);
                            String fulldescription = laptop.child("fulldescription").getValue(String.class);
                            String fullprice = laptop.child("price").getValue(String.class);
                            myDataset.add(new LapData(id, brand,description, fullprice, image,fulldescription));


                            recyclerView.setLayoutManager(layoutManager);
                            pAdapter = new productAdapter(productMain.this,myDataset,user);
                            recyclerView.setAdapter(pAdapter);
                        }



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        

    }
}