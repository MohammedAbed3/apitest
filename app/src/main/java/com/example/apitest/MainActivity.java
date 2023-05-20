package com.example.apitest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREFS_KEY = "MyPrefs";
    private static final String SELECTED_COUNTRY_KEY = "selectedCountry";

    String url;
    ProgressDialog pDialog;
    private static final String TAG = "tag";

    TextView mFajerTv, mDhuhrTv, mAsrTv, mMaghribTv, mIshaTv, mLocationTv, mDataTv;
    Spinner mCountrySpinner;
    Button mSearchBtn;

    private String selectedCountry;
    private SharedPreferences sharedPreferences;

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
        mSearchBtn = findViewById(R.id.btn_search);
        mCountrySpinner = findViewById(R.id.spinner_country);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);

        // Initialize country spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.arabic_countries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCountrySpinner.setAdapter(adapter);

        // Set the selected country from SharedPreferences
        selectedCountry = sharedPreferences.getString(SELECTED_COUNTRY_KEY, "");
        if (!selectedCountry.isEmpty()) {
            int position = adapter.getPosition(selectedCountry);
            mCountrySpinner.setSelection(position);
        }

        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedCountry = adapterView.getItemAtPosition(position).toString();
                url = "https://muslimsalat.com/" + selectedCountry + ".json?key=66984889ad34aef5bb33d0b072cf73de";
                searchLocation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCountry.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Select a Country", Toast.LENGTH_SHORT).show();
                } else {
                    url = "https://muslimsalat.com/" + selectedCountry + ".json?key=66984889ad34aef5bb33d0b072cf73de";
                    searchLocation();
                }
            }
        });
    }

    private void searchLocation() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Get location
                            String country = response.get("country").toString();
                            String state = response.get("state").toString();
                            String city = response.get("city").toString();
                            String location = country + ", " + state + ", " + city;

                            // Get data
                            String date = response.getJSONArray("items").getJSONObject(0).get("date_for").toString();

                            // Get prayer timings
                            String mFajer = response.getJSONArray("items").getJSONObject(0).get("fajr").toString();
                            String mDhuhr = response.getJSONArray("items").getJSONObject(0).get("dhuhr").toString();
                            String mAsr = response.getJSONArray("items").getJSONObject(0).get("asr").toString();
                            String mMaghrib = response.getJSONArray("items").getJSONObject(0).get("maghrib").toString();
                            String mIsha = response.getJSONArray("items").getJSONObject(0).get("isha").toString();

                            mFajerTv.setText(mFajer);
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

    @Override
    protected void onPause() {
        super.onPause();
        // Save selected country in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SELECTED_COUNTRY_KEY, selectedCountry);
        editor.apply();
    }
}
