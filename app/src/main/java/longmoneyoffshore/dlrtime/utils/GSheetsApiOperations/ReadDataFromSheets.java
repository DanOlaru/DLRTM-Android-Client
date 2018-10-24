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
import longmoneyoffshore.dlrtime.R;
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientAdapter;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientArray;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.blankClient;

public class ReadDataFromSheets extends AsyncTask<String, Void, ClientArray> {

    //internal data
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

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
        int rowIndex = -1;

        Client iterationClient = new Client();

        String range = "A2:J";

        ValueRange response = this.mService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();
        if (values != null) {
            String val = "";
            for (List row : values) {
                iterationClient = new Client(blankClient);

                try {
                val = row.get(0).toString();
                if (!(val.isEmpty())) {
                    iterationClient.setClientName(val);
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                } } catch (Exception e) {Log.e("EXCEPTION_NAME", e.getLocalizedMessage());}

                try {
                val = row.get(1).toString();
                if (!(val.isEmpty())) {
                    iterationClient.setClientPhoneNo(val);
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                } } catch (Exception e) {Log.e("EXCEPTION_PHONE", e.getLocalizedMessage());}

                try {
                val = row.get(2).toString();
                if (!(val.isEmpty())) {
                    iterationClient.setClientLocation(val);
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                } }catch (Exception e) {Log.e("EXCEPTION_LOCATION", e.getLocalizedMessage());}

                try {
                val = row.get(3).toString();
                if (!(val.isEmpty())) {
                    iterationClient.setClientProductID(val);
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                }}catch (Exception e) {Log.e("EXCEPTION_PRODUCT_ID", e.getLocalizedMessage());}

                try {
                val = row.get(4).toString();
                if (!(val.isEmpty())) {
                    iterationClient.setClientQuantity(Float.parseFloat(val));
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                } }catch (Exception e) {Log.e("EXCEPTION_QUANTITY", e.getLocalizedMessage());}

                try {
                val = row.get(5).toString();
                if (!(val.isEmpty())) {
                    iterationClient.setClientPrice(Float.parseFloat(val));
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                }}catch (Exception e) {Log.e("EXCEPTION_PRICE", e.getLocalizedMessage());}

                try {
                val = row.get(6).toString();
                if (!(val.isEmpty())) {
                    iterationClient.setClientPriceAdjust(Float.parseFloat(val));
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                }} catch (Exception e) {Log.e("EXCEPTION_PRICE_ADJ", e.getLocalizedMessage());}

                try {
                val = row.get(7).toString();
                if (!(val.isEmpty())) {
                    iterationClient.setClientUrgency(Float.parseFloat(val));
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                } }catch (Exception e) {Log.e("EXCEPTION_URGENCY", e.getLocalizedMessage());}

                try {
                val = row.get(8).toString();
                if (!(val.isEmpty())) {
                    iterationClient.setClientValue(Float.parseFloat(val));
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                }}catch (Exception e) {Log.e("EXCEPTION_CL_VALUE", e.getLocalizedMessage());}

                try {
                val = row.get(9).toString();
                if (!(val.isEmpty())) {
                    iterationClient.setClientStatus(val);
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                }}catch (Exception e) {Log.e("EXCEPTION_STATUS", e.getLocalizedMessage());}

                iterationClient.setClientReferenceCode(++rowIndex);

                try{
                if (!iterationClient.getRevision().equals("0")) {
                    iterationClient.formatPhoneNo();
                    iterationClient.setClientReferenceCode(rowIndex);

                    resultsArray.getClientArray().add(new Client(iterationClient));
                } else {
                    Log.d ("BLANK ROW", " WE HAVE HIT A BLANK ROW # " +  rowIndex);
                } }catch (Exception e) {Log.e("WRITE CLIENT EXCEPTION", e.getLocalizedMessage());}

            }
        } else {
            Log.d ("READING_CUSTOMER", " WE HAVE NULL VALUES ");
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

            final ClientAdapter adapter = new ClientAdapter(thisActivity, R.layout.client_item, new ArrayList<Client>(clients.getClientArray()));

            listview.setAdapter(adapter);
        }
    }

    @Override
    protected void onCancelled() {
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                thisActivity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(), REQUEST_AUTHORIZATION);
            }
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(thisActivity, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

}