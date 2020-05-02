package br.com.fitcareplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class DoctorView extends BaseActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // don’t set any content view here, since its already set in DrawerActivity
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.activity_frame);
        // inflate the custom activity layout
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View activityView = layoutInflater.inflate(R.layout.activity_doctor_view, null,false);
        // add the custom layout of this activity to frame layout.
        frameLayout.addView(activityView);
        // now you can do all your other stuffs
        listView = (ListView) findViewById(R.id.listViewPacients);
        final ArrayList<Pacient> allPacients = addPatients();
        ArrayAdapter adapter = new CustomAdapterView(this, allPacients);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DoctorView.this, PacientDetail.class);
                intent.putExtra("name", allPacients.get(i).getUsername());
                startActivity(intent);
            }
        });

    }

    public ArrayList<Pacient> addPatients(){
        ArrayList<Pacient> pacients = new ArrayList<Pacient>();

        Pacient newPacient = new Pacient("Natália", "SJC", R.drawable.ic_launcher_background);
        pacients.add(newPacient);
        newPacient = new Pacient("Mauricio", "SJC", R.drawable.ic_launcher_background);
        pacients.add(newPacient);
        newPacient = new Pacient("Ana", "Jacarei", R.drawable.ic_launcher_background);
        pacients.add(newPacient);
        newPacient = new Pacient("José", "Taubaté", R.drawable.ic_launcher_background);
        pacients.add(newPacient);
        newPacient = new Pacient("Gilson", "Lorena", R.drawable.ic_launcher_background);
        pacients.add(newPacient);
        newPacient = new Pacient("Hudson", "Sampaio Correa", R.drawable.ic_launcher_background);
        pacients.add(newPacient);

        return pacients;

    }
}