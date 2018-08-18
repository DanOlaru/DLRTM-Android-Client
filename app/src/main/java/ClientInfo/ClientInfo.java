package ClientInfo;

public class ClientInfo {
    /* Client info consists of: name, location, phone number, product, quantity, total price, price per unit,
     * ClientPriceAdjust is positive if client owes and negative if client is owed,
     * ClientUrgency is on a scale of 1 to 5
     * ClientValue is on a scale of 1 to 5 — expresses how important client is
     * ClientSeen - expresses whether the client was served or not
     *
     * this is the data with which the core code operates, pulled off of the database
     */

    String ClientName;

    long ClientPhoneNo; // perhaps int[]?

    String ClientLocation;

    String ClientProductID; // what the customer is buying
    int ClientQuantity; //how much the customer is buying
    int ClientPrice;
    int ClientPricePerUnit = ClientPrice / ClientQuantity;
    int ClientPriceAdjust;


    enum ClientUrgency {
        one(1),two(2),three(3),four(4),five(5);
        private int relativeCustUrgency;

        ClientUrgency (int relCusUrg) {relativeCustUrgency = relCusUrg;} //constructor

        int getClientValue () {return relativeCustUrgency;}

    };

    enum ClientValue {
        one(1),two(2),three(3),four(4),five(5);  //stars
        private int relativeCustValue;

        ClientValue (int relCusVal) {relativeCustValue = relCusVal;} //constructor

        int getClientValue () {return relativeCustValue;}

    };

    enum ClientSeen {seen, notseen, issue}; // 0 means not seen, 1 means seen, 2 means incomplete transaction



}
