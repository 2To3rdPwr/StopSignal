package com.example.jake.stopsignal;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import java.io.FileOutputStream;
import java.util.Random;
import android.os.Handler;


import static android.R.attr.data;
import static android.R.style.Theme_Material_Dialog;
import static com.example.jake.stopsignal.R.id.high;
import static com.example.jake.stopsignal.R.id.low;

public class Trials extends AppCompatActivity {

    static int HARD = 600;
    static int EASY = 1200;
    private int response;
    private String user;
    private String diff;
    private int timeAllowed;
    private int side;
    private String trialdata = "";
    private long startTime;
    private int stopFlag;
    private MediaPlayer mediaPlayer;
    private int responseTime;
    private int vibTrials = 48;
    //total trial data
    private int block1Mean = 0;
    private int nonStopCorrect;
    private int stopCorrect;
    private int timeoutCorrect;
    private int nonStopAveTime;
    private int stopAveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trials);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getting previous info file
        this.diff = getIntent().getStringExtra("EXTRA_DIFF");
        this.user = getIntent().getStringExtra("EXTRA_USERID");
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
                startTrials();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void startTrials() {
        //setup
        if (diff.equals("true"))
            timeAllowed = HARD;
        else
            timeAllowed = EASY;

        //First, play both tones and light up side demonstrating which tone goes where

        //TODO: side demonstration

        //starting test trials
        Log.d("Here","Starting Pretrial 1");
        preTrial(48, false);
    }

    public void preTrial(final int toGo, final boolean block2)
    {
        Log.d("Here", "Pretrial Started");
        response = 3;
        responseTime = timeAllowed;
        Random rand = new Random();
        Button low = (Button) findViewById(R.id.low);
        Button high = (Button) findViewById(R.id.high);
        side = rand.nextInt(2);
        Log.d("Here","SettingSide");
        if(side == 0)
        {
            //play left side tone
            mediaPlayer = MediaPlayer.create(Trials.this, R.raw.low_tone);
            mediaPlayer.start();
            //set a listener to delete the mediaPlayer when it is done
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(mediaPlayer!=null)
                    {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            });
            Log.d("Here","Left");
        }
        else
        {
            //play right side tone
            mediaPlayer = MediaPlayer.create(Trials.this, R.raw.high_tone);
            mediaPlayer.start();
            //set a listener to delete the mediaPlayer when it is done
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(mediaPlayer!=null)
                    {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            });
            Log.d("Here","Right");
        }
        if(block2)
        {
            int whatTrial = rand.nextInt(toGo);
            if(whatTrial <= vibTrials)//decide if this is one of the 48 out of 120
            {
                vibTrials--;
                side = 3;
                stopFlag=1;
            }
            else
                stopFlag=0;
        }

        //actual trial occurs with the listeners
        //CountdownTimer
        final Handler countDown = new Handler();
        //countDown's runnable
        final Runnable timer = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postTrial(toGo-1, block2);
                    }
                });
            }
        };

        Log.d("Here", "CountDownTimer");

        //TODO: vibrate trials
        /**
        if(block2)//&&48 out of 120
        {
            final Handler vibration=new Handler();
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(40);
                }
            };
        }*/

        //button click listeners
        Log.d("Here", "ClickListener");
        low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response = 0;
                Log.d("Clicked", "Clicked!");
                responseTime = (int)(System.currentTimeMillis() - startTime);
            }
        });
        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response = 1;
                Log.d("Clicked", "Clicked!");
                responseTime = (int)(System.currentTimeMillis() - startTime);
            }
        });
        startTime = System.currentTimeMillis();

        //start the timer
        countDown.postDelayed(timer, timeAllowed);
        //block 2 vibration trials
        if(block2 && stopFlag == 1)
        {
            final Handler vibrationTimer = new Handler();
            final Runnable vibrateRunner = new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                            vibrator.vibrate(40);
                        }
                    });
                }
            };
            vibrationTimer.postDelayed(vibrateRunner, block1Mean - 220);
        }
    }

    public void postTrial(int toGo, boolean block2)
    {
        //What trial is this?
        int index;
        if(block2)
            index = 120 - toGo;
        else
            index = 48 - toGo;
        //How did the participant do?
        int answerOut;
        if(response == side)
            answerOut=1;
        else if(response == 3)
            answerOut=3;
        else
            answerOut=2;
        //what block is this test?
        int block;
        if(block2)
            block = 2;
        else
            block = 1;
        //SAVE THE DATA!!!
        trialdata += index + "\n" + block + "\n" + stopFlag + "\n" + side + "\n" + (stopFlag*(block1Mean-220)) + "\n" + responseTime + "\n" + answerOut + "\n";
        //            trial         block         0=nostop         0=left               stopSignalTime                                       1=correct
        //                                        1=stop           1=right                                                                   2=incorrect
        //                                                                                                                                   3=TimedOut

        //Where to from here?
        if(toGo == 0 && !block2) {//block 1 done. Go to block 2
            preTrial(120, true);
        }
        else if(toGo == 0 && block2)//block 2 done. Go
            done(trialdata);
        else
            preTrial(toGo, block2);
    }

    public void done(String trialdata)
    {
        //TODO: process that long-ass string into a document for human consumption
    }
}
