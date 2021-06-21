package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView register;
    private EditText editTextEmail, editTextPassword;
    private Button loginbtn;
    private CheckBox remember;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private Executor executor;
    private static BiometricPrompt biometricPrompt;
    private static BiometricPrompt.PromptInfo promptInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (TextView) findViewById(R.id.registerlink);
        register.setOnClickListener(this);

        loginbtn = (Button) findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        //editTextPassword= (EditText) findViewById(R.id.editTextPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        remember = (CheckBox) findViewById(R.id.checkBox);

        executor = ContextCompat.getMainExecutor(this);
        //BiometricPrompt
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent (MainActivity.this,Profile.class );
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {

                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this,"Please Sign In ",Toast.LENGTH_SHORT).show();
            }
        });
        //PromptInfo
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock the App")
                .setDescription("Your passwords are protected from unauthorized people")
                .setDeviceCredentialAllowed(true)
                .build();

        mAuth = FirebaseAuth.getInstance();
        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);

        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true")){
            openBiometricAuth();
//            Intent intent = new Intent (MainActivity.this,Profile.class );
//            startActivity(intent);
        }
        else if (checkbox.equals("false")){
            Toast.makeText(this,"Please Sign In ",Toast.LENGTH_SHORT).show();
        }



        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){

                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember","true");
                    editor.apply();
                    Toast.makeText(MainActivity.this,"Checked",Toast.LENGTH_SHORT).show();

                }else if (!compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember","false");
                    editor.apply();
                    Toast.makeText(MainActivity.this,"Unhecked",Toast.LENGTH_SHORT).show();
                }
            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        editTextEmail.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        editTextEmail.setCustomInsertionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }
    public static void openBiometricAuth() {
        biometricPrompt.authenticate(promptInfo);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerlink:
                startActivity(new Intent(this,Register.class));
                break;
            case R.id.loginbtn:
                userLogin();
                break;


        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        //String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Invalid Password Address");
            editTextEmail.requestFocus();
            return;
        }

//        if(password.length()<6){
//            editTextPassword.setError("Min password length should be 6 characters ");
//            editTextPassword.requestFocus();
//            return;
//        }

        progressBar.setVisibility(View.INVISIBLE);

        //Code to check email availability

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful()) {
                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                    Log.d("TAG", "Naan ingge irukken");
                    if (isNewUser) {
                        Log.d("TAG", "Is New User!");
                        Toast.makeText(MainActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("TAG", "Is Old User!");
                        Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                        myIntent.putExtra("email", email);
                        startActivity(myIntent);
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}