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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.chaos.view.PinView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CreatePassCode extends AppCompatActivity {

    Button save;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    ImageView back;
    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pass_code);

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

        final PinView pinView2 = findViewById(R.id.secondPinView);
        pinView2.setTextColor(
                ResourcesCompat.getColor(getResources(), R.color.teal_200, getTheme()));
        pinView2.setTextColor(
                ResourcesCompat.getColorStateList(getResources(), R.color.white, getTheme()));
        pinView2.setLineColor(
                ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
        pinView2.setLineColor(
                ResourcesCompat.getColorStateList(getResources(), R.color.teal_700, getTheme()));
        pinView2.setItemCount(4);
        pinView2.setItemHeight(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_size));
        pinView2.setItemWidth(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_size));
        pinView2.setItemRadius(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_radius));
        pinView2.setItemSpacing(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_spacing));
        pinView2.setLineWidth(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_line_width));
        pinView2.setAnimationEnable(true);// start animation when adding text
        pinView2.setCursorVisible(false);
        pinView2.setCursorColor(
                ResourcesCompat.getColor(getResources(), R.color.purple_200, getTheme()));
        pinView2.setCursorWidth(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_cursor_width));
        pinView2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }});
        pinView2.setItemBackgroundColor(Color.BLACK);
        //pinView.setItemBackground(getResources().getDrawable(R.drawable.item_background));
       // pinView.setItemBackgroundResources(R.drawable.item_background);
        pinView2.setHideLineWhenFilled(false);
        pinView2.setPasswordHidden(false);
        pinView2.setTransformationMethod(new PasswordTransformationMethod());

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CreatePassCode.this,AccountList.class);
                startActivity(intent);
            }
        });

        save = findViewById(R.id.next);

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
               Integer pin= Integer.valueOf(String.valueOf(pinView.getEditableText()));
               Integer pin2= Integer.valueOf(String.valueOf(pinView2.getEditableText()));
               compare(pin,pin2);
            }
        });

    }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void compare(Integer pin, Integer pin2)
    {
        if(pin.equals(pin2))
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference().child("Users").child(user.getUid()).child("PinCode");
            reference.removeValue();
            String en = " ";
            try {
                en = encrypt(String.valueOf(pin));
                PinCode helperClass = new PinCode(en);
                reference.child(en).setValue(helperClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent=new Intent(CreatePassCode.this,AccountList.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(CreatePassCode.this,"Both Pin Does Not Match",Toast.LENGTH_SHORT).show();
        }
    }
}