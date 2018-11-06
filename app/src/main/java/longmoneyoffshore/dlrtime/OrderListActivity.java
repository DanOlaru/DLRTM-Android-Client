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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;

import longmoneyoffshore.dlrtime.utils.GSheetsApiOperations.ReadDataFromSheets;
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientArray;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientParcel;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientAdapter;
import longmoneyoffshore.dlrtime.utils.MapDestinationsParcel;
import longmoneyoffshore.dlrtime.utils.GSheetsApiOperations.PassDataBackToSheets;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.*;

public class OrderListActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private ClientArray clients = new ClientArray();
    public volatile ArrayList<String> destinationLocations = new ArrayList<String>();

    private ListView listview;
    private ImageButton btnDownload;
    private ImageButton signOutButton;
    private ImageButton goToMapsButton;
    private ImageButton makeNewOrderButton;
    private ImageButton settingsButton;

    private int positionOnListClicked;

    String sheetID;
    Client backupClickedOrder;

    GoogleAccountCredential mCredential;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    private static final List<String> SCOPES_LIST = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        //read saved preferences

        SharedPreferences pref;
        pref = getApplicationContext().getSharedPreferences("preferences.xml", 0); // 0 - for private mode

        GLOBAL_ANONYMIZER_PREFIX = pref.getString("anonymizer_prefix", US_ANONYMIZER_PREFIX); // getting String
        //Log.d("ANON PREF SETTING", anonPrefSetting);

        GLOBAL_SCANNER_SETTING = pref.getBoolean("scanner_preference", true); // getting boolean


        listview = (ListView) findViewById(R.id.listview);
        btnDownload = (ImageButton) findViewById(R.id.btnOrdersSync);

        // Internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            btnDownload.setEnabled(true);
        } else {
            btnDownload.setEnabled(false);
        }

        signOutButton = (ImageButton) findViewById(R.id.sign_out);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signOutIntent = new Intent(OrderListActivity.this, SheetsListActivity.class);
                setResult(REQUEST_CODE_SIGN_OUT, signOutIntent);
                finish();
            }
        });

        goToMapsButton = (ImageButton) findViewById(R.id.btn_go_to_maps);
        goToMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMapsRoute = new Intent(OrderListActivity.this, MapsRouteActivity.class);

                //Get an ArrayList of all the destinations where the user needs to go
                destinationLocations.clear();
                for (int j = 0; j<clients.getClientArray().size();j++) {
                    if (!(clients.getClientArray().get(j).getClientStatus().toUpperCase().equals("DONE")
                    || clients.getClientArray().get(j).getClientStatus().toUpperCase().equals("CANCEL")))
                    destinationLocations.add(clients.getClientArray().get(j).getClientLocation());
                }

                if (destinationLocations.size() > 0) {

                    MapDestinationsParcel destinationsParcel = new MapDestinationsParcel(destinationLocations);
                    toMapsRoute.putExtra("locations to go to", destinationsParcel);
                    startActivity(toMapsRoute);

                } else {/*TODO: nothing while the list of locations isn't populated */}
            }
        });

        makeNewOrderButton = (ImageButton) findViewById(R.id.btn_make_new_order);
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


        settingsButton = (ImageButton) findViewById(R.id.app_settings);

        //TODO: button visibility depends on settings

        int BUTTON_VISIBILITY = View.VISIBLE;
        settingsButton.setVisibility(BUTTON_VISIBILITY);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(OrderListActivity.this, AppSettings.class);
                startActivity(settingsIntent);
            }
        });

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
                if (resultCode == REQUEST_CODE_SIGN_OUT) {

                    Intent signOutIntent = new Intent(OrderListActivity.this, SheetsListActivity.class);
                    setResult(REQUEST_CODE_SIGN_OUT, signOutIntent);
                    finish();
                } else if (resultCode == RESULT_OK) {
                    //new/edited data on the OrderListActivity is passed back to the google sheets document
                    Client returnClient = (Client) data.getParcelableExtra("edited order");

                    if (returnClient.clientDifferences(blankClient).equals("0000000000"))
                    {
                        //TODO: we delete the entry in the sheets
                        clients.getClientArray().remove(positionOnListClicked);
                        ClientAdapter reAdapter = new ClientAdapter(this, R.layout.client_item, clients.getClientArray());
                        listview.setAdapter(reAdapter);

                        String rangeToModify = "Sheet1!" + "A" + (positionOnListClicked + 2) + ":J" + (positionOnListClicked + 2);

                        new PassDataBackToSheets(mCredential, returnClient, backupClickedOrder, positionOnListClicked, DELETE_FIELD).execute(sheetID);
                    } else if (!(returnClient.equalsRevision(backupClickedOrder))) {

                        clients.getClientArray().set(positionOnListClicked, returnClient);

                        ClientAdapter reAdapter = new ClientAdapter(this, R.layout.client_item, clients.getClientArray());
                        listview.setAdapter(reAdapter);

                        String rangeToModify = "Sheet1!" + "A" + (positionOnListClicked + 2) + ":J" + (positionOnListClicked + 2);
                        new PassDataBackToSheets(mCredential, returnClient, backupClickedOrder, positionOnListClicked, UPDATE_FIELD).execute(sheetID);
                    }
                }
                break;
            case CREATE_NEW_ORDER:
                if (resultCode == REQUEST_CODE_SIGN_OUT) {
                    Intent signOutIntent = new Intent(OrderListActivity.this, SheetsListActivity.class);
                    setResult(REQUEST_CODE_SIGN_OUT, signOutIntent);
                    finish();
                } else if (resultCode == RESULT_OK) {
                    Client returnClient = (Client) data.getParcelableExtra("edited order");

                    if (!returnClient.clientDifferences(blankClient).equals("0000000000")) {
                        clients.getClientArray().add(returnClient);
                        final ClientAdapter reAdapter = new ClientAdapter(this, R.layout.client_item, clients.getClientArray());
                        listview.setAdapter(reAdapter);
                        int positionToAppend = reAdapter.getCount()+1;

                        new PassDataBackToSheets(mCredential, returnClient, backupClickedOrder, positionToAppend, APPEND_FIELD).execute(sheetID);
                    } else {/*do nothing*/}
                }
                break;

        }
    }

    private void getResultsFromApi(String selectedSheetID) {
        if (!isGooglePlayServicesAvailable()) { acquireGooglePlayServices(); }
        else if (mCredential.getSelectedAccountName() == null) { chooseAccount();}
        else if (! isDeviceOnline()) { } else {
            listview.setAdapter(null);
            clients.getClientArray().clear();
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