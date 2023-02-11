package com.example.mychapapp.activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mychapapp.R;
import com.example.mychapapp.adapters.MessegeAdapter;

import com.example.mychapapp.databinding.ActivityChatBinding;
import com.example.mychapapp.models.MessegeDetails;

import com.example.mychapapp.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;


    ArrayList<MessegeDetails> messege;

    MessegeAdapter messegeAdapter;

    String senderRoom, receiverRoom;
    DatabaseReference databaseReference, statusRef,userRef;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    String Username, userProfile,token,Usernamee;
    String sendUid;


    String receiverUuid;
    ImageView imageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        userRef = FirebaseDatabase.getInstance().getReference("users");
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");

        storageReference = FirebaseStorage.getInstance().getReference("chats");
        statusRef = FirebaseDatabase.getInstance().getReference("Presence");



        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        imageView= findViewById(R.id.sendImage);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending picture...");
        progressDialog.setCancelable(false);

        messege = new ArrayList<>();


        Username = getIntent().getStringExtra("name");
        userProfile = getIntent().getStringExtra("imageProfile");

      receiverUuid = getIntent().getStringExtra("Uuid");



        sendUid = FirebaseAuth.getInstance().getUid();

        senderRoom = sendUid + receiverUuid;
        receiverRoom = receiverUuid + sendUid;

userRef.child(sendUid).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
     if(snapshot.child("name").exists())
     {
         Usernamee = snapshot.child("name").getValue(String.class);
         Toast.makeText(ChatActivity.this, ""+Username, Toast.LENGTH_SHORT).show();
     }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

        userRef.child(receiverUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("token").exists())
                {
                    token = snapshot.child("token").getValue(String.class);
//                    Username = snapshot.child("name").getValue(String.class);
//                    Toast.makeText(ChatActivity.this, ""+token, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        statusRef.child(receiverRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if (!status.isEmpty()) {
                        if (status.equals("offline")) {
                            binding.statusPresence.setVisibility(View.GONE);

                        } else {
                            binding.statusPresence.setText(status);
                            binding.statusPresence.setVisibility(View.VISIBLE);
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        databaseReference.child(senderRoom)
                .child("messeges")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        messege.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessegeDetails messegeDetails = snapshot1.getValue(MessegeDetails.class);

                            messege.add(messegeDetails);
                        }
                        messegeAdapter = new MessegeAdapter(ChatActivity.this, messege, senderRoom, receiverRoom);
                        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

                        binding.chatRecycler.setAdapter(messegeAdapter);
                        messegeAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = binding.messegeBox.getText().toString();

                Date date = new Date();
                String randomKey = firebaseDatabase.getReference().push().getKey();
                MessegeDetails messegeDetails = new MessegeDetails(mess, sendUid, date.getTime(), randomKey);
                binding.messegeBox.setText("");


                binding.messegeBox.setText("");


                databaseReference.child(senderRoom)
                        .child("lastMessege")
                        .setValue(messegeDetails.getMessege());

                databaseReference.child(senderRoom)
                        .child("lastMessegeTime")
                        .setValue(messegeDetails.getTimeStamp());

                databaseReference.child(receiverRoom)
                        .child("lastMessege")
                        .setValue(messegeDetails.getMessege());

                databaseReference.child(receiverRoom)
                        .child("lastMessegeTime")
                        .setValue(messegeDetails.getTimeStamp());

                //

                databaseReference.child(senderRoom)
                        .child("messeges")
                        .child(randomKey)
                        .setValue(messegeDetails)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                databaseReference.child(receiverRoom)
                                        .child("messeges")
                                        .child(randomKey)
                                        .setValue(messegeDetails)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                onSendNotification(Usernamee,messegeDetails.getMessege(),token);
//                                                onSendNotification(Username,messegeDetails.getMessege(),token);

                                            }
                                        });


                            }
                        });
            }


        });


        binding.attachFIleChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 131);
            }
        });


        binding.textView3.setText(Username);
        Glide.with(this).load(userProfile).placeholder(R.drawable.profile).into(binding.circleImageView);

        binding.arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        statusRef.child(receiverUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if (!status.isEmpty()) {
                        binding.statusPresence.setText(status);
                        binding.statusPresence.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Handler handler = new Handler();
        binding.messegeBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                statusRef.child(sendUid).setValue("typing...");

                handler.removeCallbacksAndMessages(null);

                handler.postDelayed(userStopTyping, 1000);
            }

            Runnable userStopTyping = new Runnable() {
                @Override
                public void run() {
                    statusRef.child(sendUid).setValue("online");


                }
            };
        });


        binding.btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent();
                cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,1113);
                startActivity(cameraIntent);
            }
        });

    }



