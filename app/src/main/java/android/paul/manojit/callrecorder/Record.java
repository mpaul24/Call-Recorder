package android.paul.manojit.callrecorder;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Record extends Service {

    boolean isRunning;
    Context context;
    Handler handler;
    MediaRecorder mediaRecorder;
    AudioManager audioManager;
    boolean onForeground;
    public Record() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.isRunning = false;
        this.context = this;
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String state = intent.getStringExtra("State");
        if((!this.isRunning && state.equals("onIncomingCallAnswered")) || (!this.isRunning && state.equals("onOutgoingCallStarted")))
        {
            this.isRunning = true;
            handler.post(runnable);
            onForeground = false;
            startService();
        }
        else if((this.isRunning && state.equals("onIncomingCallEnded")) || (this.isRunning && state.equals("onOutgoingCallEnded")))
        {
            if(mediaRecorder!=null)
                mediaRecorder.stop();
            if(audioManager!=null)
            {
                audioManager.setSpeakerphoneOn(false);
                audioManager = null;
            }
            onForeground = false;
            handler.removeCallbacks(runnable);
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File folder = new File(Environment.getExternalStorageDirectory()+"/Call_Recorder");
            if(!folder.exists()) folder.mkdirs();
            String OUTPUT_FILE = Environment.getExternalStorageDirectory()+"/Call_Recorder/callRecorder_"+timeStamp+ ".aac";

            if(mediaRecorder!=null)
            {
                mediaRecorder.release();
            }
            /*audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(true);*/
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);//done
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(OUTPUT_FILE);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setAudioEncodingBitRate(44100);
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    private void startService() {


        if (onForeground)
            return;

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getBaseContext(), 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(
                getBaseContext())
                .setContentTitle("Recording")
                .setTicker("Hello")
                .setContentText("sdas")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        notification.flags = Notification.FLAG_NO_CLEAR;

        startForeground(0, notification);
        onForeground = true;
    }
}
