package com.example.mychapapp.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.example.mychapapp.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class PhoneNumberActivity extends AppCompatActivity {

   ActivityPhoneNumberBinding binding;

ProgressDialog progressDialog;
FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

firebaseAuth= FirebaseAuth.getInstance();
if(firebaseAuth.getCurrentUser()!=null)
{
    startActivity(new Intent(PhoneNumberActivity.this,DashBoardActivity.class));
    finish();
}


        progressDialog = new ProgressDialog(this);

     binding.btnOpt.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

                 CheckValidaion();

         }
     });


    }

public void CheckValidaion()
{
    if(binding.number.getText().toString().isEmpty())
    {
        Toast.makeText(this, "Enter number", Toast.LENGTH_SHORT).show();
    }
    else if(binding.number.getText().toString().trim().length()!=10) {

        Toast.makeText(this, "Invalid Number", Toast.LENGTH_SHORT).show();
    }
    else
    {

        progressDialog.setTitle("Sending Otp");
        progressDialog.setMessage("PLease Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String number = binding.number.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+92" + number,
                60l,
                TimeUnit.SECONDS, PhoneNumberActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {



                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        Log.d("Exception",e.getMessage());
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {


                        progressDialog.dismiss();

                        Intent intent = new Intent(PhoneNumberActivity.this,OTPActivity.class);
                        intent.putExtra("verificationID" ,s);
                        intent.putExtra("number"+92,number);
                        startActivity(intent);


                    }
                });
    }

}
}