package com.example.mychapapp.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.mychapapp.databinding.ActivityOtpactivityBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;


public class OTPActivity extends AppCompatActivity {

    ActivityOtpactivityBinding binding;

    String number;
    FirebaseAuth firebaseAuth;
    String OTPID;
    String verificatonID;
    PhoneAuthProvider.ForceResendingToken ResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();
        number = (getIntent().getStringExtra("number"));
        verificatonID = getIntent().getStringExtra("verificationID");
        binding.textNumber.setText(String.format("+92", number));


        new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long l) {
                binding.counter.setText(String.valueOf(l / 1000));

            }

            @Override
            public void onFinish() {
                binding.btnResend.setVisibility(View.VISIBLE);

            }
        }.start();

        OtpListner();


        binding.btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  OtpAgain();
                OtpListner();

                new CountDownTimer(30000, 1000) {

                    @Override
                    public void onTick(long l) {
                        binding.counter.setText(String.valueOf(l / 1000));

                    }

                    @Override
                    public void onFinish() {
                        binding.btnResend.setVisibility(View.VISIBLE);

                    }
                }.start();
            }
        });


    }

    private void OtpListner() {
        binding.otpview.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {


            }

            @Override
            public void onOTPComplete(String otp) {

                if (verificatonID != null) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificatonID, otp);
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                startActivity(new Intent(OTPActivity.this, SetupProfileActivity.class));

                            } else {
                                Toast.makeText(OTPActivity.this, "verificaton code invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OTPActivity.this, "Exception" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(OTPActivity.this, "Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });


    }

    private void OtpAgain() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber("+92" + number
                , 60l
                , TimeUnit.SECONDS
                , OTPActivity.this
                , new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {


                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        binding.otpview.requestFocusOTP();
                        startActivity(new Intent(OTPActivity.this, DashBoardActivity.class));
                    }
                });
    }


}
