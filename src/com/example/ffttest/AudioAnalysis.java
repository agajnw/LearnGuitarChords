package com.example.ffttest;

import com.example.ffttest.FFT;

import android.util.Log;

//to change , code frome here - https://www.ee.columbia.edu/~ronw/code/MEAPsoft/doc/html/FFT_8java-source.html
	
public class AudioAnalysis {

	public final int MAX_BUCKET = 28; //max frequency produced by guitar is 1174.624Hz
	
	private int dataLength;
	private FFT fft;

	  // Lookup tables. Only need to recompute when size of FFT changes.
	  double[] cos;
	  double[] sin;

	  public AudioAnalysis(int n) {
		  dataLength = n;
		  
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
	}