package longmoneyoffshore.dlrtime;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import android.support.v7.widget.Toolbar;

//local imports — Dan

import android.content.Intent;
import longmoneyoffshore.dlrtime.utils.Client;
import longmoneyoffshore.dlrtime.utils.ClientParcel;



public class IndividualClientOrderActivity extends AppCompatActivity {

    //Dan's code

    //create dummy data set
    //private Client myPassedClient = new Client ("Johnny T Apple" , "773 845 1234" , "Argyle & Lawrence" , "BD" , 60, 60, 0, 0, 3, "pending");


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
        Bundle passedData = passedIntent.getExtras();
        ClientParcel myPassedClientParcel = (ClientParcel) passedData.getParcelable("order");

        Client myPassedClient = new Client (myPassedClientParcel);



        //is casting necessary here?

        orderNameField = (EditText) findViewById(R.id.orderNameClient);
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

        /* — these listeners feed back the rating bar settings into the Client object and Client Parcel Object and eventually back to GSheets
        orderUrgencyField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                myPassedClient.setClientUrgency ((int) ratingBar.getRating());
            }
        }); */


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

