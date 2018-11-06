package longmoneyoffshore.dlrtime;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.content.Intent;
import android.app.Activity;
import java.util.ArrayList;
import java.util.Arrays;
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientParcel;
import longmoneyoffshore.dlrtime.utils.ClientCaller;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.*;

public class IndividualClientOrderActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Client localFeedbackClient = new Client();
    private Client myPassedClient;

    //individual variables for the display screen
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

    //TODO these have to be set according to setting activity
    String anonymizerPrefix = US_ANONYMIZER_PREFIX;
    private boolean scanCapability = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///TODO: get the settings as established in settting view

        Log.d("SCANNER ON OR OFF?", "the prefix is: " + GLOBAL_ANONYMIZER_PREFIX + " and the scanner setting is " + GLOBAL_SCANNER_SETTING);

        int BUTTON_VISIBILITY;

        if (GLOBAL_SCANNER_SETTING) {

            setContentView(R.layout.activity_individual_client_order);

        } else {

            setContentView(R.layout.activity_inidvidual_client_order_no_scan);
        }

        ImageButton scanButton = (ImageButton) findViewById(R.id.perform_scan_button);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(IndividualClientOrderActivity.this, TransactionScannerActivity.class);
                startActivity(settingsIntent);
            }
        });

        // Get the transferred data from source activity.
        Intent passedIntent = getIntent();
        ClientParcel myPassedClientParcel = passedIntent.getParcelableExtra("order");

        String command = passedIntent.getAction();

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

        //other buttons

        ImageButton callButton = (ImageButton) findViewById(R.id.make_call_button);
        callButton.setEnabled(false);

        //this makes the text box at the bottom of the screen
        //cycle through the possible messages to display upon clicking the Done/Issue/Cancel button
        final ImageButton issueOrCancelButton = (ImageButton) findViewById(R.id.issue_or_cancel_button);
        ArrayList<String> ordersStates = new ArrayList<String>(Arrays.asList(getResources().getStringArray (R.array.issue_or_cancel_options)));

        if (command.equals("individual order")) {

            myPassedClient = new Client(myPassedClientParcel.returnClientFromParcel());
            localFeedbackClient = new Client(myPassedClient);

            anonymizerPrefix = myPassedClient.getAnonymizerPrefix();

            if (!myPassedClient.getClientName().equals(blankClient.getClientName())) orderNameClientField.setText(myPassedClient.getClientName());
            else orderNameClientField.setText(null);

            if (!myPassedClient.getClientPhoneNo().equals(blankClient.getClientPhoneNo())) orderPhoneClientField.setText(myPassedClient.getClientPhoneNo());
            else orderPhoneClientField.setText(null);

            if (!myPassedClient.getClientLocation().equals(blankClient.getClientLocation())) orderLocationClientField.setText(myPassedClient.getClientLocation());
            else orderLocationClientField.setText(null);

            if (!myPassedClient.getClientProductID().equals(blankClient.getClientProductID())) orderProductIdField.setText(myPassedClient.getClientProductID());
            else orderProductIdField.setText(null);

            //float values that don't require the same treatment
            orderProductQuantField.setText(String.valueOf(myPassedClient.getClientQuantity()));
            orderProductPriceField.setText(String.valueOf(myPassedClient.getClientPrice()));
            orderPriceAdjustField.setText(String.valueOf(myPassedClient.getClientPriceAdjust()));
            orderUrgencyField.setRating(myPassedClient.getClientUrgency());
            orderValueClientField.setRating(myPassedClientParcel.getClientValue());

            if (!myPassedClient.getClientStatus().equals(blankClient.getClientStatus())) orderStatusClientField.setText(myPassedClient.getClientStatus());
            else orderStatusClientField.setText(null);

            //button that starts the dialer
            callButton.setEnabled(true);
            /*
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //establish number to call
                    TextView numberToCall = (TextView) findViewById(R.id.orderPhoneClnt);
                    String basicNumberToCall = numberToCall.getText().toString();

                    ClientCaller.dialClient(basicNumberToCall, anonymizerPrefix, IndividualClientOrderActivity.this);
                }
            }); */

            if (!ordersStates.contains(localFeedbackClient.getClientStatus())) ordersStates.add(localFeedbackClient.getClientStatus());
        } else {
            // we're filling in info for a new order
            localFeedbackClient = new Client(blankClient);
        }
        ordersStates.add(blankClient.getClientStatus());

        orderPhoneClientField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    callButton.setEnabled(true);

                    callButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            placeCall();
                        }
                    });
                }
                else callButton.setEnabled(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//if(s.length() != 0) localFeedbackClient.setClientPhoneNo(s.toString());
            }
        });

        //BUTTONS
        issueOrCancelButton.setOnClickListener(new View.OnClickListener() {
            private int click_counter=0;
            String state = "";

            @Override
            public void onClick(View v) {
                state = orderStatusClientField.getText().toString();
                if (!ordersStates.contains(state) && state.length()>0) {
                    ordersStates.add(state);
                }

                if (click_counter >= ordersStates.size()) {click_counter = 0;}

                if (!ordersStates.get(click_counter).equals(blankClient.getClientStatus())) orderStatusClientField.setText(ordersStates.get(click_counter));
                else orderStatusClientField.setText(null);

                click_counter++;
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localFeedbackClient = saveOnScreenState();
                //if (command.equals("individual order")) localFeedbackClient.setRevision(myPassedClient.getRevision());

                if (command.equals("individual order")) {
                    localFeedbackClient.setRevision(myPassedClient.getRevision());

                    if (!(localFeedbackClient.clientDifferences(myPassedClient).equals("0000000000")))
                        localFeedbackClient.setRevision(localFeedbackClient.getRevision()+1);
                }

                localFeedbackClient.formatPhoneNo();

                ClientParcel localFeedbackParcel = new ClientParcel(localFeedbackClient);
                Intent feedbackIntent = new Intent(IndividualClientOrderActivity.this, OrderListActivity.class);
                feedbackIntent.putExtra("edited order", localFeedbackParcel);
                setResult(RESULT_OK, feedbackIntent);

                finish();
            }
        });

        ImageButton signOutButton = (ImageButton) findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signOutIntent = new Intent(IndividualClientOrderActivity.this, OrderListActivity.class);
                setResult(REQUEST_CODE_SIGN_OUT, signOutIntent);

                finish();
            }
       });

       if (callButton.isEnabled())
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeCall();
            }
        });

    }

    public void placeCall () {
        String basicNumberToCall = orderPhoneClientField.getText().toString();
        //ClientCaller.dialClient(basicNumberToCall, anonymizerPrefix, IndividualClientOrderActivity.this);
        ClientCaller.dialClient(basicNumberToCall, GLOBAL_ANONYMIZER_PREFIX, IndividualClientOrderActivity.this);

    }

    public Client saveOnScreenState () {
        //save state as is on screen
        Client fdbckClient = new Client();
        if (orderNameClientField.getText().length()>0) fdbckClient.setClientName(orderNameClientField.getText().toString());
        else fdbckClient.setClientName(blankClient.getClientName());

        if(orderPhoneClientField.getText().length()>0) fdbckClient.setClientPhoneNo(orderPhoneClientField.getText().toString());
        else fdbckClient.setClientPhoneNo(blankClient.getClientPhoneNo());

        if (orderLocationClientField.getText().length()>0) fdbckClient.setClientLocation(orderLocationClientField.getText().toString());
        else fdbckClient.setClientLocation(blankClient.getClientLocation());

        if(orderProductIdField.getText().length()>0) fdbckClient.setClientProductID(orderProductIdField.getText().toString());
        else fdbckClient.setClientProductID(blankClient.getClientProductID());

        if (orderProductQuantField.getText().length()>0) fdbckClient.setClientQuantity(Float.parseFloat(orderProductQuantField.getText().toString()));
        else fdbckClient.setClientQuantity(blankClient.getClientQuantity());

        if (orderProductPriceField.getText().length()>0) fdbckClient.setClientPrice(Float.parseFloat(orderProductPriceField.getText().toString()));
        else fdbckClient.setClientPrice(blankClient.getClientPrice());

        if (orderPriceAdjustField.getText().length()>0) fdbckClient.setClientPriceAdjust(Float.parseFloat(orderPriceAdjustField.getText().toString()));
        else fdbckClient.setClientPriceAdjust(blankClient.getClientPriceAdjust());

        fdbckClient.setClientUrgency(orderUrgencyField.getRating());
        fdbckClient.setClientValue(orderValueClientField.getRating());

        if (orderStatusClientField.getText().length()>0) fdbckClient.setClientStatus(orderStatusClientField.getText().toString());
        else fdbckClient.setClientStatus(blankClient.getClientStatus());

        return fdbckClient;
    }


    //TODO Implement other lifecycle components to save the modified data upon destruction, and send it back to OrderListActivity and back to GSheets

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    }
}