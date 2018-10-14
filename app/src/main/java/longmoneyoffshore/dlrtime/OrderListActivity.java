/* Author: Dan Olaru, (c) 2018 */

package longmoneyoffshore.dlrtime;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import longmoneyoffshore.dlrtime.utils.GSheetsApiOperations.PassDataBackToSheets;
import longmoneyoffshore.dlrtime.utils.GSheetsApiOperations.ReadDataFromSheets;
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientArray;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientParcel;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientAdapter;
import longmoneyoffshore.dlrtime.utils.MapDestinationsParcel;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static longmoneyoffshore.dlrtime.utils.GlobalValues.APPEND_FIELD;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.UPDATE_FIELD;

public class OrderListActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String DEBUG_TAG = "HttpExample";
    private static final int REQUEST_CODE_1 = 1;
    private ClientArray clients = new ClientArray();
    //private volatile ArrayList<String> destinationLocations = new ArrayList<String>();
    public volatile ArrayList<String> destinationLocations = new ArrayList<String>();

    private ListView listview;
    private Button btnDownload;
    private Button signOutButton;
    private Button goToMapsButton;
    private Button makeNewOrderButton;

    private int positionOnListClicked;

    String sheetID;
    Client saveClickedOrder;

    GoogleAccountCredential mCredential;
    private Button mCallApiButton;
    private Button goToOrdersList;


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    //private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    //private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    //private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    public static final int INDIVIDUAL_ORDER_CHANGED=1005;
    public static final int CLICK_INDIVIDUAL_ORDER = 1006;
    public static final int CREATE_NEW_ORDER=1007;

    final String dummyFileID = "16ujt55GOJVgcgxox1NrGT_iKf2LIVlEU7ywxtzOtngY";

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
        signOutButton = (Button) findViewById(R.id.sign_out);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("SIGNOUT", "TRYING TO SIGN OUT");
                //TODO: Start intent to sign out
                //Intent signOutIntent = new Intent(OrderListActivity.this, LoginActivity.class);
                //setResult(REQUEST_CODE_SIGN_OUT, signOutIntent);
                //finish();
            }
        });

        //clients list is already populated
        goToMapsButton = (Button) findViewById(R.id.btn_go_to_maps);
        goToMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //testing
                for (int k=0;k<destinationLocations.size();k++) {
                    Log.d("DESTINATIONS EXTRACTED", String.valueOf(k)+ " " + destinationLocations.get(k));
                }
                Intent toMapsRoute = new Intent(OrderListActivity.this, MapsRouteActivity.class);

                if (destinationLocations.size() > 0) {
                    MapDestinationsParcel destinationsParcel = new MapDestinationsParcel(destinationLocations);
                    toMapsRoute.putExtra("locations to go to", destinationsParcel);

                } else {
                    //TODO: the list of locations isn't yet populated
                    }
                startActivity(toMapsRoute);
            }
        });

        makeNewOrderButton = (Button) findViewById(R.id.btn_make_new_order);

        makeNewOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIndividualOrder = new Intent(OrderListActivity.this, IndividualClientOrderActivity.class);
                newIndividualOrder.setAction("new order");
                startActivityForResult(newIndividualOrder,CREATE_NEW_ORDER);
            }
        });

        Intent intentFromSheetsList = getIntent();
        sheetID = intentFromSheetsList.getStringExtra("file selected");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());

        getResultsFromApi(sheetID);

        listview.setAdapter(null);
        //clients.clear();
        clients.getClientArray().clear();

        if(listview!=null)
        {
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //position clicked on — visible outside of this scope to the onActivityResult
                    positionOnListClicked = position;
                    Client clickedOrder = new Client((Client) parent.getItemAtPosition(position));
                    saveClickedOrder = clickedOrder;

                    ClientParcel clickedOrderParcel = new ClientParcel(clickedOrder);
                    Intent thisIndividualOrder = new Intent(OrderListActivity.this, IndividualClientOrderActivity.class);
                    thisIndividualOrder.putExtra("order", clickedOrderParcel);
                    thisIndividualOrder.setAction("individual order");

                    startActivityForResult(thisIndividualOrder,CLICK_INDIVIDUAL_ORDER);
                }
            });
        }
    }

    public void buttonClickHandler(View view) {
        getResultsFromApi(sheetID);
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {

            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {

                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi(sheetID);
            } else {

                startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(this, "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            // this is from login&permissions
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    //mOutputText.setText(
                    //        "This app requires Google Play Services. Please install " +
                    //                "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi(sheetID);
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi(sheetID);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi(sheetID);
                }
                break;
                // this is from IndividualClientOrder
            case CLICK_INDIVIDUAL_ORDER:
                if (resultCode == RESULT_OK) {
                    //set the new/edited data on the OrderListActivity and pass it back - to the google sheets document
                    Client returnClient = (Client) data.getParcelableExtra("edited order");

                    //TODO: implement comparison to make sure that something has changed
                    clients.getClientArray().set(positionOnListClicked, returnClient);
                    //final ClientAdapter reAdapter = new ClientAdapter(this, R.layout.client_item, clients);
                    final ClientAdapter reAdapter = new ClientAdapter(this, R.layout.client_item, clients.getClientArray());
                    listview.setAdapter(reAdapter);

                    //Sending the change back to GSheets
                    //Log.d("CLIENT_MODIFIES", " ####################################");
                    new PassDataBackToSheets(mCredential, returnClient, saveClickedOrder, positionOnListClicked, UPDATE_FIELD).execute(sheetID);
                }
                break;
            case CREATE_NEW_ORDER:
                if (resultCode == RESULT_OK) {
                    Client returnClient = (Client) data.getParcelableExtra("new order");
                    clients.getClientArray().set(positionOnListClicked, returnClient);
                    final ClientAdapter reAdapter = new ClientAdapter(this, R.layout.client_item, clients.getClientArray());
                    listview.setAdapter(reAdapter);
                    new PassDataBackToSheets(mCredential, returnClient, saveClickedOrder, positionOnListClicked, APPEND_FIELD).execute(sheetID);
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    private void getResultsFromApi(String selectedSheetID) {
        if (! isGooglePlayServicesAvailable()) { acquireGooglePlayServices(); }
        else if (mCredential.getSelectedAccountName() == null) { chooseAccount(); }
        else if (! isDeviceOnline()) { } else {
            listview.setAdapter(null);
            //clients.clear();
            clients.getClientArray().clear();
            //new OrderListActivity.RequestDataTask(mCredential).execute(selectedSheetID);
            new ReadDataFromSheets(mCredential, clients, destinationLocations, OrderListActivity.this,listview).execute(selectedSheetID);
        }
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(OrderListActivity.this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

}