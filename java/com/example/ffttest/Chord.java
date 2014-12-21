package com.example.ffttest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by agnieszka on 21.12.14.
 */
public class Chord implements Parcelable {
    private String name;
    private int frets[];
    //@TODO tips for playing - which finger where

    public Chord(String n, int s1, int s2, int s3, int s4, int s5, int s6){
        frets = new int[6];

        name = n;
        frets[0] = s1;
        frets[1] = s2;
        frets[2] = s3;
        frets[3] = s4;
        frets[4] = s5;
        frets[5] = s6;
    }

    public String nameGet()
    {
        return name;
    }

    public int fretValGet(int stringNumber)
    {
        return frets[stringNumber];
    }

    public boolean compareNote(int stringNumber, int fretNumber)
    {
        return (frets[stringNumber]==fretNumber);
    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeIntArray(frets);
    }

    public static final Parcelable.Creator<Chord> CREATOR
            = new Parcelable.Creator<Chord>() {
        public Chord createFromParcel(Parcel in) {
            return new Chord(in);
        }

        public Chord[] newArray(int size) {
            return new Chord[size];
        }
    };

    private Chord(Parcel in) {
        name = in.readString();
        frets = in.createIntArray();
    }
}
