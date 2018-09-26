package longmoneyoffshore.dlrtime.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import longmoneyoffshore.dlrtime.R;
import longmoneyoffshore.dlrtime.utils.Client;

public class ClientAdapter extends ArrayAdapter<Client> {

    Context context;
    private ArrayList<Client> clients; // List of clients

    // Constructor of the Adapter
    public ClientAdapter(Context context, int textViewResourceId, ArrayList<Client> clients)
    {
        super(context, textViewResourceId, clients);
        this.context = context;
        this.clients = clients;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.client_item, null);
        }

        Client o = clients.get(position);

        if (o != null)
        {
            TextView orderName = (TextView) v.findViewById(R.id.order_name);
            TextView orderLocation = (TextView) v.findViewById(R.id.order_location);
            TextView orderPhoneNum = (TextView) v.findViewById(R.id.order_phone_number);
            TextView orderPrice = (TextView) v.findViewById(R.id.order_price);
            TextView orderPriceAdjust = (TextView) v.findViewById(R.id.order_price_adjust);
            TextView orderProductID = (TextView) v.findViewById(R.id.order_product_id);
            TextView orderStatus = (TextView) v.findViewById(R.id.order_status);

            orderName.setText(String.valueOf(o.getClientName()) + "|");
            orderLocation.setText(String.valueOf(o.getClientLocation()) + "|");
            orderPhoneNum.setText(String.valueOf(o.getClientPhoneNo()) + "|");
            orderPrice.setText(String.valueOf(o.getClientPrice()) + "|");
            orderPriceAdjust.setText(String.valueOf(o.getClientPriceAdjust()) + "|");
            orderProductID.setText(String.valueOf(o.getClientProductID()) + "|");
            orderStatus.setText(String.valueOf(o.getClientStatus()) + "|");
        }
        return v;
    }
}