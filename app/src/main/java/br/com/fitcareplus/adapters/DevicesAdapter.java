package br.com.fitcareplus.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import br.com.fitcareplus.R;

import java.util.ArrayList;

public class DevicesAdapter extends ArrayAdapter<BluetoothDevice> {

    public DevicesAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        super(context, R.layout.device_list_row, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_list_row, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.device_name);
        TextView uidTextView = convertView.findViewById(R.id.device_uid);

        final String name = device.getName() == null ? "Desconhecido" : device.getName();

        nameTextView.setText(name);
        uidTextView.setText(device.getAddress());

        return convertView;
    }
}
