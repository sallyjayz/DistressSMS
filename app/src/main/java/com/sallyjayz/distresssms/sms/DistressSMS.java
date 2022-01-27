package com.sallyjayz.distresssms.sms;

import android.telephony.SmsManager;

public class DistressSMS {

    public static void smsSendMessage(String myLatitude, String myLongitude, String myDistressNumber) {
        String message;
        if (myLatitude.equals("0.0") && myLongitude.equals("0.0")) {
            message = myDistressNumber + " is in distress and unable to send location because his/her phone location is off at the moment";
        } else {
            message = "I NEED HELP, Click http://www.google.com/maps/place/" + myLatitude + "," + myLongitude + " to get my location";
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(myDistressNumber, null, message, null, null);

    }

}
