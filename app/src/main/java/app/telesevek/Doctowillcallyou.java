package app.telesevek;

import androidx.annotation.NonNull;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sinch.android.rtc.SinchError;

import app.sinch.BaseActivity;
import app.sinch.SinchService;
import app.telesevek.NewSceen.HomePatient;


public class Doctowillcallyou extends BaseActivity implements SinchService.StartFailedListener {
    Button past;
    Button buy;
    Button our;
    Button consult;
    private ProgressDialog mSpinner;
    String CallID;
    String username;
    Intent intent;
    String pname;
    TextView timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctowillcallyou);


        timer = findViewById(R.id.mili);


        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished / 1000 + "");
                //here you can have your logic to set text to edittext

            }

            public void onFinish() {
                timer.setText("0");
            }

        }.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
        {

            //startPlayer();
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if(keyguardManager!=null)
                keyguardManager.requestDismissKeyguard(this, null);
        }
        else
        {

            //startPlayer();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }



     /*  Bundle bundle = getIntent().getExtras();
        CallID = bundle.getString("CALL_ID","nodate");
        username=bundle.getString("username","nodate");*/

       /* CallID = getIntent().getStringExtra("CALL_ID");
        username = getIntent().getStringExtra("username");*/

        SharedPreferences prefs = getSharedPreferences("Image", MODE_PRIVATE);
        final String phonenumber = prefs.getString("pimageid", "nodata");
        pname = prefs.getString("pnamefull","no");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    log(pname);



            }
        }, 100);

        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.consultDoctor);

        findViewById(R.id.btHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), HomePatient.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(0,0);
                startActivity(intent);
            }
        });

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        startActivity(new Intent(getApplicationContext(),PastConsultationNewLoginScreen.class));
                        overridePendingTransition(0,0);
                        return true;

                    /*case R.id.buyCard:
                        startActivity(new Intent(getApplicationContext(),Buycard.class));
                        overridePendingTransition(0,0);
                        return true;*/

                    case R.id.ourDoctors:
                        startActivity(new Intent(getApplicationContext(),OurDoctor.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.consultDoctor:
                        return true;
                }
                return false;
            }
        });




        /*
        past = findViewById(R.id.Past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(Doctowillcallyou.this, MainActivity.class);
                startActivity(chemistinten);
            }
        });
        buy = findViewById(R.id.card);
        buy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chemistinten = new Intent(Doctowillcallyou.this, Buycard.class);
                        startActivity(chemistinten);
                    }
                });

        our = findViewById(R.id.odoctor);
        our.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chemistinten = new Intent(Doctowillcallyou.this, OurDoctor.class);
                        startActivity(chemistinten);
                    }
                });

        consult = findViewById(R.id.consult);
        consult.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chemistinten = new Intent(Doctowillcallyou.this, ScratchCardNew.class);
                        startActivity(chemistinten);
                    }
                });

        */



    }
    /*@Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Doctowillcallyou.this,ScratchCardNew.class);
                        startActivity(intent);
                        finish();

                        System.exit(0);
                    }
                }).setNegativeButton("no", null).show();
    }*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),ScratchCardNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);
    }







    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }


    private void stopButtonClicked() {
    }




    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }



    @Override
    public void onStarted() {
        mSpinner.dismiss();
       }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Prepering video call from doctor ");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
    public void log(String uname){
          if (uname.isEmpty()) {
            Toast.makeText(Doctowillcallyou.this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!uname.equals(getSinchServiceInterface().getUserName())) {
            getSinchServiceInterface().stopClient();
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(uname);
            showSpinner();
        }


    }


}