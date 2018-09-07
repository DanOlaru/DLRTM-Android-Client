package longmoneyoffshore.dlrtime;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import longmoneyoffshore.dlrtime.utils.Client;

public class IndividualClientOrderActivity extends AppCompatActivity {

    private Client myClient = new Client();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_client_order);

        TextView orederInfoTextView = findViewById(R.id.orderName);

//        orederInfoTextView.setText();

        //begin by Dan

        /*
        // set customer name
        String cust_name = (String) savedInstanceState.getString();
        final TextView customer_name = (TextView) findViewById(R.id.customerName);
        customer_name.setText(cust_name);


        //set phone number

        String ph_number = (String) savedInstanceState.getString();
        final TextView phone_number = (TextView) findViewById(R.id.phoneNumber);

        phone_number.setText(ph_number);


        //end by Dan
        */



//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

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
