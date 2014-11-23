package com.example.ffttest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.example.ffttest.AudioCapture;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

	private static boolean is_listening = false;
	private AudioCapture ac = null;
	private Thread thread = null;
	
	private double[] fftData;
	private double[] dData;
	
	private int dataLength = 2048;
	
	public void showInitialChart(View view)
	{
		if(dData != null)
		{
			createInitialDataChart(dData, false);
		}
	}
	
	public void showFFTChart(View view)
	{
		if(fftData != null)
		{
			//createInitialDataChart(fftData, true);
		}
	}
	
	public void onButtonClicked(View view)
	{
		Button button = (Button)view;
		if(!is_listening)
		{
			button.setText(getString(R.string.stop_button));
			
			ac = new AudioCapture();
			thread = new Thread(ac);
			thread.start();
			Log.d("FFTTEST", "Audio capture thread started");
			
			fftData = null;
			dData = null;
		}
		else
		{
			button.setText(getString(R.string.listen_button));
			ac.stopListening();
			
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d("FFTTEST", "Audio capture thread stopped");
			
			thread = null;
			ac = null;
			
			playRecording();
		}
		is_listening = !is_listening;
	}
	
	public void playRecording() {
		String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String fn = filepath + "/FFTPrototype/ftt_prototype_recording.wav";
		
		Log.d("FFTTEST", "file name " + fn);
		
		writeAudioData(fn);
		
		MediaPlayer player = new MediaPlayer();
		try {
			player.setDataSource(fn);
			player.prepare();
			player.start();
		} catch(IOException e) {
			Log.e("FFTTEST", "play prepare() failed");
		}
	}
	
	public void writeAudioData(String fn) {
		FileInputStream in;
		byte[] bData = new byte[dataLength*2];
		short[] sData = new short[dataLength];
		
        try {
            in = new FileInputStream(fn);
            try {
                in.read(bData);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        
        for (int i = 0; i < dataLength*2; i += 2) {
            sData[i/2] = (short) ((bData[i]) | bData[i + 1] << 8);
            Log.d("TAG1", "sample " + i/2 + ": " + sData[i/2]);
        }
        
        dData = new double[dataLength];
        for(int i=0;i<dataLength;i++)
        {
        	dData[i] = (double)sData[i];
        	Log.d("TAG2", "sample " + i + ": " + dData[i]);
        }
	}
	
	private void createInitialDataChart(double[] data, boolean is_fft)
	{
		Log.d("TAG", "Create initial data chart");
		XYSeries series = new XYSeries(getString(R.string.initial_data_chart));
		
		XYMultipleSeriesDataset mSeries = new XYMultipleSeriesDataset();
		mSeries.addSeries(series);
		
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setLineWidth(2);
		renderer.setColor(Color.RED);
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.setXLabels(0);
		mRenderer.setChartTitle(getString(R.string.initial_data_chart));
		mRenderer.setXTitle("Sample number");
		mRenderer.setYTitle("Sample PMC");
		mRenderer.setShowGrid(true);
		
		mRenderer.addSeriesRenderer(renderer);
		
		Intent intent;

		if(!is_fft)
		{
			for(int i=0;i<data.length;i++)
			{
				mRenderer.addXTextLabel(i+1, ""+i+1);
				series.add(i, data[i]);
				Log.d("TAG3", "sample  " + i + ": " + data[i]);
			}
			intent = ChartFactory.getLineChartIntent(getBaseContext(), mSeries, mRenderer);
		}
		else
		{
			for(int i=0;i<data.length/2;i++)
			{
				mRenderer.addXTextLabel(i+1, ""+i+1);
				series.add(i, data[i]);
				Log.d("TAG4", "fftsample  " + i + ": " + data[i]);
			}
			intent = ChartFactory.getBarChartIntent(getBaseContext(), mSeries, mRenderer, BarChart.Type.DEFAULT);
		}
		startActivity(intent);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
