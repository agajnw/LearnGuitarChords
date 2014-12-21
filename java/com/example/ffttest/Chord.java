package com.example.ffttest;

/**
 * Created by agnieszka on 21.12.14.
 */
public class Chord {
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

    public boolean compareNote(int stringNumber, int fretNumber)
    {
        return (frets[stringNumber]==fretNumber);
    }
}
