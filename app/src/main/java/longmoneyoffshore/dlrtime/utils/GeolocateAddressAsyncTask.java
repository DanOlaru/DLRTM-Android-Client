/*
package longmoneyoffshore.dlrtime.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import longmoneyoffshore.dlrtime.MapsRouteActivity;

import static longmoneyoffshore.dlrtime.utils.GlobalValues.APP_API_KEY;

public class GeolocateAddressAsyncTask extends AsyncTask<CompositeType<Context, ArrayList<String>>,Void,ArrayList<LatLng>> {

    //dummy — to be updated by other method
    String userLocale = "Chicago";

    private int counter;

    @Override
    protected ArrayList<LatLng> doInBackground(CompositeType<Context, ArrayList<String>>...input) {

        CompositeType<Context,ArrayList<String>> inputData = input[0];

        Context workingContext = inputData.firstArg;
        ArrayList<String> listOfAddressesToParse = inputData.secondArg;
        ArrayList<LatLng> result = new ArrayList<LatLng>();

        counter = 0;

        Log.d("INSIDEASYNCTASK","BISH_OUTSIDE");
        try {
            //Log.d("INSIDEASYNCTASK","BISH");
            for (int i=0; i<listOfAddressesToParse.size(); i++) {
                result.add(geoLocateString(workingContext, listOfAddressesToParse.get(counter++)));
                //Log.d("INSIDEASYNCTASK", result.get(i).toString());
            }

        } catch (IOException e) { Log.d("Background Task", e.toString()); }

        Log.d("INSIDEASYNCTASK","ADDRESSES PARSED " + counter);

        return result;
    }

    @Override
    protected void onPostExecute (ArrayList<LatLng> result) {
        //MapsRouteActivity.DownloadTask downloadTask = new MapsRouteActivity.DownloadTask();

        // Start downloading json data from Google Directions API
        //downloadTask.execute(url + "&key=" + APP_API_KEY);
    }



    //works fairly well – keep for now
    private LatLng geoLocateString (Context thisContext, String addressInput) throws IOException {
        Geocoder gc = new Geocoder(thisContext);
        List<Address> listAddress = gc.getFromLocationName(addressInput + "," + userLocale, 1);
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

*/