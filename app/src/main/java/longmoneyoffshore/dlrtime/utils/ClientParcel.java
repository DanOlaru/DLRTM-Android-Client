package longmoneyoffshore.dlrtime.utils;


// Parcelable test implementation - Dan

import android.os.Parcel;
import android.os.Parcelable;


public class ClientParcel extends Client implements Parcelable {

    public static final Parcelable.Creator<ClientParcel> CREATOR = new Parcelable.Creator<ClientParcel>() {
        @Override
        public ClientParcel createFromParcel (Parcel in) { return new ClientParcel(in); }

        @Override
        public ClientParcel[] newArray(int size) { return new ClientParcel[size]; }
    };


    //constructors

    //simple constructor
    public ClientParcel () { }

    //constructor of ClientParcel from Client object — is this right? Does it do what I want it to do?
    public ClientParcel (Client thisClient) { super (thisClient); }

    //Parcelation
    //the order matters?

    private ClientParcel(Parcel in) {
        this.setClientName(in.readString());
        this.setClientPhoneNo(in.readString());
        this.setClientLocation(in.readString());
        this.setClientProductID(in.readString());
        this.setClientQuantity(in.readFloat());
        this.setClientPrice(in.readFloat());
        this.setClientPriceAdjust(in.readFloat());
        this.setClientUrgency(in.readFloat());
        this.setClientValue(in.readFloat());
        this.setClientStatus(in.readString());
        //
        this.setAnonymizerPrefix(in.readString());
    }


    public Client getClient () {
        return this.getClient(); //this or super?
    }

    public Client returnClientFromParcel () {

        Client out = new Client();

        out.setClientName(this.getClientName());
        out.setClientPhoneNo(this.getClientPhoneNo());
        out.setClientLocation(this.getClientLocation());
        out.setClientProductID(this.getClientProductID());
        out.setClientQuantity(this.getClientQuantity());
        out.setClientPrice(this.getClientPrice());
        out.setClientPriceAdjust(this.getClientPriceAdjust());
        out.setClientUrgency(this.getClientUrgency());
        out.setClientValue(this.getClientValue());
        out.setClientStatus(this.getClientStatus());

        return out;
    }

    //write object values to parcel for storage
    public void writeToParcel (Parcel outClient, int flags) {

        outClient.writeString(this.getClientName());
        outClient.writeString(this.getClientPhoneNo());
        outClient.writeString(this.getClientLocation());
        outClient.writeString(this.getClientProductID());
        outClient.writeFloat(this.getClientQuantity());
        outClient.writeFloat(this.getClientPrice());
        outClient.writeFloat(this.getClientPriceAdjust());
        outClient.writeFloat(this.getClientUrgency());
        outClient.writeFloat(this.getClientValue());
        outClient.writeString(this.getClientStatus());
        //
        outClient.writeString(this.getAnonymizerPrefix());
    }

    //implement describeContents
    @Override
    public int describeContents() { return 0; }


}
