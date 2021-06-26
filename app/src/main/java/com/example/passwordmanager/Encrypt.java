package com.example.passwordmanager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Encrypt extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    EditText appname,notes,email,username,password;
    private ImageView image,back;
    private ImageView imageview;
    int res_image;
    Button save;
    String pass_en,user_en;

    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        image = (ImageView) findViewById(R.id.imageView3);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileactivity();
            }
        });

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String lastedited = df.format(c);

        appname = findViewById(R.id.editTextTextPersonName2);
        email=findViewById(R.id.editTextTextEmailAddress);
        username=findViewById(R.id.editTextTextPersonName4);
        password = findViewById(R.id.editTextTextPersonName);
        notes=findViewById(R.id.editTextTextMultiLine);
        save = findViewById(R.id.button);
        back = findViewById(R.id.back);
        imageview = (ImageView) findViewById(R.id.imageView3);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            res_image = bundle.getInt("id");
            imageview.setImageResource(res_image);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("Users").child(user.getUid()).child("Accounts");

        save.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                String app = appname.getLayout().getText().toString().trim();
                String mail = email.getLayout().getText().toString().trim();
                String user = username.getLayout().getText().toString().trim();
                String pass = password.getLayout().getText().toString().trim();
                String note = notes.getLayout().getText().toString().trim();
                String logo = String.valueOf(res_image);

                if (app.isEmpty()) {
                    appname.setError("App Name is required");
                    appname.requestFocus();
                    return;
                }

                if (mail.isEmpty()) {
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }

                if (user.isEmpty()) {
                    username.setError("Username is required");
                    username.requestFocus();
                    return;
                }

                if (pass.isEmpty()) {
                    password.setError("App Name is required");
                    password.requestFocus();
                    return;
                }

                try{
                    user_en=encrypt(user);
                    pass_en = encrypt(pass);
                }catch (Exception e){
                    e.printStackTrace();
                }

                account helperClass = new account(app,user_en,pass_en,mail,note,lastedited,logo);
                reference.child(app).setValue(helperClass);

                Intent intent = new Intent(Encrypt.this, AccountList.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Encrypt.this, AccountList.class);
                startActivity(intent);
            }
        });
    }

    // This method use to encrypt to string
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void profileactivity(){
        Intent intent = new Intent (this, profilephoto.class);
        startActivity(intent);

    }

}