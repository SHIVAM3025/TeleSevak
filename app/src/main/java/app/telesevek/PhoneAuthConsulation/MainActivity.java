package app.telesevek.PhoneAuthConsulation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import app.telesevek.Buycard;
import app.telesevek.OurDoctor;
import app.telesevek.PastCounsulation;
import app.telesevek.R;
import app.telesevek.ScratchCardNew;

public class MainActivity extends AppCompatActivity {
    private EditText etNumber;
    private View btCard;
    private View btPhoneNumber;
    private Button btContinue;
    private FirebaseAuth firebaseAuth;
    private boolean isPhone=true;
    private String phoneNumber;
    private String cardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_consultation_new_login_screen);

        SharedPreferences prefs = getSharedPreferences("past", MODE_PRIVATE);
        String dname = prefs.getString("passdata", null);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //Starting the User Profile Activity if the user is already Logged in
            if(TextUtils.isEmpty(dname)){
            }
            else {
                if (Objects.equals(dname, "already")) {
                    startActivity(new Intent(getApplicationContext(), PastCounsulation.class));
                }
            }


        }

        etNumber=findViewById(R.id.etEnterNumber);
        btCard=findViewById(R.id.relCard);
        btPhoneNumber=findViewById(R.id.relButtons);
        btContinue=findViewById(R.id.btnContinue);
        final TextView tvCard=findViewById(R.id.tvCard);
        final TextView tvPhone=findViewById(R.id.tvPhone);
        final ImageView ivPhone=findViewById(R.id.ibCall);
        final ImageView ivCard=findViewById(R.id.ivCard);


        btPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btPhoneNumber.setBackgroundResource(R.drawable.button_clr_shp);
                btCard.setBackgroundResource(R.drawable.faded_shape);
                tvCard.setTextColor(Color.parseColor("#D1CDCD"));
                ivCard.setColorFilter(getResources().getColor(R.color.whitish));
                tvPhone.setTextColor(Color.parseColor("#ffffff"));
                ivPhone.setColorFilter(getResources().getColor(R.color.white));
                isPhone=true;
                etNumber.setHint("फ़ोन नंबर भरें");
            }
        });

        btCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btCard.setBackgroundResource(R.drawable.color_shape_right);
                btPhoneNumber.setBackgroundResource(R.drawable.faded_shape_left);
                tvPhone.setTextColor(Color.parseColor("#D1CDCD"));
                ivPhone.setColorFilter(getResources().getColor(R.color.whitish));
                tvCard.setTextColor(Color.parseColor("#ffffff"));
                ivCard.setColorFilter(getResources().getColor(R.color.white));
                isPhone=false;
                etNumber.setHint("कार्ड नंबर भरें");

            }
        });



        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isPhone){
                    phoneNumber = etNumber.getText().toString().trim();

                    if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
                        etNumber.setError("Valid number is required");
                        etNumber.requestFocus();
                        return;
                    }
                    String phoneNumberNew = "+" + "91" + phoneNumber;

                    Intent intent = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
                    intent.putExtra("phonenumber", phoneNumberNew);
                    startActivity(intent);

                }

                else {
                    cardNumber= etNumber.getText().toString().trim();

                    if(cardNumber.isEmpty()|| cardNumber.length()<6 ||cardNumber.length()>8){
                        etNumber.setError("Valid number is required");
                        etNumber.requestFocus();
                        return;
                    }

                    Intent intent=new Intent(getApplicationContext(),PastCounsulation.class);
                    intent.putExtra("cardNumber",cardNumber);
                    intent.putExtra("isScratchCard",true);
                    startActivity(intent);
                }
            }
        });


        BottomNavigationView bottomNav=findViewById(R.id.bottomNavPastLoginNew);
        bottomNav.setSelectedItemId(R.id.pastConsult);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        return true;

                  /*  case R.id.buyCard:
                        startActivity(new Intent(getApplicationContext(), Buycard.class));
                        overridePendingTransition(0,0);
                        return true;*/

                    case R.id.ourDoctors:
                        startActivity(new Intent(getApplicationContext(), OurDoctor.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.consultDoctor:
                        startActivity(new Intent(getApplicationContext(), ScratchCardNew.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        /*

        SharedPreferences prefs = getSharedPreferences("past", MODE_PRIVATE);
        String dname = prefs.getString("passdata", null);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //Starting the User Profile Activity if the user is already Logged in
            if(TextUtils.isEmpty(dname)){
            }
            else {
                if (Objects.equals(dname, "already")) {
                    startActivity(new Intent(getApplicationContext(), PastCounsulation.class));
                }
            }


        }

        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));
        etScratch=findViewById(R.id.etScratch);
        editText = findViewById(R.id.editTextPhone);

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];

                if(editText.isEnabled()){

                    String number = editText.getText().toString().trim();

                    if (number.isEmpty() || number.length() < 10) {
                        editText.setError("Valid number is required");
                        editText.requestFocus();
                        return;
                    }

                    String phoneNumber = "+" + code + number;

                    Intent intent = new Intent(MainActivity.this, VerifyPhoneActivity.class);
                    intent.putExtra("phonenumber", phoneNumber);
                    startActivity(intent);
                }
                else{
                    String cardNum=etScratch.getText().toString().trim();

                    if (cardNum.isEmpty() || cardNum.length() > 8|| cardNum.length()<6) {
                        etScratch.setError("Valid Card Number is required");
                        etScratch.requestFocus();
                        return;
                    }

                    Intent intent=new Intent(MainActivity.this,PastCounsulation.class);
                    intent.putExtra("cardNumber",cardNum);
                    intent.putExtra("isScratchCard",true);
                    startActivity(intent);
                }


            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableEditText(etScratch);
                enableEditText(editText);
                //String number = editText.getText().toString().trim();

            }
        });

        etScratch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                disableEditText(editText);
                enableEditText(etScratch);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        //editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);


    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);


    }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
