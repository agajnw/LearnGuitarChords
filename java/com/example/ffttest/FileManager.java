package com.example.ffttest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.util.Log;

public class FileManager {
	private int dataLength;
	
	public FileManager(int n)
	{
		dataLength = n;
	}
	
	public double[] getAudioData(String fn) {
		FileInputStream in;
		byte[] bData = new byte[dataLength*2];
		short[] sData = new short[dataLength];
		double[] dData = new double[dataLength];
		
        try {
            in = new FileInputStream(fn);
            try {
            	//Log.d("READ", "Remaining1 " + in.available());
            	in.skip(100);//file metadata
            	
            	//Log.d("READ", "Remaining2 " + in.available());
                in.read(bData);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }
        
        for (int i = 0; i < dataLength*2; i += 2) {
            sData[i/2] = (short) ((bData[i]) | bData[i + 1] << 8);
            //Log.d("TAG1", "sample " + i/2 + ": " + sData[i/2]);
        }
        
        for(int i=0;i<dataLength;i++)
        {
        	dData[i] = (double)sData[i]/32768.0;
        	//Log.d("TAG2", "sample " + i + ": " + dData[i]);
        }
        
        return dData;
	}
	
	public double[] readSampleFileFromDevice()
	{
		String path = "storage/emulated/0/Download/sound.ogg";
	
		 File file = new File(path);
		 byte[] bytes = new byte[dataLength*2];
		 double dData[] = new double[dataLength];
		 try {
		     BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
		     buf.read(bytes, 0, bytes.length);
		     buf.close();
		     
		     for(int i=0;i<40;i++)
		    	 dData[i] = 0.0;
		     for (int i = 80; i < dataLength*2; i += 2) {
		         dData[i/2] = (double) ((bytes[i]) | bytes[i + 1] << 8);
		     }

		     return dData;
		 } catch (FileNotFoundException e) {
		     // TODO Auto-generated catch block
		     e.printStackTrace();
		     return null;
		 } catch (IOException e) {
		     // TODO Auto-generated catch block
		     e.printStackTrace();
		     return null;
		 }
	}

}
