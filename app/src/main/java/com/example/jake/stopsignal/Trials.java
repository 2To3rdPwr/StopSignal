package com.example.jake.stopsignal;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.Scanner;

import static android.R.attr.data;
import static android.R.attr.start;
import static android.R.style.Theme_Material;
import static android.R.style.Theme_Material_Dialog;

public class Trials extends AppCompatActivity {

    static int HARD = 600;
    static int EASY = 1200;
    private int response;
    private long end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trials);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getting previous info file
        File filesDir = getFilesDir();
        final File dataFile = new File(filesDir, "data");
        //alertdialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this, Theme_Material_Dialog);
        builder.setMessage("In the task, you will hear a series of low or high tones." +
                "\nSimply press the left side of the screen you hear a low tone, or the right side when you hear a high tone. " +
                "\nPress the button as quickly and as accurately as you can before the next tone begins. " +
                "\nIn some trials the phone will also vibrate - do not press the button when you feel the vibration. " +
                "\nWhen you are ready to begin, press start to begin the practice trials.");
        builder.setNeutralButton("start", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //user hit ok
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                try {
                    startTrial(dataFile);
                } catch (FileNotFoundException e) {}
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void startTrial(File dataFile) throws FileNotFoundException {
        //setup
        FileOutputStream outputStream;
        int timeAllowed;
        int side;
        long responseTime;
        int answerOut;
        Button low = (Button)findViewById(R.id.low);
        Button high = (Button)findViewById(R.id.high);
        Random rand = new Random();
        Scanner in = new Scanner(dataFile);
        in.next();
        if(in.next().equals("true"))
            timeAllowed = HARD;
        else
            timeAllowed = EASY;

        String data = "";
        //First, play both tones and light up side demonstrating which tone goes where

        //TODO: side demonstration

        //starting test trials
        for(int i = 0; i < 48; i++)
        {
            //resetting repetitive values
            Log.d("Reset","here I am?");
            answerOut = 3;
            response = 3;
            responseTime = timeAllowed;
            //trial
            side = rand.nextInt(2);
            if(side == 0)
            {
                //play left side tone
                low.setText("Here");
                high.setText("");
                Log.d("Here","Left");
            }
            else
            {
                //play right side tone
                high.setText("Here");
                low.setText("");
            }
            final Long time = System.currentTimeMillis();
            end = time + timeAllowed;

            //button click listeners
            Log.d("Here", "ClickListener");
            low.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    response = 0;
                    end = (long)0;
                    Log.d("Clicked", "Clicked!");
                }
            });
            high.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    response = 1;
                    end = (long)0;
                }
            });
            //actually waiting
            //This is a really stupid hack
            while(System.currentTimeMillis() < end)
            {}//nothing happens yet
            //timeAllowed has passed
            responseTime = System.currentTimeMillis() - time;
            //Now we compare response to side
            if(response==side)
                answerOut=1;
            else if(response==3)
                answerOut=3;
            else
                answerOut=2;
            //data collection There are 7 values per trial
            data += i + "\n" + 1 + "\n" + 0 + "\n" + side + "\n" + 0 + "\n" + responseTime + "\n" + answerOut;
            //     trial     block     0=nostop     0=left     stopSignalTime                        1=correct
            //                         1=stop       1=right                                          2=incorrect
            //                                                                                       3=timeout
            //sending data to file
            try {
                outputStream = openFileOutput("data", Context.MODE_PRIVATE);
                outputStream.write(data.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File filesDir = getFilesDir();
        Scanner input = new Scanner(dataFile);
        String yee = "";
        while (input.hasNext())
        {
            yee += input.next() + " | ";
        }
        Log.d("Output", yee);
    }
}