public void onSendNotification(String name, String messege,String token)
{

    try {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://fcm.googleapis.com/fcm/send";
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title",name);
        jsonObject.put("body",messege);


        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("notification",jsonObject);
        jsonObject1.put("to",token);

//        JsonObjectRequest jor = new JsonObjectRequest(url, jsonObject1, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Toast.makeText(ChatActivity.this, ""+response.toString(), Toast.LENGTH_SHORT).show();
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, jsonObject1,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ChatActivity.this, "Successfully send messege", Toast.LENGTH_SHORT).show();
                    }

                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChatActivity.this, "messegeNotSend "+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


            }

        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                String key = "key=AAAAIV8Vaus:APA91bFOulHBkoqF-_BQq2W54S-jcPWN-b9l16uCHx-z_WrwTqSRDdUjGXhtTbO-a6heneMMRLZyyoVEU0kOkxoLy4ARyI5r1LM9_clsHLUvLbWbC_rzdGBSOvks5I2Cn3CHkbisFqRL";
                map.put("Content-type","application/json");
                map.put("Authorization",key);


                return map;
            }
        };
        requestQueue.add(jsonObjectRequest);
    } catch (JSONException e) {
        e.printStackTrace();
    }

}
    protected void onResume() {
        super.onResume();
        String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        statusRef.child(currentID).setValue("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        statusRef.child(currentID).setValue("offline");
    }

//    @Override
//    protected void onStop() {
//        String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        statusRef.child(currentID).setValue("ofline");
//        super.onStop();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1113)
        {

            assert data != null;
                try {
                    Bitmap bitmapPhoto  = (Bitmap) data.getExtras().get("data");

                    imageView.setImageBitmap(bitmapPhoto);
                }
                catch (Exception error)
                {
                    Log.d("bitmapException",error.getLocalizedMessage());
                }




        }
        if (requestCode == 131) {
            if (data.getData() != null) {
                Uri imageUri = data.getData();

                Calendar calendar = Calendar.getInstance();
                storageReference
                        .child(calendar.getTimeInMillis() + " ");
                progressDialog.show();
                storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String filepath = uri.toString();

                                    //

                                    String mess = binding.messegeBox.getText().toString();

                                    Date date = new Date();
                                    String randomKey = firebaseDatabase.getReference().push().getKey();
                                    MessegeDetails messegeDetails = new MessegeDetails(mess, sendUid, date.getTime(), randomKey);

                                    messegeDetails.setMessege("photo");
                                    messegeDetails.setImageUrl(filepath);

                                    binding.messegeBox.setText("");


                                    //    binding.messegeBox.setText("");


                                    //


                                    databaseReference.child(senderRoom)
                                            .child("lastMessege")
                                            .setValue(messegeDetails.getMessege());

                                    databaseReference.child(senderRoom)
                                            .child("lastMessegeTime")
                                            .setValue(messegeDetails.getTimeStamp());

                                    databaseReference.child(receiverRoom)
                                            .child("lastMessege")
                                            .setValue(messegeDetails.getMessege());

                                    databaseReference.child(receiverRoom)
                                            .child("lastMessegeTime")
                                            .setValue(messegeDetails.getTimeStamp());

                                    //

                                    databaseReference.child(senderRoom)
                                            .child("messeges")
                                            .child(randomKey)
                                            .setValue(messegeDetails)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    databaseReference.child(receiverRoom)
                                                            .child("messeges")
                                                            .child(randomKey)
                                                            .setValue(messegeDetails)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {


                                                                }
                                                            });


                                                }
                                            });


                                }
                            });
                        }
                    }
                });


            }
        }
    }




    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return super.onSupportNavigateUp();
    }
}