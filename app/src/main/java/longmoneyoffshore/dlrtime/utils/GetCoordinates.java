package longmoneyoffshore.dlrtime.utils;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import longmoneyoffshore.dlrtime.MapsRouteActivity;
import longmoneyoffshore.dlrtime.R;

import static longmoneyoffshore.dlrtime.utils.GlobalValues.APP_API_KEY;

public class GetCoordinates extends AsyncTask<String, Void, String> {

    private ArrayList<LatLng> locationsToSeeCoordinates = new ArrayList<LatLng>();

    private RequestQueue myRequestQueue;

    //ProgressDialog dialog = new ProgressDialog(MapsRouteActivity.this); //TODO: useless for now

        /*
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            //dialog.setMessage("Please wait....");
            //dialog.setCanceledOnTouchOutside(false);
            //dialog.show();
        }*/

    @Override
    protected String doInBackground (String ...strings) {
        String response;
        try {
            String address = strings[0];
            HttpDataHandler http = new HttpDataHandler();
            String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s%s%s", address, "&key=", APP_API_KEY);

            Log.d("BACKGROUND", "STRING TO BE GEOCODED: " + address);
            Log.d("BACKGROUND", "URL SENT TO BE GEOCODED: " + url);

            response = http.getHTTPData(url);
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute (String s) {
        //super.onPostExecute(s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            String lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                    .getJSONObject("location").get("lat").toString();
            String lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                    .getJSONObject("location").get("lng").toString();

            //TODO: save the coordinates retrieved for this address
            LatLng saveCoordForLocation = new LatLng(Float.valueOf(lat),Float.valueOf(lng));
            locationsToSeeCoordinates.add(saveCoordForLocation);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ArrayList<LatLng> getLocationsCoordinatesList () {
        return locationsToSeeCoordinates;
    }

    //TODO: useless for now
    public void getAddressFromCoord () {
        double specificLatitude = 0 ,specificLongitude = 0;

        //myRequestQueue = Volley.newRequestQueue(GetCoordinates.this);  //dunno what this does
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

    //TODO: works fairly well – keep for now

    /*
    public void geoLocate (View v) throws IOException {
        hideSoftKeyboard (v);
        EditText addressInput = findViewById(R.id.address_input_box);
        String desiredAddress = addressInput.getText().toString();

        Geocoder gc = new Geocoder(MapsRouteActivity.this);
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
    */


}

//extra code
