package app.telesevek.Services;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.TimeUnit;

import app.telesevek.PhoneAuthDoctor.MainActivity;
import app.telesevek.R;

public class CallNotificationService extends Service implements MediaPlayer.OnPreparedListener {

    public static final String NOTIFICATION_CHANNEL_ID = "com.sagar.chatdemo";
    private static final int NOTIFICATION_ID = 22011999;
    private final static String default_notification_channel_id = "default";
    MediaPlayer mediaPlayer;
    NotificationManager mNotificationManager;


    Vibrator mvibrator;
    AudioManager audioManager;
    AudioAttributes playbackAttributes;
    private Handler handler;
    AudioManager.OnAudioFocusChangeListener afChangeListener;
        private boolean status = false;
        private boolean vstatus = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String CallID=intent.getExtras().getString("CallID");
        String username=intent.getExtras().getString("username");
        Log.i("call",username);
//        SinchServiceInterface sinchServiceInterface=new SinchServiceInterface();
//        sinchServiceInterface.startClient(username);
//        NotifyUser notifyUser2 =new NotifyUser();
//        notifyUser2.getSinchServiceInterface().startClient(username);

        try {
            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

            if (audioManager != null) {
                switch (audioManager.getRingerMode()) {
                    case AudioManager.RINGER_MODE_NORMAL:
                        status = true;
                        break;
                    case AudioManager.RINGER_MODE_SILENT:
                        status = false;
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        status = false;
                        vstatus=true;
                        Log.e("Service!!", "vibrate mode");
                        break;
                }
            }

            if (status) {
                Runnable delayedStopRunnable = new Runnable() {
                    @Override
                    public void run() {
                        releaseMediaPlayer();
                    }
                };

                afChangeListener =  new AudioManager.OnAudioFocusChangeListener() {
                    public void onAudioFocusChange(int focusChange) {
                        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                            // Permanent loss of audio focus
                            // Pause playback immediately
                            //mediaController.getTransportControls().pause();
                            if (mediaPlayer!=null) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                }
                            }
                            // Wait 30 seconds before stopping playback
                            handler.postDelayed(delayedStopRunnable,
                                    TimeUnit.SECONDS.toMillis(30));
                        }
                        else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                            // Pause playback
                        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                            // Lower the volume, keep playing
                        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                            // Your app has been granted audio focus again
                            // Raise volume to normal, restart playback if necessary
                        }
                    }
                };
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

                Uri soundUri2 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.newring);

                mediaPlayer= MediaPlayer.create(this, soundUri2);
                mediaPlayer.setLooping(true);
                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    handler = new Handler();


                    playbackAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();

                    AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(afChangeListener, handler)
                            .build();
                    int res = audioManager.requestAudioFocus(focusRequest);
                    if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        if(!keyguardManager.isDeviceLocked()) {

                            mediaPlayer.start();
                        }

                    }
                }else {

                    // Request audio focus for playback
                    int result = audioManager.requestAudioFocus(afChangeListener,
                            // Use the music stream.
                            AudioManager.STREAM_MUSIC,
                            // Request permanent focus.
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                            mediaPlayer.start();

                    }

                }

            }
            else if(vstatus){
                mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Start without a delay
                // Each element then alternates between vibrate, sleep, vibrate, sleep...
                long[] pattern = {0, 250, 200, 250, 150, 150, 75,
                        150, 75, 150};

                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array

                Log.e("Service!!", "vibrate mode start");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }





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



       /* int notificationId = 1;
        String channelId = "app.telesevek";
        String channelName = "fcm";
        String channelDesc = "telesevak";



        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.newring);
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //For API 26+ you need to put some additional code like below:
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            mChannel.setDescription(channelDesc);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(soundUri, audioAttributes);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel( mChannel );
            }
        }

        //General code:
        NotificationCompat.Builder status = new NotificationCompat.Builder(getApplicationContext(),channelId);
        status.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                //.setOnlyAlertOnce(true)
                .setContentTitle("Incoming Call")
                .setContentText("Call from a Doctor")
                .addAction(R.drawable.button_end,"DECLINE",cancelCallPendingIntent)
                .addAction(R.drawable.ic_call,"ANSWER",receiveCallPendingIntent)
                .setSound(soundUri)
                .setContentIntent(callDialogPendingIntent);

        mNotificationManager.notify(notificationId, status.build());
*/

       Uri soundUri2 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.newring);
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
                .setVibrate(new long[]{0, 500, 1000})
                .setDefaults(Notification.DEFAULT_LIGHTS )
               /* .setSound(soundUri)*/
                .setColor(Color.rgb(45,196,229))
                .setContentIntent(callDialogPendingIntent) ;

        mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
        if (Build.VERSION. SDK_INT >= Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;

            NotificationChannel notificationChannel = new
                    NotificationChannel( NOTIFICATION_CHANNEL_ID , "fcm" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
          /*  AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel.setSound(soundUri, audioAttributes);*/

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


       /* if(mediaPlayer==null){
            mediaPlayer= MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);

        }*/
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

    public void shownotifysound(String title,String messageBody){

            int notificationId = 1;
            String channelId = "app.telesevek";
            String channelName = "fcm";
            String channelDesc = "telesevak";

            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            String CHANNEL_ID="1234";

            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.newring);
            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            //For API 26+ you need to put some additional code like below:
            NotificationChannel mChannel;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_HIGH);
                mChannel.setLightColor(Color.GRAY);
                mChannel.enableLights(true);
                mChannel.setDescription(channelDesc);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                mChannel.setSound(soundUri, audioAttributes);

                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel( mChannel );
                }
            }

            //General code:
            NotificationCompat.Builder status = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
            status.setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo)
                    //.setOnlyAlertOnce(true)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setVibrate(new long[]{0, 500, 1000})
                    .setDefaults(Notification.DEFAULT_LIGHTS )
                    .setSound(soundUri)
                    .setContentIntent(pendingIntent);

            mNotificationManager.notify(notificationId, status.build());
        }

        public void releaseVibration(){
            try {
                if(mvibrator!=null){
                    if (mvibrator.hasVibrator()) {
                    }
                    mvibrator=null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void releaseMediaPlayer() {
            try {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                    mediaPlayer = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {

        }

    }
