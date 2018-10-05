package longmoneyoffshore.dlrtime;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//extra
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import longmoneyoffshore.dlrtime.utils.DirectionsJSONParser;
import longmoneyoffshore.dlrtime.utils.GetCoordinates;
import longmoneyoffshore.dlrtime.utils.MapDestinationsParcel;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.APP_API_KEY;
import static longmoneyoffshore.dlrtime.utils.GlobalValues.ChicagoLocale;

public class MapsRouteActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;
    ArrayList<LatLng> markerPoints= new ArrayList <LatLng> ();
    MapDestinationsParcel locationsToSee;
    ArrayList<LatLng> locationsToSeeCoordinates = new ArrayList<LatLng>();
    final String userLocale = "chicago"; // this will be the general locale of the user
    final LatLng userLocalePos = ChicagoLocale;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng currentUserGeoPosition;

    //Dan: experimetnal---------------------

    private static final float PATTERN_GAP_LENGTH_PX = 2;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    public int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_route);

        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (ActivityCompat.checkSelfPermission(MapsRouteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(MapsRouteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsRouteActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }else{
            // Write you code here if permission already given.
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().
                    addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null)  {
                                LatLng lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                currentUserGeoPosition = lastLatLng;
                            }
                        }
                    });
        }

        //dummy location for testing
        currentUserGeoPosition = ChicagoLocale;


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //this parcel contains all the addresses that need to show up on the map
        Bundle getLocationsToSeeBundle = getIntent().getExtras();
        locationsToSee = (MapDestinationsParcel) getLocationsToSeeBundle.getParcelable("locations to go to");
        locationsToSeeCoordinates.clear();

        //TODO: make it an ASyncTask because it perceptibly slows down the transition from OrdersListActivity to MapsRouteActivity
        LatLng geoCoords;

        for (int j=0; j<locationsToSee.getMapDestinationLocations().size(); j++) {
            //new GetCoordinates().execute(locationsToSee.getMapDestinationLocations().get(j).
            // replace(" ", "%20").concat("%2C" + userLocale)); //.replace("& ","")
            String  addressToGeoLocate = locationsToSee.getMapDestinationLocations().get(j);
            try {
                //Log.d("ADDRESS", addressToGeoLocate);
                if (addressToGeoLocate != null) {
                    geoCoords = geoLocateString(addressToGeoLocate + "," + userLocale);
                    locationsToSeeCoordinates.add(geoCoords);
                    counter++;
                    //Log.d("LOCGEO'D", "OPERATION # " + j + "FOLLOWING GEO COORDINATES RETURNED " + locationsToSeeCoordinates.get(j));
                }
            } catch (IOException e) {
                Log.e("geolocateException", "couldn't grab coordinates for this address.");
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

        if (markerPoints.size() > 1) {
            markerPoints.clear();
            mMap.clear();
        }

        //Dan: experimental ----------------------------------
        Polyline routePolyLine = mMap.addPolyline(new PolylineOptions().clickable(true));
        LatLng latLng, prev, nex;
        MarkerOptions options;

        //implement the user's last known position as start point
        mMap.addMarker(new MarkerOptions().position(currentUserGeoPosition));
        markerPoints.add(currentUserGeoPosition);
        options = new MarkerOptions();
        options.position(currentUserGeoPosition);
        prev = currentUserGeoPosition;

        for (int j=0; j<counter; j++) {
            latLng = locationsToSeeCoordinates.get(j);
            mMap.addMarker(new MarkerOptions().position(latLng));
            // Adding new item to the ArrayList
            markerPoints.add(latLng);
            options = new MarkerOptions();
            options.position(latLng);
            nex = latLng;

            if (markerPoints.size() == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            } else if (markerPoints.size() >= 2) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                //LatLng origin = (LatLng) markerPoints.get(j-1);
                //LatLng dest = (LatLng) markerPoints.get(j);

                LatLng origin = prev;
                LatLng dest = nex;

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);
                Log.d("ROUTE_URL", url + "&key=" + APP_API_KEY);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url + "&key=" + APP_API_KEY);

                prev = nex;
                //nex = (LatLng) markerPoints.get(j);
            }
        }

        //Log.d("MARKERS#", "NUMBER OF MARKERS ON MAP IS " + markerPoints.size());

        mMap.setOnPolylineClickListener(this);
        //Dan: experimental ---------------------------------- end

        //mMap.addMarker(new MarkerOptions().position(chicago).title("Marker in Chicago"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chicago, 10));
        mMap.addMarker(new MarkerOptions().position(userLocalePos).title("Marker in " + userLocale));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocalePos,10));

        /*
        //TODO: may not be necessary
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                // Adding new item to the ArrayList
                markerPoints.add(latLng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() >= 2) {
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
                    Log.d("ROUTE_URL", url + "&key=" + APP_API_KEY);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url + "&key=" + APP_API_KEY);
                }
            }
        });
        */
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        //Toast.makeText(this, "Route type " + polyline.getTag().toString(),Toast.LENGTH_SHORT).show();
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

    //works fairly well – keep for now
    private LatLng geoLocateString (String addressInput) throws IOException{
        Geocoder gc = new Geocoder(MapsRouteActivity.this);
        List<Address> listAddress = gc.getFromLocationName(addressInput, 1);
        Address coords;
        if (listAddress.size()>0) {coords = listAddress.get(0);}
        else {
            coords = null;
            throw new IOException("couldn't parse address");
        }

        String locality = coords.getLocality();
        Log.e("LOCALITY", locality);

        double lat = coords.getLatitude();
        double lng = coords.getLongitude();

        LatLng actualLocation = new LatLng(lat, lng);

        //Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
        //mMap.addMarker(new MarkerOptions().position(actualLocation).title("Marker "));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualLocation, 10));

        return actualLocation;
    }

}