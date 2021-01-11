package app.telesevek.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.telesevek.PhoneAuthDoctor.MainActivity;
import app.telesevek.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService  {





    private static final String TAG = "MyFirebaseMsgService";
    Intent intent;
    String CallID;
    String username;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        String Title=remoteMessage.getData().get("title");
        String msg=remoteMessage.getData().get("body");



        //assert Title != null;
        assert msg != null;
        if (Title!=null) {


            if (Title.equals("Incoming Call")) {

                CallID = remoteMessage.getData().get("callID");
                username = remoteMessage.getData().get("username");
                //STARTING INCOMING CALL SERVICE
                Log.i("call", "incoming call");

                serviceStart();


            } else {
                showNotification2(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            }
        }
        else Log.i("call","TITLE NULL");


    }


    public void showNotification2(String title,String messageBody) {
        Context context;


        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "com.sagar.chatdemo";
        String channelName = "fcm";
        int importance = NotificationManager.IMPORTANCE_HIGH;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher_new)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setDefaults(Notification.DEFAULT_ALL);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

    public void serviceStart(){

        Log.i("call","SERVICE STARTED");


        Intent serviceIntent = new Intent(getApplicationContext(), CallNotificationService.class);
        serviceIntent.putExtra("CallID",CallID);
        serviceIntent.putExtra("username",username);
        ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);

    }
    public void serviceStop(){
        Log.i("call","SERVICE STOPPED FIREBASE");
        getApplicationContext().stopService(new Intent(getApplicationContext(), CallNotificationService.class));
        Intent istop = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        getApplicationContext().sendBroadcast(istop);
    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceStop();
    }


}
