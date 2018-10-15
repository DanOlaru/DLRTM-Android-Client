/*
 * Author: Dan Olaru, (c) 2018
 */

package longmoneyoffshore.dlrtime;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Intent;
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientParcel;
import android.app.Activity;
import longmoneyoffshore.dlrtime.utils.ClientCaller;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.RC_SIGN_IN;

public class IndividualClientOrderActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Client localFeedbackClient = new Client();
    private Client myPassedClient;

    //individual variables for the display table
    EditText orderNameField;
    EditText orderNameClientField;
    EditText orderPhoneClientField;
    EditText orderLocationClientField;
    EditText orderProductIdField;
    EditText orderProductQuantField;
    EditText orderProductPriceField;
    EditText orderPriceAdjustField;
    volatile RatingBar orderUrgencyField;
    volatile RatingBar orderValueClientField;
    EditText orderStatusClientField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_client_order);

        // Get the transferred data from source activity.
        Intent passedIntent = getIntent();
        ClientParcel myPassedClientParcel = passedIntent.getParcelableExtra("order");

        String command = passedIntent.getAction();
        Log.d("INDIVORDER","COMMAND: " + command);

        //orderNameField = (EditText) findViewById(R.id.orderNameClient); //is this necessary?
        orderNameClientField = (EditText) findViewById(R.id.orderNameClnt);
        orderPhoneClientField = (EditText) findViewById(R.id.orderPhoneClnt);
        orderLocationClientField = (EditText) findViewById(R.id.orderLocationClnt);
        orderProductIdField = (EditText) findViewById(R.id.orderProductIDClnt);
        orderProductQuantField = (EditText) findViewById(R.id.orderProductQuantClnt);
        orderProductPriceField = (EditText) findViewById(R.id.orderProductPriceClnt);
        orderPriceAdjustField = (EditText) findViewById(R.id.orderPriceAdjClnt);
        orderUrgencyField = (RatingBar) findViewById(R.id.orderUrgencyClnt);
        orderValueClientField = (RatingBar) findViewById(R.id.orderValueClnt);
        orderStatusClientField = (EditText) findViewById(R.id.individualOrderIssueOrComment);

        // — these listeners feed back the rating bar settings into the Client object and Client Parcel Object and eventually back to GSheets as integers from 1 to 5
        orderUrgencyField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                //orderUrgencyField.setOnRatingBarChangeListener();
                localFeedbackClient.setClientUrgency((float) ratingBar.getRating());
            }
        });

        orderValueClientField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                localFeedbackClient.setClientValue((float) orderValueClientField.getRating());
            }
        });

        //other buttons
        Button callButton = (Button) findViewById(R.id.make_call_button);
        //callButton.setVisibility(View.INVISIBLE);
        callButton.setEnabled(false);

        //this makes the text box at the bottom of the screen
        //cycle through the possible messages to display upon clicking the Done/Issue/Cancel button
        final Button issueOrCancelButton = (Button) findViewById(R.id.issue_or_cancel_button);
        int statusOptionsLength = getResources().getStringArray (R.array.issue_or_cancel_options).length;

        String[] temp_order_states = getResources().getStringArray (R.array.issue_or_cancel_options);
        String[] inclusive_order_states = new String [statusOptionsLength+1];
        for (int i = 0; i<statusOptionsLength-1; i++) {inclusive_order_states[i] = temp_order_states[i];}
        String[] order_states;


        if (command.equals("individual order")) {
            myPassedClient = new Client(myPassedClientParcel.returnClientFromParcel());
            localFeedbackClient = new Client(myPassedClient);

            //get the applicable anonymizerPrefix
            final String anonymizerPrefix = myPassedClient.getAnonymizerPrefix();

            //orderNameField.setText(myPassedClient.getClientName());
            orderNameClientField.setText(myPassedClient.getClientName());
            orderPhoneClientField.setText(myPassedClient.getClientPhoneNo());
            orderLocationClientField.setText(myPassedClient.getClientLocation());
            orderProductIdField.setText(myPassedClient.getClientProductID());
            orderProductQuantField.setText(String.valueOf(myPassedClient.getClientQuantity()));
            orderProductPriceField.setText(String.valueOf(myPassedClient.getClientPrice()));
            orderPriceAdjustField.setText(String.valueOf(myPassedClient.getClientPriceAdjust()));
            orderUrgencyField.setRating(myPassedClient.getClientUrgency());
            orderValueClientField.setRating(myPassedClientParcel.getClientValue());
            orderStatusClientField.setText(myPassedClient.getClientStatus());

            //TODO: implement sign-out button here

            //make_call button starts the dialer
            //callButton.setVisibility(View.VISIBLE);
            callButton.setEnabled(true);
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //establish number to call
                    TextView numberToCall = (TextView) findViewById(R.id.orderPhoneClnt);
                    String basicNumberToCall = numberToCall.getText().toString();

                    ClientCaller.dialClient(basicNumberToCall, anonymizerPrefix, IndividualClientOrderActivity.this);
                }
            });

            statusOptionsLength++;
            inclusive_order_states[statusOptionsLength-1] = localFeedbackClient.getClientStatus();
            order_states = inclusive_order_states;

        } else {
            // we're filling in info for a new order
            order_states = inclusive_order_states;

            //TODO: implement return client from here
        }

        issueOrCancelButton.setOnClickListener(new View.OnClickListener() {
            int click_counter=0;
            @Override
            public void onClick(View v) {
                if (click_counter >= order_states.length-1) {click_counter = 0;}
                orderStatusClientField.setText(order_states[click_counter++]);
                //save setting for data feedback
                //localFeedbackClient.setClientStatus(orderStatusClientField.getText().toString());
            }
        });

        //back_button takes the user back to the OrderListActivity
        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitFeedbackClient(command);

                ClientParcel localFeedbackParcel = new ClientParcel(localFeedbackClient);
                Intent feedbackIntent = new Intent(IndividualClientOrderActivity.this, OrderListActivity.class);
                feedbackIntent.putExtra("edited order", localFeedbackParcel);
                setResult(RESULT_OK, feedbackIntent);
                finish();
            }
        });

        //TODO:sign out button click references public public signOut method in utils
        Button signOutButton = (Button) findViewById(R.id.sign_out_button);

        //is this sign-out implementation correct?
       signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Start intent to sign out
                Intent signOutIntent = new Intent(IndividualClientOrderActivity.this, LoginActivity.class);
                setResult(RC_SIGN_IN, signOutIntent);
                //finish();
            }
       });
    }

    //TODO Implement other lifecycle components to save the modified data upon destruction, and send it back to OrderListActivity and back to GSheets

    private void commitFeedbackClient (String command) {

        //localFeedbackClient = getChangedOrderFromTextFields();

        if (command.equals("individual order")) {

            localFeedbackClient = getChangedOrderFromTextFields();
            //put in a different revision code if change occurred
            if (!localFeedbackClient.clientDifferences(myPassedClient).equals("0000000000"))
                localFeedbackClient.setRevision(localFeedbackClient.getRevision()+1);
        }
        else {
            localFeedbackClient = getNewOrderFromTextFields();
        }

        //else localFeedbackClient.setRevision(myPassedClient.getRevision());
        //Log.d("CLIENT_MODIFIES", " ORIGINAL REVISION " + myPassedClient.getRevision() + " CHANGED REVISION " + localFeedbackClient.getRevision());
        //Log.d("CLIENT_MODIFIES", " DIFFERENCES " + localFeedbackClient.clientDifferences(myPassedClient));
        //Log.d("CLIENT_MODIFIES",  "NEW REVISION " + localFeedbackClient.getRevision());
        //Log.d("COMPARISONMETHOD", localFeedbackClient.clientDifferences(myPassedClient));
    }

    private Client getChangedOrderFromTextFields () {

        Client clientAsDisplayed = new Client();

        clientAsDisplayed.setClientName(orderNameClientField.getText().toString());
        clientAsDisplayed.setClientPhoneNo(orderPhoneClientField.getText().toString());
        clientAsDisplayed.setClientLocation(orderLocationClientField.getText().toString());
        clientAsDisplayed.setClientProductID(orderProductIdField.getText().toString());
        clientAsDisplayed.setClientQuantity(Float.parseFloat(orderProductQuantField.getText().toString()));
        clientAsDisplayed.setClientPrice(Float.parseFloat(orderProductPriceField.getText().toString()));
        clientAsDisplayed.setClientPriceAdjust(Float.parseFloat(orderPriceAdjustField.getText().toString()));
        clientAsDisplayed.setClientUrgency(orderUrgencyField.getRating());
        clientAsDisplayed.setClientValue(orderValueClientField.getRating());
        clientAsDisplayed.setClientStatus(orderStatusClientField.getText().toString());

        return clientAsDisplayed;

    }
    private Client getNewOrderFromTextFields () {
        Client clientAsDisplayed = new Client();

        Log.d("COMMITING", "SHOW ME THE CLIENT AS IS, INCLUDING GAPS " );

        if (orderNameClientField.getText()!=null) clientAsDisplayed.setClientName(orderNameClientField.getText().toString());
        else clientAsDisplayed.setClientName("N/A");
        //Log.d("COMMITING", " orderNameClientField.getText() " +  clientAsDisplayed.getClientName());

        if (orderPhoneClientField.getText()!=null) clientAsDisplayed.setClientPhoneNo(orderPhoneClientField.getText().toString());
        else clientAsDisplayed.setClientPhoneNo("N/A");
        //clientAsDisplayed.setClientPhoneNo(orderPhoneClientField.getText().toString());

        if (orderLocationClientField.getText()!=null) clientAsDisplayed.setClientLocation(orderLocationClientField.getText().toString());
        else clientAsDisplayed.setClientLocation("N/A");
        //clientAsDisplayed.setClientLocation(orderLocationClientField.getText().toString());

        if (orderProductIdField.getText()!=null) clientAsDisplayed.setClientProductID(orderProductIdField.getText().toString());
        else clientAsDisplayed.setClientProductID("N/A");
        //clientAsDisplayed.setClientProductID(orderProductIdField.getText().toString());
        
        if (orderProductQuantField.getText()!=null) clientAsDisplayed.setClientQuantity(Float.parseFloat(orderProductQuantField.getText().toString()));
        else clientAsDisplayed.setClientQuantity(0);
        //clientAsDisplayed.setClientQuantity(Float.parseFloat(orderProductQuantField.getText().toString()));

        if (orderProductPriceField.getText()!=null) clientAsDisplayed.setClientPrice(Float.parseFloat(orderProductPriceField.getText().toString()));
        else clientAsDisplayed.setClientPrice(0);
        //clientAsDisplayed.setClientPrice(Float.parseFloat(orderProductPriceField.getText().toString()));

        if (orderPriceAdjustField.getText()!=null) clientAsDisplayed.setClientPriceAdjust(Float.parseFloat(orderPriceAdjustField.getText().toString()));
        else clientAsDisplayed.setClientPriceAdjust(0);
        //clientAsDisplayed.setClientPriceAdjust(Float.parseFloat(orderPriceAdjustField.getText().toString()));

        clientAsDisplayed.setClientUrgency(orderUrgencyField.getRating());

        clientAsDisplayed.setClientValue(orderValueClientField.getRating());

        if (orderStatusClientField.getText()!=null) clientAsDisplayed.setClientStatus(orderNameClientField.getText().toString());
        else clientAsDisplayed.setClientStatus("N/A");
        //clientAsDisplayed.setClientStatus(orderStatusClientField.getText().toString());

        clientAsDisplayed.showClient();

        return clientAsDisplayed;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //final String anonymizerPrefix = myPassedClient.getAnonymizerPrefix();
    }

    @Override
    protected void onPause () {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

        //the localFeedbackClient gets passed to the OrderListActivity
        /*
        ClientParcel localFeedbackParcel = new ClientParcel(localFeedbackClient);
        Intent feedbackIntent = new Intent(IndividualClientOrderActivity.this, OrderListActivity.class);
        feedbackIntent.putExtra("edited order", localFeedbackParcel);
        //setResult(IndividualClientOrderActivity.RESULT_OK, feedbackIntent);
        setResult(OrderListActivity.RESULT_OK, feedbackIntent);

        Log.d("!!onstop_Cl_Status",localFeedbackParcel.getClientStatus());
        Log.d("onStop Cl_urgency", String.valueOf(localFeedbackClient.getClientUrgency()));
        finish();
        */
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("orderNameClientField", localFeedbackClient.getClientName());
        outState.putString("orderPhoneClientField",localFeedbackClient.getClientPhoneNo());
        outState.putString("orderLocationClientField",localFeedbackClient.getClientLocation());
        outState.putString("orderProductIdField", localFeedbackClient.getClientProductID());
        outState.putFloat("orderProductQuantField", localFeedbackClient.getClientQuantity());
        outState.putFloat("orderProductPriceField", localFeedbackClient.getClientPrice());
        outState.putFloat("orderPriceAdjustField", localFeedbackClient.getClientPriceAdjust());
        outState.putFloat("orderUrgencyField", localFeedbackClient.getClientUrgency());
        outState.putFloat("orderValueClientField", localFeedbackClient.getClientValue());
        outState.putString("orderStatusClientField", localFeedbackClient.getClientStatus());

        // call superclass to save view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState (Bundle savedInstanceState) {
        localFeedbackClient.setClientName(savedInstanceState.getString("orderNameClientField"));
        localFeedbackClient.setClientPhoneNo(savedInstanceState.getString("orderPhoneClientField"));
        localFeedbackClient.setClientLocation(savedInstanceState.getString("orderLocationClientField"));
        localFeedbackClient.setClientProductID(savedInstanceState.getString("orderProductIdField"));
        localFeedbackClient.setClientQuantity(savedInstanceState.getFloat("orderProductQuantField"));
        localFeedbackClient.setClientPrice(savedInstanceState.getFloat("orderProductPriceField"));
        localFeedbackClient.setClientPriceAdjust(savedInstanceState.getFloat("orderPriceAdjustField"));
        localFeedbackClient.setClientUrgency(savedInstanceState.getFloat("orderUrgencyField"));
        localFeedbackClient.setClientValue(savedInstanceState.getFloat("orderValueClientField"));
        localFeedbackClient.setClientStatus(savedInstanceState.getString("orderStatusClientField"));

        //extra
        Log.d("Notif", "successfully retrieved instance state");
    }
}

