package com.example.ffttest;

import java.io.IOException;

import com.example.ffttest.AudioCapture;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
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
		
		MediaPlayer player = new MediaPlayer();
		try {
			player.setDataSource(fn);
			player.prepare();
			player.start();
		} catch(IOException e) {
			Log.e("FFTTEST", "play prepare() failed");
		}
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
