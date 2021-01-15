package app.telesevek.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import app.telesevek.R;

public class CallNotificationService extends Service

    {


    public static final String NOTIFICATION_CHANNEL_ID = "com.sagar.chatdemo";
    private static final int NOTIFICATION_ID = 22011999;
    private final static String default_notification_channel_id = "default";
    MediaPlayer mediaPlayer;
    NotificationManager mNotificationManager;




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String CallID=intent.getExtras().getString("CallID");
        String username=intent.getExtras().getString("username");
        Log.i("call",username);
//        SinchServiceInterface sinchServiceInterface=new SinchServiceInterface();
//        sinchServiceInterface.startClient(username);
//        NotifyUser notifyUser2 =new NotifyUser();
//        notifyUser2.getSinchServiceInterface().startClient(username);

        Log.i("call","SERVICE CLASS STARTED");
        Intent receiveCallAction = new Intent(getApplicationContext(), CallNotificationActionReceiver.class);

        receiveCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_RECEIVE_ACTION");
        receiveCallAction.putExtra("ACTION_TYPE", "RECEIVE_CALL");
        receiveCallAction.putExtra("NOTIFICATION_ID",NOTIFICATION_ID);
        receiveCallAction.putExtra("CALL_ID",CallID);
        receiveCallAction.putExtra("username",username);
        receiveCallAction.setAction("RECEIVE_CALL");

        Intent cancelCallAction = new Intent(getApplicationContext(), CallNotificationActionReceiver.class);
        cancelCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_CANCEL_ACTION");
        cancelCallAction.putExtra("ACTION_TYPE", "CANCEL_CALL");
        cancelCallAction.putExtra("NOTIFICATION_ID",NOTIFICATION_ID);
        cancelCallAction.putExtra("CALL_ID",CallID);
        cancelCallAction.putExtra("username",username);
        cancelCallAction.setAction("CANCEL_CALL");

        Intent callDialogAction = new Intent(getApplicationContext(), CallNotificationActionReceiver.class);
        callDialogAction.putExtra("ACTION_TYPE", "DIALOG_CALL");
        callDialogAction.putExtra("NOTIFICATION_ID",NOTIFICATION_ID);
        callDialogAction.putExtra("CALL_ID",CallID);
        callDialogAction.putExtra("username",username);
        callDialogAction.setAction("DIALOG_CALL");

        PendingIntent receiveCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent cancelCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent callDialogPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1202, callDialogAction, PendingIntent.FLAG_UPDATE_CURRENT);

        //Uri ringtoneURI= Settings.System.DEFAULT_RINGTONE_URI;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext() ,
                default_notification_channel_id )
                .setSmallIcon(R.mipmap.ic_launcher_new_foreground )
                .setContentTitle( "Incoming Call" )
                .setContentText( "Call from a Doctor" )
                .addAction(R.drawable.button_end,"DECLINE",cancelCallPendingIntent)
                .addAction(R.drawable.ic_call,"ANSWER",receiveCallPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                //.setSound(ringtoneURI)
                .setColor(Color.rgb(45,196,229))
                .setContentIntent(callDialogPendingIntent) ;

        mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;

            NotificationChannel notificationChannel = new
                    NotificationChannel( NOTIFICATION_CHANNEL_ID , "fcm" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;

        }
        mBuilder.setFullScreenIntent(callDialogPendingIntent,true);
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () ,
                mBuilder.build()) ;

        Notification incomingCallNotification = null;
        incomingCallNotification = mBuilder.build();
        startForeground(NOTIFICATION_ID, incomingCallNotification);

        startPlayer();

        // return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("call","DESTROYED SERVICE");
        stopPlayer();
        closeNotification();
    }

    public void closeNotification(){
        mNotificationManager.cancel(NOTIFICATION_ID);
        mNotificationManager.cancelAll();
    }

    public  void startPlayer(){


        if(mediaPlayer==null){
            mediaPlayer= MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);

        }
      /*  mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.start();*/
    }

    public void stopPlayer(){
        if (mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}
