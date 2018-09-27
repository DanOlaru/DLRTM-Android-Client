//test

package longmoneyoffshore.dlrtime;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import longmoneyoffshore.dlrtime.utils.AsyncResult;
import longmoneyoffshore.dlrtime.utils.Client;
import longmoneyoffshore.dlrtime.utils.DownloadAsyncTask;
import longmoneyoffshore.dlrtime.utils.ClientAdapter;

public class OrderListActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "HttpExample";
    ArrayList<Client> clients = new ArrayList<Client>(); // The list of the clients
    ListView listview;
    Button btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set-out UI
        setContentView(R.layout.activity_order_list);
        listview = (ListView) findViewById(R.id.listview);
        btnDownload = (Button) findViewById(R.id.btnDownload);

        // Internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            btnDownload.setEnabled(true);
        } else {
            btnDownload.setEnabled(false);
        }


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public void buttonClickHandler(View view) {

        // Start to retrieve data on another thread (therefore as a background activity)
        new DownloadAsyncTask(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {
                processJson(object); // Feeds with the retrieved data
            }
        }).execute("https://spreadsheets.google.com/tq?key=16ujt55GOJVgcgxox1NrGT_iKf2LIVlEU7ywxtzOtngY");


        /*

        //Dan: code from website example

        // Click this button to pass data to target activity.

        // the item clicked on will ultimately not be a button, but the line in the table of orders

        Button passDataReturnResultButton = (Button)findViewById(R.id.passDataReturnResultButton);

        passDataReturnResultSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //click on the button by the side of that specific order

                Client thisOrder = new Client(); //this has to be populated with the data from the specific row which was clicked

                //what method converts thisOrder into a Parcel?

                ClientParcel thisOrderParcel = new ClientParcel(thisOrder); // using constructor from Client object

                Intent thisIndividualOrder = new Intent(OrderListActivity.this, IndividualClientOrderActivity.class);

                thisIndividualOrder.putExtra("order", thisOrderParcel);

                int reqCode = 1; //what should be the predefined value?

                startActivityForResult(thisIndividualOrder, reqCode);
            }
        });
    }

    // This method is invoked when target activity returns result data.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);

        // The returned result data is identified by requestCode.
        // The request code is specified in startActivityForResult(intent, REQUEST_CODE_1); method.
        switch (requestCode)
        {
            // This request code is set by startActivityForResult(intent, REQUEST_CODE_1) method.
            case REQUEST_CODE_1:
                //TextView textView = (TextView)findViewById(R.id.resultDataTextView);

                if(resultCode == RESULT_OK)
                {
                    //old code
                    //String messageReturn = dataIntent.getStringExtra("message_return");
                    //textView.setText(messageReturn);

                    //new code sets the new/ edited data on the OrderListActivity and passes it back to the google sheets document
                }
        }
    }

      //end code by Dan
      */


    }

    private void processJson(JSONObject object) {

        try {
            JSONArray rows = object.getJSONArray("rows");

            Log.d("Rows lENGTH","lENGTH: " + rows.length());

            for (int r = 0; r < rows.length(); ++r)
            {
                JSONObject row = rows.getJSONObject(r);
                JSONArray columns = row.getJSONArray("c");

                Log.d("Rows Json","Content: " + columns.toString());

                String name = columns.getJSONObject(0).getString("v");

                Log.d("Fist value","1  " + name);

                String phone = columns.getJSONObject(1).getString("v");
                String location = columns.getJSONObject(2).getString("v");
                String productId = columns.getJSONObject(3).getString("v");
                int quantity = columns.getJSONObject(4).getInt("v");
                int price = columns.getJSONObject(5).getInt("v");
                int priceAdjust = columns.getJSONObject(6).getInt("v");
                int urgency = columns.getJSONObject(7).getInt("v");
                int value = columns.getJSONObject(8).getInt("v");
                String status = columns.getJSONObject(9).getString("v");

                Client client = new Client(name, phone, location, productId, quantity, price, priceAdjust, urgency, value, status);

               Log.d("Client", "Client " + client.toString());

                clients.add(client);
            }

            final ClientAdapter adapter = new ClientAdapter(this, R.layout.client_item, clients);
            listview.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
