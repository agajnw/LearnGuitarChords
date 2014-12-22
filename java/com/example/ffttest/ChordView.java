package com.example.ffttest;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ChordView extends ActionBarActivity {
    final static boolean CORRECT = true;
    final static boolean INCORRECT = false;

    private AnalysisManager aManager;
    private NoteAnalyzer nAnalyzer;
    private Thread nThread;
    private Chord chord;
    private int[][] fretsIds;

    public Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("MESSAGE", String.format("Handlemessage: msg=%s", msg));
            if(msg.what %10 == 1)
            {
                fingerDotColorSet(msg.what/10, INCORRECT);
                resultTextSet("Incorrect!\nTry again");
            }
            else if(msg.what%10 == 2)
            {
                fingerDotColorSet(msg.what/10, CORRECT);
                resultTextSet("Correct!");
                progressUpdate(msg.what/10+1);
            }
            else if(msg.what%10 == 0)
                resultTextSet("Listening...\nPlay string " + (msg.what/10 + 1));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_view);

        chord = (Chord)getIntent().getParcelableExtra(Grid.PARCELABLE_CHORD);

        final TextView nameTextView = (TextView)findViewById(R.id.name);
        nameTextView.setText(chord.nameGet());

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setMax(6);
        progressUpdate(0);

        fingerDotsInit();


        aManager = new AnalysisManager(chord);
        startChordAnalysis();
    }

    public void onStop() {
        if(nAnalyzer != null)
            nAnalyzer.stopThread();

        super.onStop();
    }

    private void startChordAnalysis()
    {
        final Handler checkHandler = new Handler();
        checkHandler.postDelayed(new Runnable() {
            public void run() {
                checkChord();
            }
        }, 2000);
    }

    private void checkChord() {
        resultTextSet("Listening");
        nAnalyzer = new NoteAnalyzer();
        nThread = new Thread(nAnalyzer);
        nThread.start();

    }

    private void fingerDotsInit()
    {
        fretsIds = new int[6][5];
        fretsIds[5][0] = R.id.dot11;
        fretsIds[5][1] = R.id.dot12;
        fretsIds[5][2] = R.id.dot13;
        fretsIds[5][3] = R.id.dot14;
        fretsIds[5][4] = R.id.dot15;

        fretsIds[4][0] = R.id.dot21;
        fretsIds[4][1] = R.id.dot22;
        fretsIds[4][2] = R.id.dot23;
        fretsIds[4][3] = R.id.dot24;
        fretsIds[4][4] = R.id.dot25;

        fretsIds[3][0] = R.id.dot31;
        fretsIds[3][1] = R.id.dot32;
        fretsIds[3][2] = R.id.dot33;
        fretsIds[3][3] = R.id.dot34;
        fretsIds[3][4] = R.id.dot35;

        fretsIds[2][0] = R.id.dot41;
        fretsIds[2][1] = R.id.dot42;
        fretsIds[2][2] = R.id.dot43;
        fretsIds[2][3] = R.id.dot44;
        fretsIds[2][4] = R.id.dot45;

        fretsIds[1][0] = R.id.dot51;
        fretsIds[1][1] = R.id.dot52;
        fretsIds[1][2] = R.id.dot53;
        fretsIds[1][3] = R.id.dot54;
        fretsIds[1][4] = R.id.dot55;

        fretsIds[0][0] = R.id.dot61;
        fretsIds[0][1] = R.id.dot62;
        fretsIds[0][2] = R.id.dot63;
        fretsIds[0][3] = R.id.dot64;
        fretsIds[0][4] = R.id.dot65;

        if(chord == null)
        {
            Log.d("ERR", "Chord is null");
            return;
        }

        for(int i=0;i<6;i++)
        {
            Log.d("DATA", "chord sample val" + chord.fretValGet(i));
            if(chord.fretValGet(i)>0) {
                ImageView dot = (ImageView) findViewById(fretsIds[i][chord.fretValGet(i)-1]);
                dot.setColorFilter(0xf0001933, PorterDuff.Mode.MULTIPLY);
                dot.setVisibility(View.VISIBLE);
            }
        }
    }

    private void fingerDotColorSet(int i, boolean isCorrect) {
        if(chord.fretValGet(i)<1)
            return;

        ImageView dot = (ImageView) findViewById(fretsIds[i][chord.fretValGet(i)-1]);

        if(isCorrect)
            dot.setColorFilter(0xf0195c0b, PorterDuff.Mode.MULTIPLY);
        else
            dot.setColorFilter(0xf08b0e12, PorterDuff.Mode.MULTIPLY);
    }

    private void resultTextSet(String text)
    {
        TextView resultText = (TextView)findViewById(R.id.results);
        resultText.setText(text);
    }

    private void progressUpdate(int value)
    {
        TextView progressText = (TextView)findViewById(R.id.progress);
        progressText.setText(""+value+"/6");

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(value);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chord_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class NoteAnalyzer implements Runnable {
        private boolean running;

        public NoteAnalyzer() {
        }

        public void analyze() {
            for(int i=0;i<6;i++)
            {
                if(!running)
                    return;

                Log.d("ANALYZE", "Analyze string " + i);
                boolean result;

                Message msg = Message.obtain();
                msg.what = 10*i;
                ChordView.this.messageHandler.sendMessage(msg);
                do {
                    if(!running)
                        return;
                    Log.d("ANALYZE", "In do loop " + i);
                    result = aManager.analyseNote(i);
                    if(!result) {
                        if(!running)
                            return;
                        msg = Message.obtain();
                        msg.what = 10*i+1;
                        Log.d("ANALYZE", "Incorrect sound " + i);
                        ChordView.this.messageHandler.sendMessage(msg);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        msg = Message.obtain();
                        msg.what = 10*i;
                        ChordView.this.messageHandler.sendMessage(msg);
                    }
                }
                while(!result);
                Log.d("ANALYZE", "Correct sound " + i);
                msg = Message.obtain();
                msg.what = 10*i+2;
                ChordView.this.messageHandler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }

        public void stopThread() { running = false; }
        public void run() {
            running = true;
            analyze();
        }
    }
}
