package longmoneyoffshore.dlrtime;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientParcel;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientAdapter;
import longmoneyoffshore.dlrtime.utils.MapDestinationsParcel;

import static longmoneyoffshore.dlrtime.utils.GlobalValues.APP_API_KEY;

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


    String sheetURL;
    String sheetID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        sheetID = sheetData.getString("file selected"); //TODO: the file ID arrives properly
        Log.d("INSIDEORDERLIST", "FILE ID: " + sheetID);

        //TODO: how to properly form dowloadURL from fild ID


        //PROPER REQUEST TEMPLATES
        //https://www.googleapis.com/drive/v3/files/FILE_ID?fields=mimeType&key={YOUR_API_KEY}
        //GET https://www.googleapis.com/drive/v3/files/1VRwq2yaAUH6dilpDbFksgT8_ioRaWHmagaQQLK2KOKk?fields=contentHints%2Fthumbnail%2FmimeType%2CcopyRequiresWriterPermission%2CcreatedTime%2CfileExtension%2CfullFileExtension%2Cid%2Ckind%2ClastModifyingUser%2CmimeType%2CmodifiedByMeTime%2CmodifiedTime%2Cname%2CownedByMe&key={YOUR_API_KEY}

        sheetURL = "https://www.googleapis.com/drive/v3/files/"+sheetID +
                "?alt=media" +
                "&key="+APP_API_KEY;
        //"?mimetype=text/csv"

        Log.d("BEFOREDOWNLOADCLIENTS", "SHEET ID: " + sheetURL);
        downLoadAndShowClients(sheetURL);
    }

    public void buttonClickHandler(View view) {
        downLoadAndShowClients(sheetURL);
    }

    private void downLoadAndShowClients (String downloadFileURL) {
        listview.setAdapter(null);
        clients.clear();

        // Start to retrieve data on another thread (as background activity)
        new DownloadAsyncTask(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {
                processJson(object); // Feeds with the retrieved data
            }
        }).execute(downloadFileURL);

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
    /*
    public void buttonClickHandler(View view) {
        downLoadAndShowClients(SHEET_TO_DOWNLOAD_ID);
    } */

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


/*
    private void downLoadAndShowClients (String downloadedFile) {
        listview.setAdapter(null);
        clients.clear();

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
    } */
