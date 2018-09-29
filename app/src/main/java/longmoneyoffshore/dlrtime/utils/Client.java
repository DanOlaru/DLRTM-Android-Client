package longmoneyoffshore.dlrtime.utils;

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
    private String anonymizerPrefix = USanonymizerPrefix;


    //Note: by convention only for the name of the classes the first letter is uppercase

    /****************************************Constructors of the classs******************/
    // 1° constructor - the basic one
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

    // 4° constructor from this same type of object — Dan

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

        //latest change
        this.anonymizerPrefix = fromClient.getAnonymizerPrefix();
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

    public void setClientPriceAdjust(float clientPriceAdjust) {
        this.clientPriceAdjust = clientPriceAdjust;
    }

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
    public void setAnonymizerPrefix (String anonymizerPrefix) {this.anonymizerPrefix = anonymizerPrefix;}

    public String getAnonymizerPrefix () {return this.anonymizerPrefix;}


    //return a reference to this Client object
    public Client getClient () {return this;}
}