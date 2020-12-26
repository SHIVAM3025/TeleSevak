package app.telesevek.Models;

import android.content.SharedPreferences;

/**
 * Created by haripal on 7/25/17.
 */

public class Doctor {
    private static final Doctor user = new Doctor();

    public static Doctor getInstance() {
        return user;
    }

    public String firebaseKey = "firebaseKey";
    public String deviceId = "";
    public String deviceToken = "";

    public static final String appPreferences = "ChattingAppPreferences" ;

    public static final String Key  = "keyKey";
    public static final String DeviceToken = "deviceTokenKey";
    public static final String DeviceId = "deviceIdKey";

    public SharedPreferences sharedpreferences;

    private Doctor() {
    }

    public Boolean login(FirebaseUserModel firebaseUserModel) {
        deviceId = firebaseUserModel.getDeviceId();
        deviceToken = firebaseUserModel.getDeviceToken();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Key, firebaseKey);
        editor.putString(DeviceId, deviceId);

        editor.apply();

        return true;
    }

    public void saveFirebaseKey(String key) {
        this.firebaseKey = key;

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Key, firebaseKey);
        editor.apply();
    }

    public void logout() {
        firebaseKey = "";
        deviceId = "";
        deviceToken = "";

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Key, firebaseKey);
        editor.putString(DeviceId, deviceId);

        editor.apply();
    }
}