package app.telesevek.Services;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.function.IntConsumer;

import app.sinch.IncomingCallActivity;
import app.telesevek.Doctowillcallyou;


public class CallNotificationActionReceiver extends BroadcastReceiver  {


    Context mContext;
    //AudioPlayer audioPlayer;
    MediaPlayer mediaPlayer;
    String CallID;
    String username;

    @Override
    public void onReceive(Context context, Intent intent) {



        Log.i("call","BROADCAST RECEIVER");
        this.mContext=context;
        //startPlayer();
        if (intent != null && intent.getExtras() != null) {

            String action ="";
            action=intent.getStringExtra("ACTION_TYPE");
            CallID=intent.getExtras().getString("CALL_ID");
            username=intent.getExtras().getString("username");
//            NotifyUser notifyUser2 =new NotifyUser();
//            if (!notifyUser2.getSinchServiceInterface().isStarted()){
//                notifyUser2.getSinchServiceInterface().startClient(username);
//            }

            if (action != null&& !action.equalsIgnoreCase("")) {
                performClickAction(context, action);
                Log.i("call",action);
            }

            // Close the notification after the click action is performed.
            Intent iclose = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(iclose);
            context.stopService(new Intent(context, CallNotificationService.class));

        }

    }


    private void performClickAction(Context context, String action) {
        if(action.equalsIgnoreCase("RECEIVE_CALL")) {

            if (checkAppPermissions()) {
                Intent intentCallReceive = new Intent(mContext, Doctowillcallyou.class);
               /* intentCallReceive.putExtra("Call", "incoming");
                intentCallReceive.putExtra("CALL_ID", CallID);
                intentCallReceive.putExtra("username",username);
                intentCallReceive.putExtra("CallFrom","call from push");*/
                intentCallReceive.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intentCallReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentCallReceive);

                // audioPlayer.stopRingtone();
                //stopPlayer();
                closeNotification();
            }
            else{
                Intent intent = new Intent(mContext, Doctowillcallyou.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

                //stopPlayer();
                closeNotification();

            }
        }
        else if(action.equalsIgnoreCase("DIALOG_CALL")){

            // show ringing activity when phone is locked
            Intent intent = new Intent(mContext, Doctowillcallyou.class);
            /*intent.putExtra("CALL_ID", CallID);
            intent.putExtra("CallFrom","call from push");
            intent.putExtra("username",username);*/
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            //stopPlayer();
            //closeNotification();
        }

        else {
            context.stopService(new Intent(context, CallNotificationService.class));
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);

            // audioPlayer.stopRingtone();
            // stopPlayer();
            closeNotification();
        }
    }

    public void closeNotification(){

        // Close the notification after the click action is performed.

        Intent iclose = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mContext.sendBroadcast(iclose);
        mContext.stopService(new Intent(mContext, CallNotificationService.class));
        Log.i("call","Notification Closed");
    }

    private Boolean checkAppPermissions() {
        return   hasCameraPermissions() && hasAudioPermissions();
    }

    private boolean hasAudioPermissions() {
        return (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
    private boolean hasCameraPermissions() {
        return (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }
}
