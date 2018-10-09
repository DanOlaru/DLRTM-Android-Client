
// for a future implementation

package longmoneyoffshore.dlrtime.utils.TransportClients;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class OrdersInternalDB implements Runnable {


    private ClientArray currentOrders = new ClientArray();

    public ClientArray getCurrentOrders () {
        return currentOrders;
    }

    public void appendOrder (Client order) {
        Client newEntry = new Client(order);
        currentOrders.appendClient(newEntry);
    }

    public void modifyOrder (int index, Client order) {
        // order;
        currentOrders.replaceClientAtIndex(index, order);
    }

    @Override
    public void run() {

    }

}
