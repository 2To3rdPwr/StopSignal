package com.example.jake.stopsignal;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.StaticLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import java.util.Random;
import android.os.Handler;


import static android.R.style.Theme_Material_Dialog;

public class Trials extends AppCompatActivity {

    static int HARD = 1000;
    static int EASY = 2000;
    static int INTERTRIAL = 2200;
    //DEBUG settings
    static boolean DEBUG = false;//for debugging
    //TODO: add more debug settings

    private int response;
    private String user;
    private String diff;
    private int timeAllowed;
    private int side;
    private String[][] trialdata = new String[240][6];
    private long startTime;
    private int stopFlag = 0;
    private MediaPlayer mediaPlayer;
    private int responseTime;
    private int vibTrials = 47;
    //total trial data
    //private double block1Mean = 0;
    private double nonStopCorrect = 0;
    private double stopCorrect = 0;
    private double timeout = 0;
    private double nonStopAveTime = 0;
    private double stopAveTime = 0;
    private int streak = 0;
    private boolean correct = false;
    private double block1Mean = 0;
    private double block1PMean = 0;
    private double block2Mean = 0;
    private double block2PMean = 0;
    private int block1PTrials = 0;
    private int block2PTrails = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trials);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getting previous info file
        this.diff = getIntent().getStringExtra("EXTRA_DIFF");
        this.user = getIntent().getStringExtra("EXTRA_USERID");

        //alertdialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(Trials.this, Theme_Material_Dialog);
        builder.setMessage(R.string.first_box);
        builder.setNeutralButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //user hit ok
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //alertdialog box
                AlertDialog.Builder builder2 = new AlertDialog.Builder(Trials.this, Theme_Material_Dialog);
                builder2.setMessage(R.string.second_box);
                builder2.setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //user hit ok
                        dialog.dismiss();
                    }
                });
                builder2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(DEBUG)
                            startTrials();
                        else
                            demoTones();
                    }
                });

                AlertDialog dialog2 = builder2.create();
                dialog2.show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void demoTones()
    {
        final Button lowButton = (Button)findViewById(R.id.low);
        final Button highButton = (Button)findViewById(R.id.high);
        final boolean h = false;
        boolean l = false;

        //lowButton demo
        lowButton.setText("[Press for low tone]");
        lowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = MediaPlayer.create(Trials.this, R.raw.low_tone);
                mediaPlayer.start();
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
            }
        });
        //high button demo
        highButton.setText("[Press for high tone]");
        highButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = MediaPlayer.create(Trials.this, R.raw.high_tone);
                mediaPlayer.start();
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
            }
        });

        //continue to practice block 1
        final Button contButton = (Button)findViewById(R.id.contButton);
        contButton.setVisibility(View.VISIBLE);
        contButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contButton.setVisibility(View.GONE);
                highButton.setText("");
                lowButton.setText("");

                //alertdialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(Trials.this, Theme_Material_Dialog);
                builder.setMessage(R.string.trial1p);
                builder.setNeutralButton("Practice", new DialogInterface.OnClickListener() {
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
        });
    }

    public void startTrials() {
        //setup
        if (diff.equals("true"))
            timeAllowed = HARD;
        else
            timeAllowed = EASY;

        //starting test trials
        Log.d("Here","Starting Pretrial 1P");
        if(DEBUG)
            interTrial(17, false, true);
        else
            ready(17, false, true);
    }

    public void interTrial(final int toGo, final boolean block2, final boolean practice)
    {
        Random rand = new Random();
        stopFlag = 0;
        //Is this a stop-trial?
        if(block2) {
            if (practice) {
                int whatTrial = rand.nextInt(4);
                if (whatTrial == 1)
                    stopFlag = 1;
                else
                    stopFlag = 0;
            } else {
                int whatTrial = rand.nextInt(toGo);
                if (whatTrial <= vibTrials)//decide if this is one of the 48 out of 120
                {
                    vibTrials--;
                    stopFlag = 1;
                } else
                    stopFlag = 0;
            }
        }
        final Handler countDown = new Handler();
        //countDown's runnable
        final Runnable timer = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        preTrial(toGo, block2, practice);
                    }
                });
            }
        };
        if(stopFlag == 1)
        {
            final Handler vibDown = new Handler();
            //countDown's runnable
            final Runnable vibTime = new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                            vibrator.vibrate(80);
                        }
                    });
                }
            };
            if(!DEBUG)
                vibDown.postDelayed(vibTime, (long)(INTERTRIAL - (block1Mean - 225)));
        }
        if(DEBUG)
            countDown.postDelayed(timer, 0);
        else
            countDown.postDelayed(timer, INTERTRIAL);
    }

    public void preTrial(final int toGo, final boolean block2, final boolean practice)
    {
        Log.d("Here", "Pretrial Started");
        response = 3;
        responseTime = timeAllowed;
        final Random rand = new Random();
        Button low = (Button) findViewById(R.id.low);
        Button high = (Button) findViewById(R.id.high);
        side = rand.nextInt(2);
        Log.d("Here","SettingSide");
        if(side == 0 && !DEBUG)
        {
            //play left side tone
            if(diff.equals("true"))
                mediaPlayer = MediaPlayer.create(Trials.this, R.raw.short_low_tone);
            else
                mediaPlayer = MediaPlayer.create(Trials.this, R.raw.long_low_tone);
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
        else if(side == 1 && !DEBUG)
        {
            //play right side tone
            if(diff.equals("true"))
                mediaPlayer = MediaPlayer.create(Trials.this, R.raw.short_high_tone);
            else
                mediaPlayer = MediaPlayer.create(Trials.this, R.raw.long_high_tone);
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


        //set side to 3 for stop-trials
        if(stopFlag == 1)
            side = 3;
        //actual trial occurs with the listeners
        //CountdownTimer Listener
        Log.d("Here", "CountDownTimer");
        final Handler countDown = new Handler();
        //countDown's runnable
        final Runnable timer = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postTrial(toGo-1, block2, practice);
                    }
                });
            }
        };

        //button click listeners
        Log.d("Here", "ClickListener");
        low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response = 0;
                Log.d("Clicked", "Clicked!");
                responseTime = (int)(System.currentTimeMillis() - startTime);
                if(mediaPlayer!=null)
                {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });
        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response = 1;
                Log.d("Clicked", "Clicked!");
                responseTime = (int)(System.currentTimeMillis() - startTime);
                if(mediaPlayer!=null)
                {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });
        startTime = System.currentTimeMillis();

        //start the timer
        if(DEBUG)
            countDown.postDelayed(timer, 0);
        else
            countDown.postDelayed(timer, timeAllowed);
    }

    public void postTrial(int toGo, boolean block2, boolean practice)
    {
        if(!practice) {//non-practice trials
            //What trial is this?
            int index;
            if (block2)
                index = 192 - toGo;
            else
                index = 48 - toGo;
            int yee = index;
            if (block2)
                yee += 48;
            //How did the participant do?
            int answerOut;
            if (response == side)
                answerOut = 1;
            else if (response == 3)
                answerOut = 3;
            else
                answerOut = 2;
            //what block is this test?
            int block;
            if (block2)
                block = 2;
            else
                block = 1;

            //DEBUG settings to override collected data
            if(DEBUG) {
                answerOut = 1;//set answer status; 1=correct, 2=incorrect, 3=timeOut
                responseTime = 500;//set response time in ms
            }
            //SAVE THE DATA!!!
            //switch this single string to an int array
            String[] d = new String[6];
            d[0] = Integer.toString(index);
            d[1] = Integer.toString(block);
            d[2] = Integer.toString(stopFlag);
            d[3] = Integer.toString(side);
            d[4] = Integer.toString(responseTime);
            d[5] = Integer.toString(answerOut);
            //Log.i("ThisTrial'sData", d);
            trialdata[yee - 1] = d;
            //Calculating averages and such
            if (stopFlag == 0) {
                nonStopAveTime += responseTime;
                if (answerOut == 1)
                    nonStopCorrect++;
            } else {
                stopAveTime += responseTime;
                if (answerOut == 1)
                    stopCorrect++;
            }
            if (response == 3)
                timeout++;
            if(block2)
                block2Mean += responseTime;
            else
                block1Mean += responseTime;
        }
        else {//practice trials
            if(response == side)
                streak++;
            else
                streak = 0;
            if(block2) {
                block2PMean += responseTime;
                block2PTrails++;
            }
            else {
                block1PMean += responseTime;
                block1PTrials++;
            }
            Log.d("Streak",Integer.toString(streak));
            Log.d("TOGO", Integer.toString(toGo));
        }


        //Where to from here?
        if(toGo <= 0 && (streak >= 9 || DEBUG) && !block2 && practice)//practice 1 is done. Go to block 1
        {
            if(toGo <= 0 && (streak >= 9 || DEBUG))
            {
                //alertdialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(this, Theme_Material_Dialog);
                builder.setMessage(R.string.trial1);
                builder.setNeutralButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //user hit ok
                        dialog.dismiss();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(DEBUG)
                            interTrial(48, false, false);
                        else
                            ready(48, false, false);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        else if(toGo == 0 && !block2 && !practice) {//block 1 done. Go to practice 2
            streak = 0;
            //set block1Mean
            block1Mean = block1Mean / 48;
            //alertdialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(this, Theme_Material_Dialog);
            builder.setMessage(R.string.trial2p);
            builder.setNeutralButton("Practice", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //user hit ok
                    dialog.dismiss();
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(DEBUG)
                        interTrial(17, true, true);
                    else
                        ready(17, true, true);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if(toGo <= 0 && (streak >= 9 || DEBUG) && block2 && practice)//practice 2 is done. Go to block 2
        {
            if(toGo <= 0 && (streak >= 9 || DEBUG))
            {
                //alertdialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(this, Theme_Material_Dialog);
                builder.setMessage(R.string.trial2);
                builder.setNeutralButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //user hit ok
                        dialog.dismiss();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(DEBUG)
                            interTrial(192, true, false);
                        else
                            ready(192, true, false);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        else if(toGo == 0 && block2 && !practice)//block 2 done. Go to ThankYou
            done();
        else {//continue with this block
            interTrial(toGo, block2, practice);
        }
    }

    public void done()
    {
        Bundle b = new Bundle();
        b.putSerializable("EXTRA_DATA", trialdata);
        stopAveTime = stopAveTime / 48;
        nonStopAveTime = nonStopAveTime / 192;
        stopCorrect = stopCorrect / 48 * 100;
        nonStopCorrect = nonStopCorrect / 192 * 100;
        timeout = timeout / 240 * 100;
        block1PMean = block1PMean / block1PTrials;
        block2PMean = block2PMean / block2PTrails;
        block2Mean = block2Mean / 192;

        Intent next = new Intent(Trials.this, ThankYou.class);
        next.putExtra("EXTRA_USERID", user);
        next.putExtra("EXTRA_DIFF", diff);
        next.putExtras(b);
        next.putExtra("EXTRA_RESPONSE_TIME_STOP", Double.toString(stopAveTime));
        next.putExtra("EXTRA_RESPONSE_TIME_GO", Double.toString(nonStopAveTime));
        next.putExtra("EXTRA_STOP_CORRECT", Double.toString(stopCorrect));
        next.putExtra("EXTRA_GO_CORRECT", Double.toString(nonStopCorrect));
        next.putExtra("EXTRA_TIMEOUT", Double.toString(timeout));
        next.putExtra("EXTRA_VIBTIME", Integer.toString((int)(block1Mean - 225)));
        next.putExtra("EXTRA_B1MEAN", Double.toString(block1Mean));
        next.putExtra("EXTRA_B2MEAN", Double.toString(block2Mean));
        next.putExtra("EXTRA_B1PMEAN", Double.toString(block1PMean));
        next.putExtra("EXTRA_B2PMEAN", Double.toString(block2PMean));
        startActivity(next);
    }
    public void ready(final int toGo, final boolean block2, final boolean practice)
    {
        final Handler countDown = new Handler();
        //countDown's runnable
        final Runnable timer = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer = MediaPlayer.create(Trials.this, R.raw.ready);
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(mediaPlayer!=null)
                        {
                            mediaPlayer.release();
                            mediaPlayer = null;
                            set(toGo, block2, practice);
                        }
                    }
                });
            }
        };
        countDown.postDelayed(timer, 50);
    }
    public void set(final int toGo, final boolean block2, final boolean practice)
    {
        final Handler countDown = new Handler();
        //countDown's runnable
        final Runnable timer = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer = MediaPlayer.create(Trials.this, R.raw.set);
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(mediaPlayer!=null)
                        {
                            mediaPlayer.release();
                            mediaPlayer = null;
                            go(toGo, block2, practice);
                        }
                    }
                });
            }
        };
        countDown.postDelayed(timer, 50);
    }
    public void go(final int toGo, final boolean block2, final boolean practice)
    {
        final Handler countDown = new Handler();
        //countDown's runnable
        final Runnable timer = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer = MediaPlayer.create(Trials.this, R.raw.go);
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(mediaPlayer!=null)
                        {
                            mediaPlayer.release();
                            mediaPlayer = null;
                            interTrial(toGo, block2, practice);
                        }
                    }
                });
            }
        };
        countDown.postDelayed(timer, 50);
    }
}
