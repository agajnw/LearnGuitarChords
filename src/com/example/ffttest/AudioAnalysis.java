package com.example.ffttest;

import com.example.ffttest.FFT;

import android.util.Log;

//to change , code frome here - https://www.ee.columbia.edu/~ronw/code/MEAPsoft/doc/html/FFT_8java-source.html
	
public class AudioAnalysis {

	  private int dataLength;
	  private FFT fft;

	  // Lookup tables. Only need to recompute when size of FFT changes.
	  double[] cos;
	  double[] sin;

	  public AudioAnalysis(int n) {
		  dataLength = n;
		  
		  fft = new FFT(dataLength);
	  }

	  public void analyseAudio(double[] dataToAnalyse, double[] resultData)
	  {
		  double [] afterHanning = hanningWindow(dataToAnalyse, dataLength);
		  fft.fft(afterHanning,  resultData);
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
	}