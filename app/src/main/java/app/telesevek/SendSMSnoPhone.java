package app.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SendSMSnoPhone extends AppCompatActivity {
    String Docuidi;
    String Patientphone;
    TextView pname;
    Button submit;
    EditText ed;
    List<String> ls=new ArrayList<>();
    public static String account_sid = "account_sid";
    public static String auth_token = "auth_token";
    public static String fromNumber="from";

    public static String ACCOUNT_SID ;
    public static String AUTH_TOKEN ;
    public static String from;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_s_m_sno_phone);

        receiveCredentials();

        Bundle bundle = getIntent().getExtras();
        Docuidi = bundle.getString("DocuId");
        Patientphone = bundle.getString("patientphone");
        ls.add(Patientphone);

        pname= findViewById(R.id.pname);
        submit= findViewById(R.id.submit);
        ed= findViewById(R.id.ed_pred);
        pname.setText(Patientphone);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS(Patientphone,ed.getText().toString());
            }
        });

    }

    public void sendSMS(String docuidi,String PatientName){


        Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show();
        for(int j=0;j<ls.size();j++){

            String body = docuidi +"Doctor Prescription is"+PatientName ;
            String from = "+17633258036";
            String to = ls.get(j);

            String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    ( ACCOUNT_SID+":"+ AUTH_TOKEN).getBytes(), Base64.NO_WRAP
            );

            Map<String, String> data = new HashMap<>();
            data.put("From", from);
            data.put("To", to);
            data.put("Body", body);
            data.put("locale","hi");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.twilio.com/2010-04-01/")
                    .build();
            AddPatiant.TwilioApi api = retrofit.create(AddPatiant.TwilioApi.class);

            api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) Log.d("TAG", "onResponse->success");
                    else {
                        Log.d("TAG", "onResponse->failure");
                        Log.i("RESPONSE",response.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("TAG", "onFailure");
                }
            });


        }

    }

    public static void receiveCredentials(){

        final FirebaseRemoteConfig remoteConfig= FirebaseRemoteConfig.getInstance();
        ACCOUNT_SID=remoteConfig.getString(account_sid);
        AUTH_TOKEN=remoteConfig.getString(auth_token);
        from=remoteConfig.getString(fromNumber);
        Log.i("remote",ACCOUNT_SID+"="+AUTH_TOKEN+"from"+from);
        //Toast.makeText(this, idOf, Toast.LENGTH_SHORT).show();
        remoteConfig.fetch(120)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            remoteConfig.fetchAndActivate();

                            //Toast.makeText(LogindcActivity.this, idOf, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}