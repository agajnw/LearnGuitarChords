package com.example.ffttest;

import java.io.IOException;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

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

	private static final boolean INITIAL_DATA = false;
	private static final boolean FFT_DATA = true;
	private static final String SAMPLE_AUDIO_ILE = "storage/emulated/0/Download/sound.ogg";
	public static final String RECORDED_AUDIO_FILE = "/FFTPrototype/ftt_prototype_recording.wav";
	//ftt_prototype_recording

	private static boolean is_listening = false;
	private Thread thread = null;
	
	private AudioCapture audioCapture = null;
	private AudioAnalysis audioAnalysis = null;
	private FileManager fileManager = null;
	
	private double[] fftData;
	private double[] dData;
	private int dataLength = 4096;
	private int maxBucket = 0;
	private String fn;
	
	public void showInitialChart(View view)
	{
		if(dData != null)
		{
			createDataChart(dData, INITIAL_DATA);
		}
	}
	
	public void showFFTChart(View view)
	{
		if(fftData != null)
		{
			createDataChart(fftData, FFT_DATA);
		}
	}
	
	public void onButtonClicked(View view)
	{
		Button button = (Button)view;
		if(!is_listening)
		{
			button.setText(getString(R.string.stop_button));
			
			audioCapture = new AudioCapture();
			thread = new Thread(audioCapture);
			thread.start();
			Log.d("FFTTEST", "Audio capture thread started");
			
			fftData = null;
			dData = null;
		}
		else
		{
			button.setText(getString(R.string.listen_button));
			audioCapture.stopListening();
			
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d("FFTTEST", "Audio capture thread stopped");
			
			thread = null;
			audioCapture = null;
			
			playRecording();
			analyseAudio();
		}
		is_listening = !is_listening;
	}
	
	public void playRecording() {
		String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		fn = filepath + RECORDED_AUDIO_FILE;
		
		Log.d("FFTTEST", "file name " + fn);
		
		MediaPlayer player = new MediaPlayer();
		try {
			player.setDataSource(fn);
			//player.setDataSource("storage/emulated/0/Download/sound.ogg");
			player.prepare();
			player.start();
		} catch(IOException e) {
			Log.e("FFTTEST", "play prepare() failed");
		}
	}
	
	private void analyseAudio()
	{
		dData = fileManager.getAudioData(fn);
		//dData = fileManager.readSampleFileFromDevice();
		//dData = fileManager.getAudioData(SAMPLE_AUDIO_FILE);
		
		fftData = audioAnalysis.analyseAudio(dData);
		maxBucket = audioAnalysis.findPeak(fftData);
		Log.d("MAX", "Max bucket at " + maxBucket + "- " + maxBucket*16000/4096 + "Hz");

        //if(audioAnalysis.checkIfMatchString(maxBucket, audioAnalysis.STRING_6, audioAnalysis.FRET_0))
        //    Log.d("MATCH", "Correct note!");
        //else
        //    Log.d("MATCH", "Incorrect note...");
        audioAnalysis.findMatch(maxBucket);
	}
	
	private void createDataChart(double[] data, boolean isFFT)
	{
		Log.d("TAG", "Create data chart");
		XYSeries series;
		
		if(isFFT)
			series = new XYSeries(getString(R.string.initial_data_chart));
		else
			series = new XYSeries(getString(R.string.fft_data_chart));
		
		XYMultipleSeriesDataset mSeries = new XYMultipleSeriesDataset();
		mSeries.addSeries(series);
		
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setLineWidth(2);
		renderer.setColor(Color.RED);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.setXLabels(0);
		mRenderer.setChartTitle(getString(R.string.initial_data_chart));
		mRenderer.setXTitle("Sample number");
		mRenderer.setYTitle("Sample PMC");
		mRenderer.setShowGrid(true);
		
		mRenderer.addSeriesRenderer(renderer);
		
		Intent intent;

		if(!isFFT)
		{
			for(int i=0;i<150;i++)
			{
				mRenderer.addXTextLabel(i+1, ""+i+1);
				series.add(i, data[i]);
				Log.d("TAG3", "sample  " + i + ": " + data[i]);
			}
			intent = ChartFactory.getLineChartIntent(getBaseContext(), mSeries, mRenderer);
		}
		else
		{
			for(int i=0;i<audioAnalysis.MAX_BUCKET;i++)
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
		setContentView(R.layout.init_screen);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		audioAnalysis = new AudioAnalysis(dataLength);
		fileManager = new FileManager(dataLength);
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
			View rootView = inflater.inflate(R.layout.init_screen, container,
					false);
			return rootView;
		}
	}

}
