package longmoneyoffshore.dlrtime.utils.TransportClients;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import longmoneyoffshore.dlrtime.R;

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
        int color = R.color.orangeNA;

        if (o != null)
        {
            TextView orderName = (TextView) v.findViewById(R.id.order_name);
            TextView orderLocation = (TextView) v.findViewById(R.id.order_location);
            TextView orderPhoneNum = (TextView) v.findViewById(R.id.order_phone_number);
            TextView orderPrice = (TextView) v.findViewById(R.id.order_price);
            TextView orderPriceAdjust = (TextView) v.findViewById(R.id.order_price_adjust);
            TextView orderProductID = (TextView) v.findViewById(R.id.order_product_id);
            TextView orderStatus = (TextView) v.findViewById(R.id.order_status);

            if (o.getClientStatus().equalsIgnoreCase("PENDING")) {
                color = R.color.pendingGreen;
            }

            if (o.getClientUrgency() >= 4 || o.getClientValue() >= 4) {
                color = R.color.urgentRed;
            }

            if (o.getClientStatus().equalsIgnoreCase("DONE")) {

                color = R.color.doneBlue;
            }
            if (o.getClientStatus().equalsIgnoreCase("CANCEL")) {

                color = R.color.cancelGrey;
            }

            v = setOrderColor (v, color);

            orderName.setText(String.valueOf(o.getClientName()) + " |");
            orderLocation.setText(String.valueOf(o.getClientLocation()) + " |");
            orderPhoneNum.setText(String.valueOf(o.getClientPhoneNo()) + " |");
            orderPrice.setText(String.valueOf(o.getClientPrice()) + " |");
            orderPriceAdjust.setText(String.valueOf(o.getClientPriceAdjust()) + " |");
            orderProductID.setText(String.valueOf(o.getClientProductID()) + " |");
            orderStatus.setText(String.valueOf(o.getClientStatus()) + " |");

        }
        return v;
    }

    public View setOrderColor (View convertView, int color ) {

        View v = convertView;

        TextView orderName = (TextView) v.findViewById(R.id.order_name);
        TextView orderLocation = (TextView) v.findViewById(R.id.order_location);
        TextView orderPhoneNum = (TextView) v.findViewById(R.id.order_phone_number);
        TextView orderPrice = (TextView) v.findViewById(R.id.order_price);
        TextView orderPriceAdjust = (TextView) v.findViewById(R.id.order_price_adjust);
        TextView orderProductID = (TextView) v.findViewById(R.id.order_product_id);
        TextView orderStatus = (TextView) v.findViewById(R.id.order_status);

        orderName.setTextColor(ContextCompat.getColor(context, color));
        orderLocation.setTextColor(ContextCompat.getColor(context, color));
        orderPhoneNum.setTextColor(ContextCompat.getColor(context, color));
        orderPrice.setTextColor(ContextCompat.getColor(context, color));
        orderPriceAdjust.setTextColor(ContextCompat.getColor(context, color));
        orderProductID.setTextColor(ContextCompat.getColor(context, color));
        orderStatus.setTextColor(ContextCompat.getColor(context, color));

        return v;

    }
}