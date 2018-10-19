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
/*
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
import com.google.api.services.sheets.v4.model.ValueRange; */

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import longmoneyoffshore.dlrtime.OrderListActivity;
import longmoneyoffshore.dlrtime.utils.CompositeType;
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;
import longmoneyoffshore.dlrtime.utils.TransportClients.ClientArray;

import static longmoneyoffshore.dlrtime.utils.GlobalValues.APPEND_FIELD;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.DELETE_FIELD;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.UPDATE_FIELD;

public class PassDataBackToSheets extends AsyncTask<String, Void, Void> {

    final static public String INPUT_OPTION_RAW = "RAW";
    final static public String INPUT_OPTION_USER = "USER_ENTERED";

    private Sheets service;
    private int position;
    private int requestCode;

    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    ClientArray feedbackClients;
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
        ValueRange feedback;

        //range = "A"+ (position+2) + ":J" + (position+2);

        List<List<Object>> values = feedbackClient.returnClientAsObjectList();

        switch (requestCode) {
            case UPDATE_FIELD:
                //Log.d("UPDATE", "IN UPDATE BRANCH");
                range = "A"+ (position+2) + ":J" + (position+2);
                //UpdateValuesResponse result1 = updateValues(spreadsheetId, range, INPUT_OPTION_RAW, values);
                UpdateValuesResponse result1 = updateValues(spreadsheetId, range, INPUT_OPTION_USER, values);
                //Log.d("UPDATE", result1.toString());
                break;

            case APPEND_FIELD:
                //Log.d("APPEND", "IN APPEND BRANCH");
                //TODO: isn't it supposed to be position+2?????

                range = "A"+ (position) + ":J" + (position);
                AppendValuesResponse result2 = appendValues(spreadsheetId, range, INPUT_OPTION_USER, values);
                //Log.d("APPEND", result2.toString());
                break;
            case DELETE_FIELD:
                range = "A"+ (position+2) + ":J" + (position+2);
                //Log.d("DELETING", "DELETING " + originalClient.getClientName() + " RANGE " + range);
                values = originalClient.returnClientAsObjectList();
                ClearValuesResponse result3 = deleteRow(spreadsheetId, range, values);

                break;
        }
        //Log.d("PREV UPDATED", result.toString());
    }



    public UpdateValuesResponse updateValues(String spreadsheetId, String range, String valueInputOption, List<List<Object>> _values)
            throws IOException {

        Sheets service = this.service;

        List<List<Object>> values = Arrays.asList(Arrays.asList());
        values = _values;

        /*
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
        Log.d("TESTING VALS", "VALUES TO BE WRITTEN: 9 " + values.get(0).get(9)); */

        //implementation #1
        try {

            ValueRange body = new ValueRange().setValues(values);
            //UpdateValuesResponse result = service.spreadsheets().values().update(spreadsheetId, range, body).setValueInputOption(valueInputOption).execute();
            UpdateValuesResponse result = this.mService.spreadsheets().values().update(spreadsheetId, range, body)
                    .setValueInputOption(valueInputOption).execute();

            return result;

        } catch (Exception e) {
            Log.d("EXCEPTIONALLY CAUGHT", e.getLocalizedMessage());
            return null;
        }
    }

    public AppendValuesResponse appendValues(String spreadsheetId, String range, String valueInputOption, List<List<Object>> _values)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_append_values]
        List<List<Object>> values = Arrays.asList(Arrays.asList());
        values = _values;

        /*
        Log.d("TESTING VALS", "RANGE TO BE APPENDED: " + range + " FILE ID: "+ spreadsheetId);
        Log.d("TESTING VALS", "VALUES TO BE APPENDED: 0 " + values.get(0).get(0));
        Log.d("TESTING VALS", "VALUES TO BE APPENDED: 1 " + values.get(0).get(1));
        Log.d("TESTING VALS", "VALUES TO BE APPENDED: 2 " + values.get(0).get(2));
        Log.d("TESTING VALS", "VALUES TO BE APPENDED: 3 " + values.get(0).get(3));
        Log.d("TESTING VALS", "VALUES TO BE APPENDED: 4 " + values.get(0).get(4));
        Log.d("TESTING VALS", "VALUES TO BE APPENDED: 5 " + values.get(0).get(5));
        Log.d("TESTING VALS", "VALUES TO BE APPENDED: 6 " + values.get(0).get(6));
        Log.d("TESTING VALS", "VALUES TO BE APPENDED: 7 " + values.get(0).get(7));
        Log.d("TESTING VALS", "VALUES TO BE APPENDED: 8 " + values.get(0).get(8));
        Log.d("TESTING VALS", "VALUES TO BE APPENDED: 9 " + values.get(0).get(9)); */


        ValueRange body = new ValueRange().setValues(values);
        AppendValuesResponse result = this.mService.spreadsheets().values().append(spreadsheetId, range, body)
                        .setValueInputOption(valueInputOption).execute();

        return result;
    }

    public ClearValuesResponse deleteRow (String spreadsheetId, String range, List<List<Object>> _values)
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

    public static Sheets createSheetsService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // TODO: Change placeholder below to generate authentication credentials. See
        // https://developers.google.com/sheets/quickstart/java#step_3_set_up_the_sample
        //
        // Authorize using one of the following scopes:
        //   "https://www.googleapis.com/auth/drive"
        //   "https://www.googleapis.com/auth/drive.file"
        //   "https://www.googleapis.com/auth/spreadsheets"
        GoogleCredential credential = null;

        return new Sheets.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Google-SheetsSample/0.1")
                .build();
    }

}
