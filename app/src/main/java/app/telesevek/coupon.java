package app.telesevek;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import app.telesevek.uploadpkg.ShowImageActivity;


public class coupon extends AppCompatActivity implements PaymentResultWithDataListener {

    Date date1;
    Date date;


    Button parcha;
    Button buy;
    Button ourdoctor;

    private ProgressDialog mSpinner;
    FirebaseFirestore fStore;
    EditText scrached;
    Button submit;

    ProgressDialog pd;
    String oldtime;
    String oldcard;

    String Strname;
    String Strdate;
    String Strphone;
    String DoctorId;
    String ConsultationId;
    String cardnumber;
    String dateFOLLOW;



    String amt;
    String des;
    String URL="https://api.razorpay.com/v1/orders";
    RequestQueue mRequestQueue;
    String id;
    String type;
    ProgressBar pb;

    TextView fixprice;
    TextView codeprice;
    TextView realprice;



    ProgressBar progressBar;
    RecyclerView friendList;
    Button past;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    Button bhugtankaire;

    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caouponxml);


        friendList = findViewById(R.id.friend_list);
        progressBar = findViewById(R.id.progress_bar);
        init();
        getFriendList();

        pb=findViewById(R.id.pbPayment);
        mRequestQueue= Volley.newRequestQueue(this);
        RelativeLayout back = findViewById(R.id.appbar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(coupon.this,SpecialDoctors.class);
                startActivity(go);
            }
        });

        //


        //

        //

        scrached = findViewById(R.id.etScratch);
        submit = findViewById(R.id.submit);
        bhugtankaire = findViewById(R.id.paynow);

        fixprice = findViewById(R.id.subtitle);
        codeprice = findViewById(R.id.subtitle2);
        realprice = findViewById(R.id.realprice);

        fStore = FirebaseFirestore.getInstance();

       /* findViewById(R.id.ivPaymentSpecial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SpecialDoctors.class));
            }
        });*/

       /* past = findViewById(R.id.Past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chemistinten = new Intent(ScratchCardNew.this, MainActivity.class);
                startActivity(chemistinten);
            }
        });


        buy = findViewById(R.id.card);
        buy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chemistinten = new Intent(ScratchCardNew.this, Buycard.class);
                        startActivity(chemistinten);
                    }
                });

        ourdoctor = findViewById(R.id.odoctor);
        ourdoctor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chemistinten = new Intent(ScratchCardNew.this, OurDoctor.class);
                        startActivity(chemistinten);
                    }
                });*/


        //


        SharedPreferences prefs = getSharedPreferences("Consultpre", MODE_PRIVATE);
        String cid = prefs.getString("cid", null);

        SharedPreferences prefs2 = getSharedPreferences("payshre", MODE_PRIVATE);
        amt = prefs2.getString("amt", "299");//"No name defined" is the default value.
        des = prefs2.getString("des", "299");//"No name defined" is the default value.
        type = prefs2.getString("type", "स्त्री रोग विशेषज्");//"No name defined" is the default value.

        if(amt.equals("29900")){
            fixprice.setText("299");
            realprice.setText("299");
            codeprice.setText("299");
        }
        if (amt.equals("19900")){
            fixprice.setText("199");
            realprice.setText("199");
            codeprice.setText("199");
        }
        if (amt.equals("10000")){
            fixprice.setText("100");
            realprice.setText("100");
            codeprice.setText("100");
        }

       /* codeprice.setText(amt);
        fixprice.setText(amt);
        realprice.setText(amt);*/

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                    DocumentReference documentReferencefollow = fStore.collection("coupon").document("coupons");
                    documentReferencefollow.addSnapshotListener(coupon.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (documentSnapshot.exists()) {
                                String c1 = documentSnapshot.getString("ref");
                                String c2 = documentSnapshot.getString("ref2");
                                if (scrached.getText().toString().equals(c1)) {
                                    codeprice.setText("1");
                                    realprice.setText("1");
                                    submit.setText("दर्ज हुवा");
                                    Toast.makeText(coupon.this, "valid  code", Toast.LENGTH_SHORT).show();
                                }
                                if (scrached.getText().toString().equals(c2)) {
                                    codeprice.setText("0");
                                    realprice.setText("0");
                                    submit.setText("दर्ज हुवा");
                                    Toast.makeText(coupon.this, "valid  code", Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(coupon.this, "invalid code", Toast.LENGTH_SHORT).show();

                              /*  SharedPreferences prefs = getSharedPreferences("payshre", MODE_PRIVATE);
                                amt = prefs.getString("amt", "299");//"No name defined" is the default value.
                                des = prefs.getString("des", "299");//"No name defined" is the default value.
                                type = prefs.getString("type", "स्त्री रोग विशेषज्");//"No name defined" is the default value.

                                pb.setVisibility(View.VISIBLE);
                                amt="29900";
                                des="स्त्री रोग विशेषज्ञ";
                                type="1";
                                order(amt,des);
                                pb.setVisibility(View.GONE);
*/
                                }


                            } else {
                                Log.d("tag", "onEvent: Document do not exists");
                            }
                        }
                    });


            }
        });

        bhugtankaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            SharedPreferences prefs = getSharedPreferences("Drselected", MODE_PRIVATE);
            String drstatus = prefs.getString("drname","0");//"No name defined" is the default value.
                String stramt = realprice.getText().toString();
                if (stramt.equals("0"))
                {
                    Intent intent=new Intent(getApplicationContext(),FormAfterPayment.class);
                    intent.putExtra("type",type);
                    startActivity(intent);
                    pb.setVisibility(View.GONE);
                }
                if (stramt.equals("1"))
                {
                    amt = "100";
                    pb.setVisibility(View.VISIBLE);
                    type="1";
                    order(amt,des);
                    pb.setVisibility(View.GONE);
                }
                if(stramt.equals("299")){
                    amt = "29900";
                    pb.setVisibility(View.VISIBLE);
                    type="1";
                    order(amt,des);
                    pb.setVisibility(View.GONE);
                }
                if (stramt.equals("199")){
                    amt = "19900" +
                            "";
                    pb.setVisibility(View.VISIBLE);
                    type="1";
                    order(amt,des);
                    pb.setVisibility(View.GONE);

                }
                if (stramt.equals("100")){
                    amt = "10000" +
                            "";
                    pb.setVisibility(View.VISIBLE);
                    type="1";
                    order(amt,des);
                    pb.setVisibility(View.GONE);

                }


            }
        });



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

                    Toast.makeText(coupon.this, "SUCCESS ORDER", Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(coupon.this, "FAIL ORDER", Toast.LENGTH_SHORT).show();
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

         JSONObject prefill=new JSONObject();
         prefill.put("email","telesevek.developer@gmail.com");
          prefill.put("contact","7318504444");

            paymentObject.put("prefill",prefill);
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

    private void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        friendList.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getFriendList(){
        String dr_id = getIntent().getStringExtra("id");
        query = db.collection("Doctor").whereEqualTo("IsActive", "true").whereEqualTo("TypeOfDoctor", dr_id);

/*
        if (dr_id.equals("5")){
                query = db.collection("Doctor").whereEqualTo("IsActive", "true").whereEqualTo("CovidDoctor", "true");
            }
            else {
                query = db.collection("Doctor").whereEqualTo("IsActive", "true").whereEqualTo("TypeOfDoctor", dr_id);
            }
*/

          FirestoreRecyclerOptions<DoctorResponse> response = new FirestoreRecyclerOptions.Builder<DoctorResponse>()
                .setQuery(query, DoctorResponse.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DoctorResponse, coupon.FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(coupon.FriendsHolder holder, int position, DoctorResponse model) {
                progressBar.setVisibility(View.GONE);
                holder.textName.setText(model.getName());
                holder.textCompany.setText(model.getFullAddress());

                Glide.with(getApplicationContext()).load(model.getUrl()).into(holder.img);

               /* Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.imageView);*/

              holder.itemView.setOnClickListener(v -> {
                    Snackbar.make(friendList, model.getName()+" you have selected ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                  // MY_PREFS_NAME - a static String variable like:
//public static final String MY_PREFS_NAME = "MyPrefsFile";
                  SharedPreferences.Editor editor = getSharedPreferences("Drselected", MODE_PRIVATE).edit();
                  editor.putString("drname", model.getPhoneNumber());
                  editor.putString("type",model.getTypeOfDoctor());
                  editor.apply();

                  holder.border.setBackgroundResource(R.drawable.coupanborder);
                });

            }

            @Override
            public coupon.FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item_online, group, false);



                return new coupon.FriendsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        friendList.setAdapter(adapter);
    }

    public class FriendsHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textCompany;

        ImageView img;
        CardView border;

        public FriendsHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.title);
            textCompany = itemView.findViewById(R.id.subtitle);

            img= itemView.findViewById(R.id.icon);
            border= itemView.findViewById(R.id.rl);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    /*@Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(OurDoctor.this,ScratchCardNew.class);
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

}