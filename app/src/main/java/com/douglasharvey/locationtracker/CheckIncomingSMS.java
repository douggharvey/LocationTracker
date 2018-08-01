package com.douglasharvey.locationtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class CheckIncomingSMS extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] rawMsgs=(Object[])intent.getExtras().get("pdus");
        for (Object raw : rawMsgs) {
            SmsMessage msg=SmsMessage.createFromPdu((byte[])raw);
            if (msg.getMessageBody().toUpperCase().contains("LOCATION")) {
                // here should check against a list of valid telephone numbers. Later add to shared preferences or database
                if ((msg.getOriginatingAddress().equals("+905057475531")) ||
                    (msg.getOriginatingAddress().equals("+905539258393"))) {
                    Intent newIntent = new Intent(context, LocationIntentService.class);
                    newIntent.putExtra("telephoneNumber", msg.getOriginatingAddress());
                    context.startService(newIntent);
                  //  context.startForegroundService(newIntent); from api level 26
                    //https://developer.android.com/about/versions/oreo/background-location-limits
                }
            }
        }
    }
}