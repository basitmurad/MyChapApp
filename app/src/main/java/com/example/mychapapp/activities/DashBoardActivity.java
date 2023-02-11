package com.example.mychapapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mychapapp.R;
import com.example.mychapapp.adapters.UserDetailAdapter;
import com.example.mychapapp.adapters.UserStatusAdapter;
import com.example.mychapapp.databinding.ActivityDashBoardBinding;
import com.example.mychapapp.models.Status;
import com.example.mychapapp.models.UserDetails;
import com.example.mychapapp.models.UserStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class DashBoardActivity extends AppCompatActivity {

    ActivityDashBoardBinding binding;
    FirebaseDatabase firebaseDatabase;
    ArrayList<UserDetails> list;
    UserDetailAdapter userDetailAdapter;
    DatabaseReference userRef, storageRef, statusRef;
    UserStatusAdapter userStatusAdapter;
    ArrayList<UserStatus> userStatuses;
    ProgressDialog progressDialog;
    UserDetails userDetails;
    ProgressDialog dialog;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



//        dialog = new ProgressDialog(this);
//        dialog.setMessage("PLease create you profile ");
//        dialog.setCancelable(true);
//        dialog.show();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("uploading status");
        progressDialog.setCancelable(false);

        userRef = FirebaseDatabase.getInstance().getReference("users");
        storageRef = FirebaseDatabase.getInstance().getReference("stories");
        statusRef = FirebaseDatabase.getInstance().getReference("Presence");


        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {

                HashMap<String, Object> map = new HashMap<>();
                map.put("token", s);

                userRef.child(FirebaseAuth.getInstance().getUid()).updateChildren(map);
                Toast.makeText(DashBoardActivity.this, "" + s, Toast.LENGTH_SHORT).show();
            }
        });


//        Toast.makeText(this, ""+token, Toast.LENGTH_SHORT).show();
        list = new ArrayList<>();
        userStatuses = new ArrayList<>();

        userRef.child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        userDetails = snapshot.getValue(UserDetails.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        userStatusAdapter = new UserStatusAdapter(this, userStatuses);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        binding.statusRecycler.setLayoutManager(linearLayoutManager);
        binding.statusRecycler.setAdapter(userStatusAdapter);
        binding.recycler.showShimmerAdapter();
        binding.statusRecycler.showShimmerAdapter();

        userRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    list.clear();

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        UserDetails data = snapshot1.getValue(UserDetails.class);

                        if(!data.getUniqueID().equals(FirebaseAuth.getInstance().getUid()))
                        list.add(new UserDetails(data.getUniqueID(), data.getName(), data.getPhoneNumber(), data.getProfileIMage()));


                    }

                    userDetailAdapter = new UserDetailAdapter(list, DashBoardActivity.this);
                    binding.recycler.setLayoutManager(new LinearLayoutManager(DashBoardActivity.this));
                    binding.recycler.setAdapter(userDetailAdapter);

                } else {
                    Toast.makeText(DashBoardActivity.this, "No data exsits", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashBoardActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        storageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userStatuses.clear();
                    for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                        UserStatus userStatus = new UserStatus();
                        userStatus.setUserName(storySnapshot.child("name").getValue(String.class));
                        userStatus.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        userStatus.setLastUpdate(storySnapshot.child("lastUpdate").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();
                        for (DataSnapshot snapshot11 : storySnapshot.child("statuses").getChildren()) {

                            Status sampleStaus = snapshot11.getValue(Status.class);
                            statuses.add(sampleStaus);

                        }
                        userStatus.setStatuses(statuses);

                        userStatuses.add(userStatus);

                    }
                    binding.recycler.hideShimmerAdapter();
                    binding.statusRecycler.hideShimmerAdapter();
                    userStatusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.status:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 75);
                        break;
                }

            }
        });


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
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
        if (data != null) {
            if (data.getData() != null) {
                progressDialog.show();

                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

                Date date = new Date();
                StorageReference storageReference = firebaseStorage.getReference("status").child(date.getTime() + "");

                storageReference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    UserStatus userStatus = new UserStatus();
                                    userStatus.setUserName(userDetails.getName());
                                    userStatus.setProfileImage(userDetails.getProfileIMage());
                                    userStatus.setLastUpdate(date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name", userStatus.getUserName());
                                    obj.put("profileImage", userStatus.getProfileImage());
                                    obj.put("lastUpdate", userStatus.getLastUpdate());


                                    String imageUrl = uri.toString();

                                    Status status = new Status(imageUrl, userStatus.getLastUpdate());
                                    storageRef
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    storageRef
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push().setValue(status);
                                    progressDialog.dismiss();
                                }
                            });
                        }

                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(this, "clicked search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                Toast.makeText(this, "clicked setting", Toast.LENGTH_SHORT).show();
                break;
            case R.id.invite:
                Toast.makeText(this, "clicked invite", Toast.LENGTH_SHORT).show();
                break;
            case R.id.group:
                startActivity(new Intent(DashBoardActivity.this, GroupChatActivity.class));
                Toast.makeText(this, "clicked group", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Profile:
                startActivity(new Intent(DashBoardActivity.this, ProfileActivity.class));

                Toast.makeText(this, "Profile  open successfully", Toast.LENGTH_SHORT).show();
                break;


            case R.id.setProfile:
                Toast.makeText(this, "Profile page open successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashBoardActivity.this, SetupProfileActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}