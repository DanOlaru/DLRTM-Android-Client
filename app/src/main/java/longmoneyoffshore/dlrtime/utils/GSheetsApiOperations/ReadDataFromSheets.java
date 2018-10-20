package longmoneyoffshore.dlrtime.utils.GSheetsApiOperations;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import longmoneyoffshore.dlrtime.OrderListActivity;
import longmoneyoffshore.dlrtime.R;
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientAdapter;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientArray;

public class ReadDataFromSheets extends AsyncTask<String, Void, ClientArray> {

    //internal data
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private ClientArray clients;
    private ArrayList<String> destinationLocations;
    private Activity thisActivity;
    private ListView listview;


    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;

    public ReadDataFromSheets(GoogleAccountCredential credential, ClientArray clientsRef, ArrayList<String> destinationLocationsRef,
                              Activity callingActivityRef, ListView listviewRef) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(transport, jsonFactory, credential)
                .setApplicationName("GSheets API Android").build();

        clients = clientsRef;
        destinationLocations = destinationLocationsRef;
        thisActivity = callingActivityRef;
        listview = listviewRef;
    }

    protected ClientArray doInBackground(String... params) {
        try {
            return getDataFromApi(params[0]);

        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

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

                iterationClient.setClientReferenceCode(++counter);

                //resultsArray.getClientArray().add(++counter ,new Client(iterationClient));
                iterationClient.formatPhoneNo();
                resultsArray.getClientArray().add(counter ,new Client(iterationClient));
                //Log.d ("READING_CUSTOMER", "# " +  counter);
                //resultsArray.getClientArray().get(counter).showClient();
            }
        }
        return resultsArray;
    }

    @Override
    protected void onPreExecute() {
        //mOutputText.setText("");
        //mProgress.show();
    }

    @Override
    protected void onPostExecute(ClientArray output) {
        if (output == null || output.getClientArray().size() == 0) {
        } else {
            clients.getClientArray().clear();
            clients.getClientArray().addAll(output.getClientArray());

            //Get an ArrayList of all the destinations where the user needs to go
            //destinationLocations.clear();
            //for (int j = 0; j<clients.getClientArray().size();j++) {destinationLocations.add(clients.getClientArray().get(j).getClientLocation());}

            //final ClientAdapter adapter = new ClientAdapter(thisActivity, R.layout.client_item, clients.getClientArray());
            final ClientAdapter adapter = new ClientAdapter(thisActivity, R.layout.client_item, new ArrayList<Client>(clients.getClientArray()));

            //listview.setAdapter(null);
            listview.setAdapter(adapter);
        }
    }

    @Override
    protected void onCancelled() {
        //mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                thisActivity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(), REQUEST_AUTHORIZATION);
            } else {
                //mOutputText.setText("The following error occurred:\n" + mLastError.getMessage());
            }
        } else {
            //mOutputText.setText("Request cancelled.");
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(thisActivity, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    //back-up from OrderListActivity



}