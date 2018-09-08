package longmoneyoffshore.dlrtime;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import android.support.v7.widget.Toolbar;

import longmoneyoffshore.dlrtime.utils.Client;


public class IndividualClientOrderActivity extends AppCompatActivity {

    //Dan's code

    //create dummy data set
    private Client myPassedClient = new Client ("Johnnt Apple" , "773 845 1234" , "Argyle & Lawrence" , "BD" , 60, 60, 0, 0, 3, "pending");


    //individual variables for the display table
    EditText orderNameField;
    EditText orderNameClientField;
    EditText orderPhoneClientField;
    EditText orderLocationClientField;
    EditText orderProductIdField;
    EditText orderProductQuantField;
    EditText orderProductPriceField;
    EditText orderPriceAdjustField;
    EditText orderUrgencyField;
    EditText orderValueClientField;
    EditText orderStatusClientField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_client_order);

        //by Dan

        /*
        // Get the transferred data from source activity.
        Intent passedIntent = getIntent();
        Client myPassedClient = (Client) passedIntent.getExtras("order");

*/

        //is casting necessary here?

        orderNameField = (EditText) findViewById(R.id.orderNameClient);
        orderNameField.setText(myPassedClient.getClientName());

        orderNameClientField = findViewById(R.id.orderNameClnt);
        orderNameClientField.setText(myPassedClient.getClientName());

        orderPhoneClientField = findViewById(R.id.orderPhoneClnt);
        orderPhoneClientField.setText(myPassedClient.getClientPhoneNo());

        orderLocationClientField = findViewById(R.id.orderLocationClnt);
        orderLocationClientField.setText(myPassedClient.getClientLocation());

        orderProductIdField = findViewById(R.id.orderProductIDClnt);
        orderProductIdField.setText(myPassedClient.getClientProductID());

        orderProductQuantField = findViewById(R.id.orderProductQuantClnt);
        orderProductQuantField.setText(myPassedClient.getClientQuantity());

        orderProductPriceField = findViewById(R.id.orderProductPriceClnt);
        orderProductPriceField.setText(myPassedClient.getClientPrice());

        orderPriceAdjustField = findViewById(R.id.orderPriceAdjClnt);
        orderPriceAdjustField.setText(myPassedClient.getClientPriceAdjust());

        //orderUrgencyField = findViewById(R.id.orderUrgencyClnt);
        //orderUrgencyField.setText(myPassedClient.getClientUrgency());

        //orderValueClientField = findViewById(R.id.orderValueClnt);
        //orderValueClientField.setText(myPassedClient.getClientValue());

        orderStatusClientField = findViewById(R.id.orderStatusClnt);
        orderStatusClientField.setText(myPassedClient.getClientStatus());


        //after any modifications are performed by the user from the Individual Client Order screen
        // the modifications are saved in the myPassedClient object and sent back to the invoking activity
        //which is OrderListActivity.

        /*

        Intent passBackClient = new Intent (IndividualClientOrderActivity.this, OrderListActivity.class);

        passBackClient.putExtra("edited_order", passedIntent);

        setResult (RESULT_OK, passBackClient);
        */


        /*

        //Dan: receive text modifications from text box






        //end by Dan
        */



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

