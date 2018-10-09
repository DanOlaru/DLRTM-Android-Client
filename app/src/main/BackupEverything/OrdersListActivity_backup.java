//test

package longmoneyoffshore.dlrtime;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import longmoneyoffshore.dlrtime.utils.AsyncResult;
import longmoneyoffshore.dlrtime.utils.Client;
import longmoneyoffshore.dlrtime.utils.DownloadAsyncTask;
import longmoneyoffshore.dlrtime.utils.ClientAdapter;

public class OrderListActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "HttpExample";
    ArrayList<Client> clients = new ArrayList<Client>();
    ListView listview;
    Button btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set-out UI
        setContentView(R.layout.activity_order_list);
        listview = (ListView) findViewById(R.id.listview);
        btnDownload = (Button) findViewById(R.id.btnDownload);

        // Internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            btnDownload.setEnabled(true);
        } else {
            btnDownload.setEnabled(false);
        }



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public void buttonClickHandler(View view) {

        // Start to retrieve data on another thread (therefore as a background activity)
        new DownloadAsyncTask(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {
                processJson(object); // Feeds with the retrieved data
            }
        }).execute("https://spreadsheets.google.com/tq?key=16ujt55GOJVgcgxox1NrGT_iKf2LIVlEU7ywxtzOtngY");



    }

    private void processJson(JSONObject object) {

        try {
            JSONArray rows = object.getJSONArray("rows");

            Log.d("Rows lENGTH","lENGTH: " + rows.length());

            for (int r = 0; r < rows.length(); ++r)
            {
                JSONObject row = rows.getJSONObject(r);
                JSONArray columns = row.getJSONArray("c");

                Log.d("Rows Json","Content: " + columns.toString());

                String name = columns.getJSONObject(0).getString("v");

                Log.d("Fist value","1  " + name);

                String phone = columns.getJSONObject(1).getString("v");
                String location = columns.getJSONObject(2).getString("v");
                String productId = columns.getJSONObject(3).getString("v");
                int quantity = columns.getJSONObject(4).getInt("v");
                int price = columns.getJSONObject(5).getInt("v");
                int priceAdjust = columns.getJSONObject(6).getInt("v");
                int urgency = columns.getJSONObject(7).getInt("v");
                int value = columns.getJSONObject(8).getInt("v");
                String status = columns.getJSONObject(9).getString("v");

                Client client = new Client(name, phone, location, productId, quantity, price, priceAdjust, urgency, value, status);

                Log.d("Client", "Client " + client.toString());

                clients.add(client);
            }

            final ClientAdapter adapter = new ClientAdapter(this, R.layout.client_item, clients);
            listview.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
