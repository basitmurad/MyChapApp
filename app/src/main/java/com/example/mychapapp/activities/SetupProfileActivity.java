package com.example.mychapapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mychapapp.databinding.ActivitySetupProfileBinding;
import com.example.mychapapp.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    Uri selectedImage;
    DatabaseReference userRef;

    SessionManager sessionManager;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRef = FirebaseDatabase.getInstance().getReference("users");


        sessionManager = new SessionManager(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Profile Picture");
        progressDialog.setMessage("Please Wait....");
        progressDialog.setCancelable(false);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();





        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });


        binding.btnProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // String name = binding.profileName.getText().toString();
                if (binding.profileName.getText().toString().isEmpty()) {
                    binding.profileName.setError("Enter your name");


                }
                progressDialog.show();
             if (selectedImage != null) {
                    StorageReference storageReference = firebaseStorage.getReference().child("Profiles").child(firebaseAuth.getUid());
                    storageReference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String uuId = firebaseAuth.getUid();
                                        String name = binding.profileName.getText().toString();
                                        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();



                                        String imageUrl = uri.toString();



                                        UserDetails userrDetails = new UserDetails(uuId, name, phone, imageUrl);

                                        userRef.child(uuId).setValue(userrDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isComplete() && task.isSuccessful()) {
                                                    progressDialog.dismiss();

                                                    startActivity(new Intent(SetupProfileActivity.this, DashBoardActivity.class));
                                                    finish();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SetupProfileActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
                            }
                        }
                    });
                } else {
                    String uuId = firebaseAuth.getUid();
                    String name = binding.profileName.getText().toString();
                    String phone = firebaseAuth.getCurrentUser().getPhoneNumber();


                    UserDetails userrDetails = new UserDetails(uuId, name, phone, "No Image ");


                    userRef.child(uuId).setValue(userrDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete() && task.isSuccessful()) {
                                progressDialog.dismiss();




                                startActivity(new Intent(SetupProfileActivity.this, DashBoardActivity.class));
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SetupProfileActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null) {
            binding.profileImage.setImageURI(data.getData());
            selectedImage = data.getData();
        }
    }
}