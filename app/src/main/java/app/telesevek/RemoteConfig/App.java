package app.telesevek.RemoteConfig;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final FirebaseRemoteConfig remoteConfig=FirebaseRemoteConfig.getInstance();
        Map<String,Object> defaultValues=new HashMap<>();
        defaultValues.put(UpdateHelper.UPDATE_KEY_ENABLED,false);
        defaultValues.put(UpdateHelper.UPDATE_KEY_VERSION,"1.2.6");
        defaultValues.put(UpdateHelper.UPDATE_KEY_URL,"https://play.google.com/store/apps/details?id=app.telesevek");

        remoteConfig.setDefaultsAsync(defaultValues);
        remoteConfig.fetch(5)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            remoteConfig.fetchAndActivate();
                        }
                    }
                });
    }
}
