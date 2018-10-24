package longmoneyoffshore.dlrtime.utils.TransportClients;

import java.util.ArrayList;

public class ClientArray extends Client {

    ClientArray thisClass;
    private ArrayList<Client> clientList;

    //constructors
    public ClientArray () {
        clientList = new ArrayList<>();
    }

    public ClientArray (ArrayList<Client> inputListOfClients) {
        clientList = new ArrayList<Client> (inputListOfClients);
    }

    //"self-referential" constructor
    public ClientArray (ClientArray inputClientArray) {
        //thisClass.setClientArray (inputListOfClients.getClientArray());
        clientList = new ArrayList<>(inputClientArray.getClientArray());
    }

    //getters and setters
    public ArrayList<Client> getClientArray () {
        return clientList;
    }

    public void setClientArray(ArrayList<Client> inputClientArray) {
        clientList = new ArrayList<Client>(inputClientArray);
    }

    public void setClientArray(ClientArray inputClientArray) {

        clientList = new ArrayList<Client>(inputClientArray.getClientArray());
    }


    public Client getClientFromArray(int index) {
        return clientList.get(index);
    }

    public void appendClient (Client inputClient) {
        clientList.add(inputClient);
    }

    public void appendClientArray (ArrayList<Client> inputClientArray) {
        clientList.addAll(inputClientArray);
    }

    public void appendClientArray (ClientArray inputClientArray) {
        clientList.addAll(inputClientArray.getClientArray());
    }

    public void replaceClientAtIndex(int index, Client updateClient) {

        Client newClientToUpdate= new Client(updateClient);

        if (index>=clientList.size()) {clientList.add(newClientToUpdate);}
        else {
            clientList.remove(index);
            clientList.set(index,updateClient);
        }
    }
}
