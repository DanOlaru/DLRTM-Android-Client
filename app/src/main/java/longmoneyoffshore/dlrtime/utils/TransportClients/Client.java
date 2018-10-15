package longmoneyoffshore.dlrtime.utils.TransportClients;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class Client {
    /* Client info consists of: name, location, phone number, product, quantity, total price, price per unit,
     * ClientPriceAdjust is positive if client owes and negative if client is owed,
     * ClientUrgency is on a scale of 1 to 5
     * ClientValue is on a scale of 1 to 5 — expresses how important client is
     * ClientSeen - expresses whether the client was served or not
     * this is the data with which the core code operates, pulled off of the database
     */

    //TODO: implement anonymizer prefix to be user-inputtable from Settings page (TBD)
    public final String USanonymizerPrefix = "*67"; //US - default

    // Attributes
    private String clientName;
    private String clientPhoneNo; // It's a string because the sequence of numbers does not have any logic behind it
    private String clientLocation;
    private String clientProductID; // what the customer is buying
    private float clientQuantity; //how much the customer is buying
    private float clientPrice;
    private float clientPriceAdjust;
    private float clientUrgency;
    private float clientValue;
    private String clientStatus;

    // hidden variable
    private String anonymizerPrefix = USanonymizerPrefix;
    private String clientReferenceCode = "0";
    private String revision = "0";


    //Note: by convention only for the name of the classes the first letter is uppercase

    /****************************************Constructors******************/
    // 1° constructor -  basic
    public Client()
    {}

    // 2° constructor

    public Client(String clientName, String clientPhoneNo, String clientLocation, String clientProductID, float clientQuantity,
                  float clientPrice, float clientPriceAdjust, float clientUrgency, float clientValue, String clientStatus) {
        this.clientName = clientName;
        this.clientPhoneNo = clientPhoneNo;
        this.clientLocation = clientLocation;
        this.clientProductID = clientProductID;
        this.clientQuantity = clientQuantity;
        this.clientPrice = clientPrice;
        this.clientPriceAdjust = clientPriceAdjust;
        this.clientUrgency = clientUrgency;
        this.clientValue = clientValue;
        this.clientStatus = clientStatus;
    }

    // 3° constructor that also takes anonymizerPrefix

    public Client(String clientName, String clientPhoneNo, String clientLocation, String clientProductID, float clientQuantity,
                  float clientPrice, float clientPriceAdjust, float clientUrgency, float clientValue, String clientStatus,
                  String anonymizerPrefix) {
        this.clientName = clientName;
        this.clientPhoneNo = clientPhoneNo;
        this.clientLocation = clientLocation;
        this.clientProductID = clientProductID;
        this.clientQuantity = clientQuantity;
        this.clientPrice = clientPrice;
        this.clientPriceAdjust = clientPriceAdjust;
        this.clientUrgency = clientUrgency;
        this.clientValue = clientValue;
        this.clientStatus = clientStatus;
        this.anonymizerPrefix = anonymizerPrefix;
    }

    // 4° constructor that also takes anonymizerPrefix and clientReferenceCode

    public Client(String clientName, String clientPhoneNo, String clientLocation, String clientProductID, float clientQuantity,
                  float clientPrice, float clientPriceAdjust, float clientUrgency, float clientValue, String clientStatus,
                  String anonymizerPrefix, String clientReferenceCode) {
        this.clientName = clientName;
        this.clientPhoneNo = clientPhoneNo;
        this.clientLocation = clientLocation;
        this.clientProductID = clientProductID;
        this.clientQuantity = clientQuantity;
        this.clientPrice = clientPrice;
        this.clientPriceAdjust = clientPriceAdjust;
        this.clientUrgency = clientUrgency;
        this.clientValue = clientValue;
        this.clientStatus = clientStatus;
        this.anonymizerPrefix = anonymizerPrefix;
        this.clientReferenceCode = clientReferenceCode;
    }

    // 5° constructor that also takes anonymizerPrefix and clientReferenceCode and revision
    public Client(String clientName, String clientPhoneNo, String clientLocation, String clientProductID, float clientQuantity,
                  float clientPrice, float clientPriceAdjust, float clientUrgency, float clientValue, String clientStatus,
                  String anonymizerPrefix, String clientReferenceCode, String revCode) {
        this.clientName = clientName;
        this.clientPhoneNo = clientPhoneNo;
        this.clientLocation = clientLocation;
        this.clientProductID = clientProductID;
        this.clientQuantity = clientQuantity;
        this.clientPrice = clientPrice;
        this.clientPriceAdjust = clientPriceAdjust;
        this.clientUrgency = clientUrgency;
        this.clientValue = clientValue;
        this.clientStatus = clientStatus;
        this.anonymizerPrefix = anonymizerPrefix;
        this.clientReferenceCode = clientReferenceCode;
        this.revision = revCode;
    }

    // 6° constructor from the same type of object — Dan
    public Client (Client fromClient)
    {
        this.clientName = fromClient.getClientName();
        this.clientPhoneNo = fromClient.getClientPhoneNo();
        this.clientLocation = fromClient.getClientLocation();
        this.clientProductID = fromClient.getClientProductID();
        this.clientQuantity = fromClient.getClientQuantity();
        this.clientPrice = fromClient.getClientPrice();
        this.clientPriceAdjust = fromClient.getClientPriceAdjust();
        this.clientUrgency = fromClient.getClientUrgency();
        this.clientValue = fromClient.getClientValue();
        this.clientStatus = fromClient.getClientStatus();

        //latest
        this.anonymizerPrefix = fromClient.getAnonymizerPrefix();
        this.clientReferenceCode = fromClient.getClientReferenceCode();
        this.revision = fromClient.getRevision();
    }

    /****************Getter and setter - methods use to access the private attributes of a class **/

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhoneNo() {
        return clientPhoneNo;
    }

    public void setClientPhoneNo(String clientPhoneNo) {
        this.clientPhoneNo = clientPhoneNo;
    }

    public String getClientLocation() {
        return clientLocation;
    }

    public void setClientLocation(String clientLocation) {
        this.clientLocation = clientLocation;
    }

    public String getClientProductID() {
        return clientProductID;
    }

    public void setClientProductID(String clientProductID) {
        this.clientProductID = clientProductID;
    }

    public float getClientQuantity() {
        return clientQuantity;
    }
    public void setClientQuantity(float clientQuantity) {
        this.clientQuantity = clientQuantity;
    }

    public float getClientPrice() {
        return clientPrice;
    }
    public void setClientPrice(float clientPrice) {
        this.clientPrice = clientPrice;
    }

    public float getClientPriceAdjust() {
        return clientPriceAdjust;
    }
    public void setClientPriceAdjust(float clientPriceAdjust) { this.clientPriceAdjust = clientPriceAdjust; }

    public float getClientUrgency() {
        return clientUrgency;
    }
    public void setClientUrgency(float clientUrgency) {
        this.clientUrgency = clientUrgency;
    }

    public float getClientValue() {
        return clientValue;
    }
    public void setClientValue(float clientValue) {
        this.clientValue = clientValue;
    }

    public String getClientStatus() {
        return clientStatus;
    }
    public void setClientStatus(String clientStatus) {
        this.clientStatus = clientStatus;
    }

    //set and get AnonymizerPrefix which is added in front of the phone number if the caller ID needs to be private for any reason
    public String getAnonymizerPrefix () {return this.anonymizerPrefix;}
    public void setAnonymizerPrefix (String anonymizerPrefix) {this.anonymizerPrefix = anonymizerPrefix;}

    public String getClientReferenceCode () {return clientReferenceCode;}
    public void setClientReferenceCode(String refCode) {this.clientReferenceCode = refCode;}
    public void setClientReferenceCode(int refCode) { this.clientReferenceCode = String.valueOf(refCode); }

    public String getRevision () {return revision;}
    public void setRevision (String rev) {this.revision = rev;}


    //return a reference to this Client object
    public Client getClient () {return this;}

    //other methods

    //TODO: convert client to List<List<Object>>
    public List<List<Object>> returnClientAsObjectList() {
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        this.clientName, this.clientPhoneNo, this.clientLocation, this.clientProductID,
                        this.clientQuantity, this.clientPrice, this.clientPriceAdjust, this.clientUrgency,
                        this.clientValue, this.clientStatus
                )
        );
        return values;
    }

    public boolean equalsRevision(Client toCompare) {
        return this.getRevision().equals(toCompare.getRevision());
    }

    public String clientDifferences(Client other) {
        String differencesIndex="";

        if (!this.clientName.equals(other.getClientName())) differencesIndex+=1;
        else differencesIndex+=0;
        if (!this.clientPhoneNo.equals(other.getClientPhoneNo())) differencesIndex+=1;
        else differencesIndex+=0;
        if (!this.clientLocation.equals(other.getClientLocation())) differencesIndex+=1;
        else differencesIndex+=0;
        if (!this.clientProductID.equals(other.getClientProductID())) differencesIndex+=1;
        else differencesIndex+=0;
        if (this.clientQuantity != other.getClientQuantity()) differencesIndex+=1;
        else differencesIndex+=0;
        if (this.clientPrice != other.getClientPrice()) differencesIndex+=1;
        else differencesIndex+=0;
        if (this.clientPriceAdjust != other.getClientPriceAdjust()) differencesIndex+=1;
        else differencesIndex+=0;
        if (this.clientUrgency != other.getClientUrgency()) differencesIndex+=1;
        else differencesIndex+=0;
        if (this.clientValue != other.getClientValue()) differencesIndex+=1;
        else differencesIndex+=0;
        if (!this.clientStatus.equals(other.getClientStatus())) differencesIndex+=1;
        else differencesIndex+=0;

        return differencesIndex;
    }

    public void showClient () {
        Log.d("THIS_CLIENT NAME", clientName);
        Log.d("THIS_CLIENT PHONE", clientPhoneNo);
        Log.d("THIS_CLIENT LOCATION", clientLocation);
        Log.d("THIS_CLIENT PROD ID", clientProductID);
        Log.d("THIS_CLIENT QUANT", String.valueOf(clientQuantity));
        Log.d("THIS_CLIENT PRICE", String.valueOf(clientPrice));
        Log.d("THIS_CLIENT PR ADJ", String.valueOf(clientPriceAdjust));
        Log.d("THIS_CLIENT URGENCY", String.valueOf(clientUrgency));
        Log.d("THIS_CLIENT VALUE", String.valueOf(clientValue));
        Log.d("THIS_CLIENT STATUS", clientStatus);
        Log.d("THIS_CLIENT REF CODE", clientReferenceCode);
        Log.d("THIS_CLIENT REVISION", revision);
    }

}