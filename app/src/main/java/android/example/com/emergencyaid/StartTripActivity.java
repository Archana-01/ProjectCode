package android.example.com.emergencyaid;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import static android.R.attr.layout_marginTop;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.M;
import static java.time.format.DateTimeFormatter.ofPattern;


public class StartTripActivity extends AppCompatActivity {

    EditText startLoc;
    EditText endLoc;
    String startLocName;
    String endLocName;

    LatLng latLngStart;
    LatLng latLngEnd;
    double longitudeStart;
    double latitudeStart;
    double longitudeEnd;
    double latitudeEnd;
    String token;
    int tripID = 0;
    boolean isStarted;
    boolean isCompleted;
    String startedTime;
    String createdTime;

    private TextView txtVwStart;
    private TextView txtVwEnd;
    private TextView latView;
    private TextView longView;
    private TextView timeOfCreation;
    private boolean StartedService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        setTitle("Trip to start");
        txtVwStart = (TextView) findViewById(R.id.startloctext);
        txtVwEnd = (TextView) findViewById(R.id.endloctext);
        timeOfCreation = (TextView) findViewById(R.id.createdTime);
        String startLoc = "";
        String endLoc = "";
        String urlTrip="";
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("tokenDetails",Context.MODE_PRIVATE);
        token = sharedPref.getString("token","");


        urlTrip ="http://projectaa.eastus.cloudapp.azure.com/api/Trip/GetList";

        JSONObject postparam = new JSONObject();
        try {
          //  Toast.makeText(getApplicationContext(),"Token in getList:"+token,Toast.LENGTH_SHORT).show();
            postparam.put("token",token);
            Toast.makeText(getApplicationContext(),"sending getList request",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlTrip, postparam,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = 26)
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response!=null) {


                                JSONArray trips = response.getJSONArray("trips");
                                //Toast.makeText(getApplicationContext(),trips.length()+"",Toast.LENGTH_SHORT).show();

                                for (int i = 0; i <trips.length(); i++) {


                                    JSONObject trip = trips.getJSONObject(i);
                                  //  Toast.makeText(getApplicationContext(),"trip:"+trip,Toast.LENGTH_SHORT).show();

                                    isStarted = trip.getBoolean("isStarted");
                                    isCompleted = trip.getBoolean("isCompleted");

                                    if(!isStarted && !isCompleted) {
                                        tripID = trip.getInt("id");
                                        endLocName = trip.getString("destination");
                                        startLocName = trip.getString("source");
//                                        Toast.makeText(getApplicationContext(),startLocName+"",Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(getApplicationContext(),"time:"+createdTime,Toast.LENGTH_LONG).show();
                                        latitudeStart = trip.getDouble("sourceLat");
                                        longitudeStart = trip.getDouble("sourceLong");
                                        longitudeEnd = trip.getDouble("destinationLat");
                                        latitudeEnd = trip.getDouble("destinationLong");
                                        txtVwStart.setText(startLocName+"");
                                        txtVwEnd.setText(endLocName+"");
                                        createdTime = trip.getString("createdTime");
                                        startedTime=trip.getString("startTime");
                                       // Toast.makeText(getApplicationContext(),"time:"+createdTime,Toast.LENGTH_LONG).show();
                                        //Toast.makeText(getApplicationContext(),"timeS:"+startedTime,Toast.LENGTH_LONG).show();

                                        //DateTimeFormatter formatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                                        //LocalDateTime dateTime = LocalDateTime.parse(createdTime, formatter);
                                        //String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
                                        timeOfCreation.setText(createdTime);
                                        txtVwStart.setText(startLocName);
                                        break;

                                    }
                                    else{continue;}


                                }

                                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("tripDetails", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();

                                editor.putInt("tripID",tripID);
                               // Toast.makeText(getApplicationContext(),"id in start:"+tripID,Toast.LENGTH_SHORT).show();
                                editor.putFloat("latitude", (float) latitudeStart);
                                editor.putFloat("longitude", (float) longitudeStart);
                                editor.putString("sourceName",startLocName);
                                editor.putString("destinationName",endLocName);
                                editor.putString("startTime",startedTime);
                                editor.putString("startTime",startedTime);
                                editor.commit();
                                //Intent endIntent = new Intent(StartTripActivity.this, EndTripActivity.class);
                                //startActivity(endIntent);


                            }else{
                                Toast.makeText(getApplicationContext(),"Response is null ",Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Unable to get trip details ",Toast.LENGTH_SHORT).show();

                    }
                });

        Volley.newRequestQueue(this).add(jsonObjReq);


       }

    @Override
    public void onBackPressed() {

    }


    public void startTrip(View v) {
        String url ="http://projectaa.eastus.cloudapp.azure.com/api/trip/starttrip";
       // Toast.makeText(getApplicationContext(),"clicked "+token,Toast.LENGTH_SHORT).show();


        JSONObject postparam = new JSONObject();
        try {
            postparam.put("Token",token);
            postparam.put("TripId",tripID);
            Toast.makeText(getApplicationContext(),"sending start trip request",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    //    Toast.makeText(getApplicationContext(),"Before requestObject",Toast.LENGTH_SHORT).show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, postparam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                    Toast.makeText(getApplicationContext(),response+"",Toast.LENGTH_SHORT).show();
                            if(response!=null) {

                                String message = response.getString("message");
                                int tripIDIn = response.getInt("tripID");
                                Log.i("token in start:",token+"");
                                Log.i("id in start:",tripIDIn+"");
                           //     Toast.makeText(getApplicationContext(),"before ok",Toast.LENGTH_SHORT).show();

                                if(message.equals("Ok")) {
                         //           Toast.makeText(getApplicationContext(),"Ok?1",Toast.LENGTH_SHORT).show();

                           //         Toast.makeText(getApplicationContext(),"Reponse got, ok",Toast.LENGTH_SHORT).show();
                                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("tripDetails", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                            //        Toast.makeText(getApplicationContext(),"Ok?2",Toast.LENGTH_SHORT).show();

                                    editor.putInt("tripID",tripIDIn);
                                    editor.putString("token",token);
                            //        Toast.makeText(getApplicationContext(),"Ok?3",Toast.LENGTH_SHORT).show();

                                    editor.commit();
                                    Log.v("start",1+"");
                          //          Toast.makeText(getApplicationContext(),"Will go?",Toast.LENGTH_SHORT).show();
                                    Intent endingIntent = new Intent(StartTripActivity.this, EndTripActivity.class);
                                    startActivity(endingIntent);
                                }else{
                                    Toast.makeText(getApplicationContext(),"Response is null",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(),"unable to start trip ",Toast.LENGTH_SHORT).show();

                    }
                });
        Volley.newRequestQueue(this).add(jsonObjReq);

    }

   public void signout(View v){
//Enter correct api url
 /*  String url ="logout url";
        JSONObject postparam = new JSONObject();
        try {
            postparam.put("token", token);
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
                            if(response!=null && message.equals("Ok")){
                                Toast.makeText(getApplicationContext(),"You have been logged out successfully",Toast.LENGTH_SHORT).show();
                                Intent toStart = new Intent(StartTripActivity.this,MainActivity.class);
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

*/
       SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("tripDetails", Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = sharedPref.edit();
       editor.putString("token","");
       editor.commit();

       Toast.makeText(getApplicationContext(),"You have been successfully logged out",Toast.LENGTH_SHORT).show();
        Intent loggingOut = new Intent(StartTripActivity.this,LoginActivity.class);
        startActivity(loggingOut);
    }



}
