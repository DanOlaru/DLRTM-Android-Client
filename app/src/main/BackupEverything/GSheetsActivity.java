package longmoneyoffshore.dlrtime;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

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
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientArray;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientArrayParcel;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class GSheetsActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private Button mCallApiButton;
    private Button goToOrdersList;

    //test
    private TextView mOutputText;
    //ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };

    final String dummyFileID = "16ujt55GOJVgcgxox1NrGT_iKf2LIVlEU7ywxtzOtngY";
    private String selectedFileId= dummyFileID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsheets);


        Intent passedIntent = getIntent();
        Bundle passedDataBundle = passedIntent.getExtras();
        selectedFileId = passedDataBundle.getString("file selected");
        //Log.d("INSIDE GSHEETS_ACT", "THE SPREADSHEET ID IS: " + selectedFileId);

        //test
        mOutputText = (TextView) findViewById(R.id.messages_gsheets);
        //ProgressBar mProgress = findViewById(R.id.indeterminateBar);
        //mProgress.setEnabled(false);

        //Button to orders list
        goToOrdersList = (Button) findViewById(R.id.go_to_orders_list);
        goToOrdersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToOrdersIntent = new Intent(GSheetsActivity.this, OrderListActivity.class);
                String myChosenFileContent = "";

                //TODO: pass the contents of the open file thru intent

                goToOrdersIntent.putExtra("file_contents", myChosenFileContent);
                startActivity(goToOrdersIntent);
            }
        });


        mCallApiButton = (Button)findViewById(R.id.access_gsheets_button);
        mCallApiButton.setEnabled(true);

        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResultsFromApi();
            }
        });

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());

        getResultsFromApi();
    }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {

            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {

            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {

                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {

                startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {

            EasyPermissions.requestPermissions(this, "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
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
        Dialog dialog = apiAvailability.getErrorDialog(GSheetsActivity.this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    //TODO: correct, for backup
    //private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
    private class MakeRequestTask extends AsyncTask<Void, Void, ClientArray> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(transport, jsonFactory, credential)
                    .setApplicationName("GSheets API Android").build();
        }

         protected ClientArray doInBackground(Void... params) {
         try {
         return getDataFromApi(selectedFileId);

         } catch (Exception e) {
         mLastError = e;
         cancel(true);
         return null;
         }
         }


         /* //TODO: correct, for backup
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi(selectedFileId);

            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        } */

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         *
         * my own Clients GSheets
         * https://docs.google.com/spreadsheets/d/16ujt55GOJVgcgxox1NrGT_iKf2LIVlEU7ywxtzOtngY/edit#gid=0
         */
        private ClientArray getDataFromApi(String spreadsheetId) throws IOException {

            ClientArray resultsArray = new ClientArray();
            int counter = -1;

            Client iterationClient = new Client();

            String range = "A2:J";

            ValueRange response = this.mService.spreadsheets().values().get(spreadsheetId, range).execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                for (List row : values) {
                    iterationClient.setClientName(row.get(0).toString());
                    iterationClient.setClientPhoneNo(row.get(1).toString());
                    iterationClient.setClientLocation(row.get(2).toString());
                    iterationClient.setClientProductID(row.get(3).toString());
                    iterationClient.setClientQuantity(Float.parseFloat(row.get(4).toString()));
                    iterationClient.setClientPrice(Float.parseFloat(row.get(5).toString()));
                    iterationClient.setClientPriceAdjust(Float.parseFloat(row.get(6).toString()));
                    iterationClient.setClientUrgency(Float.parseFloat(row.get(7).toString()));
                    iterationClient.setClientValue(Float.parseFloat(row.get(8).toString()));
                    iterationClient.setClientStatus(row.get(9).toString());

                    resultsArray.getClientArray().add(++counter ,new Client(iterationClient));
                }
            }
            return resultsArray;
        }

        /* //TODO: this code is CORRECT for backup puposes
        private List<String> getDataFromApi(String spreadsheetId) throws IOException {
            //spreadsheetId = "16ujt55GOJVgcgxox1NrGT_iKf2LIVlEU7ywxtzOtngY";
            //spreadsheetId = selectedFileId;

            String range = "A2:J";

            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values().get(spreadsheetId, range).execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                results.add("Name, etc");
                for (List row : values) {
                    results.add(row.get(0) + ", "
                            + row.get(1) + ", "
                            + row.get(2) + ", "
                            + row.get(3) + ", "
                            + row.get(4) + ", "
                            + row.get(5) + ", "
                            + row.get(6) + ", "
                            + row.get(7) + ", "
                            + row.get(8) + ", "
                            + row.get(9) + ", "
                    );
                }
            }
            return results;
        } */

        //TODO: results is actually an array of Client objects, which will be passed to
        //TODO: and displayed in the OrdersListActivity
        /*
        private List<Client> getDataFromApi() throws IOException {

            String spreadsheetId = "16ujt55GOJVgcgxox1NrGT_iKf2LIVlEU7ywxtzOtngY";
            String range = "Class Data!A2:J";

            List<Client> results = new ArrayList<Client>();
            ValueRange response = this.mService.spreadsheets().values().get(spreadsheetId, range).execute();
            List<List<Client>> values = response.getValues();
            if (values != null) {
                results.add("Name, Phone, Location, ID, Quantity, $, $Adj, Urgency, Value, Status");
                for (List row : values) {
                    results.add(row.get(0) + ", "
                            + row.get(1) + ", "
                            + row.get(2) + ", "
                            + row.get(3) + ", "
                            + row.get(4) + ", "
                    );
                }
            }
            return results;
        }
         */

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            //mProgress.show();
        }

        @Override
        protected void onPostExecute(ClientArray output) {
            //mProgress.hide();
            if (output == null || output.getClientArray().size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                //TODO: OK SO FAR!!!!!!

                //TODO: here we have a clientArray to send to OrdersListActivity
                ClientArrayParcel clientsListParcel = new ClientArrayParcel(output);

                Log.d("PARCELABLE ", "SIZE " + clientsListParcel.getBasicClientArray().size());

                for (int k = 0; k<clientsListParcel.getBasicClientArray().size();k++) {
                    Log.d(String.valueOf(k) + " ", "SHOWING CLIENTS NAME " + clientsListParcel.getClientArray().get(k).getClientName()); }

                Intent goToOrdersList = new Intent(GSheetsActivity.this, OrderListActivity.class);
                goToOrdersList.putExtra("list of clients", clientsListParcel);

                //startActivity(goToOrdersList);
            }
        }

        /* //TODO: this code is CORRECT for backup puposes
        @Override
        protected void onPostExecute(List<String> output) {
            //mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Google Sheets API:");
                mOutputText.setText(TextUtils.join("\n", output));
            }
        } */

        @Override
        protected void onCancelled() {
            //mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GSheetsActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }
}