package com.example.mychapapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychapapp.R;
import com.example.mychapapp.activities.ChatActivity;
import com.example.mychapapp.activities.DashBoardActivity;
import com.example.mychapapp.models.UserDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class UserDetailAdapter extends RecyclerView.Adapter<UserDetailAdapter.Myholder> {

    private ArrayList<UserDetails> lists;
    private Context context;

    DatabaseReference databaseReference,userRef;

    public UserDetailAdapter(ArrayList<UserDetails> lists, Context context) {
        this.lists = lists;
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        userRef = FirebaseDatabase.getInstance().getReference("users");





    }


    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custumchats, parent, false);
        return new Myholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        UserDetails data = lists.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + data.getUniqueID();

// databaseReference.child(senderRoom).addListenerForSingleValueEvent(new ValueEventListener() {
//     @Override
//     public void onDataChange(@NonNull DataSnapshot snapshot) {
//         if(snapshot.exists())
//         {
//             if (snapshot.exists()) {
//
//
//
//                                String lastMsg = snapshot.child("lastMessege").getValue(String.class);
//
//                                long lastmesstime = snapshot.child("lastMessegeTime").getValue(Long.class);
//                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
//                                holder.time.setText(dateFormat.format(lastmesstime));
//
//                                holder.lastMessage.setText(lastMsg);
//
//
//                            }
//
//                        }
//                        else
//                        {
//                            holder.lastMessage.setText("tap to chat");
//
//
//                        }
//
//     }
//
//     @Override
//     public void onCancelled(@NonNull DatabaseError error) {
//
//     }
// });
        databaseReference
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {

//                            if (snapshot.child("lastMessegeTime").equals(null)) {


                                String lastMsg = snapshot.child("lastMessege").getValue(String.class);

                                long lastmesstime = snapshot.child("lastMessegeTime").getValue(Long.class);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                holder.time.setText(dateFormat.format(lastmesstime));

                                holder.lastMessage.setText(lastMsg);


                            }

//                        }
                            else
                            {
                                holder.lastMessage.setText("tap to chat");
                            holder.time.setText("00:00");


                            }
                        }
                        catch (Exception e )
                        {

                            Log.d("Exception",e.getLocalizedMessage());
                        }
                        finally {

                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.name.setText(data.getName());
        Glide.with(context).load(data.getProfileIMage())
                .placeholder(R.drawable.profile)
                .into(holder.imageView);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {







                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", data.getName());
                intent.putExtra("imageProfile",data.getProfileIMage());
                intent.putExtra("Uuid", data.getUniqueID());


              context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class Myholder extends RecyclerView.ViewHolder {


        TextView name, lastMessage, time;
        ImageView imageView;

        public Myholder(@NonNull View itemView) {
            super(itemView);


            name = itemView.findViewById(R.id.myName);
            imageView = itemView.findViewById(R.id.circleimage);


            lastMessage = itemView.findViewById(R.id.myMessege);
            time = itemView.findViewById(R.id.timee);


        }
    }

}
