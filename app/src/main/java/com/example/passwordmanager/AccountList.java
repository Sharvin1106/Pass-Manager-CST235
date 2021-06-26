package com.example.passwordmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class AccountList extends AppCompatActivity {

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

    private static final int STORAGE_REQUEST_CODE_EXPORT=1;
    private static final int STORAGE_REQUEST_CODE_IMPORT=2;
    private String[] storagePermission;

    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);
        rv = findViewById(R.id.recyclerview);

        try{position_from_pin_page = Integer.parseInt(getIntent().getStringExtra("position"));
            x_from_pin_page = Integer.parseInt(getIntent().getStringExtra("x")); child=getIntent().getStringExtra("delete_child");
        }catch(Exception e){}


        fetchdata = new ArrayList<>();

        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
        bsd.dismiss();
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



    private boolean check(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestExport(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE_EXPORT);
    }

    private void requestImport(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE_IMPORT);
    }

    private void exportCSV() {
        File folder=new File(Environment.getExternalStorageDirectory()+"/"+"BackUp");
        //File folder = new File(String.valueOf(getExternalFilesDir("BackUp")));
        boolean isFolderCreated = false;
        if(!folder.exists()){
            isFolderCreated = folder.mkdir();
        }

        Log.d("CSC_TAG","exportCSV: "+isFolderCreated);

        String csvFilename = "BackUp.csv";

        String fileP= folder.toString() + "/" + csvFilename;

        try {
            FileWriter fw = new FileWriter(fileP,false);
            for(int i =0;i< fetchdata.size();i++)
            {
                fw.append(""+fetchdata.get(i).getApp_name());
                fw.append(",");
                fw.append(""+fetchdata.get(i).getUser_name());
                fw.append(",");
                fw.append(""+fetchdata.get(i).getPass_word());
                fw.append(",");
                fw.append(""+fetchdata.get(i).getE_mail());
                fw.append(",");
                fw.append(""+fetchdata.get(i).getNotes());
                fw.append(",");
                fw.append(""+fetchdata.get(i).getLast_edited());
                fw.append(",");
                fw.append(""+fetchdata.get(i).getLogo());
                fw.append("\n");
            }
            fw.flush();
            fw.close();

            Toast.makeText(this,""+getFilesDir(),Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

        }
    }

    private void importCSV() {

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("Account");

        String path=Environment.getExternalStorageDirectory()+"/BackUp/"+"BackUp.csv";
        File csvfile = new File(path);

        String appname,username,password,email,notes,lastedited,logo;

        if(csvfile.exists()){

            String test = null;
            try{
                CSVReader csvReader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));

                String[] nextline;
                while((nextline=csvReader.readNext())!=null){

                    appname=nextline[0];
                    username=nextline[1];
                    password=nextline[2];
                    email=nextline[3];
                    notes=nextline[4];
                    lastedited=nextline[5];
                    logo=nextline[6];

                    account helperClass = new account(appname,username,password,email,notes,lastedited,logo);
                    reference.child(appname).setValue(helperClass);
                }

                Toast.makeText(this,"YES IMPORTED",Toast.LENGTH_SHORT).show();

            }
            catch(Exception e){
                Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this,"No Back Up Found...",Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        switch (requestCode){
            case STORAGE_REQUEST_CODE_EXPORT:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    exportCSV();
                }
                else{
                    Toast.makeText(this,"Storage Permission Required...",Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE_IMPORT:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    importCSV();
                }
                else{
                    Toast.makeText(this,"Storage Permission Required...",Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

}