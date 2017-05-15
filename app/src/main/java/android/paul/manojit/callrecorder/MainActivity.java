package android.paul.manojit.callrecorder;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private String OUTPUT_FILE;
    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        File folder = new File(Environment.getExternalStorageDirectory()+"/Call_Recorder");
        if(!folder.exists()) folder.mkdirs();
        OUTPUT_FILE = Environment.getExternalStorageDirectory()+"/Call_Recorder/callRecorder_"+timeStamp+ ".aac";

        Log.e("Manojit",OUTPUT_FILE);

        try {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mAdminName = new ComponentName(this, DeviceAdminDemo.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                // mDPM.lockNow();
                // Intent intent = new Intent(MainActivity.this,
                // TrackDeviceService.class);
                // startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }



    public void record(View v){
        try {
            beginRecording();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void beginRecording() throws IOException {
        if(mediaRecorder!=null)
        {
            mediaRecorder.release();
        }
        File outFile = new File(OUTPUT_FILE);

        if(outFile.exists())
            outFile.delete();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        mediaRecorder.setOutputFile(OUTPUT_FILE);
        mediaRecorder.prepare();
        mediaRecorder.start();
        Toast.makeText(this, "Started Recording", Toast.LENGTH_SHORT).show();

    }


    public void stop_record(View v){
        try {
            stopRecording();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void stopRecording() {
        if(mediaRecorder!=null)
            mediaRecorder.stop();
        Toast.makeText(this, "Stop Recoding", Toast.LENGTH_SHORT).show();
    }
    public void play(View v){
        try {
            beginPlaying();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void beginPlaying() throws IOException {
        if(mediaPlayer!=null)
            try {
                mediaPlayer.release();
            }catch(Exception e){
                e.printStackTrace();
            }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(OUTPUT_FILE);
        mediaPlayer.prepare();
        mediaPlayer.start();
        Toast.makeText(this, "Start Playing", Toast.LENGTH_SHORT).show();

    }
    public void stop_play(View v){
        try {
            stopPlaying();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void stopPlaying() {
        if(mediaPlayer!=null)
            mediaPlayer.stop();
        Toast.makeText(this, "Stop Playing", Toast.LENGTH_SHORT).show();
    }


}
