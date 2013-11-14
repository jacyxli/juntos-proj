package org.vamosjuntos.juntos;

import java.io.IOException;

import android.app.Activity;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

public class AudioRecord extends Activity {

	public static final String NUM_AUDIO_RECORD = "NUM_AUDIO_RECORD";
	public static final String AUDIO_PATH = "AUDIO_PATH";
	private static final String LOG_TAG = "AudioRecord";
	private static String mFileName = null;

	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	private boolean mStartPlaying = true;
	private boolean mStartRecording = true;
	

	@Override
	public void onCreate(Bundle savedInstances) {
		super.onCreate(savedInstances);
		setContentView(R.layout.report_audio);
		
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor prefsEditor = mPrefs.edit();
		int audioCount = mPrefs.getInt(NUM_AUDIO_RECORD, 0);
		int numAudio = ++audioCount;
		prefsEditor.putInt(NUM_AUDIO_RECORD, audioCount);
		prefsEditor.commit();
		
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/JuntosAudio" + Integer.toString(numAudio) + ".3gp";
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}
	
	public void handlePlayButton(View view){
		//final Button play = (Button)findViewById(R.id.recordAudio);
		final Button play = (Button)view;
        onPlay(mStartPlaying);
        if (mStartPlaying) {
           play.setText("Stop playing");
        } else {
            play.setText("Start playing");
        }
        mStartPlaying = !mStartPlaying;
	}
	
	public void handleRecordButton(View view){
		//final Button record = (Button)findViewById(R.id.recordAudio);
		final Button record = (Button)view;
        onRecord(mStartRecording);
        if (mStartRecording) {
            record.setText("Stop recording");
        } else {
            record.setText("Start recording");
        }
        mStartRecording = !mStartRecording;
	}
	
	public void submitAudio(View view) {
		Intent intent = getIntent();
		intent.putExtra(AUDIO_PATH, mFileName);
		AudioRecord.this.setResult(RESULT_OK, intent);
		finish();
	}

}
