package com.example.userlocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.security.Permission;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.Html;
import android.util.FloatProperty;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class MainActivity<sensorManager> extends AppCompatActivity implements SensorEventListener {
    private final String TAG = "MainActivity";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    DatabaseReference reff;
    DatabaseReference reffi;
    DatabaseReference pulling;
    String latitude;
    String longitude;
    boolean running=false;
    String s;
    SensorManager sensorManager;
    Button btn;
    String address;
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("general pool (to be city)/ specific pool (to be combined latitude and longitude ");


    String Random;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callPermissions();

        Toast.makeText(MainActivity.this, "firebase connection success ", Toast.LENGTH_LONG).show();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);










    }


    public void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            fusedLocationProviderClient = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(2000);
            locationRequest.setInterval(4000);


            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    Log.e(TAG, "latitude = " + locationResult.getLastLocation().getLatitude() + "      longitude = " + locationResult.getLastLocation().getLongitude() );




                    latitude = ""+ (locationResult.getLastLocation().getLatitude());
                    longitude = ""+(locationResult.getLastLocation().getLongitude());


                    Random = Math.random()+"";

                    Map<String,Object> dataToSave=new HashMap<>();
                    dataToSave.put(Random ,latitude+"****"+longitude);




                    mDocRef.update(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG","SUCCESS");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG","FAILED");
                        }
                    });























                }
            }, getMainLooper());
        } else {
            callPermissions();
        }
    }
















    public void callPermissions() {

        Permissions.check(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, "Location permission required", new Permissions.Options().setSettingsDialogTitle("Warning!").setRationaleDialogTitle("Info"), new PermissionHandler() {
            @Override
            public void onGranted() {
                requestLocationUpdates();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                callPermissions();
            }
        });

    }



























    @Override
        protected void onResume()
        {
            super.onResume();
            running = true;
            Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if(countSensor!=null)
            {
                sensorManager.registerListener(this, countSensor,SensorManager.SENSOR_DELAY_UI);
            }
            else
            {
                Toast.makeText((this),"not found",Toast.LENGTH_SHORT).show();
            }
        }




















































        protected void onPause()
        {
            super.onPause();
            running = false;

        }


















    @Override
    public void onSensorChanged(SensorEvent event) {
        if(running)
        {
            getLocation();
           // FirebaseDatabase database = FirebaseDatabase.getInstance();
           // DatabaseReference myRef = database.getReference().child("Member");
           // reff= FirebaseDatabase.getInstance().getReference().child("Member");
           // reff.push().setValue("****************_next_value_******************");
          //  reff.push().setValue(latitude);
           // reff.push().setValue(longitude);
           // reff.push().setValue(address);
          //  reff.push().setValue("****************_next_value_******************");


        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private <datainsert> void getLocation() {

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                //Initialize location
                Location location = task.getResult();
                if(location != null)
                {

                    try {
                        // Initialize geoCoder
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        // Initialize address list

                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        //Set Latitude on TextView



address=""+addresses.get(0).getAddressLine(0);





                    } catch (IOException e) {
                        e.printStackTrace();
                    }





                }





            }







        });



    }





























}
