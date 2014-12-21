package com.example.ffttest;

import android.os.Environment;
import android.util.Log;

/**
 * Created by agnieszka on 21.12.14.
 */
public class AnalysisManager {
    private static final boolean INITIAL_DATA = false;
    private static final boolean FFT_DATA = true;
    private static final String SAMPLE_AUDIO_ILE = "storage/emulated/0/Download/sound.ogg";
    public static final String RECORDED_AUDIO_FILE = "/FFTPrototype/ftt_prototype_recording.wav";
    //ftt_prototype_recording

    private Thread thread = null;

    private AudioCapture audioCapture = null;
    private AudioAnalysis audioAnalysis = null;
    private FileManager fileManager = null;

    Chord chord;

    private int dataLength = 4096;
    private String fn;

    public AnalysisManager(Chord nChord) {
        audioAnalysis = new AudioAnalysis(dataLength);
        fileManager = new FileManager(dataLength);
        chord = nChord;

        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();

        fn = filepath + RECORDED_AUDIO_FILE;
    }

    private void listen() {
        audioCapture = new AudioCapture();
        Log.d("ANALYZE", "Audio capture started");
        audioCapture.startListening();
    }

    public boolean analyseNote(int stringNumber) {
        if(chord == null)
            return false;

        listen();//this should not be done in another thread
        return analyseAudio(stringNumber);
    }

    private boolean analyseAudio(int stringNumber)
    {
        double [] dData = fileManager.getAudioData(fn);
        if(dData == null)
            return false;
        double [] fftData = audioAnalysis.analyseAudio(dData);
        if(fftData == null)
            return false;

        int maxBucket = audioAnalysis.findPeak(fftData);
        Log.d("ANALYZE", "Max bucket at " + maxBucket + "- " + maxBucket * 16000 / 4096 + "Hz");
        Log.d("ANALYZE", "checking " + stringNumber + " string, fret " + chord.fretValGet(stringNumber));

       if(audioAnalysis.checkIfMatchString(maxBucket, stringNumber, chord.fretValGet(stringNumber))) {
           Log.d("ANALYZE", "Correct note!");
           return true;
       }
       else {
           Log.d("ANALYZE", "Incorrect note...");
           return false;
       }
    }

}
