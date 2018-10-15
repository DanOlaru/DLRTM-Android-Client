package longmoneyoffshore.dlrtime.utils.GSheetsApiOperations;

import android.os.AsyncTask;
import android.util.Log;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.FindReplaceRequest;
import com.google.api.services.sheets.v4.model.FindReplaceResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import longmoneyoffshore.dlrtime.utils.CompositeType;
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientArray;

import static longmoneyoffshore.dlrtime.utils.GlobalValues.APPEND_FIELD;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.UPDATE_FIELD;

public class PassDataBackToSheets extends AsyncTask<String, Void, Void> {

    private Sheets service;
    private int position;
    private int requestCode;

    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    ClientArray feedbackClients;
    Client feedbackClient, originalClient;

    public PassDataBackToSheets(GoogleAccountCredential credential, Client feedbackOrder, Client savedClickedOrder, int feedbackOrderPosition, int reqCode) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(transport, jsonFactory, credential)
                .setApplicationName("GSheets API Android").build();

        //originalClient = new Client (savedClickedOrder);
        feedbackClient = feedbackOrder;
        position = feedbackOrderPosition;
        requestCode = reqCode;
        //Log.d ("FEEDBACK CLIENT"," "); feedbackClient.showClient();
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
            ValueRange feedback;

            range = "A"+ (position+2) + ":J" + (position+2); //!!!!

            List<List<Object>> values = feedbackClient.returnClientAsObjectList();

            switch (requestCode) {
                case UPDATE_FIELD:
                    Log.d("PREV UPDATED", "IN UPDATE BRANCH");
                    UpdateValuesResponse result1 = updateValues(spreadsheetId, range, "RAW", values);
                    Log.d("PREV UPDATED", result1.toString());
                    break;

                case APPEND_FIELD:
                    Log.d("PREV APPENDED", "IN APPEND BRANCH");
                    AppendValuesResponse result2 = appendValues(spreadsheetId, range, "RAW", values);
                    Log.d("PREV APPENDED", result2.toString());
                    break;
            }
            //Log.d("PREV UPDATED", result.toString());
        }

    public UpdateValuesResponse updateValues(String spreadsheetId, String range, String valueInputOption, List<List<Object>> _values)
            throws IOException {
        Sheets service = this.service;

        // [START sheets_update_values]
        List<List<Object>> values = Arrays.asList(Arrays.asList());

        values = _values;

        //test to see if my list of values is good
        Log.d("TESTING VALS", "RANGE TO BE WRITTEN: " + range + " FILE ID: "+ spreadsheetId);
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 0 " + values.get(0).get(0));
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 1 " + values.get(0).get(1));
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 2 " + values.get(0).get(2));
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 3 " + values.get(0).get(3));
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 4 " + values.get(0).get(4));
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 5 " + values.get(0).get(5));
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 6 " + values.get(0).get(6));
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 7 " + values.get(0).get(7));
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 8 " + values.get(0).get(8));
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 9 " + values.get(0).get(9));
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 10 " + values.get(0).get(10));
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 11 " + values.get(0).get(11));

        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse result = service.spreadsheets().values().update(spreadsheetId, range, body)
                        .setValueInputOption(valueInputOption).execute();
        Log.d("CELLS UPDATED", "DKJHFDKJH");
        Log.d("CELLS UPDATED", result.toString());
        return result;
    }


    public AppendValuesResponse appendValues(String spreadsheetId, String range, String valueInputOption, List<List<Object>> _values)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_append_values]
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        // Cell values ...
                )
                // Additional rows ...
        );
        // [START_EXCLUDE silent]
        values = _values;
        // [END_EXCLUDE]
        ValueRange body = new ValueRange()
                .setValues(values);
        AppendValuesResponse result =
                service.spreadsheets().values().append(spreadsheetId, range, body)
                        .setValueInputOption(valueInputOption)
                        .execute();
        System.out.printf("%d cells appended.", result.getUpdates().getUpdatedCells());
        // [END sheets_append_values]
        return result;
    }

    public BatchUpdateValuesResponse batchUpdateValues(String spreadsheetId, String range, String valueInputOption, List<List<Object>> _values)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_batch_update_values]
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        // Cell values ...
                )
                // Additional rows ...
        );
        // [START_EXCLUDE silent]
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
        System.out.printf("%d cells updated.", result.getTotalUpdatedCells());
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
