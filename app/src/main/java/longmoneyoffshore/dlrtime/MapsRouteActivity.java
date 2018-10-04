package longmoneyoffshore.dlrtime;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//extra
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.api.client.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import longmoneyoffshore.dlrtime.utils.AsyncResult;
import longmoneyoffshore.dlrtime.utils.DirectionsJSONParser;
import longmoneyoffshore.dlrtime.utils.HttpDataHandler;
import longmoneyoffshore.dlrtime.utils.MapDestinationsParcel;
import javax.net.ssl.HttpsURLConnection;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.APP_API_KEY;

public class MapsRouteActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList markerPoints= new ArrayList();
    MapDestinationsParcel locationsToSee;
    ArrayList<LatLng> locationsToSeeCoordinates = new ArrayList<LatLng>();

    private RequestQueue myRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_route);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //TODO: this parcel contains all the addresses that need to show up on the map
        Bundle getLocationsToSeeBundle = getIntent().getExtras();
        locationsToSee = (MapDestinationsParcel) getLocationsToSeeBundle.getParcelable("locations to go to");
        locationsToSeeCoordinates.clear();
        //TODO: transform locations to see, which is a list of String addresses, to a list of LatLng coordinates

        //dan
        //TODO: get coordinates for a ArrayList of addresses

        Log.d("NUMBEROFLOCSTOSEE", "there are " + locationsToSee.getMapDestinationLocations().size() + "locations");
        for (int j=0; j<locationsToSee.getMapDestinationLocations().size(); j++) {
            Log.d("ADDRESS", locationsToSee.getMapDestinationLocations().get(j).replace("& ","").replace(" ", "+"));
        }

        for (int j=0; j<locationsToSee.getMapDestinationLocations().size(); j++) {
            Log.d("LOCATION# ", "PROCESSING COORDS FOR LOCATION " + j);
            new GetCoordinates().execute(locationsToSee.getMapDestinationLocations().get(j).replace(" ", "+"));
            Log.d("LOCATION# ", "BACK FROM LOCATION " + j);
        }

        /*
        //testing
        Log.d("coordlist", "list of the coordinates gotten from those addresses" );
        for (int j=0; j<locationsToSee.getMapDestinationLocations().size(); j++) {
            Log.d("coord#", j + " " + locationsToSeeCoordinates.get(j).toString());
        }
        */

    }

    private class GetCoordinates extends AsyncTask <String, Void, String> {
        //ProgressDialog dialog = new ProgressDialog(MapsRouteActivity.this); //TODO: useless for now

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            //dialog.setMessage("Please wait....");
            //dialog.setCanceledOnTouchOutside(false);
            //dialog.show();
        }


        @Override
        protected String doInBackground (String ...strings) {
            String response = "";
            try {
                String address = strings[0];
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s", address);
                response = http.getHTTPData(url);
                return response;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute (String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString();
                String lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString();

                //TODO: save the coordinates retrieved for this address
                LatLng saveCoordForLocation = new LatLng(Float.valueOf(lat),Float.valueOf(lng));
                locationsToSeeCoordinates.add(saveCoordForLocation);

                //testing
                Log.d("COORDLIST", "list of the coordinates gotten from those addresses #" +  locationsToSeeCoordinates.size());
                for (int j=0; j<locationsToSeeCoordinates.size(); j++) {
                    Log.d("COORD#", j + " " + locationsToSeeCoordinates.get(j).toString());
                    Log.d("ADDRESS#", j + " " + locationsToSee.getMapDestinationLocations().get(j));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng chicago = new LatLng(41.8781, -87.6298);

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.addMarker(new MarkerOptions().position(chicago).title("Marker in Chicago"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chicago, 10));

        //TODO: may not be necessary
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = (LatLng) markerPoints.get(0);
                    LatLng dest = (LatLng) markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
            }
        });
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    //A class to parse the Google Places in JSON format
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //TODO: routes is where the locations for my route are stored???
            return routes;
        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            //ArrayList points = null;
            ArrayList<LatLng> points = new ArrayList<>();
            //PolylineOptions lineOptions = null;
            PolylineOptions lineOptions = new PolylineOptions();

            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                //points = new ArrayList();
                //lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                //lineOptions.addAll(points);
                //lineOptions.width(12);
                //lineOptions.color(Color.RED);
                //lineOptions.geodesic(true);
            }

            lineOptions.addAll(points);
            lineOptions.width(12);
            lineOptions.color(Color.RED);
            lineOptions.geodesic(true);

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    //TODO: works fairly well – keep for now

    public void geoLocate (View v) throws IOException{
        hideSoftKeyboard (v);
        EditText addressInput = findViewById(R.id.address_input_box);
        String desiredAddress = addressInput.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list_address = gc.getFromLocationName(desiredAddress, 1);

        Address add = list_address.get(0);

        String locality = add.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        double lat = add.getLatitude();
        double lng = add.getLongitude();

        LatLng actualLocation = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(actualLocation).title("Marker "));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualLocation, 10));
    }

    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }


    //TODO: useless for now
    public void getAddressFromCoord () {
        double specificLatitude = 0 ,specificLongitude = 0;

        myRequestQueue = Volley.newRequestQueue(this);  //dunno what this does
        JsonObjectRequest request = new JsonObjectRequest("http://maps.googleapis" +
                ".com/maps/api/geocode/json?latlng=" + String.valueOf(specificLatitude) + "," +
                String.valueOf(specificLongitude) + "&key=" + APP_API_KEY, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String address = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myRequestQueue.add(request);
    }

}



