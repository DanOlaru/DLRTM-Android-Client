package longmoneyoffshore.dlrtime;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import longmoneyoffshore.dlrtime.utils.GSheetsApiOperations.PassDataBackToSheets;
import longmoneyoffshore.dlrtime.utils.GSheetsApiOperations.SpreadSheetUpdate;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.google.api.client.auth.oauth2.Credential;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import longmoneyoffshore.dlrtime.utils.GSheetsApiOperations.ReadDataFromSheets;
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientArray;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientParcel;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientAdapter;
import longmoneyoffshore.dlrtime.utils.MapDestinationsParcel;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.*;

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
    Client backupClickedOrder;

    GoogleAccountCredential mCredential;

    private Button mCallApiButton;
    private Button goToOrdersList;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String APPLICATION_NAME = "DLRTM - Digital Logistics Resource Time Management";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    //private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };
    //private static final String[] SCOPES = { SheetsScopes.DRIVE_FILE };

    //private static final List<String> SCOPES_LIST= Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final List<String> SCOPES_LIST = Collections.singletonList(SheetsScopes.SPREADSHEETS);

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
                Intent signOutIntent = new Intent(OrderListActivity.this, SheetsListActivity.class);
                setResult(REQUEST_CODE_SIGN_OUT, signOutIntent);
                finish();
            }
        });

        goToMapsButton = (Button) findViewById(R.id.btn_go_to_maps);
        goToMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMapsRoute = new Intent(OrderListActivity.this, MapsRouteActivity.class);

                //Get an ArrayList of all the destinations where the user needs to go
                destinationLocations.clear();
                for (int j = 0; j<clients.getClientArray().size();j++) {
                    destinationLocations.add(clients.getClientArray().get(j).getClientLocation());
                    Log.d("MAPS DESTINATIONS", "PLACES GOING" + clients.getClientArray().get(j).getClientLocation());
                }

                if (destinationLocations.size() > 0) {

                    MapDestinationsParcel destinationsParcel = new MapDestinationsParcel(destinationLocations);
                    toMapsRoute.putExtra("locations to go to", destinationsParcel);
                    startActivity(toMapsRoute);

                } else {/*TODO: nothing while the list of locations isn't populated */}
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
                    positionOnListClicked = position;
                    Client clickedOrder = new Client((Client) parent.getItemAtPosition(position));
                    backupClickedOrder = new Client(clickedOrder);

                    Intent thisIndividualOrder = new Intent(OrderListActivity.this, IndividualClientOrderActivity.class);
                    ClientParcel clickedOrderParcel = new ClientParcel(clickedOrder);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            // from login&permissions
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    //mOutputText.setText("This app requires Google Play Services. Please install " +
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
                    //new/edited data on the OrderListActivity is passed back to the google sheets document
                    Client returnClient = (Client) data.getParcelableExtra("edited order");
                    //Log.d("WHATS THERE", "SHOW ME THE RETURN CLIENT");
                    //returnClient.showClient();

                    if (!(returnClient.equalsRevision(backupClickedOrder))) {
                        clients.getClientArray().set(positionOnListClicked, returnClient);
                        final ClientAdapter reAdapter = new ClientAdapter(this, R.layout.client_item, clients.getClientArray());
                        listview.setAdapter(reAdapter);

                        String rangeToModify = "Sheet1!" + "A" + (positionOnListClicked + 2) + ":J" + (positionOnListClicked + 2);
                        new PassDataBackToSheets(mCredential, returnClient, backupClickedOrder, positionOnListClicked, UPDATE_FIELD).execute(sheetID);
                    } else {
                        //TODO: nothing Log.d("NO_CLIENT_MODIFIES", " #################################### WAS NOT MODIFIED");
                    }

                    /*
                    List<List<Object>> values = returnClient.returnClientAsObjectList();
                    try {
                        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                        new SpreadSheetUpdate(sheetID, rangeToModify, values, getCredentials(HTTP_TRANSPORT), OrderListActivity.this);

                    } catch (Exception e) {
                        Log.e("EXCEPTION CAUGHT", e.getMessage());
                    }
                    */
                }
                break;
            case CREATE_NEW_ORDER:
                if (resultCode == RESULT_OK) {
                    //Client returnClient = (Client) data.getParcelableExtra("new order");
                    Client returnClient = (Client) data.getParcelableExtra("edited order");

                    if (!returnClient.clientDifferences(blankClient).equals("0000000000")) {

                        //Log.d("NEWORDER", "CLIENT BEING ADDED #################################### ");
                        //returnClient.showClient();

                        clients.getClientArray().add(returnClient);
                        final ClientAdapter reAdapter = new ClientAdapter(this, R.layout.client_item, clients.getClientArray());
                        listview.setAdapter(reAdapter);

                        new PassDataBackToSheets(mCredential, returnClient, backupClickedOrder, positionOnListClicked, APPEND_FIELD).execute(sheetID);
                    } else {/*do nothing*/
                        Log.d("NO_CLIENT_ADDED", " #################################### WAS NOT MODIFIED");}
                }
        }
    }

    /*
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = OrderListActivity.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES_LIST)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
    */

    private void getResultsFromApi(String selectedSheetID) {
        if (!isGooglePlayServicesAvailable()) { acquireGooglePlayServices(); }
        else if (mCredential.getSelectedAccountName() == null) { chooseAccount();}
        else if (! isDeviceOnline()) { } else {
            listview.setAdapter(null);
            //clients.clear();
            clients.getClientArray().clear();
            //new OrderListActivity.RequestDataTask(mCredential).execute(selectedSheetID);
            new ReadDataFromSheets(mCredential, clients, destinationLocations, OrderListActivity.this,listview).execute(selectedSheetID);
        }
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