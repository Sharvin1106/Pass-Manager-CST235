package com.example.passwordmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.chaos.view.PinView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class PasscodeView extends AppCompatActivity {

    Button next;
    DatabaseReference dr;
    List<PinCode> fetchdata;
    ImageView back;

    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_view);

        next = findViewById(R.id.next);

        final PinView pinView = findViewById(R.id.firstPinView);
        pinView.setTextColor(
                ResourcesCompat.getColor(getResources(), R.color.teal_200, getTheme()));
        pinView.setTextColor(
                ResourcesCompat.getColorStateList(getResources(), R.color.white, getTheme()));
        pinView.setLineColor(
                ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
        pinView.setLineColor(
                ResourcesCompat.getColorStateList(getResources(), R.color.teal_700, getTheme()));
        pinView.setItemCount(4);
        pinView.setItemHeight(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_size));
        pinView.setItemWidth(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_size));
        pinView.setItemRadius(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_radius));
        pinView.setItemSpacing(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_spacing));
        pinView.setLineWidth(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_line_width));
        pinView.setAnimationEnable(true);// start animation when adding text
        pinView.setCursorVisible(false);
        pinView.setCursorColor(
                ResourcesCompat.getColor(getResources(), R.color.purple_200, getTheme()));
        pinView.setCursorWidth(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_cursor_width));
        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }});
        pinView.setItemBackgroundColor(Color.BLACK);
        //pinView.setItemBackground(getResources().getDrawable(R.drawable.item_background));
       // pinView.setItemBackgroundResources(R.drawable.item_background);
        pinView.setHideLineWhenFilled(false);
        pinView.setPasswordHidden(false);
        pinView.setTransformationMethod(new PasswordTransformationMethod());

        fetchdata = new ArrayList<PinCode>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dr = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("PinCode");

        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    PinCode code=dataSnapshot.getValue(PinCode.class);
                    fetchdata.add(code);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PasscodeView.this,AccountList.class);
                startActivity(intent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Integer decrypt_pin;
                Integer pin= Integer.valueOf(String.valueOf(pinView.getEditableText()));

                String realpin= fetchdata.get(0).getPinCode();
                decrypt_pin= Integer.valueOf(decrypt(realpin));

                if(pin.equals(decrypt_pin))
                {
                    final String position = getIntent().getStringExtra("position");
                    final String x=getIntent().getStringExtra("travel");

                    if(x.equals("view"))
                    {
                        Intent intent=new Intent(PasscodeView.this,AccountList.class);
                        intent.putExtra("x", "1");
                        intent.putExtra("position",position);
                        startActivity(intent);
                    }
                    else if(x.equals("delete"))
                    {
                        final String child=getIntent().getStringExtra("child");
                        Intent intent=new Intent(PasscodeView.this,AccountList.class);
                        intent.putExtra("x", "2");
                        intent.putExtra("position",position);
                        intent.putExtra("delete_child",child);
                        startActivity(intent);
                    }

                }
                else
                    Toast.makeText(PasscodeView.this,"Wrong Pin",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = new byte[0];
            original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}


