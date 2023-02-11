package com.example.mychapapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychapapp.R;
import com.example.mychapapp.databinding.DeletemessegesBinding;
import com.example.mychapapp.databinding.ReceiveMessegeBinding;
import com.example.mychapapp.databinding.ReceiveMessegeGroupBinding;
import com.example.mychapapp.databinding.SendMessegeBinding;
import com.example.mychapapp.databinding.SendMessegeGroupBinding;
import com.example.mychapapp.models.MessegeDetails;
import com.example.mychapapp.models.UserDetails;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class GroupMessegeAdapter extends RecyclerView.Adapter {

    private Context context;
    ArrayList<MessegeDetails> list;
    final int sendItem = 1;
    final int receiveitem = 2;
    String senderRoom;
    String receiverRoom;

    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;


    public GroupMessegeAdapter(Context context, ArrayList<MessegeDetails> list) {
        this.context = context;
        this.list = list;

        databaseReference = FirebaseDatabase.getInstance().getReference("public");
//        firebaseStorage = FirebaseDatabase.getInstance().getReference("public");


    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == sendItem) {
            View view = LayoutInflater.from(context).inflate(R.layout.send_messege_group, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receive_messege_group, parent, false);
            return new ReceiceViewHolder(view);
        }

    }


    @Override
    public int getItemViewType(int position) {


        MessegeDetails messegeDetails = list.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(messegeDetails.getSenderId())) {
            return sendItem;
        } else {
            return receiveitem;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessegeDetails messegeDetails = list.get(position);

        int reactions[] = new int[]
                {
                        R.drawable.sad,
                        R.drawable.angry,
                        R.drawable.like,
                        R.drawable.thumbs,
                        R.drawable.laughing,
                        R.drawable.surprised
                };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            try {
                if (holder.getClass() == SentViewHolder.class) {
                    SentViewHolder sentViewHolder = (SentViewHolder) holder;

                    sentViewHolder.binding.feelingsend.setImageResource(reactions[pos]);
                    sentViewHolder.binding.feelingsend.setVisibility(View.VISIBLE);


                } else if (holder.getClass() == ReceiceViewHolder.class) {
                    ReceiceViewHolder receiceViewHolder = (ReceiceViewHolder) holder;

                    receiceViewHolder.binding.receivefeeling.setImageResource(reactions[pos]);
                    receiceViewHolder.binding.receivefeeling.setVisibility(View.VISIBLE);


                } else

                    Toast.makeText(context, "Nothing added", Toast.LENGTH_SHORT).show();


                messegeDetails.setFeeling(pos);

                databaseReference
                        .child(messegeDetails.getPushId()).setValue(messegeDetails);


                databaseReference
                        .child(messegeDetails.getPushId()).setValue(messegeDetails);


            } catch (ArrayIndexOutOfBoundsException e) {
                Log.d("TAG", e.getLocalizedMessage());

            } finally {

            }
            return true;
        });

        //SentViewHolder

        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder sentViewHolder = (SentViewHolder) holder;



            if(messegeDetails.getMessege().equals("photo"))
            {
                sentViewHolder.binding.sendImage12.setVisibility(View.VISIBLE);
                sentViewHolder.binding.sendItemm     .setVisibility(View.GONE);

                Glide.with(context)
                        .load(messegeDetails
                                .getImageUrl())
                        .placeholder(R.drawable.placeholder1)
                        .into(sentViewHolder.binding.sendImage12);
            }

            FirebaseDatabase.getInstance().getReference().child("users").child(messegeDetails
                    .getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        UserDetails userDetails = snapshot.getValue(UserDetails.class);
                        sentViewHolder.binding.nameGroupSend.setText("@"+userDetails.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            sentViewHolder.binding.sendItemm.setText(messegeDetails.getMessege());

            if (messegeDetails.getFeeling() >= 0) {

                sentViewHolder.binding.feelingsend.setImageResource(reactions[messegeDetails.getFeeling()]);
                sentViewHolder.binding.feelingsend.setVisibility(View.VISIBLE);

            } else {
                sentViewHolder.binding.feelingsend.setVisibility(View.GONE);

            }

            sentViewHolder.binding.sendItemm.setOnTouchListener((view, motionEvent) -> {
                popup.onTouch(view, motionEvent);
                return true;
            });

            sentViewHolder.binding.sendImage12.setOnTouchListener((view, motionEvent) -> {
                popup.onTouch(view, motionEvent);
                return true;
            });






            sentViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    View view1 = LayoutInflater.from(context).inflate(R.layout.deletemesseges, null);
                    DeletemessegesBinding binding = DeletemessegesBinding.bind(view1);
                    AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("delete messege")
                            .setView(binding.getRoot())
                            .create();


                    binding.btnDeleteEveryone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            messegeDetails.setMessege("this messege is removed");
                              messegeDetails.setFeeling(-1);

                              databaseReference
                                      .child(messegeDetails.getPushId()).setValue(messegeDetails);

                              alertDialog.dismiss();


//

                        }
                    });


                    binding.btnDeleteMe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            databaseReference
                                    .child(messegeDetails.getPushId())
                                    .removeValue();



                            alertDialog.dismiss();



                        }
                    });
                    binding.btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();


                    return false;
                }
            });


        }

        //ReceiveViewHolder

        else


        {
            ReceiceViewHolder receiceViewHolder = (ReceiceViewHolder) holder;

            try {

                if (messegeDetails.getMessege().equals("photo")) {
                    receiceViewHolder.binding.receiveImage.setVisibility(View.VISIBLE);
                    receiceViewHolder.binding.receiveritem.setVisibility(View.GONE);

                    Glide.with(context)
                            .load(messegeDetails
                                    .getImageUrl()).placeholder(R.drawable.placeholder1)
                            .into(receiceViewHolder.binding.receiveImage);
                }

                FirebaseDatabase.getInstance().getReference().child("users").child(messegeDetails
                        .getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            receiceViewHolder.binding.nameGroupReceice.setText("@" + userDetails.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                receiceViewHolder.binding.receiveritem.setText(messegeDetails.getMessege());

                if (messegeDetails.getFeeling() >= 0) {

                    receiceViewHolder.binding.receivefeeling.setImageResource(reactions[messegeDetails.getFeeling()]);

                    receiceViewHolder.binding.receivefeeling.setVisibility(View.VISIBLE);
                } else {
                    receiceViewHolder.binding.receivefeeling.setVisibility(View.GONE);

                }
                receiceViewHolder.binding.receiveritem.setOnTouchListener((view, motionEvent) -> {
                    popup.onTouch(view, motionEvent);
                    return true;
                });

                receiceViewHolder.binding.receiveImage.setOnTouchListener((view, motionEvent) -> {
                    popup.onTouch(view, motionEvent);
                    return true;
                });

            }
            catch (Exception e)
            {
                Log.d("Exception",e.getLocalizedMessage());

            }
            finally {

            }


            receiceViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    View view1 = LayoutInflater.from(context).inflate(R.layout.deletemesseges, null);
                    DeletemessegesBinding binding = DeletemessegesBinding.bind(view1);
                    AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("delete messge")
                            .setView(binding.getRoot())
                            .create();


                    binding.btnDeleteEveryone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            messegeDetails.setMessege("this messge is removed");
                            messegeDetails.setFeeling(-1);

                            databaseReference
                                    .child(messegeDetails.getPushId()).setValue(messegeDetails);


                            alertDialog.dismiss();

                        }
                    });


                    binding.btnDeleteMe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            databaseReference
                                    .child(messegeDetails.getPushId())
                                    .removeValue();


                            alertDialog.dismiss();

                        }
                    });
                    binding.btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();


                    return false;
                }
            });




        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {


        SendMessegeGroupBinding binding;


        public SentViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = SendMessegeGroupBinding.bind(itemView);
        }
    }

    public class ReceiceViewHolder extends RecyclerView.ViewHolder {

        ReceiveMessegeGroupBinding binding;

        public ReceiceViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ReceiveMessegeGroupBinding.bind(itemView);
        }
    }
}
