package ClientInfo;

// individual client info screen

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

    String ClientPhone; // perhaps int[]?

    String ClientLocation;

    String ClientProductID; // what the customer is buying

    int ClientQuantity; //how much the customer is buying

    int ClientPrice;

    int ClientPricePerUnit = ClientPrice / ClientQuantity; //internal info

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

    enum ClientSeen {

        seen(0), notseen(1), issue(2);

        private int clientSeenStatus=0;
        String issueComment;

        ClientSeen (int clStat) {clientSeenStatus=clStat;}

        ClientSeen (int clStat, String issueCmt) { //overloaded constructor that takes the comment regarding the issue with the order
            clientSeenStatus = clStat;
            issueComment = issueCmt;
        }

        String wasClientSeen () {
            if (clientSeenStatus == 0) return "0";
            else if (clientSeenStatus == 1) return "1";
            else return "2" + issueComment;
        }

    }; // 0 means not seen, 1 means seen, 2 means incomplete transaction
}
