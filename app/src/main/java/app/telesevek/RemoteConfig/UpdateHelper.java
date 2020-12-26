package app.telesevek.RemoteConfig;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class UpdateHelper {

    public static String UPDATE_KEY_ENABLED="is_update";
    public static String UPDATE_KEY_VERSION="version";
    public static String UPDATE_KEY_URL="update_url";

    public interface OnUpdateCheckListener{
        void onUpdateCheckListener(String urlApp);
    }

    public static Builder with(Context context){
        return new Builder(context);
    }

    private Context context;
    private OnUpdateCheckListener onUpdateCheckListener;

    public UpdateHelper(Context context,OnUpdateCheckListener onUpdateCheckListener){
        this.onUpdateCheckListener=onUpdateCheckListener;
        this.context=context;

    }

    public void check(){

        FirebaseRemoteConfig remoteConfig=FirebaseRemoteConfig.getInstance();
        if(remoteConfig.getBoolean(UPDATE_KEY_ENABLED)){
            String currentVersion=remoteConfig.getString(UPDATE_KEY_VERSION);
            String appVersion=getAppversion(context);
            Log.i("VERSION","remote-"+currentVersion+"app-"+appVersion);
            String updateURL=remoteConfig.getString(UPDATE_KEY_URL);
            if(!TextUtils.equals(appVersion,currentVersion)&& onUpdateCheckListener!=null){
                onUpdateCheckListener.onUpdateCheckListener(updateURL);
            }
        }
    }

    private String getAppversion(Context context){
        String result="";
        try{
            result=context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
            result=result.replaceAll("[a-zA-Z]|-","");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }



    public static class Builder{

        private Context context;
        private OnUpdateCheckListener onUpdateCheckListener;

        public Builder(Context context){
            this.context=context;
        }

        public Builder OnUpdateCheck(OnUpdateCheckListener onUpdateCheckListener){
            this.onUpdateCheckListener=onUpdateCheckListener;
            return this;
        }

        public UpdateHelper build(){
            return new UpdateHelper(context,onUpdateCheckListener);
        }
        public UpdateHelper check(){

            UpdateHelper updateHelper=build();
            updateHelper.check();
            return updateHelper;
        }
    }
}
