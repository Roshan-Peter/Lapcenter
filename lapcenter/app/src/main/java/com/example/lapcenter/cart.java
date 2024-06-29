package com.example.lapcenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cart extends AppCompatActivity {

    ImageView logo;
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<LapData> myDataset = new ArrayList<>();
    FirebaseUser user;
    FirebaseAuth auth;
    List<String> cartId = new ArrayList<>();
    FirebaseFirestore firebase;
    TextView tprice;
    Button order;
    LinearLayout line;

    int totalPrice = 0;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        logo = findViewById(R.id.logo);
        recyclerView = findViewById(R.id.cart);
        layoutManager = new LinearLayoutManager(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebase = FirebaseFirestore.getInstance();
        //CollectionReference cartCollection = firebase.collection("cart");
        tprice = findViewById(R.id.tprice);
        order = findViewById(R.id.order);
        line = findViewById(R.id.line);



        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(cart.this, Home.class);
                startActivity(intent);
            }
        });

        if(user != null) {


            DatabaseReference cart = FirebaseDatabase.getInstance().getReference("cart");


            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



            cart.orderByChild("userEmail").equalTo(user.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            myDataset.clear();
                            totalPrice = 0;

                            for (DataSnapshot document : dataSnapshot.getChildren()) {
                                Long ID = document.child("Id").getValue(Long.class);

                                cartId.add(String.valueOf(ID));

                                DatabaseReference laptopReference = FirebaseDatabase.getInstance().getReference("laptop");

                                laptopReference.orderByChild("id").equalTo(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot laptop : snapshot.getChildren()) {
                                            if (laptop.hasChild("name") && laptop.hasChild("description") && laptop.hasChild("image") && laptop.hasChild("fulldescription") && laptop.hasChild("price")) {
                                                int id = laptop.child("id").getValue(Integer.class);
                                                String brand = laptop.child("name").getValue(String.class);
                                                String description = laptop.child("description").getValue(String.class);
                                                String image = laptop.child("image").getValue(String.class);
                                                String fulldescription = laptop.child("fulldescription").getValue(String.class);
                                                String fullprice = laptop.child("price").getValue(String.class);

                                                myDataset.add(new LapData(id, brand, description, fullprice, image, fulldescription));

                                                //totalPrice += Integer.parseInt(fullprice);



                                                DatabaseReference cartCollectionRef = FirebaseDatabase.getInstance().getReference("cart");

                                                Query query = cartCollectionRef.orderByChild("Id").equalTo(ID);
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot cartItemSnapshot : dataSnapshot.getChildren()) {
                                                            // Check the email condition
                                                            String email = cartItemSnapshot.child("userEmail").getValue(String.class);

                                                            if (user.getEmail().equals(email)) {
                                                                int count = cartItemSnapshot.child("itemCount").getValue(Integer.class);
                                                                int total = Integer.parseInt(fullprice) * count;
                                                                totalPrice += total;

                                                                //Toast.makeText(cart.this,Integer.toString(total),Toast.LENGTH_SHORT).show();

                                                            }
                                                            tprice.setText("Total Price:  $ " + totalPrice);
                                                            tprice.setVisibility(View.VISIBLE);
                                                            order.setVisibility(View.VISIBLE);
                                                            line.setVisibility(View.VISIBLE);

                                                            order.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    Intent intent = new Intent(cart.this,Checkout.class);
                                                                    intent.putExtra("price", Integer.toString(totalPrice));
                                                                    intent.putExtra("send","cart");
                                                                    startActivity(intent);
                                                                }
                                                            });

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        //Log.e("CartSearch", "Error searching cart items", databaseError.toException());
                                                    }
                                                });



                                            }
                                        }

                                        if (cartId.size() == myDataset.size()) {
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(cart.this, "Error in laptop query!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(cart.this, "Error in cart query!!", Toast.LENGTH_SHORT).show();
                        }
                    });




                    }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };

            cart.addValueEventListener(valueEventListener);


        }else{
            Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
        }


        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new cartAdapter(cart.this, myDataset,user);
        recyclerView.setAdapter(mAdapter);
    }
}