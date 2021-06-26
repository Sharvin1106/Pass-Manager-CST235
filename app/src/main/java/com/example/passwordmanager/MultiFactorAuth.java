package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MultiFactorAuth extends AppCompatActivity {
    Button verifyBtn;
    String inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    String Code = "";
    String code;
    private String verifySys;
    String userId;
    DatabaseReference reff;
    Boolean checked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify);

        verifyBtn = findViewById(R.id.buttonotp);

        Intent intent = getIntent();
        String num = intent.getStringExtra("phoneNum");
        checked = intent.getBooleanExtra("checked", false);
        userId = intent.getStringExtra("userId");
        Log.d("phone num", num);
        reff = FirebaseDatabase.getInstance().getReference();
        initializeCode();
        //String num = "+601136055713";

        sendVerificationCodeToUser(num);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkUserCode(Code);
            }
        });
    }
    private void initializeCode(){
        inputCode1 = findViewById(R.id.inputCode1).toString();
        inputCode2 = findViewById(R.id.inputCode2).toString();;
        inputCode3 = findViewById(R.id.inputCode3).toString();;
        inputCode4 = findViewById(R.id.inputCode4).toString();;
        inputCode5 = findViewById(R.id.inputCode5).toString();;
        inputCode6 = findViewById(R.id.inputCode6).toString();;

        Code += inputCode1 + inputCode2 + inputCode3 + inputCode4 + inputCode5 + inputCode6;
    }
    private void sendVerificationCodeToUser(String num) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder()
                .setActivity(this)
                .setPhoneNumber(num)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Snackbar.make(findViewById(R.id.mflayout), "Verification Code Sent", Snackbar.LENGTH_SHORT)
                    .show();
            verifySys = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                //verifyCode(code);
                if(checked){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember","true");
                    editor.apply();
                    Toast.makeText(MultiFactorAuth.this,"Checked",Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember","false");
                    editor.apply();
                    Toast.makeText(MultiFactorAuth.this,"Unhecked",Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MultiFactorAuth.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

//    private void verifyCode(String vCode){
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifySys, vCode);
//        signInTheUserByCredentials(credential);
//    }
//
//    private void signInTheUserByCredentials(PhoneAuthCredential credential) {
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                            if(checked){
//                                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
//                                SharedPreferences.Editor editor = preferences.edit();
//                                editor.putString("remember","true");
//                                editor.apply();
//                                Toast.makeText(MultiFactorAuth.this,"Checked",Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//                                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
//                                SharedPreferences.Editor editor = preferences.edit();
//                                editor.putString("remember","false");
//                                editor.apply();
//                                Toast.makeText(MultiFactorAuth.this,"Unhecked",Toast.LENGTH_SHORT).show();
//                            }
//                            Intent intent = new Intent(getApplicationContext(), Profile.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                        }
//                        else{
//                            Toast.makeText(MultiFactorAuth.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void checkUserCode(String Code){
        final boolean checked = false;
        reff.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    GenericTypeIndicator<ArrayList<String>> codes = new GenericTypeIndicator<ArrayList<String>>() {};
                    ArrayList<String> secureCodes = task.getResult().getValue(codes);
                    if(secureCodes.contains(Code)){
                        if(checked){
                            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("remember","true");
                            editor.apply();
                            Toast.makeText(MultiFactorAuth.this,"Checked",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("remember","false");
                            editor.apply();
                            Toast.makeText(MultiFactorAuth.this,"Unhecked",Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(getApplicationContext(), Profile.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else if(code.equals(Code)){
                        if(checked){
                            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("remember","true");
                            editor.apply();
                            Toast.makeText(MultiFactorAuth.this,"Checked",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("remember","false");
                            editor.apply();
                            Toast.makeText(MultiFactorAuth.this,"Unhecked",Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(getApplicationContext(), Profile.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else{
                        Snackbar.make(findViewById(R.id.mflayout), "Please enter your correct secure code", Snackbar.LENGTH_SHORT)
                                .show();
                    }

                }
            }
        });

    }

}
