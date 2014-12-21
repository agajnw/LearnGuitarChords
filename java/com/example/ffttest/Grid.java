package com.example.ffttest;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


public class Grid extends ActionBarActivity {
    public final static String PARCELABLE_CHORD = "com.example.fftest.CHORD";

    public Grid(){
    }

    public void startChordActivity(Chord chord) {
        Bundle b = new Bundle();
        b.putParcelable(PARCELABLE_CHORD, chord);

        Intent intent = new Intent(this, ChordView.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chord_grid);

        GridView gridView = (GridView)findViewById(R.id.gridview);
        gridView.setAdapter(new ChordAdapter(this));
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
}
