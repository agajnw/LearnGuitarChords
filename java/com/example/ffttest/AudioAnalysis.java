package com.example.ffttest;

import com.example.ffttest.FFT;

import android.util.Log;

//to change , code frome here - https://www.ee.columbia.edu/~ronw/code/MEAPsoft/doc/html/FFT_8java-source.html
	
public class AudioAnalysis {

	public final int MAX_BUCKET = 110; //max frequency produced by guitar is 1174.624Hz
    public final int STRING_1 = 0;
    public final int STRING_2 = 1;
    public final int STRING_3 = 2;
    public final int STRING_4 = 3;
    public final int STRING_5 = 4;
    public final int STRING_6 = 5;

    public final int FRET_0 = 0;
    public final int FRET_1 = 1;
    public final int FRET_2 = 2;
    public final int FRET_3 = 3;
    public final int FRET_4 = 4;
    public final int FRET_5 = 5;
	
	private int dataLength;
	private FFT fft;

    private int stringBins[][]; //for 6 strings, 5 frets - bins in which produced frequency will land


	// Lookup tables. Only need to recompute when size of FFT changes.
	double[] cos;
	double[] sin;

	public AudioAnalysis(int n) {
	    dataLength = n;

        initStringBins();
		  
		fft = new FFT(dataLength);
	}

	public double[] analyseAudio(double[] dataToAnalyse)
	{
	    double [] resultData = new double[dataLength];
		double [] afterHanning = hanningWindow(dataToAnalyse, dataLength);

		fft.fft(afterHanning,  resultData);
		  
		return resultData;
	  }
	  
	double [] hanningWindow(double[] data, int size)
	{
	    double [] result = new double[size];
		for(int i=0;i<size;i++)
		{
		    Log.d("FFT", "before " + data[i]);
			result[i] = (double)(data[i]*0.5*(1.0 - Math.cos(2.0*Math.PI*i/(size-1))));
			Log.d("FFT", "after " + data[i]);
		}
		  
		return result;
	}
	  
    public int findPeak(double [] data)
    {
	    //max: C6 - 1046.50hz
		double maxValue = 0;
		int maxI = 0;

		for(int i=0;i<MAX_BUCKET;i++)
		{
			Log.d("VALUE", "Value of " + i + "is " + data[i]);
			if(data[i]>=maxValue)
			{
				maxValue = data[i];
				maxI = i;
			}
		}
		Log.d("PEAK", "Peak is at " + maxI + ", value " + maxValue);
		return maxI;
	 }

    //ew. dodatkowo sprawdzanie odleglosci miedzy szczytami
   public boolean checkIfMatchString(int peakValue, int stringNumber, int fretNumber)
   {
       if(peakValue == stringBins[stringNumber][fretNumber])
           return true;
       if(peakValue == 2*stringBins[stringNumber][fretNumber]) //if peakValue is the first harmonic
           return true;
       //for now accept value greater by 1
       if(peakValue-1 == stringBins[stringNumber][fretNumber])
           return true;
       if((peakValue-1) == 2*stringBins[stringNumber][fretNumber])
           return true;

       return false;
   }

    public void findMatch(int peakValue)
    {
        for(int i=0;i<6;i++)
            for(int j=0;j<6;j++)
            {
                if(checkIfMatchString(peakValue, i,j))
                    Log.d("MATCH", "note on " + i+1 + " string, " + j + "fret");
            }
    }

   private void initStringBins()
   {
       stringBins = new int[6][6];

       stringBins[STRING_1][FRET_0] = 21;
       stringBins[STRING_1][FRET_1] = 22;
       stringBins[STRING_1][FRET_2] = 23;
       stringBins[STRING_1][FRET_3] = 25;
       stringBins[STRING_1][FRET_4] = 26;
       stringBins[STRING_1][FRET_5] = 28;

       stringBins[STRING_2][FRET_0] = 28;
       stringBins[STRING_2][FRET_1] = 29;
       stringBins[STRING_2][FRET_2] = 31;
       stringBins[STRING_2][FRET_3] = 33;
       stringBins[STRING_2][FRET_4] = 35;
       stringBins[STRING_2][FRET_5] = 37;

       stringBins[STRING_3][FRET_0] = 37;
       stringBins[STRING_3][FRET_1] = 39;
       stringBins[STRING_3][FRET_2] = 42;
       stringBins[STRING_3][FRET_3] = 44;
       stringBins[STRING_3][FRET_4] = 47;
       stringBins[STRING_3][FRET_5] = 50;

       stringBins[STRING_4][FRET_0] = 50;
       stringBins[STRING_4][FRET_1] = 53;
       stringBins[STRING_4][FRET_2] = 56;
       stringBins[STRING_4][FRET_3] = 57;
       stringBins[STRING_4][FRET_4] = 63;
       stringBins[STRING_4][FRET_5] = 67;

       stringBins[STRING_5][FRET_0] = 63;
       stringBins[STRING_5][FRET_1] = 66;
       stringBins[STRING_5][FRET_2] = 70;
       stringBins[STRING_5][FRET_3] = 75;
       stringBins[STRING_5][FRET_4] = 79;
       stringBins[STRING_5][FRET_5] = 81;

       stringBins[STRING_6][FRET_0] = 84;
       stringBins[STRING_6][FRET_1] = 89;
       stringBins[STRING_6][FRET_2] = 94;
       stringBins[STRING_6][FRET_3] = 100;
       stringBins[STRING_6][FRET_4] = 106;
       stringBins[STRING_6][FRET_5] = 112;
    }
	}