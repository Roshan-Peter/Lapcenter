package com.example.lapcenter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.LapDataViewHolder> {
    private List<LapData> lapList;
    Context maincontext;
    FirebaseUser isUser;

    FirebaseDatabase firebaseM;
    String userName;
    FirebaseFirestore firestore;
    Map<String, Object> cartItem = new HashMap<>();



    MainAdapter( Context context,List<LapData> lapDataList, FirebaseUser user, FirebaseDatabase firebase) {
        maincontext = context;

        lapList = lapDataList;
        isUser = user;
        firebaseM = firebase;

    }

    static class LapDataViewHolder extends RecyclerView.ViewHolder {
        TextView lapName;
        TextView description;
        TextView Price;
        ImageView lapImage;

        Button buy, cart;


        LapDataViewHolder( LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.home_products,parent,false));
            lapName = itemView.findViewById(R.id.ImageText);
            description = itemView.findViewById(R.id.description);
            Price = itemView.findViewById(R.id.price);
            lapImage = itemView.findViewById(R.id.MainImage);
            buy = itemView.findViewById(R.id.buynow);
            cart = itemView.findViewById(R.id.btn);
        }
    }


    @Override
    public LapDataViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new LapDataViewHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(LapDataViewHolder holder, int position) {
        LapData lapData = lapList.get(position);

        String imgUrl = lapData.image();

        holder.lapName.setText(lapData.lapName());
        holder.description.setText(lapData.lapdescription());
        holder.Price.setText("$ "+lapData.price());
        Glide.with(maincontext)
                        .load(imgUrl)
                        .into(holder.lapImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Lapid = String.valueOf(lapData.id());
//                Toast.makeText(maincontext, Lapid, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext() , productMain.class);
                intent.putExtra("id", Lapid);
                intent.putExtra("send","normal");
                view.getContext().startActivity(intent);
            }
        });


        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isUser == null){
                    Toast.makeText(maincontext,"Please Login",Toast.LENGTH_SHORT).show();
                }else{
                    String LapPrice = lapData.price();
                    Intent intent = new Intent(maincontext, Checkout.class);
                    intent.putExtra("price", LapPrice);
                    view.getContext().startActivity(intent);
                }
            }
        });

        holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isUser == null){
                    Toast.makeText(maincontext,"Please Login",Toast.LENGTH_SHORT).show();
                }else{
                    userName = isUser.getEmail();
                    int id = lapData.id();



                    DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference("cart");

                    Query query = cartReference.orderByChild("Id").equalTo(id);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String userEmail = snapshot.child("userEmail").getValue(String.class);
                                if (userEmail != null && userEmail.equals(userName)) {
                                    Toast.makeText(maincontext, "Already Exists", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            cartReference.push().setValue(cartItem, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null) {
                                        Toast.makeText(maincontext, "Successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(maincontext, "Fail", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                    cartItem.put("Id",lapData.id());
                    cartItem.put("userEmail",userName);
                    cartItem.put("itemCount",1);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return lapList.size();
    }
}
