package longmoneyoffshore.dlrtime.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//Dan

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import longmoneyoffshore.dlrtime.utils.AsyncResult;

import static longmoneyoffshore.dlrtime.utils.GlobalValues.THE_FIRST_DOWNLOAD_SHEET;

public class DownloadAsyncTask extends AsyncTask<String, Void, String> {

    AsyncResult callback;
    public DownloadAsyncTask(AsyncResult callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url.
        try {
            return downloadUrl(urls[0]);
        } catch (IOException e) {
            return "Unable to download the requested page.";
        }
    }

    //TODO: why is this private here? How can I access it from a different class?

    private String downloadUrl(String urlString) throws IOException {

        InputStream is = null;
        try {
            URL url = new URL(urlString);

            Log.d("DOWNLOADURL", "URL TO CONNECT: " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();
            is = conn.getInputStream();

            String contentAsString = convertStreamToString(is);
            //Log.d("DOWNLOAD_URL", "CONTENT_AS_STRING: " + contentAsString);

            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    private String convertStreamToString(InputStream is) {


        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        //StringBuilder line = new StringBuilder();

        try {
            //while ((line = reader.readLine()) != null) {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                Log.d("CONVERTINPUTSTREAM", "LINE: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Log.d("CONVERTINPUTSTREAM", "INPUTSTREAMTO STRING: " + sb);
        return sb.toString();
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {

        //Log.d("JSON", "INPUT STRING : " + result);
        // remove the unnecessary parts from the response and construct a JSON
        int start = result.indexOf("{", result.indexOf("{") + 1);
        int end = result.lastIndexOf("}");
        String jsonResponse = result.substring(start, end);

        try {
            JSONObject table = new JSONObject(jsonResponse);
            //Log.d("POSTEXECUTE", "TRUNCATED JSON: " + jsonResponse.toString());
            //Log.d("Table content JSON"," Table: " +table.toString());
            callback.onResult(table);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class handleFileDownload {
        public String DOWNLOAD_TEMPLATE = "https://spreadsheets.google.com/tq?key=";
        public String FILE_DOWNLOAD_TEMPLATE_START = "https://docs.google.com/spreadsheets/d/";
        public String FILE_DOWNLOAD_TEMPLATE_CODA = "/edit#gid=1268876841";
        public String SHEET_DOWNLOAD_PRE = "https://spreadsheets.google.com/tq?key=";

        /*
        protected void getXLS () throws GeneralSecurityException {

            List<String> ranges = new ArrayList<>(); // TODO: Update placeholder value.
            //ranges.add(line);

            Sheets sheetsService = createSheetsService();
            Sheets.Spreadsheets.Get request = sheetsService.spreadsheets().get(THE_FIRST_DOWNLOAD_SHEET);
            request.setRanges(ranges);
            //request.setIncludeGridData(includeGridData);

            Spreadsheet response = request.execute();
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
            //   "https://www.googleapis.com/auth/drive.readonly"
            //   "https://www.googleapis.com/auth/spreadsheets"
            //   "https://www.googleapis.com/auth/spreadsheets.readonly"
            GoogleCredential credential = null;

            return new Sheets.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName("Google-SheetsSample/0.1")
                    .build();
        }
        } */

    }

}