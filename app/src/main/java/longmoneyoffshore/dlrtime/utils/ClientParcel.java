package longmoneyoffshore.dlrtime.utils;


// Parcelable test implementation - Dan

import android.os.Parcel;
import android.os.Parcelable;


public class ClientParcel extends Client implements Parcelable {

    public static final Parcelable.Creator<ClientParcel> CREATOR = new Parcelable.Creator<ClientParcel>() {
        public ClientParcel createFromParcel (Parcel in) {
            return new ClientParcel(in);
        }
        public ClientParcel[] newArray(int size) {
            return new ClientParcel[size];
        }
    };


    //constructors

    //simple constructor
    public ClientParcel () { }

    //constructor of ClientParcel from Client object — is this right?
    public ClientParcel (Client thisClient) {
        super (thisClient);
    }

    //Parcelation
    //is it supposed to be public or private?
    //the order matters?

    public ClientParcel(Parcel in) {
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
    }


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
    }

    //implement describeContents
    public int describeContents() {
        return 0;
    }


}
