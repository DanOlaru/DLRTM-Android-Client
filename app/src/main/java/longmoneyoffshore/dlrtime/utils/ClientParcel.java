package longmoneyoffshore.dlrtime.utils;


// Parcelable test implementation - Dan

import android.os.Parcel;
import android.os.Parcelable;


public class ClientParcel extends Client implements Parcelable {

    public static final Parcelable.Creator<ClientParcel> CREATOR = new Parcelable.Creator<ClientParcel>() {
        public ClientParcel createFromParcel (Parcel in) { return new ClientParcel(in); }

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
        this.setClientQuantity(in.readInt());
        this.setClientPrice(in.readInt());
        this.setClientPriceAdjust(in.readInt());
        this.setClientUrgency(in.readInt());
        this.setClientValue(in.readInt());
        this.setClientStatus(in.readString());


        //or is this the correct way to represent / write / parcel the data internally????

        //     this.internalClient.setClientName(in.readString()); //Is 'this' even necessary?
        //     this.internalClient.setClientPhoneNo(in.readString());
        //     this.internalClient.setClientLocation(in.readString());
        //     this.internalClient.setClientProductID(in.readString());
        //     this.internalClient.setClientQuantity(in.readInt());
        //     this.internalClient.setClientPrice(in.readInt());
        //     this.internalClient.setClientPriceAdjust(in.readInt());
        //     this.internalClient.setClientUrgency(in.readInt());
        //     this.internalClient.setClientValue(in.readInt());
        //     this.internalClient.setClientStatus(in.readString());
    }


    public Client getClient () {
        return this.getClient(); //this or super?
    }

    public Client returnClientFromParcel () { return this.getClient(); }


    //writing to parcel
    public void writeToParcel (Parcel outClient, int flags) {

        outClient.writeString(this.getClientName());
        outClient.writeString(this.getClientPhoneNo());
        outClient.writeString(this.getClientLocation());
        outClient.writeString(this.getClientProductID());
        outClient.writeInt(this.getClientQuantity());
        outClient.writeInt(this.getClientPrice());
        outClient.writeInt(this.getClientPriceAdjust());
        outClient.writeInt(this.getClientUrgency());
        outClient.writeInt(this.getClientValue());
        outClient.writeString(this.getClientStatus());


        // or this??

        //     outClient.writeString(this.internalClient.getClientName()); //do I use 'this' or 'internalClient'???
        //     outClient.writeString(this.internalClient.getClientPhoneNo());
        //     outClient.writeString(this.internalClient.getClientLocation());
        //     outClient.writeInt(this.internalClient.getClientProductID());
        //     this.internalClient.setClientQuantity(in.readInt());
        //     this.internalClient.setClientPrice(in.readInt());
        //     this.internalClient.setClientPriceAdjust(in.readInt());
        //     this.internalClient.setClientUrgency(in.readInt());
        //     this.internalClient.setClientValue(in.readInt());
        //     this.internalClient.setClientStatus(in.readString());
    }

    //implement describeContents
    @Override
    public int describeContents() { return 0; }


}
