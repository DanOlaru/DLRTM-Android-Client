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

import org.mortbay.util.IO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import longmoneyoffshore.dlrtime.OrderListActivity;
import longmoneyoffshore.dlrtime.R;
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientAdapter;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientArray;

import static longmoneyoffshore.dlrtime.utils.GlobalValues.blankClient;

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

        //Log.i("INSIDE READFILE", "FILE ID IS " + spreadsheetId);

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

                //iterationClient.setClientName(row.get(0).toString());
                try {
                val = row.get(0).toString();
                if (!(val.isEmpty())) {
                    //Log.i("NAME", "VAL IS " + val);
                    iterationClient.setClientName(val);
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                } else {
                    Log.i("EMPTY", "VAL IS EMPTY ");
                } } catch (Exception e) {Log.e("EXCEPTION_NAME", e.getLocalizedMessage());}

                //iterationClient.setClientPhoneNo(row.get(1).toString());
                try {
                val = row.get(1).toString();
                if (!(val.isEmpty())) {
                    //Log.i("PHONE", "VAL IS " + val);
                    iterationClient.setClientPhoneNo(val);
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                } } catch (Exception e) {Log.e("EXCEPTION_PHONE", e.getLocalizedMessage());}

                //iterationClient.setClientLocation(row.get(2).toString());
                try {
                val = row.get(2).toString();
                if (!(val.isEmpty())) {
                    //Log.i("LOCATION", "VAL IS " + val);
                    iterationClient.setClientLocation(val);
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                } }catch (Exception e) {Log.e("EXCEPTION_LOCATION", e.getLocalizedMessage());}

                //iterationClient.setClientProductID(row.get(3).toString());
                try {
                val = row.get(3).toString();
                if (!(val.isEmpty())) {
                    //Log.i("PROD ID", "VAL IS " + val);
                    iterationClient.setClientProductID(val);
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                }}catch (Exception e) {Log.e("EXCEPTION_PRODUCT_ID", e.getLocalizedMessage());}

                //iterationClient.setClientQuantity(Float.parseFloat(row.get(4).toString()));
                try {
                val = row.get(4).toString();
                if (!(val.isEmpty())) {
                    //Log.i("QUANTITY", "VAL IS " + val);
                    iterationClient.setClientQuantity(Float.parseFloat(val));
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                } }catch (Exception e) {Log.e("EXCEPTION_QUANTITY", e.getLocalizedMessage());}

                //iterationClient.setClientPrice(Float.parseFloat(row.get(5).toString()));
                try {
                val = row.get(5).toString();
                if (!(val.isEmpty())) {
                    //Log.i("PRICE", "VAL IS " + val);
                    iterationClient.setClientPrice(Float.parseFloat(val));
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                }}catch (Exception e) {Log.e("EXCEPTION_PRICE", e.getLocalizedMessage());}

                //iterationClient.setClientPriceAdjust(Float.parseFloat(row.get(6).toString()));
                try {
                val = row.get(6).toString();
                if (!(val.isEmpty())) {
                    //Log.i("PRICE ADJ", "VAL IS " + val);
                    iterationClient.setClientPriceAdjust(Float.parseFloat(val));
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                }} catch (Exception e) {Log.e("EXCEPTION_PRICE_ADJ", e.getLocalizedMessage());}

                //iterationClient.setClientUrgency(Float.parseFloat(row.get(7).toString()));
                try {
                val = row.get(7).toString();
                if (!(val.isEmpty())) {
                    //Log.i("URGENCY", "VAL IS " + val);
                    iterationClient.setClientUrgency(Float.parseFloat(val));
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                } }catch (Exception e) {Log.e("EXCEPTION_URGENCY", e.getLocalizedMessage());}

                //iterationClient.setClientValue(Float.parseFloat(row.get(8).toString()));
                try {
                val = row.get(8).toString();
                if (!(val.isEmpty())) {
                    //Log.i("CL VALUE", "VAL IS " + val);
                    iterationClient.setClientValue(Float.parseFloat(val));
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                }}catch (Exception e) {Log.e("EXCEPTION_CL_VALUE", e.getLocalizedMessage());}

                //iterationClient.setClientStatus(row.get(9).toString());
                try {
                val = row.get(9).toString();
                if (!(val.isEmpty())) {
                    //Log.i("STATUS", "VAL IS " + val);
                    iterationClient.setClientStatus(val);
                    iterationClient.setRevision(iterationClient.getRevision()+1);
                }}catch (Exception e) {Log.e("EXCEPTION_STATUS", e.getLocalizedMessage());}

                //iterationClient.setClientReferenceCode(++counter);
                iterationClient.setClientReferenceCode(++rowIndex);

                //resultsArray.getClientArray().add(++counter ,new Client(iterationClient));
                try{
                if (!iterationClient.getRevision().equals("0")) {
                    iterationClient.formatPhoneNo();
                    iterationClient.setClientReferenceCode(rowIndex);

                    //Log.i("CLIENT", "AS IS ");
                    //iterationClient.showClient();

                    //resultsArray.getClientArray().add(++counter, new Client(iterationClient));
                    resultsArray.getClientArray().add(new Client(iterationClient));
                } else {
                    Log.d ("BLANK ROW", " WE HAVE HIT A BLANK ROW # " +  rowIndex);
                } }catch (Exception e) {Log.e("WRITE CLIENT EXCEPTION", e.getLocalizedMessage());}

                //Log.d ("READING_CUSTOMER", "# " +  counter);
                //iterationClient.showClient();
                //resultsArray.getClientArray().get(counter).showClient();
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