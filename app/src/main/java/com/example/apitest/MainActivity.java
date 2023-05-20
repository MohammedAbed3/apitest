package com.example.apitest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {


    String url;
    String tag_json_obj = "json_obj_req";
    ProgressDialog pDialog;
    private static String TAG = "tag";

    TextView mFajerTv,mDhuhrTv,mAsrTv,mMaghribTv,mIshaTv,mLocationTv,mDataTv;
    EditText mSearshEt;
    Button mSearchBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mFajerTv = findViewById(R.id.tv_fajer);
        mDhuhrTv = findViewById(R.id.tv_dhur);
        mAsrTv = findViewById(R.id.tv_asr);
        mMaghribTv = findViewById(R.id.tv_maghrib);
        mIshaTv = findViewById(R.id.tv_isha);
        mDataTv = findViewById(R.id.tv_data);
        mLocationTv = findViewById(R.id.tv_location);
        mSearchBtn =findViewById(R.id.btn_search);
        mSearshEt =findViewById(R.id.et_search);



        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mLocation = mSearshEt.getText().toString().trim();
                if (mLocation.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter Your City", Toast.LENGTH_SHORT).show();
                }else {


                    url = "https://muslimsalat.com/"+mLocation+".json?key=66984889ad34aef5bb33d0b072cf73de";
                    searchLocation();
                }
            }
        });







    }

    private void searchLocation() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {

                    //get location

                    String country = response.get("country").toString();
                    String state = response.get("state").toString();
                    String city = response.get("city").toString();
                    String location = country+", "+ state+", "+city;

                    //get data
                    String date = response.getJSONArray("items").getJSONObject(0).get("date_for").toString();


                    //get namez timing
                    String mFijer = response.getJSONArray("items").getJSONObject(0).get("fajr").toString();
                    String mDhuhr = response.getJSONArray("items").getJSONObject(0).get("dhuhr").toString();
                    String mAsr = response.getJSONArray("items").getJSONObject(0).get("asr").toString();
                    String mMaghrib = response.getJSONArray("items").getJSONObject(0).get("maghrib").toString();
                    String mIsha = response.getJSONArray("items").getJSONObject(0).get("isha").toString();

                    mFajerTv.setText(mFijer);
                    mDhuhrTv.setText(mDhuhr);
                    mAsrTv.setText(mAsr);
                    mMaghribTv.setText(mMaghrib);
                    mIshaTv.setText(mIsha);
                    mLocationTv.setText(location);
                    mDataTv.setText(date);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                pDialog.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.toString());
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                pDialog.hide();
            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);


    }
}