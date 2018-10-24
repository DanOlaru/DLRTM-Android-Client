package longmoneyoffshore.dlrtime.utils.GSheetsApiOperations;

import android.os.AsyncTask;
import android.util.Log;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientArray;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.*;

public class PassDataBackToSheets extends AsyncTask<String, Void, Void> {

    final static public String INPUT_OPTION_RAW = "RAW";
    final static public String INPUT_OPTION_USER = "USER_ENTERED";

    private Sheets service;
    private int position;
    private int requestCode;

    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    Client feedbackClient, originalClient;

    public PassDataBackToSheets(GoogleAccountCredential credential, Client feedbackOrder, Client savedClickedOrder, int orderPosition, int reqCode) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(transport, jsonFactory, credential)
                .setApplicationName("GSheets API Android").build();

        //originalClient = new Client (savedClickedOrder);
        feedbackClient = feedbackOrder;
        originalClient = savedClickedOrder;
        position = orderPosition;
        requestCode = reqCode;
        //Log.d ("FEEDBACK CLIENT"," "); feedbackClient.showClient();

        /*
        try {
            service = createSheetsService();
        } catch (GeneralSecurityException e) {
            Log.d("GENSEC EXCEPTION", e.getLocalizedMessage());
        } catch (IOException e) {
            Log.d("IO EXCEPTION", e.getLocalizedMessage());
        } */
    }
    public PassDataBackToSheets(Sheets service) {
        this.service = service;
    }

    protected Void doInBackground(String...params) {
        try {
            writeChangesToApi(params[0]);
            return null;

        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    private void writeChangesToApi (String spreadsheetId) throws IOException {
        String range; // = "A2:J";
        List<List<Object>> values = feedbackClient.returnClientAsObjectList();

        switch (requestCode) {
            case UPDATE_FIELD:
                range = "A"+ (position+2) + ":J" + (position+2);
                UpdateValuesResponse result1 = updateValues(spreadsheetId, range, INPUT_OPTION_USER, values);
                break;

            case APPEND_FIELD:
                range = "A"+ (position+2) + ":J" + (position+2);
                AppendValuesResponse result2 = appendValues(spreadsheetId, range, INPUT_OPTION_USER, values);
                break;
            case DELETE_FIELD:
                range = "A"+ (position+2) + ":J" + (position+2);
                values = originalClient.returnClientAsObjectList();
                ClearValuesResponse result3 = clearRow(spreadsheetId, range, values);
                break;
        }
    }



    public UpdateValuesResponse updateValues(String spreadsheetId, String range, String valueInputOption, List<List<Object>> _values)
            throws IOException {

        Sheets service = this.service;

        List<List<Object>> values = Arrays.asList(Arrays.asList());
        values = _values;

        //implementation #1
        try {

            ValueRange body = new ValueRange().setValues(values);
            //UpdateValuesResponse result = service.spreadsheets().values().update(spreadsheetId, range, body).setValueInputOption(valueInputOption).execute();
            UpdateValuesResponse result = this.mService.spreadsheets().values().update(spreadsheetId, range, body)
                    .setValueInputOption(valueInputOption).execute();

            return result;

        } catch (Exception e) {
            Log.d("EX CAUGHT", e.getLocalizedMessage());
            return null;
        }
    }

    public AppendValuesResponse appendValues(String spreadsheetId, String range, String valueInputOption, List<List<Object>> _values)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_append_values]
        List<List<Object>> values = Arrays.asList(Arrays.asList());
        values = _values;

        ValueRange body = new ValueRange().setValues(values);
        AppendValuesResponse result = this.mService.spreadsheets().values().append(spreadsheetId, range, body)
                        .setValueInputOption(valueInputOption).execute();

        return result;
    }

    public ClearValuesResponse clearRow (String spreadsheetId, String range, List<List<Object>> _values)
            throws IOException {

        Sheets service = this.service;
        List<List<Object>> values = Arrays.asList(Arrays.asList());
        values = _values;

        //Log.d("DELETING", "DELETING " + values.get(0) + " RANGE " + range);

        ClearValuesRequest requestBody = new ClearValuesRequest();
        try {
            //Sheets sheetsService = createSheetsService();
            Sheets.Spreadsheets.Values.Clear request = this.mService.spreadsheets().values().clear(spreadsheetId, range, requestBody);

            ClearValuesResponse response = request.execute();

        } catch (Exception e) {
            Log.d("EXCEPTION CAUGHT", e.getLocalizedMessage());
        }

        return null;
    }


    //TODO: currently unused

    public DeleteRangeRequest deleteRow (String spreadsheetId, String range)
            throws IOException {

        Sheets service = this.service;
        //TODO: implement the ability to delete a row
        //Log.d("DELETING", "DELETING " + values.get(0) + " RANGE " + range);

        DeleteRangeRequest requestBody = new DeleteRangeRequest();
        try {

        } catch (Exception e) {
            Log.d("EXCEPTION CAUGHT", e.getLocalizedMessage());
        }

        return null;
    }

    public BatchUpdateValuesResponse batchUpdateValues(String spreadsheetId, String range, String valueInputOption, List<List<Object>> _values)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_batch_update_values]
        List<List<Object>> values = Arrays.asList(Arrays.asList());
        values = _values;
        // [END_EXCLUDE]
        List<ValueRange> data = new ArrayList<ValueRange>();
        data.add(new ValueRange()
                .setRange(range)
                .setValues(values));
        // Additional ranges to update ...

        BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                .setValueInputOption(valueInputOption)
                .setData(data);
        BatchUpdateValuesResponse result = service.spreadsheets().values().batchUpdate(spreadsheetId, body).execute();
        //System.out.printf("%d cells updated.", result.getTotalUpdatedCells());
        return result;
    }

    public String create(String title) throws IOException {
        Sheets service = this.service;
        // [START sheets_create]
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle(title));
        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();
        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
        // [END sheets_create]
        return spreadsheet.getSpreadsheetId();
    }

}
