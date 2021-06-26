package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

public class Profile extends AppCompatActivity implements LogOutTimerUtil.LogOutListener, LifecycleObserver {

    private static final String TAG = "Main";
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private Button logout, smtg;
    Timer timer;
    private Executor executor;
    private static BiometricPrompt biometricPrompt;
    private static BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimerUtil.startLogoutTimer(this, this);
        Log.e(TAG, "User interacting with screen");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogOutTimerUtil.startLogoutTimer(this, this);
        Log.e(TAG, "User interacting with screen");
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(Profile.this,
                MainActivity.class));
        Log.e(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
    }

    @Override
    public void doLogout() {
        Intent intent = new Intent(Profile.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(Profile.this, MainActivity.class));
        finish();
    }

    /**
     * Performing idle time logout
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppStopped() {
        stopService(new Intent(Profile.this, MainActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logout = findViewById(R.id.logout);
        smtg = findViewById(R.id.button1);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember","false");
                editor.apply();
                SharedPreferences preferences1 = getSharedPreferences("Users", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = preferences1.edit();
                editor1.putString("userId","");
                editor1.apply();
                finish();
            }
        });
        SharedPreferences prefer = getSharedPreferences("Users",MODE_PRIVATE);
        user= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users ");
        userID= prefer.getString("userId","Hello");
        Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();
        smtg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Preference.class);
                intent.putExtra("userId", userID);
                startActivity(intent);
            }
        });



        final TextView usernameshowTextview = (TextView) findViewById(R.id.usernameshow);
        final TextView emailaddshowTextView = (TextView) findViewById(R.id.emailaddshow);
        final TextView passwordshowTextView = (TextView) findViewById(R.id.passwordshow);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!= null){
                    String email = userProfile.email;
                    String password = userProfile.password;


                    emailaddshowTextView.setText(email);
                    passwordshowTextView.setText(password);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(Profile.this,"Something has went wrong ",Toast.LENGTH_LONG).show();

            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Profile.this);
        builder.setTitle("Secure Codes");
        builder.setMessage("Testing");

//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                onUserInteraction();
//                Intent intent = new Intent(Profile.this, MainActivity.class);
//                toast();
//                startActivity(intent);
//            }
//        }, 40000);
    }

//    public void onUserInteraction(){
//        super.onUserInteraction();
//        timer.cancel();
//    }
//    public void toast() {
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            public void run() {
//                Snackbar.make(findViewById(R.id.proLayout), "Session Time Out", Snackbar.LENGTH_SHORT)
//                        .show();
//            }
//        });
//    }
}