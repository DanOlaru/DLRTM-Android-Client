
package longmoneyoffshore.dlrtime.utils.GSheetsApiOperations;

import android.content.Context;
//import android.net.wifi.hotspot2.pps.Credential;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class SpreadSheetUpdate {

    //private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };
    //private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };
    private static final String[] SCOPES = { SheetsScopes.DRIVE_FILE };

    //public SpreadSheetUpdate (String passedSheetID, String passedRange, List<List<Object>>  passedValues, Credential passedCredential, Context passedContext) throws IOException, GeneralSecurityException {
    public SpreadSheetUpdate (String passedSheetID, String passedRange, List<List<Object>>  passedValues,
                              Credential passedCredential, Context passedContext) throws IOException, GeneralSecurityException {
        // The ID of the spreadsheet to update.
        String spreadsheetId = passedSheetID; // TODO: Update placeholder value.

        // The A1 notation of the values to update.
        String range = passedRange; // TODO: Update placeholder value.

        // How the input data should be interpreted.
        String valueInputOption = "USER_ENTERED"; // TODO: .
        //String valueInputOption = "RAW";

        // TODO: Assign values to desired fields of `requestBody`. All existing
        // fields will be replaced:
        ValueRange requestBody = new ValueRange();
        requestBody.setValues(passedValues);

        Log.e("INFO", "INSIDE SPREADSHEETUPDATE.JAVA");

        Sheets sheetsService = createSheetsService(passedCredential, passedContext);
        //Sheets sheetsService = createSheetsService();


        Sheets.Spreadsheets.Values.Update request = sheetsService.spreadsheets().values().update(spreadsheetId, range, requestBody);
        request.setValueInputOption(valueInputOption);
        request.execute();

        //UpdateValuesResponse response = request.execute();
        //ValueRange response = sheetsService.spreadsheets().values().update(spreadsheetId, range, requestBody).execute();

    }

    public static Sheets createSheetsService(Credential passedCredential, Context passedContext) throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // TODO: Change placeholder below to generate authentication credentials. See
        // https://developers.google.com/sheets/quickstart/java#step_3_set_up_the_sample
        //
        // Authorize using one of the following scopes:
        //   "https://www.googleapis.com/auth/drive"
        //   "https://www.googleapis.com/auth/drive.file"
        //   "https://www.googleapis.com/auth/spreadsheets"
        //GoogleCredential credential = passedCredential;
        //GoogleCredential credential = passedCredential;
        //GoogleCredential credential = null;
        Credential credential = passedCredential;

        //GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(passedContext, Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());

        return new Sheets.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Google-SheetsSample/0.1")
                .build();
    }
}

