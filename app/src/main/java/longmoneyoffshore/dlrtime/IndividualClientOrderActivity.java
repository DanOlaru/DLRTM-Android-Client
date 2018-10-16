/*
 * Author: Dan Olaru, (c) 2018
 */

package longmoneyoffshore.dlrtime;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import longmoneyoffshore.dlrtime.utils.ClientCaller;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.*;

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

        //other buttons
        Button callButton = (Button) findViewById(R.id.make_call_button);
        //callButton.setVisibility(View.INVISIBLE);
        callButton.setEnabled(false);

        //this makes the text box at the bottom of the screen
        //cycle through the possible messages to display upon clicking the Done/Issue/Cancel button
        final Button issueOrCancelButton = (Button) findViewById(R.id.issue_or_cancel_button);
        ArrayList<String> ordersStates = new ArrayList<String>(Arrays.asList(getResources().getStringArray (R.array.issue_or_cancel_options)));

        if (command.equals("individual order")) {
            myPassedClient = new Client(myPassedClientParcel.returnClientFromParcel());
            localFeedbackClient = new Client(myPassedClient);

            //get the applicable anonymizerPrefix
            final String anonymizerPrefix = myPassedClient.getAnonymizerPrefix();

            //orderNameField.setText(myPassedClient.getClientName());
            if (!myPassedClient.getClientName().equals(blankClient.getClientName())) orderNameClientField.setText(myPassedClient.getClientName());
            else orderNameClientField.setText(null);

            //orderPhoneClientField.setText(myPassedClient.getClientPhoneNo());
            if (!myPassedClient.getClientPhoneNo().equals(blankClient.getClientPhoneNo())) orderPhoneClientField.setText(myPassedClient.getClientPhoneNo());
            else orderPhoneClientField.setText(null);


            //orderLocationClientField.setText(myPassedClient.getClientLocation());
            if (!myPassedClient.getClientLocation().equals(blankClient.getClientLocation())) orderLocationClientField.setText(myPassedClient.getClientLocation());
            else orderLocationClientField.setText(null);

            //orderProductIdField.setText(myPassedClient.getClientProductID());
            if (!myPassedClient.getClientProductID().equals(blankClient.getClientProductID())) orderProductIdField.setText(myPassedClient.getClientProductID());
            else orderProductIdField.setText(null);

            orderProductQuantField.setText(String.valueOf(myPassedClient.getClientQuantity()));
            //if (myPassedClient.getClientQuantity() != blankClient.getClientQuantity()) orderProductQuantField.setText(String.valueOf(myPassedClient.getClientQuantity()));
            //else orderProductQuantField.setText(null);

            orderProductPriceField.setText(String.valueOf(myPassedClient.getClientPrice()));
            //if (myPassedClient.getClientPrice() != blankClient.getClientPrice()) orderProductPriceField.setText(String.valueOf(myPassedClient.getClientPrice()));
            //else orderProductPriceField.setText(null);

            orderPriceAdjustField.setText(String.valueOf(myPassedClient.getClientPriceAdjust()));
            //if (myPassedClient.getClientPriceAdjust() != blankClient.getClientPriceAdjust()) orderPriceAdjustField.setText(String.valueOf(myPassedClient.getClientPriceAdjust()));
            //else orderPriceAdjustField.setText(null);

            orderUrgencyField.setRating(myPassedClient.getClientUrgency());
            //if (myPassedClient.getClientUrgency() != blankClient.getClientUrgency()) orderUrgencyField.setRating(myPassedClient.getClientUrgency());
            //else orderUrgencyField.setRating(0);

            orderValueClientField.setRating(myPassedClientParcel.getClientValue());
            //if (myPassedClient.getClientValue() != blankClient.getClientValue()) orderValueClientField.setRating(myPassedClient.getClientValue());
            //else orderValueClientField.setRating(0);

            //orderStatusClientField.setText(myPassedClient.getClientStatus());
            if (!myPassedClient.getClientStatus().equals(blankClient.getClientStatus())) orderStatusClientField.setText(myPassedClient.getClientStatus());
            else orderStatusClientField.setText(null);

            //make_call button starts the dialer
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

            ordersStates.add(localFeedbackClient.getClientStatus());
            //ordersStates.add(blankClient.getClientStatus());
            //ordersStates.add(null);

        } else {
            // we're filling in info for a new order
            //ordersStates.add(blankClient.getClientStatus());
            localFeedbackClient = new Client(blankClient);
        }
        ordersStates.add(blankClient.getClientStatus());

        //if (orderNameClientField.getText()!=null) clientAsDisplayed.setClientName(orderNameClientField.getText().toString());
        orderNameClientField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) localFeedbackClient.setClientName(s.toString());
            }
        });

        //if (orderPhoneClientField.getText()!=null) clientAsDisplayed.setClientPhoneNo(orderPhoneClientField.getText().toString());
        orderPhoneClientField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) localFeedbackClient.setClientPhoneNo(s.toString());
            }
        });

        //if (orderLocationClientField.getText()!=null) clientAsDisplayed.setClientLocation(orderLocationClientField.getText().toString());
        orderLocationClientField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) localFeedbackClient.setClientLocation(s.toString());
            }
        });

        //if (orderProductIdField.getText()!=null) clientAsDisplayed.setClientProductID(orderProductIdField.getText().toString());
        orderProductIdField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) localFeedbackClient.setClientProductID(s.toString());
            }
        });

        orderProductQuantField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) localFeedbackClient.setClientQuantity(Float.parseFloat(s.toString()));
            }
        });

        //orderProductPriceField.setText(String.valueOf(myPassedClient.getClientPrice()));

        orderProductPriceField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) localFeedbackClient.setClientPrice(Float.parseFloat(s.toString()));
            }
        });

        //orderPriceAdjustField.setText(String.valueOf(myPassedClient.getClientPriceAdjust()));
        orderPriceAdjustField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) localFeedbackClient.setClientPriceAdjust(Float.parseFloat(s.toString()));
            }
        });

        // — these listeners feed back the rating bar settings into the Client object and Client Parcel Object and eventually back to GSheets as integers from 1 to 5
        orderUrgencyField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                localFeedbackClient.setClientUrgency((float) ratingBar.getRating());
            }
        });

        orderValueClientField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                localFeedbackClient.setClientValue((float) orderValueClientField.getRating());
            }
        });

        String lastState = localFeedbackClient.getClientStatus();
        orderStatusClientField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //if (!localFeedbackClient.getClientStatus().equals(lastState)) ordersStates.add(localFeedbackClient.getClientStatus());
                //if (!orderStatusClientField.getText().equals(lastState)) ordersStates.add(localFeedbackClient.getClientStatus());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    localFeedbackClient.setClientStatus(s.toString());
                    //if (!localFeedbackClient.getClientStatus().equals(lastState)) ordersStates.add(localFeedbackClient.getClientStatus());
                }
            }
        });

        //BUTTONS
        issueOrCancelButton.setOnClickListener(new View.OnClickListener() {
            int click_counter=0;
            @Override
            public void onClick(View v) {

                if (!orderStatusClientField.getText().equals(lastState) && !ordersStates.contains(orderStatusClientField.getText()))
                    ordersStates.add(localFeedbackClient.getClientStatus());

                if (click_counter >= ordersStates.size()) {click_counter = 0;}

                if (!ordersStates.get(click_counter++).equals(blankClient.getClientStatus())) orderStatusClientField.setText(ordersStates.get(click_counter-1));
                else orderStatusClientField.setText(null);

                //orderStatusClientField.setText(ordersStates.get(click_counter++));
                //save setting for data feedback
                //localFeedbackClient.setClientStatus(orderStatusClientField.getText().toString());
            }
        });

        //back_button takes the user back to the OrderListActivity
        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!orderStatusClientField.getText().equals(lastState) && !ordersStates.contains(orderStatusClientField.getText()))
                    ordersStates.add(localFeedbackClient.getClientStatus());

                if (command.equals("individual order")) {
                    if (!localFeedbackClient.clientDifferences(myPassedClient).equals("0000000000"))
                        localFeedbackClient.setRevision(localFeedbackClient.getRevision()+1);
                }

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