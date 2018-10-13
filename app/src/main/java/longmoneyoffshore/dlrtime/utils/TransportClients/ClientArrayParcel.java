package longmoneyoffshore.dlrtime.utils.TransportClients;


//TODO: currently does NOT work as such

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ClientArrayParcel extends ClientArray implements Parcelable {

    //object to transport
    private ClientArray clientArrayList;

    public static final Parcelable.Creator<ClientArrayParcel> CREATOR = new Parcelable.Creator<ClientArrayParcel>() {
        @Override
        public ClientArrayParcel createFromParcel (Parcel in) { return new ClientArrayParcel(in); }

        @Override
        public ClientArrayParcel[] newArray(int size) { return new ClientArrayParcel[size]; }
    };

    //constructors
    public ClientArrayParcel () { }

    public ClientArrayParcel(ClientArray inputClientArray) {
        super(inputClientArray);
        //this.clientArrayList.setClientArray() = new ClientArray(inputClientArray);
    }

    public ClientArrayParcel(ArrayList<Client> inputClientBasicArray) {
        super(inputClientBasicArray);
    }

    public ClientArray getClientArrayList () {
        //return clientArrayList;
        return this.clientArrayList;
    }

    public ArrayList<Client> getBasicClientArray () {
        return this.getClientArray();
    }

    public void setClientArrayList (ClientArray inputClientArray) {
        //this.clientArrayList = inputClientArray;
        super.setClientArray(inputClientArray.getClientArray());
    }



    //parcellation
    public ClientArrayParcel(Parcel in) {
        //this.clientArrayList = (ClientArray) in.readSerializable();

        //this.clientArrayList = (ClientArray) in.readSerializable();
        this.setClientArray((ClientArray) in.readSerializable());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(this.getClientArray());
        //out.writeSerializable(super.getClientArray());
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
