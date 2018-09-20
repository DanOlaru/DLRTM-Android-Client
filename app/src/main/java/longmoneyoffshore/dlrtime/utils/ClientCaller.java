package longmoneyoffshore.dlrtime.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import longmoneyoffshore.dlrtime.IndividualClientOrderActivity;
import longmoneyoffshore.dlrtime.R;


//we want a separate ClientCaller class in case the calling happens from somewhere else than IndividualClientOrderActivity,
//for instance from Maps Activity

public class ClientCaller {

    //TODO: dunno what this does
    public static final String INDIVIDUAL_CLIENT_ORDER_ERROR_TAG = IndividualClientOrderActivity.class.getSimpleName();

    //supposing we might need other error String constants, depending on where this dialer is called from
    public static final String ORDERS_LIST_ERROR_TAG = IndividualClientOrderActivity.class.getSimpleName();
    public static final String MAPS_ROUTE_ERROR_TAG = IndividualClientOrderActivity.class.getSimpleName();


    //private Context myLocalContext;

    //public void setLocalContext (Context locContxt) {this.myLocalContext = locContxt;}

    public void dialClient (String basicNumberToCall, String callPrefix, ContextWrapper myContextWrapper) {

        //establish the right context
        //setLocalContext (myContextWrapper);

        //add any needed prefixes to the number to call

        String orderPhoneNum = String.format("tel: %s", callPrefix + basicNumberToCall);

        //get implicit intent
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse(orderPhoneNum));

        //establish that an activity exists which can respond to this intent and, if yes, make the call
        if (dialIntent.resolveActivity(myContextWrapper.getPackageManager()) != null) { ((Activity) myContextWrapper).startActivity (dialIntent); }
        //if (dialIntent.resolveActivity(myContext.getPackageManager()) != null) { ((Activity) myContext).startActivity (dialIntent); }
        else { Log.e(INDIVIDUAL_CLIENT_ORDER_ERROR_TAG, "Can't resolve app for ACTION_DIAL Intent.");}
    }
}
