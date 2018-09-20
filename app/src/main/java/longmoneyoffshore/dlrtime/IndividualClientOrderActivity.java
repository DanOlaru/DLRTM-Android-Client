package longmoneyoffshore.dlrtime;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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



public class IndividualClientOrderActivity extends Activity {

    //Dan's code

    //create dummy data set
    //private Client myLocalClient = new Client ("Johnny T Apple" , "773 845 1234" , "Argyle & Lawrence" , "BD" , 60, 60, 0, 0, 3, "pending");

    //this Client object will take any modifications that are made in the IndividualClientOrder window and pass them back
    // to the OrderListActivity and back to GSheets
    private Client localFeedbackClient;

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

        //by Dan
        // Get the transferred data from source activity.
        Intent passedIntent = getIntent();
        ClientParcel myPassedClientParcel = passedIntent.getParcelableExtra("order");
        Client myPassedClient = new Client(myPassedClientParcel.returnClientFromParcel());


        //or??
        //Bundle passedData = passedIntent.getExtras();
        //ClientParcel myPassedClientParcel = (ClientParcel) passedData.getParcelable("order");

        //TODO: we make the localFeedbackClient identical to the passed Client object, except any modifications that will be made
        // which will be passed back to the invoking activity or perhaps the ASyncTask!!!

        localFeedbackClient = new Client (myPassedClient);

        //create dummy data set
        myPassedClient = new Client ("Johnny T Apple" , "773 845 1234" , "Argyle & Lawrence" , "BD" , 60, 60, 0, 0, 3, "pending");


        //get the applicable anonymizerPrefix
        final String anonymizerPrefix = myPassedClient.getAnonymizerPrefix();

        //fill the fields with info from passed Client object

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

        orderUrgencyField = (RatingBar) findViewById(R.id.orderUrgencyClnt);

        // — these listeners feed back the rating bar settings into the Client object and Client Parcel Object and eventually back to GSheets as integers from 1 to 5
        orderUrgencyField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                localFeedbackClient.setClientUrgency ((int) ratingBar.getRating());
            }
        });


        orderValueClientField = (RatingBar) findViewById(R.id.orderValueClnt);

        /*
        orderValueClientField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                myPassedClient.setClientValue ((int) ratingBar.getRating());
            }
        }); */


        //orderStatusClientField = (EditText) findViewById(R.id.orderStatusClnt);

        orderStatusClientField = (EditText) findViewById(R.id.individualOrderIssueOrComment);
        orderStatusClientField.setText(myPassedClient.getClientStatus());


        //Dan: receive text modifications from text box

        //set on click listener here

        String retrievedClientStatus = orderStatusClientField.getText().toString();
        myPassedClient.setClientStatus(retrievedClientStatus);

        //after any modifications are performed by the user from the Individual Client Order screen
        // the modifications are saved in the myPassedClient object and sent back to the invoking activity
        //which is OrderListActivity.

        /*

        Intent passBackClient = new Intent (IndividualClientOrderActivity.this, OrderListActivity.class);

        passBackClient.putExtra("edited_order", passedIntent);

        setResult (RESULT_OK, passBackClient);
        */


        //implement sign-out button here

        //make_call button starts the dialer
        Button callButton = (Button) findViewById(R.id.make_call_button);
        //TODO: this button opens up the dialer with the customer's phone number so the call can be placed

        // public void dialNumber(View view) {
        //    }


        //get the context of this activity
        final ContextWrapper thisContextWrapper = new ContextWrapper(this);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //establish number to call
                TextView numberToCall = (TextView) findViewById(R.id.orderPhoneClnt);
                String basicNumberToCall = numberToCall.getText().toString();

                //instantiate caller object
                ClientCaller thisClientCaller = new ClientCaller();
                //instantiate context object


                thisClientCaller.dialClient (basicNumberToCall, anonymizerPrefix, thisContextWrapper);

            }
        });


        //issue_or_cancel button sets the message 'Canceled' or 'Issue' or sets cursor / carel on the text box at the bottom of the page
        Button issueOrCancelButton = (Button) findViewById(R.id.issue_or_cancel_button);
        //TODO:press it once and the status is changed from pending to done
        //TODO:press it three times and the status is issue


        //back_button takes me back to the previous Activity — presumably OrderListActivity
        Button backButton = (Button) findViewById(R.id.back_button);
        //TODO: the Back button makes sure that the data in the text boxes is saved and sent back to the previous activity / GSheets


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

        Button commitCommentButton = (Button) findViewById(R.id.btnSubmit);
        //TODO: this button saves the comment typed into the comment box and feeds it back to OrderListActivity

        //end by Dan



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

