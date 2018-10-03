package longmoneyoffshore.dlrtime.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MapDestinationsParcel implements Parcelable {

    private volatile ArrayList<String> destinationLocations = new ArrayList<String>();


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MapDestinationsParcel createFromParcel(Parcel in) {
            return new MapDestinationsParcel(in);
        }

        public MapDestinationsParcel[] newArray(int size) {
            return new MapDestinationsParcel[size];
        }
    };

    public MapDestinationsParcel(ArrayList<String> inputDestinationLocations) {
        this.destinationLocations = inputDestinationLocations;
    }
    public ArrayList<String> getMapDestinationLocations () {
        return destinationLocations;
    }

    public void setMapDestinationLocations (ArrayList<String> inputDestinationLocations) {
        this.destinationLocations = inputDestinationLocations;
    }

    //parcellation
    public MapDestinationsParcel(Parcel in) {

        this.destinationLocations = (ArrayList<String>) in.readSerializable();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(destinationLocations);
    }


    @Override
    public int describeContents() {
        return 0;
    }

}
