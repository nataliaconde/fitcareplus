package br.com.fitcareplus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

class CustomAdapterView extends ArrayAdapter<Pacient> {
    Context context;
    private final ArrayList<Pacient> elements;

    CustomAdapterView (Context c, ArrayList<Pacient> elements) {
        super(c, R.layout.rowlistview, elements);
        this.context = c;
        this.elements = elements;

    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.rowlistview, parent, false);
        ImageView images = row.findViewById(R.id.userImage);
        TextView username = row.findViewById(R.id.nameUser);
        TextView description = row.findViewById(R.id.description);

        // now set our resources on views
        images.setImageResource(elements.get(position).getImage());
        username.setText(elements.get(position).getUsername());
        description.setText(elements.get(position).getDescription());

        return row;
    }
}
