package com.example.lapcenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.Objects;

public class Checkout extends AppCompatActivity {

    EditText name,email,number,zip,address,card,mmyy,cvv;
    TextView price;
    FirebaseAuth auth;
    FirebaseUser user;
    String Cuser,type="Nornal";
    CheckBox debit,credit;
    Button order;
    ImageView logo;
    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference();

    String phoneNumber= "^[0-9]{10}$";
    String PostalCode = "^[A-Za-z]\\d[A-Za-z] \\d[A-Za-z]\\d$";
    String CardNumber = "^(\\d{4}\\s?){3}\\d{4}$";
    String cardExpiry = "^(0[1-9]|1[0-2])(\\d{2})$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        zip = findViewById(R.id.zip);
        address = findViewById(R.id.address);
        debit = findViewById(R.id.Debit);
        credit = findViewById(R.id.credit);
        order = findViewById(R.id.order);
        card = findViewById(R.id.card);
        mmyy = findViewById(R.id.mmyy);
        cvv = findViewById(R.id.cvv);
        logo = findViewById(R.id.logo);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Cuser = user.getEmail();


        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Checkout.this,Home.class);
                startActivity(intent);
            }
        });


        credit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                debit.setChecked(false);
            }
        });

        debit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                credit.setChecked(false);
            }
        });



        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot user: snapshot.child("user").getChildren()){
                        String email = user.child("userEmail").getValue(String.class);

                        if(Cuser.equals(email)){
                            String Name = user.child("userName").getValue(String.class);
                            String Number = user.child("phoneNumber").getValue(String.class);
                            String Zip = user.child("postal").getValue(String.class);
                            String Address = user.child("address").getValue(String.class);

                            if(!Objects.equals(Zip, "")){
                                zip.setText(Zip);
                            }

                            if(!Objects.equals(Address, "")){
                                address.setText(Address);
                            }

                            name.setText(Name);
                            number.setText(Number);



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Intent LPrice = getIntent();
        String LapPrice = LPrice.getStringExtra("price");
        type = LPrice.getStringExtra("send");
        price.setText("Total Amount $ "+LapPrice);
        email.setText(user.getEmail());




        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Name = name.getText().toString().trim();
                String Email = name.getText().toString().trim();
                String Number = number.getText().toString().trim();
                String Zip = zip.getText().toString().trim();
                String Card = card.getText().toString().trim();
                String MmYy = mmyy.getText().toString().trim();
                String Cvv = cvv.getText().toString().trim();

                if(Name.equals(null) || Name.equals("")){
                    Toast.makeText(Checkout.this,"Invalid Details",Toast.LENGTH_SHORT).show();
                } else if (Email.equals(null)) {
                    Toast.makeText(Checkout.this,"Invalid Details",Toast.LENGTH_SHORT).show();
                } else if (!Number.matches(phoneNumber)) {
                    Toast.makeText(Checkout.this,"Invalid Number",Toast.LENGTH_SHORT).show();
                } else if (!Zip.matches(PostalCode)) {
                    Toast.makeText(Checkout.this,"Invalid Postal Code",Toast.LENGTH_SHORT).show();
                } else if (!debit.isChecked() && !credit.isChecked()) {
                    Toast.makeText(Checkout.this,"Invalid Card Type",Toast.LENGTH_SHORT).show();
                } else if (!Card.matches(CardNumber)) {
                    Toast.makeText(Checkout.this,"Invalid Card Number",Toast.LENGTH_SHORT).show();
                } else if (!MmYy.matches(cardExpiry)) {
                    Toast.makeText(Checkout.this,"Invalid Card Expiry",Toast.LENGTH_SHORT).show();
                } else if (!Cvv.matches("[0-9]{3}")) {
                    Toast.makeText(Checkout.this,"Invalid Security Code",Toast.LENGTH_SHORT).show();
                } else{

                    if(type != null){

                        if(type.equals("cart")){

                            DatabaseReference cartdel = FirebaseDatabase.getInstance().getReference("cart");
                            Query query = cartdel.orderByChild("userEmail").equalTo(user.getEmail());

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        // Delete each node found
                                        snapshot.getRef().removeValue();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(Checkout.this, "Error deleting data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }


                        Intent intent = new Intent(Checkout.this,Final.class);
                        startActivity(intent);

                }


            }
        });



    }
}