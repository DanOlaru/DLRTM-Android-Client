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
        if (o != null) {
            TextView pos = (TextView) v.findViewById(R.id.position);
            TextView name = (TextView) v.findViewById(R.id.name);
            TextView wins = (TextView) v.findViewById(R.id.wins);
            TextView draws = (TextView) v.findViewById(R.id.draws);
            TextView losses = (TextView) v.findViewById(R.id.losses);
            TextView points = (TextView) v.findViewById(R.id.points);

            pos.setText(String.valueOf(o.getClientName()));
            name.setText(String.valueOf(o.getClientLocation()));
            wins.setText(String.valueOf(o.getClientPhoneNo()));
            draws.setText(String.valueOf(o.getClientPrice()));
            losses.setText(String.valueOf(o.getClientPriceAdjust()));
            points.setText(String.valueOf(o.getClientProductID()));
        }
        return v;
    }
}