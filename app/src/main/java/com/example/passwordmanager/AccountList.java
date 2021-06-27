package com.example.passwordmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;

public class AccountList extends AppCompatActivity implements LogOutTimerUtil.LogOutListener, LifecycleObserver {

    private BottomSheetDialog bsd;
    private TextView e,u,p,n,a;
    private int position;
    private ImageButton viewer,delete,edit;
    private ImageView i;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    private int position_from_pin_page,x_from_pin_page;
    String child;

    List<account> fetchdata;
    RecyclerView rv;
    AccountAdapter aa;
    DatabaseReference dr;

    Button encrypt;
    String decrypted,decrypted2;

    private Toolbar toolbar;

    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";

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
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
    }

    @Override
    public void doLogout() {
        Intent intent = new Intent(AccountList.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Performing idle time logout
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);

        toolbar=findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);

        rv = findViewById(R.id.recyclerview);

        try{position_from_pin_page = Integer.parseInt(getIntent().getStringExtra("position"));
            x_from_pin_page = Integer.parseInt(getIntent().getStringExtra("x")); child=getIntent().getStringExtra("delete_child");
        }catch(Exception e){}

        fetchdata = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        rootNode = FirebaseDatabase.getInstance();
        dr = rootNode.getReference().child("Users").child(user.getUid()).child("Accounts");

        if(x_from_pin_page==2)
        {
            dr.child(child).removeValue();
        }

        dr.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    account acc_list=dataSnapshot.getValue(account.class);
                    fetchdata.add(acc_list);
                }
                setCards(fetchdata);

                if(x_from_pin_page==1)
                {strDialog(position_from_pin_page,1);}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.add:
                startActivity(new Intent(AccountList.this, Encrypt.class));
                break;

            case R.id.setting:
                startActivity(new Intent(AccountList.this, Preference.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setCards(List<account> fetchdata){
        rv = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        rv.setLayoutManager(layoutManager);
        aa = new AccountAdapter(this,fetchdata);

        aa.ocl(new View.OnClickListener(){

            //@RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                position = rv.getChildAdapterPosition(v);
                strDialog(position,0);
            }
        });
        rv.setAdapter(aa);
    }


    //@RequiresApi(api = Build.VERSION_CODES.O)
    public void strDialog(int pn, int x)
    {

        bsd = new BottomSheetDialog(AccountList.this);
        View view=getLayoutInflater().from(AccountList.this).inflate(R.layout.layout_bottom_sheet2,null);

        String pass,user;

        u = view.findViewById(R.id.username);
        e = view.findViewById(R.id.email);
        p = view.findViewById(R.id.password);
        n = view.findViewById(R.id.notes);
        a = view.findViewById(R.id.appname);
        i=view.findViewById(R.id.roundview);
        viewer = view.findViewById(R.id.view);
        delete=view.findViewById(R.id.delete);

        if(pn != -1)
        {

            n.setText(fetchdata.get(pn).getNotes());
            a.setText(fetchdata.get(pn).getApp_name());
            e.setText(fetchdata.get(pn).getE_mail());
            i.setImageResource(Integer.parseInt(fetchdata.get(pn).getLogo()));
            pass=fetchdata.get(pn).getPass_word();
            user=fetchdata.get(pn).getUser_name();

            if(x==1)
            {
                try {
                    decrypted=decrypt(pass);
                    decrypted2=decrypt(user);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                p.setText(decrypted);
                u.setText(decrypted2);
            }
            else
            {
                p.setText("##########");
                u.setText("##########");
            }


        }

        viewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AccountList.this, PasscodeView.class);
                String p = String.valueOf(position);
                String travel="view";
                intent.putExtra("position", p);
                intent.putExtra("travel",travel);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AccountList.this, PasscodeView.class);
                String p = String.valueOf(position);
                String travel="delete";
                intent.putExtra("position", p);
                intent.putExtra("travel",travel);
                intent.putExtra("child",fetchdata.get(Integer.parseInt(p)).getApp_name());
                startActivity(intent);
            }
        });

        bsd.setContentView(view);
        bsd.show();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //bsd.dismiss();
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