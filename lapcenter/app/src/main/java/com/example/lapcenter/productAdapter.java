package com.example.lapcenter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class productAdapter extends RecyclerView.Adapter<productAdapter.LapDataViewHolder>{

    private List<LapData> lapList;
    Context maincontext;
    FirebaseUser isUser;
    String userName;
    
    Map<String, Object> cartItem = new HashMap<>();


    productAdapter( Context context,List<LapData> lapDataList,FirebaseUser user) {
        maincontext = context;
        lapList = lapDataList;
        isUser = user;
    }

    static class LapDataViewHolder extends RecyclerView.ViewHolder {
        TextView lapName;
        TextView description;
        TextView itemCount;
        TextView Price;
        TextView fulldescription;
        ImageView lapImage;
        Button buy, cart;

        ImageView subtract, add;
        int i = 1,addprice;
        TextView addedprice;
        String LapPrice;


        LapDataViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.product_view,parent,false));
            lapName = itemView.findViewById(R.id.brand);
            description = itemView.findViewById(R.id.description);
            Price = itemView.findViewById(R.id.price);
            lapImage = itemView.findViewById(R.id.mainImage);
            itemCount = itemView.findViewById(R.id.number);
            fulldescription = itemView.findViewById(R.id.fulldescription);
            buy = itemView.findViewById(R.id.buy);
            cart = itemView.findViewById(R.id.cart);
            subtract = itemView.findViewById(R.id.subtract);
            add = itemView.findViewById(R.id.add);
            addedprice = itemView.findViewById(R.id.addedprice);
        }
    }

    @NonNull
    @Override
    public LapDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new productAdapter.LapDataViewHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull LapDataViewHolder holder, int position) {
        LapData lapData = lapList.get(position);

        String imgUrl = lapData.image();
        holder.lapName.setText("Brand: "+lapData.lapName());
        Glide.with(maincontext)
                .load(imgUrl)
                .into(holder.lapImage);
        holder.Price.setText("$ "+lapData.price());
        holder.description.setText(lapData.lapdescription());
        holder.fulldescription.setText(lapData.fulldescription());

        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isUser == null){
                    Toast.makeText(maincontext,"Please Login", Toast.LENGTH_SHORT).show();
                }else{


                    if(holder.i == 1) {
                        holder.LapPrice = lapData.price();
                    }else{
                        holder.LapPrice =  String.valueOf(holder.addprice);
                    }

                    Intent intent = new Intent(maincontext, Checkout.class);
                    intent.putExtra("price", holder.LapPrice);
                    intent.putExtra("send","normal");
                    view.getContext().startActivity(intent);

                }
            }
        });

        holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isUser == null){
                    Toast.makeText(maincontext,"Please Login", Toast.LENGTH_SHORT).show();
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
                                        // Successful addition
                                        Toast.makeText(maincontext, "Successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Failed addition
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
                    cartItem.put("itemCount",holder.i);


                }
            }
        });


        //item Count
//            holder.itemCount.setText("2");
        holder.addprice = Integer.parseInt(lapData.price());
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.i >= 5 ){
                    Toast.makeText(maincontext, "Maximum Limit is 5",Toast.LENGTH_SHORT).show();
                } else {
                    holder.i = holder.i + 1;

                    holder.addprice = holder.addprice + Integer.parseInt(lapData.price());

                    holder.addedprice.setText("Total $ " + String.valueOf(holder.addprice));
                    holder.itemCount.setText(String.valueOf(holder.i));
                }

            }
        });

        holder.subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.i < 2 ){
                    holder.itemCount.setText(String.valueOf(holder.i));
                }else{
                    holder.addprice = holder.addprice - Integer.parseInt(lapData.price());
                    holder.i = holder.i - 1;


                    holder.itemCount.setText(String.valueOf(holder.i));

                    if(String.valueOf(holder.addprice).equals(lapData.price())){
                        holder.addedprice.setText("");
                    }else{
                        holder.addedprice.setText("Total $ " +String.valueOf(holder.addprice));
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return lapList.size();
    }
}
