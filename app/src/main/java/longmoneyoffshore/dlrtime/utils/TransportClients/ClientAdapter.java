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
import longmoneyoffshore.dlrtime.utils.TransportClients.Client;

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
        int color;

        if (o != null)
        {
            TextView orderName = (TextView) v.findViewById(R.id.order_name);
            TextView orderLocation = (TextView) v.findViewById(R.id.order_location);
            TextView orderPhoneNum = (TextView) v.findViewById(R.id.order_phone_number);
            TextView orderPrice = (TextView) v.findViewById(R.id.order_price);
            TextView orderPriceAdjust = (TextView) v.findViewById(R.id.order_price_adjust);
            TextView orderProductID = (TextView) v.findViewById(R.id.order_product_id);
            TextView orderStatus = (TextView) v.findViewById(R.id.order_status);

            if (o.getClientUrgency() >= 4 || o.getClientValue() >= 4) {
                //Log.d("INSIDE COLORRED", "CLIENT IS " + o.getClientName());
                //setOrderColor(position, v,parent, R.color.urgentRed);
                color = R.color.urgentRed;

                orderName.setTextColor(ContextCompat.getColor(context, color));
                orderLocation.setTextColor(ContextCompat.getColor(context, color));
                orderPhoneNum.setTextColor(ContextCompat.getColor(context, color));
                orderPrice.setTextColor(ContextCompat.getColor(context, color));
                orderPriceAdjust.setTextColor(ContextCompat.getColor(context, color));
                orderProductID.setTextColor(ContextCompat.getColor(context, color));
                orderStatus.setTextColor(ContextCompat.getColor(context, color));
            }


            String clStat = o.getClientStatus().toUpperCase();

            if (clStat.equals("PENDING")) {
                //Log.d("INSIDE COLORRED", "CLIENT IS " + o.getClientName());
                //setOrderColor(position, v,parent, R.color.urgentRed);

                color = R.color.pendingGreen;

                orderName.setTextColor(ContextCompat.getColor(context, color));
                orderLocation.setTextColor(ContextCompat.getColor(context, color));
                orderPhoneNum.setTextColor(ContextCompat.getColor(context, color));
                orderPrice.setTextColor(ContextCompat.getColor(context, color));
                orderPriceAdjust.setTextColor(ContextCompat.getColor(context, color));
                orderProductID.setTextColor(ContextCompat.getColor(context, color));
                orderStatus.setTextColor(ContextCompat.getColor(context, color));
            } else if (clStat.equals("DONE")) {
                //Log.d("INSIDE COLORBROWN", "CLIENT IS " + o.getClientName());
                //setOrderColor(position, v ,parent, R.color.doneBrown);

                color = R.color.doneBlue;

                orderName.setTextColor(ContextCompat.getColor(context, color));
                orderLocation.setTextColor(ContextCompat.getColor(context, color));
                orderPhoneNum.setTextColor(ContextCompat.getColor(context, color));
                orderPrice.setTextColor(ContextCompat.getColor(context, color));
                orderPriceAdjust.setTextColor(ContextCompat.getColor(context, color));
                orderProductID.setTextColor(ContextCompat.getColor(context, color));
                orderStatus.setTextColor(ContextCompat.getColor(context, color));
            } else if (o.getClientUrgency() < 4 && o.getClientValue() < 4){
                color = R.color.orangeNA;

                orderName.setTextColor(ContextCompat.getColor(context, color));
                orderLocation.setTextColor(ContextCompat.getColor(context, color));
                orderPhoneNum.setTextColor(ContextCompat.getColor(context, color));
                orderPrice.setTextColor(ContextCompat.getColor(context, color));
                orderPriceAdjust.setTextColor(ContextCompat.getColor(context, color));
                orderProductID.setTextColor(ContextCompat.getColor(context, color));
                orderStatus.setTextColor(ContextCompat.getColor(context, color));
            }

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

    public void setOrderColor (int position, View convertView, ViewGroup parent, int color ) {

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

    }
}