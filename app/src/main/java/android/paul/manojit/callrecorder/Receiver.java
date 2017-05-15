package android.paul.manojit.callrecorder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

/**
 * Created by manojit on 12/5/17.
 */

public class Receiver extends PhonecallReceiver{
    Date recieved;
    PendingResult pendingResult;
    MediaPlayer mediaPlayer;
    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        Toast.makeText(ctx, number+" Received "+start.toString(), Toast.LENGTH_SHORT).show();



    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        Toast.makeText(ctx, number+" Answered "+start.toString(), Toast.LENGTH_SHORT).show();
        //Log.e("Manojit","Start");
        Intent intent = new Intent(ctx,Record.class);
        intent.putExtra("State","onIncomingCallAnswered");
        ctx.startService(intent);


    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

       // pendingResult.finish();
        //Log.e("Manojit","Finish");
        long time = end.getTime() - start.getTime();
        Toast.makeText(ctx, number+" Ended "+time, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ctx,Record.class);
        intent.putExtra("State","onIncomingCallEnded");
        ctx.startService(intent);

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, number+" Started ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ctx,Record.class);
        intent.putExtra("State","onOutgoingCallStarted");
        ctx.startService(intent);

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, number+" Dropped ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ctx,Record.class);
        intent.putExtra("State","onOutgoingCallEnded");
        ctx.startService(intent);

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {

    }
}
