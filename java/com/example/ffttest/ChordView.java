package com.example.ffttest;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class ChordView extends ActionBarActivity {
    final static boolean CORRECT = true;
    final static boolean INCORRECT = false;

    private AnalysisManager aManager;
    private NoteAnalyzer nAnalyzer;
    private Thread nThread;

    public Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("MESSAGE", String.format("Handlemessage: msg=%s", msg));
            if(msg.what %10 == 1)
            {
                stringColorSet(msg.what/10, INCORRECT);
                resultTextSet("Incorrect! Try again\nPlay string " + msg.what/10 + 1);
            }
            else if(msg.what%10 == 2)
            {
                stringColorSet(msg.what/10, CORRECT);
                resultTextSet("Correct!");
            }
            else if(msg.what%10 == 0)
                resultTextSet("Listening...\nPlay string " + msg.what/10 + 1);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_view);

        Chord chord = (Chord)getIntent().getParcelableExtra(Grid.PARCELABLE_CHORD);

        final TextView nameTextView = (TextView)findViewById(R.id.name);
        nameTextView.setText(chord.nameGet());

        TextView fretTextView = (TextView)findViewById(R.id.s1);
        fretTextView.setText(""+chord.fretValGet(0));
        fretTextView = (TextView)findViewById(R.id.s2);
        fretTextView.setText(""+chord.fretValGet(1));
        fretTextView = (TextView)findViewById(R.id.s3);
        fretTextView.setText(""+chord.fretValGet(2));
        fretTextView = (TextView)findViewById(R.id.s4);
        fretTextView.setText(""+chord.fretValGet(3));
        fretTextView = (TextView)findViewById(R.id.s5);
        fretTextView.setText(""+chord.fretValGet(4));
        fretTextView = (TextView)findViewById(R.id.s6);
        fretTextView.setText(""+chord.fretValGet(5));

        aManager = new AnalysisManager(chord);
        startChordAnalysis();
    }

    public void onStop() {
        if(nAnalyzer != null)
            nAnalyzer.stopThread();
        if(nThread != null) {
            try {
                nThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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

    private void stringColorSet(int i, boolean isCorrect) {
        TextView fretText;
        //@TODO CHANGE IT!!!
        if(i==0)
            fretText = (TextView)findViewById(R.id.s1);
        else if(i==1)
            fretText = (TextView)findViewById(R.id.s2);
        else if(i==2)
            fretText = (TextView)findViewById(R.id.s3);
        else if(i==3)
            fretText = (TextView)findViewById(R.id.s4);
        else if(i==4)
            fretText = (TextView)findViewById(R.id.s5);
        else
            fretText = (TextView)findViewById(R.id.s6);

        if(isCorrect)
            fretText.setTextColor(Color.GREEN);
        else
            fretText.setTextColor(Color.RED);
    }

    public void resultTextSet(String text)
    {
        TextView resultText = (TextView)findViewById(R.id.results);
        resultText.setText(text);
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
