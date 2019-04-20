package android.example.com.emergencyaid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class LoginActivity extends AppCompatActivity {

EditText uname;
EditText passwd;
String message;
String token;
Button logIn;
String username;
String password;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    setTitle("Enter login credentials");
    uname = (EditText) findViewById(R.id.unametext);
    passwd = (EditText) findViewById(R.id.passtext);
    logIn = (Button) findViewById(R.id.loginbtn);

    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("tripDetails", Context.MODE_PRIVATE);
    sharedPref.getString("token","");
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString("token",token);
    editor.commit();

}

@Override
public void onBackPressed() {

}

public void SubmitCred(View v) {
    username=uname.getText().toString();
    password=passwd.getText().toString();

    String url = "http://projectaa.eastus.cloudapp.azure.com/api/user/login";
    JSONObject postparam = new JSONObject();
    try {
        postparam.put("username", username);
        postparam.put("password", password);
        Toast.makeText(getApplicationContext(),"sending login request",
                Toast.LENGTH_SHORT).show();

    } catch (JSONException e) {
        e.printStackTrace();
    }
    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
            url, postparam,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        token = response.getString("token");
                        message = response.getString("message");
                        if(response!=null ) {

                            if (message.equals("Ok")) {
                                Toast.makeText(getApplicationContext(),"You have been logged in",
                                        Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPref = getApplicationContext().
                                        getSharedPreferences("tokenDetails", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("token", token);
                                editor.commit();
                                Intent next = new Intent(LoginActivity.this, StartTripActivity.class);
                                startActivity(next);
                            }else{
                                Toast.makeText(getApplicationContext(),"Invalid credentials",
                                        Toast.LENGTH_SHORT);
                                finish();
                                startActivity(getIntent());
                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), "could not get a response",
                                    Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ERROR:",error+"");
                    Log.d("VOLLEY", error.toString());
                    Toast.makeText(getApplicationContext(),"Error in logging in.Try again",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
            });
    Volley.newRequestQueue(this).add(jsonObjReq);
}
}


/*
for (int c = 0; c < lat.length; c++) {


    Handler handler = new Handler();
final int finalC = c;
    handler.postDelayed(new Runnable() {
public void run() {




    JSONObject postparam = new JSONObject();
    try {
    postparam.put("token", token);
    postparam.put("tripid", tripID);
    postparam.put("latitude", lat[finalC]);
    postparam.put("longitude", longitude[finalC]);
    // Log.v("request", postparam + "");
//                        Toast.makeText(getApplicationContext(), "sending update request", Toast.LENGTH_SHORT).show();
    //Toast.makeText(getApplicationContext(), postparam + "", Toast.LENGTH_LONG).show();
    } catch (JSONException e) {
    e.printStackTrace();
    }

    DateTimeFormatter formatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    LocalDateTime now = LocalDateTime.now();
    //String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
    Log.v("Send time", formatter.format(now) + "");



    }
    }, 5000);


    }
    */