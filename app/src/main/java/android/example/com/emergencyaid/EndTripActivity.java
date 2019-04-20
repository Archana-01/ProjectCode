package android.example.com.emergencyaid;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import static java.time.format.DateTimeFormatter.ofPattern;


public class EndTripActivity extends AppCompatActivity {
    String token = "";
    int tripID = 0;
    String startTime="";

    private double[] lat={12.825920,12.830691,12.861840,12.876112,12.914788,12.919154,12.919408,12.921880,12.922552,12.922949,12.923083,12.923448,12.923534};
    private double[] longitude={80.040725,80.045653,80.073049,80.078994,80.101887,80.109843,80.110166,80.112858,80.113567,80.113764,80.113854,80.114213,80.114298};


    private TextView latView;
    private TextView longView;
    private TextView timeOfStart;
    private boolean StartedService = false;

    @RequiresApi(api = 26)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_trip);
        setTitle("Current trip details");
        final TextView txtVwStart = (TextView) findViewById(R.id.startloctext);
        final TextView txtVwEnd = (TextView) findViewById(R.id.endloctext);
        timeOfStart = (TextView) findViewById(R.id.startedTime);
        Log.v("end",2+"");

        String startLoc = "";
        String endLoc = "";
        latView = (TextView) findViewById(R.id.latitudeText);
        latView.setVisibility(View.INVISIBLE);
        longView = (TextView) findViewById(R.id.longitudeText);
        longView.setVisibility(View.INVISIBLE);

       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Could not get location permission",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }
        */
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("tripDetails", Context.MODE_PRIVATE);
        startLoc=sharedPref.getString("sourceName","");
        endLoc=sharedPref.getString("destinationName","");
        tripID=sharedPref.getInt("tripID",0);
        token=sharedPref.getString("token","");
        startTime=sharedPref.getString("startTime","");
        txtVwStart.setText(startLoc+"");
        txtVwEnd.setText(endLoc+"");
        //DateTimeFormatter formatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        //LocalDateTime dateTime = LocalDateTime.parse(startTime, formatter);
        //String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
        timeOfStart.setText(startTime);


       /* LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationService.ELATITUDE);
                        String longitude = intent.getStringExtra(LocationService.ELONGITUDE);
                        if (latitude != null && longitude != null) {
                            latView.setText(latitude+"");
                            longView.setText(longitude);
                            Toast.makeText(getApplicationContext(), latitude+","+longitude, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new IntentFilter(LocationService.LOCATION_BROADCAST)
        );
        */

        final String url ="http://projectaa.eastus.cloudapp.azure.com/api/trip/update";
        Toast.makeText(getApplicationContext(),"token:"+token,Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),"id:"+tripID,Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),"latitude:"+latView.getText().toString(),Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),"longitude:"+longView.getText().toString(),Toast.LENGTH_SHORT).show();

        Handler handler = new Handler();
        for(int c = 0; c < lat.length; c++){
            final int finalCount = c;
            int delay=10000*finalCount;
            Log.v("delay:",delay+"");
            handler.postDelayed(new Runnable() {
                public void run() {
                  Log.i("c:", finalCount +"");


                    JSONObject postparam = new JSONObject();
                    try {
                        postparam.put("token", token);
                        postparam.put("tripid", tripID);
                        postparam.put("latitude", lat[finalCount]);
                        postparam.put("longitude", longitude[finalCount]);
                         Log.v("request", postparam + "");
//                        Toast.makeText(getApplicationContext(), "sending update request", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), postparam + "", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            url, postparam,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response != null) {

                                            String message = response.getString("message");
                                            if ((message.equals("Ok"))) {
                                                Toast.makeText(getApplicationContext(), "Updated EV coordinates sent", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    error.printStackTrace();
                                    Toast.makeText(getApplicationContext(),"Updated EV coordinates not sent",Toast.LENGTH_LONG).show();
                                }
                            });
                    Volley.newRequestQueue(getApplicationContext()).add(jsonObjReq);

                    DateTimeFormatter formatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    LocalDateTime now = LocalDateTime.now();
                    //String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
                    Log.v("Send time", formatter.format(now) + "");

                }
            }, 30000*finalCount);
        }

//        Timer timer = new Timer();
//        timer.schedule(updateTrip(), 1000L, 5000L);

            /*
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, postparam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response != null) {

                                    String message = response.getString("message");
                                    if ((message.equals("Ok"))) {
                                        Toast.makeText(getApplicationContext(), "Updated EV coordinates sent", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Updated EV coordinates not sent",Toast.LENGTH_LONG).show();
                        }
                    });
            Volley.newRequestQueue(this).add(jsonObjReq);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
*/
        }



    @RequiresApi(api = 26)
    public void updateTrip() {
        for (int c = 0; c < lat.length; c++) {
            JSONObject postparam = new JSONObject();
            try {
                postparam.put("token", token);
                postparam.put("tripid", tripID);
                postparam.put("latitude", lat[c]);
                postparam.put("longitude", longitude[c]);
                Log.v("request", postparam + "");
                Toast.makeText(getApplicationContext(), "sending update request", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), postparam + "", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            DateTimeFormatter formatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            LocalDateTime now = LocalDateTime.now();
            //String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
            Log.v("Send time", formatter.format(now) + "");

            /*
            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        */

            /*
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, postparam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response != null) {

                                    String message = response.getString("message");
                                    if ((message.equals("Ok"))) {
                                        Toast.makeText(getApplicationContext(), "Updated EV coordinates sent", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Updated EV coordinates not sent",Toast.LENGTH_LONG).show();
                        }
                    });
            Volley.newRequestQueue(this).add(jsonObjReq);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        */
        }
    }

    @Override
    public void onBackPressed() {

    }

 /*   @Override
    public void onResume() {
        super.onResume();
        startService();
    }
*/
 /*   private void startService(){

        if (!StartedService && latView != null && longView != null) {
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
            StartedService = true;
        }
    }
*/
  /*  @Override
    public void onDestroy() {

        stopService(new Intent(this, LocationService.class));
        StartedService = false;


        super.onDestroy();
    }
*/
    public void endTrip(View v) {
        StartedService = false;
        String url ="http://projectaa.eastus.cloudapp.azure.com/api/trip/Completed";
        JSONObject postparam = new JSONObject();
        try {
            postparam.put("token", token);
            postparam.put("tripid", tripID);
            Log.v("request in end", postparam+"");
            Toast.makeText(getApplicationContext(),"sending end trip request",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, postparam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                           // int newTripId = response.getInt("tripID");
                            if(response!=null && message.equals("Ok")){
                                Toast.makeText(getApplicationContext(),"Trip ended successfully",Toast.LENGTH_SHORT).show();
                                Intent toStart = new Intent(EndTripActivity.this,StartTripActivity.class);
                                startActivity(toStart);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(this).add(jsonObjReq);
    }
}
