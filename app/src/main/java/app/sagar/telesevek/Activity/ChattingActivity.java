package app.sagar.telesevek.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import app.sagar.telesevek.Models.Doctor;
import app.sagar.telesevek.Models.FirebaseUserModel;
import app.sagar.telesevek.R;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by haripal on 7/25/17.
 */

public class ChattingActivity extends AppCompatActivity {
    private static final String TAG = "ChattingActivity";

    Doctor user = Doctor.getInstance();

    ListView listView;

    EditText textComment;
    ImageView btnSend;


    FirebaseDatabase database;
    DatabaseReference messagesRef;
    DatabaseReference usersRef;

    public static ChattingActivity chattingActivity;

    JSONArray registration_ids = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Doctor");
        btnSend = (ImageView) findViewById(R.id.send_button);



        final com.google.firebase.database.ValueEventListener userValueEventListener = new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                registration_ids = new JSONArray();

                for (com.google.firebase.database.DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    System.out.println("Child: " + postSnapshot);
                    //Getting the data from snapshot
                    FirebaseUserModel firebaseUserModel = postSnapshot.getValue(FirebaseUserModel.class);
                    if (!firebaseUserModel.getDeviceToken().isEmpty()) {
                        registration_ids.put(firebaseUserModel.getDeviceToken());
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                registration_ids = new JSONArray();


                System.out.println("The read failed: " + databaseError.getMessage());
            }
        };

        usersRef.addValueEventListener(userValueEventListener);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(ChattingActivity.this, ""+registration_ids, Toast.LENGTH_SHORT).show();


                if (registration_ids.length() > 0) {

                    String url = "https://fcm.googleapis.com/fcm/send";
                    AsyncHttpClient client = new AsyncHttpClient();

                    client.addHeader(HttpHeaders.AUTHORIZATION, "key=AAAAegWfV4s:APA91bEVZoKIoRPEEO49PQgqm3k9dBDQ9Nvfpb67AFKNxau1etaOMieW96DG_JgBpPSH8UsdNA-xmaiTDBvxaHTw9iTr_zv40pJGAnuqh0LtTEfq-CXC-68m0kq1wsqRZJ73QSHpmpbV");
                    client.addHeader(HttpHeaders.CONTENT_TYPE, RequestParams.APPLICATION_JSON);

                    try {
                        JSONObject params = new JSONObject();

                        params.put("registration_ids", registration_ids);

                        JSONObject notificationObject = new JSONObject();
                        notificationObject.put("body", "sagardevicesend");
                        notificationObject.put("title", "nothing");

                        params.put("notification", notificationObject);

                        StringEntity entity = new StringEntity(params.toString());

                        client.post(getApplicationContext(), url, entity, RequestParams.APPLICATION_JSON, new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                                Log.i(TAG, responseString);
                                Toast.makeText(ChattingActivity.this, "failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                                Log.i(TAG, responseString);
                                Toast.makeText(ChattingActivity.this, "succeses", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {

                    }


                }
            }
        });


    }
}