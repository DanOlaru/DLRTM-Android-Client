package longmoneyoffshore.dlrtime.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;


//this class stores the current orders being handled by OrderListActivity and
// the current order being handled by IndividualClientOrderActivity

public class ClientListViewModel extends ViewModel {
    private MutableLiveData<List<Client>> ordersList;

    public LiveData<List<Client>> getOrders() {
        if (ordersList == null) {
            ordersList = new MutableLiveData<List<Client>>();
            loadOrders ();
        }
        return ordersList;
    }

    private void loadOrders () {
        //TODO: Do an asynchronous operation to fetch orders

    }
}
