package longmoneyoffshore.dlrtime.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;


public class OptimizeRoute extends AsyncTask <ArrayList<LatLng>, Void, ArrayList<LatLng>> {

    //AsyncOrderedOrdersResult callback;
    //public OptimizeRoute(AsyncOrderedOrdersResult callback) {this.callback = callback;
    //}

    @Override
    protected ArrayList<LatLng> doInBackground(ArrayList<LatLng> ...unordered) {
        // params comes from the execute() call: params[0] is the url.

        ArrayList<LatLng> unorderedLocations = unordered[0];
        ArrayList<LatLng> reorderedList = new ArrayList<LatLng>();

        double min, dist;
        reorderedList.add(unorderedLocations.get(0));
        LatLng closestNeighbor = unorderedLocations.get(1); // = unorderedLocations.get(1);
        int counter = 0;

        if (unorderedLocations.size()<=1) {
            //array is null or has only one element
            return unorderedLocations;
        }

        while (unorderedLocations.size()>1) {
            min = pythagoreanDistance(reorderedList.get(counter), unorderedLocations.get(0));

            for (int i=0; i<unorderedLocations.size();i++) {
                dist = pythagoreanDistance(reorderedList.get(counter), unorderedLocations.get(i));
                if (dist < min) {
                    min = dist;
                    closestNeighbor = unorderedLocations.get(i);
                }
            }
            reorderedList.add(closestNeighbor);
            counter++;
        }

        /*
        for (int i=0;i<unorderedLocations.size()-2;i++) {
            min = pythagoreanDistance(unorderedLocations.get(i), unorderedLocations.get(i+1));
            closestNeighbor = unorderedLocations.get(i+1);

            for (int j=i+1; j<unorderedLocations.size()-1; j++) {
                dist = pythagoreanDistance(unorderedLocations.get(i), unorderedLocations.get(j));
                if (min>dist) {
                    closestNeighbor = unorderedLocations.get(j);
                    min = dist;
                }
                }
            reorderedList.add(closestNeighbor);
        }
        */

        Log.d ("REORDERD LIST", "SHOWING REORDERED LIST OF LOCATIONS");
        for (int k=0; k<reorderedList.size(); k++) {
            Log.d ("UNORDERD LIST", "LATITUDE" + unorderedLocations.get(k).latitude + " LONGITUDE" + unorderedLocations.get(k).longitude);
            Log.d ("REORDERD LIST", "LATITUDE" + reorderedList.get(k).latitude + " LONGITUDE" + reorderedList.get(k).longitude);
        }

        return reorderedList;
        }


    @Override
    protected void onPostExecute(ArrayList<LatLng> resultOrderedList) {
        ArrayList<LatLng> orderedResult = resultOrderedList;
        ArrayList<LatLng> orderedList = new ArrayList<LatLng>(orderedResult);

        //callback.onResult(orderedResult);

    }

    double pythagoreanDistance (LatLng pointA, LatLng pointB) {

        return Math.sqrt(Math.pow(pointA.latitude-pointB.latitude,2) + Math.pow(pointA.longitude-pointB.longitude,2));

    }


}
