package com.example.jake.stopsignal;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import static java.util.Calendar.getInstance;

public class ThankYou extends AppCompatActivity {

    private String diff;
    private int[][] trialsData = new int[240][6];
    private String user;
    private double stopTimeAve;
    private double nonStopTimeAve;
    private double percentTimeout;
    private double percentCorrectGo;
    private double percentCorrectStop;
    private double b1MRT;
    private double b2MRT;
    private double b1PMRT;
    private double b2PMRT;
    private int vibTime;

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
        setContentView(R.layout.activity_thank_you);

        this.diff=getIntent().getStringExtra("EXTRA_DIFF");
        //Trials data!
        String[][] dataInterpreter = null;
        Object[] objectArray = (Object[]) getIntent().getExtras().getSerializable("EXTRA_DATA");
        dataInterpreter = new String[objectArray.length][];
        for(int i=0;i<objectArray.length;i++){
            dataInterpreter[i]=(String[]) objectArray[i];
        }
        for(int ii = 0; ii < dataInterpreter.length; ii++)
        {
            for(int j = 0; j < dataInterpreter[0].length; j++)
            {
                trialsData[ii][j] = Integer.parseInt(dataInterpreter[ii][j]);
            }
        }
        this.user=getIntent().getStringExtra("EXTRA_USERID");
        this.stopTimeAve=Double.parseDouble(getIntent().getStringExtra("EXTRA_RESPONSE_TIME_STOP"));
        this.nonStopTimeAve=Double.parseDouble(getIntent().getStringExtra("EXTRA_RESPONSE_TIME_GO"));
        this.percentCorrectGo=Double.parseDouble(getIntent().getStringExtra("EXTRA_GO_CORRECT"));
        this.percentCorrectStop=Double.parseDouble(getIntent().getStringExtra("EXTRA_STOP_CORRECT"));
        this.percentTimeout=Double.parseDouble(getIntent().getStringExtra("EXTRA_TIMEOUT"));
        this.b1MRT=Double.parseDouble(getIntent().getStringExtra("EXTRA_B1MEAN"));
        this.b1PMRT=Double.parseDouble(getIntent().getStringExtra("EXTRA_B1PMEAN"));
        this.b2MRT=Double.parseDouble(getIntent().getStringExtra("EXTRA_B2MEAN"));
        this.b2PMRT=Double.parseDouble(getIntent().getStringExtra("EXTRA_B2PMEAN"));
        this.vibTime=Integer.parseInt(getIntent().getStringExtra("EXTRA_VIBTIME"));

        parseData();
    }

    /**
     * Interprets that long-ass string for human consumption
     * @return out: a nicely formatted string fit for emailing
     */
    public void parseData()
    {
        Calendar c = getInstance();
        String fileName = (c.get(Calendar.MONTH)+1) + "." + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ".txt";
        File file = new File(getDocStorageDir("StopTrials"), fileName);
        Log.d("File", fileName);
        String out = "", add;
        out = "Participant ID: " + user + "      Trial type: ";
        if(diff.equals("true"))
            out += "Condition 2\n";
        else
            out += "Condition 1\n";

        out += " Trial, Block, Trial Type, Required Response, Response Time, Status\n";
        toFile(file, out);
        for(int i = 0; i < 240; i++) {
            //out += "       |       |            |                    |                      |               |       \n";
            add = Integer.toString(trialsData[i][0]);
            out = add + ", ";
            add = Integer.toString(trialsData[i][1]);
            out += add + ", ";
            add = Integer.toString(trialsData[i][2]);
            if (add.equals("1"))
                add = " Stop, ";
            else
                add = " No Stop, ";
            out += add;
            add = Integer.toString(trialsData[i][3]);
            if (add.equals("0"))
                add = "Low, ";
            else if (add.equals("1"))
                add = "High, ";
            else
                add = "Time Out, ";
            out += add;
            add = Integer.toString(trialsData[i][4]);
            out += add + ", ";
            add = Integer.toString(trialsData[i][5]);
            if (add.equals("1"))
                add = " Correct\n";
            else if (add.equals("2"))
                add = " Incorrect\n";
            else
                add = " Time Out\n";
            out += add;
            toFile(file, out);
        }
        out = String.format("%s %.0f %s", "Average response time for block 1: ", b1MRT, "ms\n");
        out += String.format("%s %.0f %s", "Average response time for block 2: ", b2MRT, "ms\n");
        out += String.format("%s %.0f %s", "Average response time for block 1 Practice: ", b1PMRT, "ms\n");
        out += String.format("%s %.0f %s", "Average response time for block 2 Practice: ", b2PMRT, "ms\n");
        out += String.format("%s %.0f %s", "Average response time in stop trials: ", stopTimeAve, "ms\n");
        out += String.format("%s %.0f %s", "Average response time in non-stop trials: ", nonStopTimeAve, "ms\n");
        out += String.format("%s %d %s", "Time between vibration and tone: ", vibTime, "ms\n");
        out += String.format("%s %.2f %s", "% of stop trials correct: ", percentCorrectStop, "%\n");
        out += String.format("%s %.2f %s", "% of non-stop trials correct: ", percentCorrectGo, "%\n");
        out += String.format("%s %.2f %s", "% of trials that timed out: ", percentTimeout, "%\n");
        toFile(file, out);
    }

    public File getDocStorageDir(String fileDir) {
          // Get the directory for the user's public pictures directory.
          File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileDir);
          if (!file.mkdirs()) {
                //Log.e(LOG_TAG, "Directory not created");
              }
          return file;
    }

    public void toFile(File file, String out)
    {
        try {
            FileWriter fStream = new FileWriter(file, true);
            PrintWriter fileOut = new PrintWriter(new BufferedWriter(fStream));
            fileOut.write(out);
            fileOut.close();
            Log.d("File:", out);
        } catch (IOException e) {
            Log.e("FILE", "file not written to");
        }
    }
}
