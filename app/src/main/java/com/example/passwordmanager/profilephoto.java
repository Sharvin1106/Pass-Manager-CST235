package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class profilephoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilephoto);


    }

    public void send(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        String id = "insta";
        i.putExtra( "id" , R.drawable.instagramlogo);
        startActivity(i);
    }

    public void send2(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        String id = "fb";
        i.putExtra( "id" , R.drawable.facebooklogo);
        startActivity(i);
    }

    public void send3(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        i.putExtra( "id" , R.drawable.whatsapp);
        startActivity(i);
    }

    public void send4(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        i.putExtra( "id" , R.drawable.tinder);
        startActivity(i);
    }

    public void send5(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        i.putExtra( "id" , R.drawable.tiktok);
        startActivity(i);
    }

    public void send6(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        i.putExtra( "id" , R.drawable.twitterlogo);
        startActivity(i);
    }

    public void send7(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        i.putExtra( "id" , R.drawable.telegramlogo);
        startActivity(i);
    }

    public void send8(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        i.putExtra( "id" , R.drawable.shopping);
        startActivity(i);
    }

    public void send9(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        i.putExtra( "id" , R.drawable.book);
        startActivity(i);
    }

    public void send10(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        i.putExtra( "id" , R.drawable.games);
        startActivity(i);
    }

    public void send11(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        i.putExtra( "id" , R.drawable.browsers);
        startActivity(i);
    }

    public void send12(View v) {
        Intent i = new Intent(profilephoto.this, Encrypt.class);
        i.putExtra( "id" , R.drawable.bankcards);
        startActivity(i);
    }
}