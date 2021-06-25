package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity implements View.OnClickListener {

        private TextView title , register;
        private EditText editEmail,phone, pass, cpass;
        private ProgressBar progressBar;
        private CheckBox chkbox;
        private boolean captchaChk;
        private FirebaseAuth mAuth;
        private FirebaseAnalytics mFirebaseAnalytics;
        RequestQueue queue;
        private String SiteKey = "6LfmB0obAAAAANrWiaf3BBGjZiKFjdNBPpBhaLmc";
        private final String SecretKey = "6LfmB0obAAAAAAR49kgBz0h0FFFFF7wZqW4kAlqC";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_register);

                mAuth = FirebaseAuth.getInstance();

                title= (TextView) findViewById(R.id.texttitle);
                title.setOnClickListener(this);

                register = (Button) findViewById(R.id.Registerbtn);
                register.setOnClickListener(this);
                chkbox = (CheckBox) findViewById(R.id.captchaReg);
                chkbox.setOnClickListener(this);
                captchaChk = false;
                editEmail = (EditText)findViewById(R.id.emailAddress);
                phone= (EditText)findViewById(R.id.phone);
                pass= (EditText)findViewById(R.id.password);
                cpass= (EditText)findViewById(R.id.cpassword);
                progressBar= (ProgressBar)findViewById(R.id.progressBar);
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

                queue = Volley.newRequestQueue(getApplicationContext());
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE);

        }

        public void sendEvent(String login , String content){
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, login);
                //       bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, content);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }


        @Override
        public void onClick(View v) {
                switch (v.getId()){
                        case R.id.texttitle:
                                startActivity(new Intent(this,MainActivity.class));
                                break;
                        case R.id.Registerbtn:
                                Registerbtn();
                                break;
                        case R.id.captchaReg:
                                verify();
                                break;
                }

        }
        private void verify(){
                if(chkbox.isChecked()){
                        verifyGoogleReCAPTCHA();
                }
                else{
                        Toast.makeText(Register.this, "Not verified", Toast.LENGTH_SHORT).show();
                }
        }

        private void Registerbtn() {
                Pattern number = Pattern.compile("[0-9]", Pattern.CASE_INSENSITIVE);
                Pattern special = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                String email = editEmail.getText().toString().trim();
                String phoneNum = phone.getText().toString().trim();
                String password = pass.getText().toString().trim();
                Matcher matcherNumber = number.matcher(password);
                Matcher matcher = special.matcher(password);
                String ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (email.isEmpty()) {
                        editEmail.setError("Email is required");
                        editEmail.requestFocus();
                }

                if (password.isEmpty()) {
                        pass.setError("Password is required");
                        pass.requestFocus();
                        return;
                }
                if (password.length() < 8) {
                        pass.setError("Min password length is 8");
                        pass.requestFocus();
                        return;
                }
                if (!matcherNumber.find()) {
                        pass.setError("Password must be alphanumeric");
                        pass.requestFocus();
                        return;
                }
                if (!matcher.find()) {
                        pass.setError("Password must contains at least a special character");
                        pass.requestFocus();
                        return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                                User user = new User(email, password, phoneNum);

                                                FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                                user.sendEmailVerification();
                                                                Toast.makeText(Register.this, "Check Your Email for Verification", Toast.LENGTH_LONG).show();
                                                                if(user.isEmailVerified()){
                                                                        if(task.isSuccessful()){
                                                                                Toast.makeText(Register.this,"User has been registered successfully !",Toast.LENGTH_LONG).show();
                                                                                progressBar.setVisibility(View.VISIBLE);
                                                                                sendEvent(email,"user has just registered");
                                                                                //redirect to login layout
                                                                        }
                                                                        else {
                                                                                Toast.makeText(Register.this, "Failed to resgister! Try again !", Toast.LENGTH_LONG).show();
                                                                                progressBar.setVisibility(View.GONE);
                                                                                sendEvent(email, "user is failed to be registered");
                                                                        }
                                                                }
                                                        }
                                                });


                                        } else {
                                                Toast.makeText(Register.this, "Failed to resgister! Try again !", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                        }
                                }
                        });
        }

        private void verifyGoogleReCAPTCHA() {

                // below line is use for getting our safety
                // net client and verify with reCAPTCHA
                SafetyNet.getClient(this).verifyWithRecaptcha(SiteKey)
                        // after getting our client we have
                        // to add on success listener.
                        .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                                @Override
                                public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                        // in below line we are checking the response token.
                                        if (!response.getTokenResult().isEmpty()) {
                                                // if the response token is not empty then we
                                                // are calling our verification method.
                                                handleVerification(response.getTokenResult());
                                        }
                                }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                        // this method is called when we get any error.
                                        if (e instanceof ApiException) {
                                                ApiException apiException = (ApiException) e;
                                                // below line is use to display an error message which we get.
                                                Log.d("TAG", "Error message: " +
                                                        CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                                        } else {
                                                // below line is use to display a toast message for any error.
                                                Toast.makeText(Register.this, "Error found is : " + e, Toast.LENGTH_SHORT).show();
                                        }
                                }
                        });
        }

        protected void handleVerification(final String responseToken) {
                // inside handle verification method we are
                // verifying our user with response token.
                // url to sen our site key and secret key
                // to below url using POST method.
                String url = "https://www.google.com/recaptcha/api/siteverify";

                // in this we are making a string request and
                // using a post method to pass the data.
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                        // inside on response method we are checking if the
                                        // response is successful or not.
                                        try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                if (jsonObject.getBoolean("success")) {
                                                        // if the response is successful then we are
                                                        // showing below toast message.
                                                        Toast.makeText(Register.this, "User verified with reCAPTCHA", Toast.LENGTH_SHORT).show();
                                                        captchaChk = true;
                                                } else {
                                                        // if the response if failure we are displaying
                                                        // a below toast message.
                                                        Toast.makeText(getApplicationContext(), String.valueOf(jsonObject.getString("error-codes")), Toast.LENGTH_LONG).show();
                                                }
                                        } catch (Exception ex) {
                                                // if we get any exception then we are
                                                // displaying an error message in logcat.
                                                Log.d("TAG", "JSON exception: " + ex.getMessage());
                                        }
                                }
                        },
                        new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                        // inside error response we are displaying
                                        // a log message in our logcat.
                                        Log.d("TAG", "Error message: " + error.getMessage());
                                }
                        }) {
                        // below is the getParamns method in which we will
                        // be passing our response token and secret key to the above url.
                        @Override
                        protected Map<String, String> getParams() {
                                // we are passing data using hashmap
                                // key and value pair.
                                Map<String, String> params = new HashMap<>();
                                params.put("secret", SecretKey);
                                params.put("response", responseToken);
                                return params;
                        }
                };
                // below line of code is use to set retry
                // policy if the api fails in one try.
                request.setRetryPolicy(new DefaultRetryPolicy(
                        // we are setting time for retry is 5 seconds.
                        50000,

                        // below line is to perform maximum retries.
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                // at last we are adding our request to queue.
                queue.add(request);
        }
}
