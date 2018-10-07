package longmoneyoffshore.dlrtime;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import longmoneyoffshore.dlrtime.utils.AsyncResult;
import longmoneyoffshore.dlrtime.utils.Client;
import longmoneyoffshore.dlrtime.utils.ClientParcel;
import longmoneyoffshore.dlrtime.utils.DownloadAsyncTask;
import longmoneyoffshore.dlrtime.utils.ClientAdapter;
import longmoneyoffshore.dlrtime.utils.MapDestinationsParcel;

import static longmoneyoffshore.dlrtime.utils.GlobalValues.APP_API_KEY;

//Dan - test
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;


public class OrderListActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "HttpExample";
    private static final int REQUEST_CODE_1 = 1;

    private ArrayList<Client> clients = new ArrayList<Client>();
    private volatile ArrayList<String> destinationLocations = new ArrayList<String>();

    private ListView listview;
    private Button btnDownload;
    private Button signOutButton;
    private Button goToMapsButton;
    private int positionOnListClicked;

    public String DOWNLOAD_TEMPLATE = "https://spreadsheets.google.com/tq?key=";
    public String FILE_DOWNLOAD_TEMPLATE_START = "https://docs.google.com/spreadsheets/d/";
    public String FILE_DOWNLOAD_TEMPLATE_CODA = "/edit#gid=1268876841";
    public String A_SHEET_TO_DOWNLOAD = "https://spreadsheets.google.com/tq?key=16ujt55GOJVgcgxox1NrGT_iKf2LIVlEU7ywxtzOtngY";
    public String SHEET_DOWNLOAD_PRE = "https://spreadsheets.google.com/tq?key=";

    String SHEET_TO_DOWNLOAD_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        listview = (ListView) findViewById(R.id.listview);

        btnDownload = (Button) findViewById(R.id.btnDownload);
        //its activity is handled by buttonClickHandler

        // Internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            btnDownload.setEnabled(true);
        } else {
            btnDownload.setEnabled(false);
        }

        //TODO: sign out button click references public public signOut method in utils
        signOutButton = (Button) findViewById(R.id.sign_out_button);

        //clients list is already populated
        goToMapsButton = (Button) findViewById(R.id.btn_go_to_maps);
        goToMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMapsRoute = new Intent(OrderListActivity.this, MapsRouteActivity.class);
                //TODO: generate Parcel object from clients list and send the locations as array to MapsRouteActivity
                MapDestinationsParcel destinationsParcel = new MapDestinationsParcel(destinationLocations);

                toMapsRoute.putExtra("locations to go to", destinationsParcel);
                startActivity(toMapsRoute);
            }
        });

        Intent intentFromGSheets = getIntent();
        Bundle sheetData = intentFromGSheets.getExtras();

        SHEET_TO_DOWNLOAD_ID = sheetData.getString("file selected");
        //Log.d("FILEID", "COMPLETE DOWNLOAD LINK: " + SHEET_DOWNLOAD_PRE + SHEET_TO_DOWNLOAD_ID);
        Log.d("INSIDEASYNC", "FILE URL: " + SHEET_DOWNLOAD_PRE);

        downLoadAndShowClients(SHEET_TO_DOWNLOAD_ID);
    }

    public void buttonClickHandler(View view) {
        downLoadAndShowClients(SHEET_TO_DOWNLOAD_ID);
    }
    //@Override
    //public void writeToParcel(Parcel destinations, int flags) {
    //    destinations.writeList(destinationLocations);
    //}

    private void downLoadAndShowClients (String downloadFileId) {
        listview.setAdapter(null);
        clients.clear();

        // Start to retrieve data on another thread (as background activity)
        new DownloadAsyncTask(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {
                processJson(object); // Feeds with the retrieved data
                Log.d("INSIDEASYNC", "FILE URL: " + SHEET_DOWNLOAD_PRE + downloadFileId + "key=" + APP_API_KEY);
            }
        //}).execute(SHEET_DOWNLOAD_PRE + downloadFileId + "key=" + APP_API_KEY);
    }).execute(A_SHEET_TO_DOWNLOAD);

        if(listview!=null)
        {
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //position clicked on — it is visible outside of this scope to the onActivityResult
                    positionOnListClicked = position;

                    Client clickedOrder = new Client((Client) parent.getItemAtPosition(position));
                    ClientParcel clickedOrderParcel = new ClientParcel(clickedOrder);
                    Intent thisIndividualOrder = new Intent(OrderListActivity.this, IndividualClientOrderActivity.class);

                    thisIndividualOrder.putExtra("order", clickedOrderParcel);
                    int reqCode = 1; //what should be the predefined value?
                    startActivityForResult(thisIndividualOrder,reqCode);
                }
            });
        }
    }

    // method invoked when target activity returns result data.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        super.onActivityResult(requestCode, resultCode, returnIntent);

        // The returned result data is identified by requestCode.
        //Log.d("return_resultcode ", String.valueOf(resultCode));

        if(resultCode == RESULT_OK) {
            //set the new/edited data on the OrderListActivity and pass it back - to the google sheets document
            Client returnLocalClient = (Client) returnIntent.getParcelableExtra("edited order");

            //clients - is modified to take returnLocalClient at the positionOnListClicked
            //the listview is re-shown
            clients.set(positionOnListClicked, returnLocalClient);
            final ClientAdapter reAdapter = new ClientAdapter(this, R.layout.client_item, clients);
            listview.setAdapter(reAdapter);
        }
    }

    private void processJson(JSONObject object) {
        try {
            JSONArray rows = object.getJSONArray("rows");

            //Log.d("Rows lENGTH","lENGTH: " + rows.length());

            for (int r = 0; r < rows.length(); ++r)
            {
                JSONObject row = rows.getJSONObject(r);
                JSONArray columns = row.getJSONArray("c");

                //Log.d("Rows Json","Content: " + columns.toString());

                String name = columns.getJSONObject(0).getString("v");

                //Log.d("First value","1  " + name);

                String phone = columns.getJSONObject(1).getString("v");
                String location = columns.getJSONObject(2).getString("v");
                String productId = columns.getJSONObject(3).getString("v");
                float quantity = Float.parseFloat(columns.getJSONObject(4).getString("v"));
                float price = Float.parseFloat(columns.getJSONObject(5).getString("v"));
                float priceAdjust = Float.parseFloat(columns.getJSONObject(6).getString("v"));
                float urgency = Float.parseFloat(columns.getJSONObject(7).getString("v"));
                float value = Float.parseFloat(columns.getJSONObject(8).getString("v"));
                String status = columns.getJSONObject(9).getString("v");

                Client client = new Client(name, phone, location, productId, quantity, price, priceAdjust, urgency, value, status);
                clients.add(client);
                //Log.d("ClientQ", String.valueOf(client.getClientQuantity()));
            }

            //Get an ArrayList of all the destinations where the user needs to go
            destinationLocations.clear();
            for (int j = 0; j<clients.size();j++) {destinationLocations.add(clients.get(j).getClientLocation());}

            //is this code in the right onCreate place?
            final ClientAdapter adapter = new ClientAdapter(this, R.layout.client_item, clients);
            //listview.setAdapter(null);
            listview.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ClientAdapter passBackOrderChanges () {

        //return modifications to GSheets

        //clients.set(positionOnListClicked, returnLocalClient);
        //final ClientAdapter reAdapter = new ClientAdapter(this, R.layout.client_item, clients);
        //listview.setAdapter(reAdapter);
        ClientAdapter dummy = new ClientAdapter(this, R.layout.client_item, clients);
        return dummy;
    }
}
