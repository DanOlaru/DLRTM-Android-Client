package longmoneyoffshore.dlrtime.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface AsyncOrderedOrdersResult {
    void onResult(ArrayList<LatLng> orderedOrdersList);
}
