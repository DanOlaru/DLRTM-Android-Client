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

public class PassDataBackToSheets extends AsyncTask<String, Void, Void> {
    private Sheets service;

    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    ClientArray feedbackClients;
    Client feedbackClient, originalClient;

    public PassDataBackToSheets(GoogleAccountCredential credential, Client feedbackOrder, Client savedClickedOrder, int feedbackOrderPosition) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(transport, jsonFactory, credential)
                .setApplicationName("GSheets API Android").build();

        originalClient = new Client (savedClickedOrder);
        feedbackClient = feedbackOrder;
        Log.d ("FEEDBACK CLIENT"," "); feedbackOrder.showClient();
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
            String range = "A2:J";
            ValueRange feedback;

            Client iterationClient = new Client();
            String desiredClientReferenceCode = feedbackClient.getClientReferenceCode();

            String operationTitle = "title";
            //operationTitle =
            //BatchUpdateSpreadsheetResponse();

            updateSheetEntry (spreadsheetId, operationTitle, originalClient, feedbackClient);

            //try {
            //    this.mService.spreadsheets().values().update(spreadsheetId, range, feedback);
            //} catch (IOException e) {Log.e("EXCEPTION", e.getMessage());}
        }

    public void updateSheetEntry (String spreadsheetId, String title, Client find, Client replacement) {
        BatchUpdateSpreadsheetResponse responseField;
        try {
            responseField = batchUpdate(spreadsheetId, title, find.getClientName(), replacement.getClientName());
            responseField = batchUpdate(spreadsheetId, title, find.getClientPhoneNo(), replacement.getClientPhoneNo());
            responseField = batchUpdate(spreadsheetId, title, find.getClientLocation(), replacement.getClientLocation());
            responseField = batchUpdate(spreadsheetId, title, find.getClientProductID(), replacement.getClientProductID());
            responseField = batchUpdate(spreadsheetId, title, String.valueOf(find.getClientQuantity()), String.valueOf(replacement.getClientQuantity()));
            responseField = batchUpdate(spreadsheetId, title, String.valueOf(find.getClientPrice()), String.valueOf(replacement.getClientPrice()));
            responseField = batchUpdate(spreadsheetId, title, String.valueOf(find.getClientPriceAdjust()), String.valueOf(replacement.getClientPriceAdjust()));
            responseField = batchUpdate(spreadsheetId, title, String.valueOf(find.getClientUrgency()), String.valueOf(replacement.getClientUrgency()));
            responseField = batchUpdate(spreadsheetId, title, String.valueOf(find.getClientValue()), String.valueOf(replacement.getClientValue()));
            responseField = batchUpdate(spreadsheetId, title, find.getClientStatus(), replacement.getClientStatus());
            //
            responseField = batchUpdate(spreadsheetId, title, find.getAnonymizerPrefix(), replacement.getAnonymizerPrefix());
            responseField = batchUpdate(spreadsheetId, title, find.getClientReferenceCode(), replacement.getClientReferenceCode());
        } catch (IOException e) {
            Log.e("REPLCEMENT_EXCEPTION", e.getMessage());
        }
    }


    public BatchUpdateSpreadsheetResponse batchUpdate(String spreadsheetId, String title, String find, String replacement)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_batch_update]
        List<Request> requests = new ArrayList<>();
        // Change the spreadsheet's title.
        requests.add(new Request().setUpdateSpreadsheetProperties(new UpdateSpreadsheetPropertiesRequest()
                        .setProperties(new SpreadsheetProperties().setTitle(title)).setFields("title")));
        // Find and replace text.
        requests.add(new Request().setFindReplace(new FindReplaceRequest()
                        .setFind(find).setReplacement(replacement).setAllSheets(true)));

        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
        FindReplaceResponse findReplaceResponse = response.getReplies().get(1).getFindReplace();
        System.out.printf("%d replacements made.", findReplaceResponse.getOccurrencesChanged());

        return response;
    }

    public UpdateValuesResponse updateValues(String spreadsheetId, String range, String valueInputOption, List<List<Object>> _values)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_update_values]
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
        UpdateValuesResponse result =
                service.spreadsheets().values().update(spreadsheetId, range, body)
                        .setValueInputOption(valueInputOption)
                        .execute();
        System.out.printf("%d cells updated.", result.getUpdatedCells());
        // [END sheets_update_values]
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
