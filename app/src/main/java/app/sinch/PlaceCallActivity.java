package app.sinch;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sinch.android.rtc.calling.Call;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.telesevek.R;

public class PlaceCallActivity extends BaseActivity {

    private Button mCallButton;
    private EditText mCallName;
    String dr_id;
    String id_con;
    String PatientPassId;

    TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snich_place_call);

        timer = findViewById(R.id.timer);
        mCallButton = (Button) findViewById(R.id.callButton);

        new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext

            }

            public void onFinish() {
                timer.setText("done! Patient is Ready");
                mCallButton.setVisibility(View.VISIBLE);
            }

        }.start();

        //initializing UI elements
        mCallName = (EditText) findViewById(R.id.callName);
        mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(buttonClickListener);

        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(buttonClickListener);

        Bundle bundle = getIntent().getExtras();
        PatientPassId = bundle.getString("PatientName");
        dr_id = bundle.getString("DoctorName");
        id_con = bundle.getString("Conid");
        mCallName.setText(PatientPassId);


    }

    // invoked when the connection with SinchServer is established
    @Override
    protected void onServiceConnected() {
        TextView userName = (TextView) findViewById(R.id.loggedInName);
        userName.setText(getSinchServiceInterface().getUserName());
        mCallButton.setEnabled(true);
    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }

    //to kill the current session of SinchService
    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    //to place the call to the entered name
    private void callButtonClicked() {
        String userName = mCallName.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        Call call = getSinchServiceInterface().callUserVideo(PatientPassId);
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);



    }



    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.callButton:
                    callButtonClicked();
                    break;

                case R.id.stopButton:
                    stopButtonClicked();
                    break;

            }
        }
    };
}