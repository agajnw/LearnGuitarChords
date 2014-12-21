package com.example.ffttest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

/**
 * Created by agnieszka on 21.12.14.
 */
public class ChordAdapter extends BaseAdapter{
    private Grid mContext;
    private Chord chords[];

    public ChordAdapter(Grid c){
        mContext = c;

        chords = new Chord[15];
        initChords();
    }

    public int getCount() {
        return chords.length;
    }

    public Object getItem(int position) {
        return chords[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Button button;
        if(convertView == null) {
            button = new Button(mContext);
            button.setText(chords[position].nameGet());
            button.setTextColor(Color.rgb(0, 0, 0));
            button.setBackgroundColor(Color.argb(50, 255, 255, 255));
            button.setClickable(true);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mContext.startChordActivity(chords[position]);
                }
            });
        }
        else {
            button = (Button) convertView;
        }

        return button;
    }

    private void initChords(){
        chords[0] = new Chord("C-dur", 0, 3, 2, 0, 1, 0);
        chords[1] = new Chord("a-moll", 0, 0, 2, 2, 1, 0);
        chords[2] = new Chord("e-moll", 0, 2, 2, 0, 0, 0);
        chords[3] = new Chord("F7+", 0, 0, 3, 2, 1, 0);
        chords[4] = new Chord("G-dur", 3, 2, 0, 0, 3, 3);
        chords[5] = new Chord("d-moll", 0, 0, 0, 2, 3, 1);
        chords[6] = new Chord("E-dur", 0, 2, 2, 1, 0, 0);
        chords[7] = new Chord("D-dur", 0, 0, 0, 2, 3, 2);
        chords[8] = new Chord("A-dur", 0, 0, 2, 2, 2, 0);
        chords[9] = new Chord("G7", 3, 2, 0, 0, 0, 1);
        chords[10] = new Chord("H7", 0, 2, 1, 2, 0, 2);
        chords[11] = new Chord("D7", 0, 0, 0, 2, 1, 2);
        chords[12] = new Chord("a-mol7", 0, 0, 2, 0, 1, 0);
        chords[13] = new Chord("e-moll7", 0, 2, 0, 0, 0, 0);
        chords[14] = new Chord("A7", 0, 0, 2, 0, 2, 0);
        chords[14] = new Chord("C7+", 0, 3, 2, 0, 0, 0);
        chords[14] = new Chord("C7", 0, 3, 2, 3, 1, 0);
        chords[14] = new Chord("E7", 0, 2, 0, 1, 0, 0);
    }

}
