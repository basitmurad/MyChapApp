package com.example.mychapapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mychapapp.adapters.GroupMessegeAdapter;
import com.example.mychapapp.databinding.ActivityGroupChatBinding;
import com.example.mychapapp.models.MessegeDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {
    ActivityGroupChatBinding binding;


    ArrayList<MessegeDetails> messege;

    GroupMessegeAdapter adapter;
    String sendUid;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    DatabaseReference groupReference;
    StorageReference storageReference;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("Group Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending picture...");
        progressDialog.setCancelable(false);

        sendUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        groupReference = FirebaseDatabase.getInstance().getReference("public");
        storageReference = FirebaseStorage.getInstance().getReference("public");


        messege = new ArrayList<>();

        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messege.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessegeDetails messegeDetails = snapshot1.getValue(MessegeDetails.class);
                    messege.add(messegeDetails);
                }

                adapter = new GroupMessegeAdapter(GroupChatActivity.this, messege);
                binding.groupChatRecycler.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
                binding.groupChatRecycler.setAdapter(adapter);

                adapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("databsae error",error.getMessage());
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messege = binding.messegeBox.getText().toString();
                Date date = new Date();
                String randomKey = firebaseDatabase.getReference().push().getKey();
                MessegeDetails messegeDetails = new MessegeDetails(messege, sendUid, date.getTime(), randomKey);

                groupReference.child(randomKey)
                      .setValue(messegeDetails);
                binding.messegeBox.setText("");
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

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 131) {

            if (data.getData() != null) {
                Uri imageUir = data.getData();
                Calendar calendar = Calendar.getInstance();

                storageReference.child(calendar.getTimeInMillis() + " ");
                storageReference.putFile(imageUir).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    String filepath = imageUir.toString();
                                    String mess = binding.messegeBox.getText().toString();

                                    Date date = new Date();
                                    String randomKey = firebaseDatabase.getReference().push().getKey();
                                    MessegeDetails messegeDetails = new MessegeDetails(mess, sendUid, date.getTime(), randomKey);

                                    messegeDetails.setMessege("photo");
                                    messegeDetails.setImageUrl(filepath);

                                    groupReference.child(randomKey).setValue(messegeDetails);
                                    binding.messegeBox.setText("");
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
        return false;
    }
}