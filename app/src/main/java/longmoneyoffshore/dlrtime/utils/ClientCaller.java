package longmoneyoffshore.dlrtime.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import java.security.Security;

import longmoneyoffshore.dlrtime.IndividualClientOrderActivity;
import longmoneyoffshore.dlrtime.R;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


//we want a separate ClientCaller class in case the calling happens from somewhere else than IndividualClientOrderActivity,
//for instance from Maps Activity

public class ClientCaller extends ActivityCompat implements ActivityCompat.OnRequestPermissionsResultCallback {

    //TODO: dunno what this does
    public static final String INDIVIDUAL_CLIENT_ORDER_ERROR_TAG = IndividualClientOrderActivity.class.getSimpleName();

    //supposing we might need other error String constants, depending on where this dialer is called from
    public static final String ORDERS_LIST_ERROR_TAG = IndividualClientOrderActivity.class.getSimpleName();
    public static final String MAPS_ROUTE_ERROR_TAG = IndividualClientOrderActivity.class.getSimpleName();

    final static int MY_REQUEST_PERMISSION_CALL_PHONE = 1;

    //TODO: debug configuration — optimize this later
    private static Activity localPassedActivity;
    public static Intent localDialIntent;


    public static void dialClient (String basicNumberToCall, String callPrefix, Activity myPassedActivity) {

        //set local context
        //localPassedActivity = myPassedActivity;

        String orderPhoneNum = String.format("tel: %s", callPrefix + basicNumberToCall);

        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse(orderPhoneNum));

        localDialIntent = dialIntent;

        //establish that an activity exists which can respond to this intent and, if yes, ask for permission to make the call
        if (dialIntent.resolveActivity(myPassedActivity.getPackageManager()) != null) {
            //ok to pass context wrapper as 'thisActivity'?
            if (ContextCompat.checkSelfPermission(myPassedActivity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                //TODO: ask for permission and then check again; is this a correct casting of Activity from Context??
                ActivityCompat.requestPermissions (myPassedActivity, new String[] {Manifest.permission.CALL_PHONE}, MY_REQUEST_PERMISSION_CALL_PHONE);


            } // if the permission is already granted
            else (myPassedActivity).startActivity(dialIntent);
        }
        else{
            Log.e(INDIVIDUAL_CLIENT_ORDER_ERROR_TAG, "Can't resolve app for ACTION_DIAL Intent.");
        }

    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int [] grantResults) {

        switch (requestCode) {
            case MY_REQUEST_PERMISSION_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Start call
                    (localPassedActivity).startActivity(localDialIntent);
                } else {
                    Log.e(INDIVIDUAL_CLIENT_ORDER_ERROR_TAG, "Permission to call not granted.");
                }
                return;
            }
        }
    }


/*
    if (ContextCompat.checkSelfPermission(myContextWrapper, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
        ((Activity) myContextWrapper).startActivity(dialIntent);
    } else {
        Log.e(INDIVIDUAL_CLIENT_ORDER_ERROR_TAG, "Permission to call not granted.");
    }
*/



}
