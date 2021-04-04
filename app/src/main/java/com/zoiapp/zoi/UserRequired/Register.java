package com.zoiapp.zoi.UserRequired;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zoiapp.zoi.MainHomePage.MainActivity;
import com.zoiapp.zoi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private EditText phnNumber,otpCode;
    Button nextButton;
    ProgressBar progressBar;
    TextView state;
    CountryCodePicker codePicker;
    String verificationID;
    PhoneAuthProvider.ForceResendingToken Token;
    Boolean verificationInProgress = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        phnNumber = (EditText) findViewById(R.id.phone);
        otpCode = (EditText) findViewById(R.id.codeEnter);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        nextButton = (Button) findViewById(R.id.nextBtn);
        state = (TextView) findViewById(R.id.state);
        codePicker = (CountryCodePicker) findViewById(R.id.ccp);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnectivity())
                {
                if (!verificationInProgress) {
                    String num = phnNumber.getText().toString().trim();
                    if (num.isEmpty() || num.length() < 10) {
                        phnNumber.setError("Number is required");
                        phnNumber.requestFocus();
                        return;
                    } else {
                        String phoneNum = "+" + codePicker.getSelectedCountryCode() + num;
                        progressBar.setVisibility(View.VISIBLE);
                        state.setText("Sending OTP");
                        state.setVisibility(View.VISIBLE);
                        requestOTP(phoneNum);
                    }
                } else {
                    String userOTP = otpCode.getText().toString();
                    if (!userOTP.isEmpty() && userOTP.length() == 6) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, userOTP);
                        verifyAuth(credential);
                    } else {
                        otpCode.setError("Valid OTP is Required.");
                    }

                }
            }
            else

            {
                Toast.makeText(Register.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            state.setText("checking...");
            state.setVisibility(View.VISIBLE);
            checkUserProfile();
        }
    }

    private void verifyAuth(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    checkUserProfile();
                }
                else {
                    if(verificationInProgress == true) {
                        Toast.makeText(Register.this, "Invalid OTP", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(Register.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void checkUserProfile()
    {
        final DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    String address= documentSnapshot.getString("UserAddress");
                    if(address!=null)
                    {
                        Intent intent=new Intent(Register.this, AddDetails.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                else
                {
                    Intent intent=new Intent(Register.this,AddDetails.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }


    private void requestOTP(String phoneNum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                otpCode.setVisibility(View.VISIBLE);
                verificationID = s;
                Token = forceResendingToken;
                nextButton.setText("Verify");
                verificationInProgress=true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(Register.this,"OTP has expired,Re-Request the OTP",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                verifyAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(Register.this,"Can not Create Account",Toast.LENGTH_LONG).show();
            }
        });
    }
    private Boolean internetConnectivity()
    {
        boolean connectivityInfo=false;
        ConnectivityManager connection=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=connection.getActiveNetworkInfo();
        if(activeNetwork!=null) {
            if (activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE||activeNetwork.getType()==ConnectivityManager.TYPE_WIFI) {
                connectivityInfo = true;
            }
        }
        return connectivityInfo;
    }
}
