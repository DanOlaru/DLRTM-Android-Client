
//commit
//try agaoin

class ClientInfo {
	/* Client info consists of: name, location, phone number, product, quantity, total price, price per unit,
	 * ClientPriceAdjust is positive if client owes and negative if client is owed,
	 * ClientUrgency is on a scale of 1 to 5
	 * ClientValue is on a scale of 1 to 5 — expresses how important client is
	 * ClientSeen - expresses whether the client was served or not
	 * 
	 * this is the data with which the core code operates, pulled off of the database
	 */
	
	String ClientName;
	String ClientLocation;
	long ClientPhoneNo; // perhaps int[]?
	
	String ClientProduct; // what the customer is buying
	int ClientQuantity; //how much the customer is buying
	int ClientPrice;
	int ClientPricePerUnit;
	int ClientPriceAdjust;
	byte ClientUrgency;
	byte ClientValue;
	
	byte ClientSeen = 0; // 0 means not seen, 1 means seen, 2 means incomplete transaction
	
}



public class DLRTimeMain {
	public static void main (String[] args) {
	//enter into the code
		
	}
}
