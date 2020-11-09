package app.sagar.telesevek.PhoneAuthConsulation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import app.sagar.telesevek.DoctorSide;
import app.sagar.telesevek.PastCounsulation;
import app.sagar.telesevek.R;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    private EditText editText;
    private EditText etScratch;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auth);

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

                if(editText.isEnabled()&& editText.isInEditMode()){

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


    }
}
