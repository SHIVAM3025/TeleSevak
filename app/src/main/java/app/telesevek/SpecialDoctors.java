package app.telesevek;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class SpecialDoctors extends AppCompatActivity implements PaymentResultWithDataListener {

    ImageView female;
    ImageView child;
    ImageView common;
    ImageView shalya;
    ImageView corona;
  /*  ImageView initech;
    ImageView tuacharog;
    ImageView netrarog;*/
    String amt;
    String des;
    String URL="https://api.razorpay.com/v1/orders";
    RequestQueue mRequestQueue;
    String id;
    String type;
    ProgressBar pb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_doctors);

        Checkout.preload(getApplicationContext());

        pb=findViewById(R.id.pbPayment);

        female=findViewById(R.id.ivFemaleDoctor);
        child=findViewById(R.id.ivChildDoctor);
        shalya=findViewById(R.id.ivShalyaDoctor);
        common=findViewById(R.id.ivCommonDoctor);
        corona=findViewById(R.id.ivcovid);

        /*initech=findViewById(R.id.intechchikstak);
        tuacharog=findViewById(R.id.tyucha);
        netrarog=findViewById(R.id.netra);*/


        mRequestQueue= Volley.newRequestQueue(this);

        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.consultDoctor);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        startActivity(new Intent(getApplicationContext(),PastConsultationNewLoginScreen.class));
                        overridePendingTransition(0,0);
                        return true;

                  /*  case R.id.buyCard:
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

        corona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt="10000";
                des="स्त्री रोग विशेषज्ञ";
                type="5";

                SharedPreferences.Editor editor = getSharedPreferences("payshre", MODE_PRIVATE).edit();
                editor.putString("amt", amt);
                editor.putString("des", des);
                editor.putString("type", type);
                editor.apply();


                SharedPreferences.Editor editor2 = getSharedPreferences("Drselected", MODE_PRIVATE).edit();
                editor2.putString("typeall",type);
                editor2.apply();

                Intent intent = new Intent(getBaseContext(), coupon.class);
                intent.putExtra("id", "5");
                startActivity(intent);

            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt="29900";
                des="स्त्री रोग विशेषज्ञ";
                type="1";

                SharedPreferences.Editor editor = getSharedPreferences("payshre", MODE_PRIVATE).edit();
                editor.putString("amt", amt);
                editor.putString("des", des);
                editor.putString("type", type);
                editor.apply();


                SharedPreferences.Editor editor2 = getSharedPreferences("Drselected", MODE_PRIVATE).edit();
                editor2.putString("typeall",type);
                editor2.apply();

                Intent intent = new Intent(getBaseContext(), coupon.class);
                intent.putExtra("id", "1");
                startActivity(intent);

            }
        });
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt="29900";
                des="बच्चों का चिकित्सक";
                type="2";

                SharedPreferences.Editor editor = getSharedPreferences("payshre", MODE_PRIVATE).edit();
                editor.putString("amt", amt);
                editor.putString("des", des);
                editor.putString("type", type);
                editor.apply();

                SharedPreferences.Editor editor2 = getSharedPreferences("Drselected", MODE_PRIVATE).edit();
                editor2.putString("typeall",type);
                editor2.apply();

                Intent intent = new Intent(getBaseContext(), coupon.class);
                intent.putExtra("id", "2");
                startActivity(intent);
            }
        });

        common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt="19900";
                des="सामान्य चिकित्सक";
                type="4";

                SharedPreferences.Editor editor2 = getSharedPreferences("Drselected", MODE_PRIVATE).edit();
                editor2.putString("typeall",type);
                editor2.apply();


                SharedPreferences.Editor editor = getSharedPreferences("payshre", MODE_PRIVATE).edit();
                editor.putString("amt", amt);
                editor.putString("des", des);
                editor.putString("type", type);
                editor.apply();

                Intent intent = new Intent(getBaseContext(), coupon.class);
                intent.putExtra("id", "4");
                startActivity(intent);
            }
        });

        shalya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt="29900";
                des="शल्य चिकित्सक";
                type="3";

                SharedPreferences.Editor editor = getSharedPreferences("payshre", MODE_PRIVATE).edit();
                editor.putString("amt", amt);
                editor.putString("des", des);
                editor.putString("type", type);
                editor.apply();


                SharedPreferences.Editor editor2 = getSharedPreferences("Drselected", MODE_PRIVATE).edit();
                editor2.putString("typeall",type);
                editor2.apply();

                Intent intent = new Intent(getBaseContext(), coupon.class);
                intent.putExtra("id", "3");
                startActivity(intent);
            }
        });


/*


        initech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt="14900";
                des="ईऐनटी चिकित्सक  ";
                type="5";

                SharedPreferences.Editor editor = getSharedPreferences("payshre", MODE_PRIVATE).edit();
                editor.putString("amt", amt);
                editor.putString("des", des);
                editor.putString("type", type);
                editor.apply();

                Intent intent = new Intent(getBaseContext(), coupon.class);
                intent.putExtra("id", "5");
                startActivity(intent);
            }
        });



        tuacharog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt="14900";
                des="त्वचा विशेषज्ञ   ";
                type="6";

                SharedPreferences.Editor editor = getSharedPreferences("payshre", MODE_PRIVATE).edit();
                editor.putString("amt", amt);
                editor.putString("des", des);
                editor.putString("type", type);
                editor.apply();

                Intent intent = new Intent(getBaseContext(), coupon.class);
                intent.putExtra("id", "6");
                startActivity(intent);
            }
        });

        netrarog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amt="14900";
                des="त्वचा विशेषज्ञ   ";
                type="7";

                SharedPreferences.Editor editor = getSharedPreferences("payshre", MODE_PRIVATE).edit();
                editor.putString("amt", amt);
                editor.putString("des", des);
                editor.putString("type", type);
                editor.apply();

                Intent intent = new Intent(getBaseContext(), coupon.class);
                intent.putExtra("id", "7");
                startActivity(intent);
            }
        });
*/

        /*codeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                final AlertDialog.Builder alert = new AlertDialog.Builder(SpecialDoctors.this);
                View mView = getLayoutInflater().inflate(R.layout.custom_dialog,null);
                final EditText txt_inputText = (EditText)mView.findViewById(R.id.txt_input);
                Button btn_cancel = (Button)mView.findViewById(R.id.btn_cancel);
                Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
                alert.setView(mView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btn_okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if(txt_inputText.getText().toString().equals("9599225823")){
                           pb.setVisibility(View.VISIBLE);
                           amt="100";
                           des="कोड विशेषज्ञ चिकित्सक";
                           type="6";
                           order(amt,des);
                           pb.setVisibility(View.GONE);
                            }
                       else {
                           Toast.makeText(SpecialDoctors.this, "wrong code", Toast.LENGTH_SHORT).show();
                       }

                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();

            }


        });
*/
    }

    public void order(String amount,String des)  {

        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_11");



            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, URL, orderRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(SpecialDoctors.this, "SUCCESS ORDER", Toast.LENGTH_SHORT).show();
                    try {
                        id=response.getString("id");
                        startPayment(amount,des,id,"offer_GZ07Ny9TQZsvnP");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(SpecialDoctors.this, "FAIL ORDER", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String cred=String.format("%s:%s","rzp_live_kVdwpjtDFeNxWV","CQZ8WbMl41I36bTbSnXlcpD5");
                    String auth="Basic "+ Base64.encodeToString(cred.getBytes(),Base64.NO_WRAP);
                    params.put("content-type","application/json");
                    params.put("authorization",auth);
                    return params;
                }
            };
            mRequestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }





    }



    public void startPayment(String amount,String description,String id,String offerid){

        final Activity activity=this;
        Checkout co=new Checkout();

        JSONObject paymentObject=new JSONObject();

        try {
             JSONObject prefill=new JSONObject();
             prefill.put("name","sagar");
             prefill.put("email","sagarsurvase525@gmail.com");
             prefill.put("contact","7038023166");

            paymentObject.put("prefill",prefill);
            paymentObject.put("name","TeleSevak");
            paymentObject.put("description",description);
            paymentObject.put("currency","INR");
            paymentObject.put("amount",amount);
            paymentObject.put("order_id",id);
            paymentObject.put("offers",offerid);



            JSONObject method=new JSONObject();
            method.put("card","1");
            method.put("netbanking","1");
            method.put("upi","1");
            method.put("wallet","1");
            //checkout.put("method",method);
            // options.put("checkout",checkout);
            paymentObject.put("method",method);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        co.open(activity,paymentObject);

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        try {
            Toast.makeText(this, "Payment Successful! Order ID: "+paymentData.getOrderId(), Toast.LENGTH_LONG).show();
            //startActivity(new Intent(getApplicationContext(),AddPatiant.class));
            Intent intent=new Intent(getApplicationContext(),FormAfterPayment.class);
            intent.putExtra("type",type);
            intent.putExtra("orderID",paymentData.getOrderId());
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Exception onPaymentSuccess", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        Toast.makeText(this, "Payment Failed!", Toast.LENGTH_SHORT).show();
    }
}
