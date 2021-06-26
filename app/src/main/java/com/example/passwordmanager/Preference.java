package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Preference extends AppCompatActivity {
    LinearLayout shwCode, updPass, updPin, showLog, backup;
    ImageView backBtn;
    String userID;
    boolean updPassCheck;
    EditText oldPass, newPass;
    String oldPassword;
    DatabaseReference reff;
    CharSequence[] cs;
    ArrayList<String> secureCodes;
    SharedPreferences sharedpreferences;
    FirebaseAuth mAuth;
    private Executor executor;
    private static BiometricPrompt biometricPrompt;
    private static BiometricPrompt.PromptInfo promptInfo;
    Pattern number;
    Pattern special;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        sharedpreferences = getSharedPreferences("Users", MODE_PRIVATE);
        userID = sharedpreferences.getString("userId", "");
        shwCode = findViewById(R.id.securecode);
        updPass = findViewById(R.id.updpass);
        updPin = findViewById(R.id.updPin);
        showLog = findViewById(R.id.logs);
        backBtn = findViewById(R.id.backBtn);
        backup = findViewById(R.id.backup);
        reff = FirebaseDatabase.getInstance().getReference();
        number = Pattern.compile("[0-9]", Pattern.CASE_INSENSITIVE);
        special = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        updPassCheck = false;
        shwCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSecureCodes();
            }
        });
        updPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Preference.this, Profile.class));
            }
        });
        updPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePin();
            }
        });

        executor = ContextCompat.getMainExecutor(this);
        //BiometricPrompt
        biometricPrompt = new BiometricPrompt(Preference.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Snackbar.make(findViewById(R.id.playout), "Biometric Authentication Error", Snackbar.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                updatePassword();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Snackbar.make(findViewById(R.id.playout), "Biometric Authentication Failed", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock the App")
                .setDescription("Your passwords are protected from unauthorized people")
                .setDeviceCredentialAllowed(true)
                .build();
    }

    private void showSecureCodes(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Preference.this);


        reff.child("Users").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    GenericTypeIndicator<ArrayList<String>> codes = new GenericTypeIndicator<ArrayList<String>>() {};
                    secureCodes = task.getResult().child("secureCodes").getValue(codes);

                    Log.d("secure code", userID);
                    cs = secureCodes.toArray(new CharSequence[secureCodes.size()]);
                }
            }
        });

        builder.setTitle("Secure Codes");
        builder.setIcon(R.drawable.securecode);
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Snackbar.make(findViewById(R.id.playout), "Please take a screenshot for future use", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        builder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_bg, null));
        builder.show();
    }

    private void changePass(){

        reff.child("Users").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    oldPassword= task.getResult().child("password").getValue().toString();
                    Log.d("Password old", oldPassword);
                }
            }
        });



        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Preference.this);
        builder.setTitle("Change Password");
        final View updPassLayout = getLayoutInflater().inflate(R.layout.alert_updpass_bg, null);
        builder.setView(updPassLayout);
        builder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_bg, null));
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                oldPass = updPassLayout.findViewById(R.id.oldPass);
                newPass = updPassLayout.findViewById(R.id.newPass);
                Matcher matcherNumber = number.matcher(newPass.getText().toString().trim());
                Matcher matcher = special.matcher(newPass.getText().toString().trim());
                if (oldPass.getText().toString().trim().isEmpty()) {
                    oldPass.setError("Password is required");
                    oldPass.requestFocus();
                    Snackbar.make(findViewById(R.id.playout), "Old Password is required", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if(!oldPass.getText().toString().trim().equals(oldPassword)){
                    oldPass.setError("Old password not match");
                    oldPass.requestFocus();
                    Snackbar.make(findViewById(R.id.playout), "Old Password does not match", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (newPass.getText().toString().trim().isEmpty()) {
                    newPass.setError("Password is required");
                    newPass.requestFocus();
                    Snackbar.make(findViewById(R.id.playout), "New Password is required", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (newPass.getText().toString().trim().length() < 8) {
                    newPass.setError("Min password length is 8");
                    newPass.requestFocus();
                    Snackbar.make(findViewById(R.id.playout), "Min password length is 8", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (!matcherNumber.find()) {
                    newPass.setError("Password must be alphanumeric");
                    newPass.requestFocus();
                    Snackbar.make(findViewById(R.id.playout), "Password must be alphanumeric", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (!matcher.find()) {
                    newPass.setError("Password must contains at least a special character");
                    newPass.requestFocus();
                    Snackbar.make(findViewById(R.id.playout), "Password must contains at least a special character", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                    openBiometricAuth();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }
    public static void openBiometricAuth() {
        biometricPrompt.authenticate(promptInfo);
    }


    private void changePin(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Preference.this);
        builder.setTitle("Change Password");
        final View updPassLayout = getLayoutInflater().inflate(R.layout.alert_updpass_bg, null);
        builder.setView(updPassLayout);
        builder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_bg, null));
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                oldPass = updPassLayout.findViewById(R.id.oldPass);
                newPass = updPassLayout.findViewById(R.id.newPass);
//                if(validatePassword()){
//                    updPassCheck = true;
//                    openBiometricAuth();
//                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }
    private void updatePassword(){
        FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Snackbar.make(findViewById(R.id.playout), "Password has been reset", Snackbar.LENGTH_SHORT)
                            .show();
                    reff.child("Users").child(userID).child("password").setValue(newPass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Snackbar.make(findViewById(R.id.playout), "Updating password in database failed", Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                }
            }
        });
    }
}