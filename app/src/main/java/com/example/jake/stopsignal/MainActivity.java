package com.example.jake.stopsignal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    private String userID;
    private Boolean difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isStoragePermissionGranted();
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void redo(View view)
    {
        TextView s = (TextView)findViewById(R.id.confirm_plz);
        s.setText("");
        Button back = (Button)findViewById(R.id.back);
        back.setVisibility(View.INVISIBLE);
        Button cont = (Button)findViewById(R.id.cont);
        cont.setVisibility(View.INVISIBLE);
        EditText in = (EditText)findViewById(R.id.participant);
        in.setVisibility(View.VISIBLE);
        Button b = (Button)findViewById(R.id.id_submit);
        b.setVisibility(View.VISIBLE);
    }

    public void submitID(View view)
    {
        EditText in = (EditText)findViewById(R.id.participant);
        userID = in.getText().toString();
        in.setVisibility(View.INVISIBLE);
        Button b = (Button)findViewById(R.id.id_submit);
        b.setVisibility(View.INVISIBLE);
        Button be = (Button)findViewById(R.id.easy_button);
        be.setVisibility(View.VISIBLE);
        Button bh = (Button)findViewById(R.id.hard_button);
        bh.setVisibility(View.VISIBLE);
    }
    //after ID is submitted
    public void easy(View view)
    {
        difficulty = false;
        Button be = (Button)findViewById(R.id.easy_button);
        be.setVisibility(View.INVISIBLE);
        Button bh = (Button)findViewById(R.id.hard_button);
        bh.setVisibility(View.INVISIBLE);
        summary();
    }

    public void hard(View view)
    {
        difficulty = true;
        Button be = (Button)findViewById(R.id.easy_button);
        be.setVisibility(View.INVISIBLE);
        Button bh = (Button)findViewById(R.id.hard_button);
        bh.setVisibility(View.INVISIBLE);
        summary();
    }
    //summary & confirmation
    public void summary()
    {
        TextView s = (TextView)findViewById(R.id.confirm_plz);
        String cdtn;
        if(difficulty)
            cdtn = "2";
        else
            cdtn = "1";
        String sString = "Participant: " + userID + "\nCondition: " + cdtn;
        s.setText(sString);
        Button back = (Button)findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        Button cont = (Button)findViewById(R.id.cont);
        cont.setVisibility(View.VISIBLE);
    }
    //All it good. Continue to experiment
    public void cont(View view) throws FileNotFoundException {
        //save data
        //Next activity
        Intent next = new Intent(MainActivity.this, Trials.class);
        next.putExtra("EXTRA_USERID", userID);
        next.putExtra("EXTRA_DIFF", difficulty.toString());
        startActivity(next);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Perms","Permission is granted");
                return true;
            } else {

                Log.v("Perms","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Perms","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("Perms","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //setContentView(R.layout.activity_main);

            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
