package longmoneyoffshore.dlrtime;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import android.support.v7.widget.Toolbar;

//local imports — Dan

import android.content.Intent;
import longmoneyoffshore.dlrtime.utils.Client;
import longmoneyoffshore.dlrtime.utils.ClientParcel;
import android.app.Activity;
import longmoneyoffshore.dlrtime.utils.SignOutFunctionality;
import longmoneyoffshore.dlrtime.utils.ClientCaller;
import android.content.ContextWrapper;

import org.w3c.dom.Text;


public class IndividualClientOrderActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    //Dan's code

    //this Client object will take any modifications that are made in the IndividualClientOrder window and pass them back
    // to the OrderListActivity and back to GSheets
    private Client localFeedbackClient;
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
    RatingBar orderUrgencyField;
    RatingBar orderValueClientField;
    EditText orderStatusClientField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_client_order);

        // Get the transferred data from source activity.
        Intent passedIntent = getIntent();
        //TODO: only commented this temporarily; This should work
        //ClientParcel myPassedClientParcel = passedIntent.getParcelableExtra("order");
        //myPassedClient = new Client(myPassedClientParcel.returnClientFromParcel());

        //or??
        //Bundle passedData = passedIntent.getExtras();
        //ClientParcel myPassedClientParcel = (ClientParcel) passedData.getParcelable("order");

        // dummy data set
        myPassedClient = new Client ("Johnny T Apple" , "773 845 1234" , "Argyle & Lawrence" , "BD" , 60, 60, 0, 0, 3, "pending");

        //TODO: initially localFeedbackClient is identical to the passed Client object. Any modifications made
        // will be passed back using it to the invoking activity or perhaps the ASyncTask!!!
        localFeedbackClient = new Client (myPassedClient);

        //test only
        //ClientParcel myPassedClientParcel = new ClientParcel(myPassedClient);

        //get the applicable anonymizerPrefix
        final String anonymizerPrefix = myPassedClient.getAnonymizerPrefix();

        //fill the fields with values from passed Client object
        //Display Data

        //TODO why are there 2 similar objects here — orderNameClient and orderNameClnt?

        orderNameField = (EditText) findViewById(R.id.orderNameClient); //is this necessary?
        orderNameField.setText(myPassedClient.getClientName());

        orderNameClientField = (EditText) findViewById(R.id.orderNameClnt);
        orderNameClientField.setText(myPassedClient.getClientName());

        orderPhoneClientField = (EditText) findViewById(R.id.orderPhoneClnt);
        orderPhoneClientField.setText(myPassedClient.getClientPhoneNo());

        orderLocationClientField = (EditText) findViewById(R.id.orderLocationClnt);
        orderLocationClientField.setText(myPassedClient.getClientLocation());

        orderProductIdField = (EditText) findViewById(R.id.orderProductIDClnt);
        orderProductIdField.setText(myPassedClient.getClientProductID());

        orderProductQuantField = (EditText) findViewById(R.id.orderProductQuantClnt);
        orderProductQuantField.setText(Integer.toString(myPassedClient.getClientQuantity()));

        orderProductPriceField = (EditText) findViewById(R.id.orderProductPriceClnt);
        orderProductPriceField.setText(Integer.toString(myPassedClient.getClientPrice()));

        orderPriceAdjustField = (EditText) findViewById(R.id.orderPriceAdjClnt);
        orderPriceAdjustField.setText(Integer.toString(myPassedClient.getClientPriceAdjust()));

        //Order Urgency
        //after any modifications are performed by the user from the Individual Client Order screen
        // the modifications are saved in the localFeedbackClient object and sent back to the invoking activity
        //which is OrderListActivity.
        orderUrgencyField = (RatingBar) findViewById(R.id.orderUrgencyClnt);

        // — these listeners feed back the rating bar settings into the Client object and Client Parcel Object and eventually back to GSheets as integers from 1 to 5
        orderUrgencyField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                //orderUrgencyField.setOnRatingBarChangeListener();
                localFeedbackClient.setClientUrgency ((int) ratingBar.getRating());
            }
        });

        //Client Value
        orderValueClientField = (RatingBar) findViewById(R.id.orderValueClnt);

        orderValueClientField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                localFeedbackClient.setClientValue ((int) orderValueClientField.getRating());
            }
        });


        //orderStatusClientField = (EditText) findViewById(R.id.orderStatusClnt);
        orderStatusClientField = (EditText) findViewById(R.id.individualOrderIssueOrComment);
        orderStatusClientField.setText(myPassedClient.getClientStatus());

        //user changes the text in statusClientField

        String retrievedClientStatus = orderStatusClientField.getText().toString();
        localFeedbackClient.setClientStatus(retrievedClientStatus);

        //TODO: implement sign-out button here

        //make_call button starts the dialer
        Button callButton = (Button) findViewById(R.id.make_call_button);

        //TODO: why can't I implement the call button onClickListener in onResume?
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //establish number to call
                TextView numberToCall = (TextView) findViewById(R.id.orderPhoneClnt);
                String basicNumberToCall = numberToCall.getText().toString();

                ClientCaller.dialClient (basicNumberToCall, anonymizerPrefix, IndividualClientOrderActivity.this);

            }
        });

        //this makes the text box at the bottom of the screen
        //cycle through the possible messages to display upon clicking the Done/Issue/Cancel button
        final Button issueOrCancelButton = (Button) findViewById(R.id.issue_or_cancel_button);
        int statusOptionsLength = getResources().getStringArray (R.array.issue_or_cancel_options).length + 1;

        String[] temp_order_states = getResources().getStringArray (R.array.issue_or_cancel_options);
        String[] inclusive_order_states = new String [statusOptionsLength];

        for (int i = 0; i<statusOptionsLength-1; i++) {inclusive_order_states[i] = temp_order_states[i];}

        inclusive_order_states[statusOptionsLength-1] = localFeedbackClient.getClientStatus();

        final String[] order_states = inclusive_order_states;

        issueOrCancelButton.setOnClickListener(new View.OnClickListener() {
            int click_counter=0;
            @Override
            public void onClick(View v) {
                if (click_counter >= order_states.length) {click_counter = 0;}
                orderStatusClientField.setText(order_states[click_counter++]);
                //save setting for data feedback
                localFeedbackClient.setClientStatus(orderStatusClientField.getText().toString());
            }
        });

        //back_button takes me back to the previous Activity — presumably OrderListActivity
        Button backButton = (Button) findViewById(R.id.back_button);
        //TODO: the Back button makes sure that the data in the text boxes is saved and sent back to the previous activity / GSheets
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //TODO:sign out button click references public public signOut method in utils
        Button signOutButton = (Button) findViewById(R.id.sign_out_button);

        //is this sign-out implementation correct?
       // signOutButton.setOnClickListener(new View.OnClickListener() {
       //     @Override
       //     public void onClick(View v) {
       //             SignOutFunctionality signOutObject = new SignOutFunctionality();
       //             signOutObject.signOut(v);
       //     }
       // });

        //pressing 'Submit' button commits any changes to the localFeedbackClient, which should happen automatically anyway
        final Button commitCommentButton = (Button) findViewById(R.id.btnSubmit);
        commitCommentButton.setOnClickListener(new View.OnClickListener() {
               @Override
            public void onClick(View v) {
                   localFeedbackClient.setClientStatus(orderStatusClientField.getText().toString());
            }
        });


        //TODO: this button saves the comment typed into the comment box and feeds it back to OrderListActivity

        //end by Dan


        //TODO: what is this code?
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    //TODO Implement other lifecycle components to save the modified data upon destruction, and send it back to OrderListActivity and back to GSheets

    @Override
    protected void onStart() {
        super.onStart(); //no need to pass savedInstanceState???

        //anything else to implement here?
    }

    //is this method going to be necessary?
    @Override
    protected void onResume() {
        super.onResume();
        //setContentView(R.id.activity_individual_client_order);

        //get the applicable anonymizerPrefix — in case the user has changed it from the Settings activity
        final String anonymizerPrefix = myPassedClient.getAnonymizerPrefix();

        /*
        Intent passBackClient = new Intent (IndividualClientOrderActivity.this, OrderListActivity.class);

        passBackClient.putExtra("edited_order", passedIntent);

        setResult (RESULT_OK, passBackClient);
        */
    }

    /*
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mTextView.setText(savedInstanceState.getString(TEXT_VIEW_KEY));
    }
*/
    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("orderNameClientField", localFeedbackClient.getClientName());
        outState.putString("orderPhoneClientField",localFeedbackClient.getClientPhoneNo());
        outState.putString("orderLocationClientField",localFeedbackClient.getClientLocation());
        outState.putString("orderProductIdField", localFeedbackClient.getClientProductID());
        outState.putInt("orderProductQuantField", localFeedbackClient.getClientQuantity());
        outState.putInt("orderProductPriceField", localFeedbackClient.getClientPrice());
        outState.putInt("orderPriceAdjustField", localFeedbackClient.getClientPriceAdjust());
        outState.putInt("orderUrgencyField", localFeedbackClient.getClientUrgency());
        outState.putInt("orderValueClientField", localFeedbackClient.getClientValue());
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
        localFeedbackClient.setClientQuantity(savedInstanceState.getInt("orderProductQuantField"));
        localFeedbackClient.setClientPrice(savedInstanceState.getInt("orderProductPriceField"));
        localFeedbackClient.setClientPriceAdjust(savedInstanceState.getInt("orderPriceAdjustField"));
        localFeedbackClient.setClientUrgency(savedInstanceState.getInt("orderUrgencyField"));
        localFeedbackClient.setClientValue(savedInstanceState.getInt("orderValueClientField"));
        localFeedbackClient.setClientStatus(savedInstanceState.getString("orderStatusClientField"));

        //extra
        Log.d("Notif", "successfully retrieve instance state");
    }
}

/*
 // set customer name
 String cust_name = (String) savedInstanceState.getString();
 final TextView customer_name = (TextView) findViewById(R.id.customerName);
 customer_name.setText(cust_name);


 //set phone number

 String ph_number = (String) savedInstanceState.getString();
 final TextView phone_number = (TextView) findViewById(R.id.phoneNumber);

 phone_number.setText(ph_number);

 */

