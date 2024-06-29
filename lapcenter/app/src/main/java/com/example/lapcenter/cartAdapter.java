package com.example.lapcenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class cartAdapter extends RecyclerView.Adapter<cartAdapter.ViewHolder>{

    private List<LapData> lapList;
    Context maincontext;

    FirebaseUser isUser;


    cartAdapter( Context context,List<LapData> lapDataList, FirebaseUser user) {
        maincontext = context;
        lapList = lapDataList;
        isUser = user;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lapName,description,Price,count;
        ImageView lapImage;
        Button remove;
        ImageView minus,plus;


        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.cart_view,parent,false));
            lapName = itemView.findViewById(R.id.ImageText);
            description = itemView.findViewById(R.id.description);
            Price = itemView.findViewById(R.id.price);
            lapImage = itemView.findViewById(R.id.MainImage);
            remove = itemView.findViewById(R.id.btn);
            minus = itemView.findViewById(R.id.minus);
            plus = itemView.findViewById(R.id.plus);
            count = itemView.findViewById(R.id.count);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new cartAdapter.ViewHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull cartAdapter.ViewHolder holder, int position) {
        LapData lapData = lapList.get(position);
        String imgUrl = lapData.image();

        DatabaseReference cartCollectionRef = FirebaseDatabase.getInstance().getReference("cart");

        Query query = cartCollectionRef.orderByChild("Id").equalTo(lapData.id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot cartItemSnapshot : dataSnapshot.getChildren()) {
                    // Check the email condition
                    String email = cartItemSnapshot.child("userEmail").getValue(String.class);

                    if (isUser.getEmail().equals(email)) {
                        int count = cartItemSnapshot.child("itemCount").getValue(Integer.class);
                        holder.count.setText(Integer.toString(count));
                        //Toast.makeText(cart.this,Integer.toString(total),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CartSearch", "Error searching cart items", databaseError.toException());
            }
        });




        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(maincontext,isUser.getEmail(),Toast.LENGTH_SHORT).show();
                DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference("cart");

                Query query = cartReference.orderByChild("userEmail").equalTo(isUser.getEmail());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> updateData = new HashMap<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            int id = snapshot.child("Id").getValue(Integer.class);
                            int Count = snapshot.child("itemCount").getValue(Integer.class);
                            if (id == lapData.id()) {
                                if(Count >= 5 ){
                                    //Toast.makeText(maincontext, "Maximum Limit is 5",Toast.LENGTH_SHORT).show();
                                } else {
                                    Count += 1;
                                    updateData.put("itemCount", Count);
                                    int finalCount = Count;
                                    snapshot.getRef().updateChildren(updateData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    holder.count.setText(Integer.toString(finalCount));
//                                                    Intent intent = new Intent(maincontext, cart.class);
//                                                    view.getContext().startActivity(intent);
                                                }
                                            })

                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(maincontext, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    return;
                                }
                            }
                        }
                        //Toast.makeText(maincontext, "Error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the cancellation
                        Toast.makeText(maincontext, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(maincontext,isUser.getEmail(),Toast.LENGTH_SHORT).show();
                DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference("cart");

                Query query = cartReference.orderByChild("userEmail").equalTo(isUser.getEmail());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> updateData = new HashMap<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            int id = snapshot.child("Id").getValue(Integer.class);
                            int Count = snapshot.child("itemCount").getValue(Integer.class);
                            if (id == lapData.id()) {
                                if(Count <= 1){
                                    break;
                                }else {
                                    Count -= 1;
                                    updateData.put("itemCount", Count);
                                    int finalCount = Count;
                                    snapshot.getRef().updateChildren(updateData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    holder.count.setText(Integer.toString(finalCount));
//                                                    Intent intent = new Intent(maincontext, cart.class);
//                                                    view.getContext().startActivity(intent);
                                                }
                                            })

                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(maincontext, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    return;
                                }
                            }
                        }
                        //Toast.makeText(maincontext, "Error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the cancellation
                        Toast.makeText(maincontext, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        holder.lapName.setText(lapData.lapName());
        holder.description.setText(lapData.lapdescription());
        holder.Price.setText("$ "+lapData.price());
        Glide.with(maincontext)
                .load(imgUrl)
                .into(holder.lapImage);


        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(maincontext,isUser.getEmail(),Toast.LENGTH_SHORT).show();
                DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference("cart");

                Query query = cartReference.orderByChild("userEmail").equalTo(isUser.getEmail());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            int id = snapshot.child("Id").getValue(Integer.class);
                            if (id == lapData.id()) {

                                snapshot.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(maincontext, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(maincontext, cart.class);
                                                view.getContext().startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(maincontext, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                return;
                            }
                        }

                        // Id not found
                        Toast.makeText(maincontext, "Item not found", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the cancellation
                        Toast.makeText(maincontext, "Error", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Lapid = String.valueOf(lapData.id());
//                Toast.makeText(maincontext, Lapid, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext() , productMain.class);
                intent.putExtra("id", Lapid);
                view.getContext().startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {

        return lapList.size();
    }
}
