package com.douglasharvey.locationtracker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;

import com.douglasharvey.locationtracker.utilities.AppExecutors;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import timber.log.Timber;

public class LocationIntentService extends Service {
    //Using Service based on following question and answer from Commonsware (Mark Murphy)
    //https://stackoverflow.com/questions/29917846/how-to-get-fusedlocation-from-intentservice

    //In the future if regular updates will be done, then review the following code for what is relevant, however does not use FusedLocationProviderClient
    //https://gist.github.com/blackcj/20efe2ac885c7297a676 - backgroundlocationservice.java

    //SHOULD I HAVE USED JOBINTENTSERVICE here - refer busy coder's
    //check wakelock management

    private FusedLocationProviderClient locationProviderClient;

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Timber.d("onStartCommand: ");
        //todo maybe execute on a thread
        final String telephoneNumber = intent.getStringExtra("telephoneNumber");

        locationProviderClient = LocationServices.getFusedLocationProviderClient(LocationIntentService.this);
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(AppExecutors.getInstance().diskIO(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            sendSMSwithLocation(location, telephoneNumber);
                            stopSelf();
                        }
                    }
                });
        return START_NOT_STICKY;
    }

    private void sendSMSwithLocation(Location location, String telephoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(telephoneNumber, null,
                "Douglas is at: https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude() +
                        " Bearing:" + location.getBearing()+ " Speed: " + location.getSpeed(),
                null, null);

    }

/*    private String getTelephoneNumber() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString("telephoneNumber", "missing");
    }
*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Timber.d("onBind: started from Broadcast");
        return null;
    }

/*
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("onHandleIntent: started from broadcast");
    //    sendLastLocation(getApplicationContext());
        //The Fused Location Provider 3275
        //https://codelabs.developers.google.com/codelabs/background-location-updates-android-o/index.html?index=..%2F..%2Findex#0
    }
*/
}
